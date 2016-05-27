#!/bin/bash

rm -rf local_out && \
sbt assembly && \
spark-submit \
    --driver-memory 10g \
    --executor-memory 10g \
    --class com.alexianus.commoncrawl_spark.CommoncrawlLocalTest \
    --master 'local[*]' \
    $(pwd)/target/scala-2.10/commoncrawl_spark-assembly-1.0.jar \
    $(pwd)/common-crawl/crawl-data/CC-MAIN-2014-35/segments/1408500800168.29/warc/CC-MAIN-20140820021320-00000-ip-10-180-136-8.ec2.internal.warc.gz \
    $(pwd)/local_out/ \
    200

