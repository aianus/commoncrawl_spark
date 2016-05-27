#!/bin/bash

sbt assembly && \
mkdir -p target/fatjar && \
cp target/scala-2.10/commoncrawl_spark-assembly*.jar target/fatjar/ && \
aws s3 sync --size-only target/fatjar/ s3://mrjob-emr-fun/spark_jar/ && \
aws emr add-steps \
   --region us-west-2 \
   --cluster-id $CLUSTER_ID \
   --steps Type=spark,Name=CommoncrawlSpark,Args=[--deploy-mode,cluster,--executor-cores,15,--class,com.alexianus.commoncrawl_spark.CommoncrawlWappalyzer,s3://mrjob-emr-fun/spark_jar/commoncrawl_spark-assembly-1.0.jar,s3n://aws-publicdatasets/common-crawl/crawl-data/CC-MAIN-2014-35/segments/1408500800168.29/warc/CC-MAIN-20140820021320-00000-ip-10-180-136-8.ec2.internal.warc.gz,s3://mrjob-emr-fun/spark_out/] && \
aws emr socks --region us-west-2 --cluster-id $CLUSTER_ID --key-pair-file ~/.ssh/EMR.pem
