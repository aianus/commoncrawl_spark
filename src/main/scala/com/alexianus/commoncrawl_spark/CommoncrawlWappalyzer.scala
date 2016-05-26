package com.alexianus.commoncrawl_spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import com.martinkl.warc._
import com.martinkl.warc.mapreduce._
import org.apache.hadoop.io.LongWritable

object CommoncrawlWappalyzer {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("CommoncrawlWappalyzer")
    val sc = new SparkContext
    val warc = sc.newAPIHadoopFile(
      args(0),
      classOf[WARCInputFormat],
      classOf[LongWritable],
      classOf[WARCWritable]
    )

    val numPages = warc
      .map(x => (1,1))
      .count()

    println("Records in warc: %s".format(numPages))
  }
}
