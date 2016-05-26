package com.alexianus.commoncrawl_spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import com.martinkl.warc._
import com.martinkl.warc.mapreduce._
import org.apache.hadoop.io.LongWritable

object CommoncrawlWappalyzer {
  def main(args: Array[String]) {
    // TODO(aianus) use options to load from S3
    val lulz = "/Users/aianus/src/commoncrawl_spark/common-crawl/crawl-data/CC-MAIN-2014-35/segments/1408500800168.29/warc/CC-MAIN-20140820021320-00000-ip-10-180-136-8.ec2.internal.warc.gz"

    val conf = new SparkConf().setAppName("CommoncrawlWappalyzer")
    val sc = new SparkContext
    val warc = sc.newAPIHadoopFile(
      lulz,
      classOf[WARCInputFormat],
      classOf[LongWritable],
      classOf[WARCWritable]
    )

    val numPages = warc.count()

    println("Records in warc: %s".format(numPages))
  }
}
