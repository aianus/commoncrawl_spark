package com.alexianus.wappalyzer

import scala.collection.JavaConversions._
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup

import scala.util.matching.Regex
import scala.util.parsing.json._

object AppDetector {
  val appsStream = getClass.getResourceAsStream("/com/alexianus/wappalyzer/apps.json")
  val rawAppsMap =
    JSON.parseFull(IOUtils.toString(appsStream))
      .get.asInstanceOf[Map[String, Any]]
    .apply("apps").asInstanceOf[Map[String, Any]]

  // Precompute maps of (compiled regex => app) for html and source
  val htmlApps = createRegexMap("html")
  val scriptApps = createRegexMap("script")

  // TODO(aianus) create an implication graph (ie. 1C-Bitrix implies PHP)

  def detect(body: String): Set[String] = {
    val htmlResults = detectApps(htmlApps, body)

    val doc = Jsoup.parse(body)
    val scripts = doc.select("script").toIterable
    val scriptResults = scripts
      .map{_.attr("src")}
      .flatMap(detectApps(scriptApps, _))
      .toSet

    scriptResults ++ htmlResults
  }

  def detectApps(regexMap: Map[Regex, String], content: String): Set[String] = {
    regexMap.keys.flatMap { k =>
      if (k.findFirstIn(content).nonEmpty) Some(regexMap(k)) else None
    }.toSet
  }

  def createRegexMap(attrName: String) : Map[Regex, String] = {
      rawAppsMap.flatMap { case (app: String, attrs: Map[String, Any]) =>
        attrs.get(attrName).flatMap({
          case (scriptRegex: String) => Some(List((scriptRegex, app)))
          case (scriptRegexes: Traversable[String]) => Some(scriptRegexes.map(regex => (regex, app)).toList)
          case _ => None
        })
      }
      .flatten
      .map { case (regex: String, app) =>
        // Remove 'version' and 'confidence' attributes at the end of the regexes
        (regex.split("\\\\;").head.r, app)
      }
      .toMap[Regex, String]
  }
}
