#!/bin/bash

sbt assembly && \
mkdir -p target/fatjar && \
cp target/scala-2.10/commoncrawl_spark-assembly*.jar target/fatjar/ && \
aws s3 sync --size-only target/fatjar/ s3://mrjob-emr-fun/spark_jar/ && \
aws emr add-steps \
   --region us-west-2 \
   --cluster-id $CLUSTER_ID \
   --steps file://./emr_steps.json
aws emr socks --region us-west-2 --cluster-id $CLUSTER_ID --key-pair-file ~/.ssh/EMR.pem
