from __future__ import print_function

from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext
from pyspark.streaming.kafka import KafkaUtils

sc = SparkContext.getOrCreate()
ssc = StreamingContext(sc, 10)

kvs = KafkaUtils.createStream(ssc, "SrvT2C2Work01:9092", "spark-streaming-consumer", {"test": 1})

kvs.pprint()

ssc.start()
ssc.awaitTermination()


