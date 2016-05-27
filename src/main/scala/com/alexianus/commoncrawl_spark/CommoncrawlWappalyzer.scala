package com.alexianus.commoncrawl_spark

import java.net.URL
import java.util.UUID

import com.alexianus.wappalyzer.AppDetector
import com.martinkl.warc._
import com.martinkl.warc.mapreduce._
import org.apache.hadoop.io.LongWritable
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Try

object CommoncrawlWappalyzer {

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("CommoncrawlWappalyzer")
      .set("spark.default.parallelism", "80")
      .set("spark.speculation", "true")
      .set("spark.speculation.interval", "1000ms")
    val sc = new SparkContext(conf)

    val response_pages = sc.accumulator(0L, "response_pages")
    val unique_domains = sc.accumulator(0L, "unique_domains")
    val analyzed_pages = sc.accumulator(0L, "analyzed_pages")

    val pathPattern = java.net.URLDecoder.decode(args(0), "UTF-8")
    val outPath     = args(1)
    val numPartitions = Integer.valueOf(args(2))

    val warc = sc.newAPIHadoopFile(
      pathPattern,
      classOf[WARCInputFormat],
      classOf[LongWritable],
      classOf[WARCWritable]
    )

    warc
    .flatMap { case (_, record: WARCWritable) =>
      val r = record.getRecord
      if (r.getHeader.getRecordType == "response") {
        Try {
          // Ignore subdomains
          val domain = new URL(r.getHeader.getTargetURI).getHost.split("\\.").reverse.take(2).reverse.mkString(".")
          response_pages += 1L
          (domain, r.getContent)
        }.toOption
      } else {
        None
      }
    }
    // Discard all but one body per domain
    .reduceByKey(
      (content: Array[Byte], _: Array[Byte]) => content,
      numPartitions
    )
    .flatMap { case (domain: String, response: Array[Byte]) =>
      Try {
        val responseString = new String(response, "UTF-8")
        analyzed_pages += 1L
        (domain, AppDetector.detect(responseString))
      }.toOption
    }
    // Dedupe  via set union
    .reduceByKey(_ | _)
    .coalesce(10)
    .saveAsTextFile(outPath + UUID.randomUUID().toString.substring(0,6))
  }
}
