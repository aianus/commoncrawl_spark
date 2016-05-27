package com.alexianus.commoncrawl_spark

import java.net.URL

import com.alexianus.wappalyzer.AppDetector
import com.martinkl.warc._
import com.martinkl.warc.mapreduce._
import org.apache.hadoop.io.LongWritable
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Try

object CommoncrawlWappalyzer {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("CommoncrawlWappalyzer")
    val sc = new SparkContext

    val num_pages = sc.accumulator(0)


    val warc = sc.newAPIHadoopFile(
      args(0),
      classOf[WARCInputFormat],
      classOf[LongWritable],
      classOf[WARCWritable]
    )

    warc
      .flatMap { case (_, record: WARCWritable) =>
          val r = record.getRecord
          if (r.getHeader.getRecordType == "response") Some(r) else None
      }
      .flatMap { record =>
        Try {
          val domain = new URL(record.getHeader.getTargetURI).getHost
          val body = new String(record.getContent, "UTF-8")
          num_pages += 1
          (domain, AppDetector.detect(body))
        }.toOption
      }
      .combineByKey(
        identity,
        (s1: Set[String], s2: Set[String]) => s1.union(s2),
        (s1: Set[String], s2: Set[String]) => s1.union(s2)
      )
      .saveAsTextFile(args(1))
  }
}
