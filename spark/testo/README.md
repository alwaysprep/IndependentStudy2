## Spark UI
```
http://http://10.100.8.55:8080/
```


## FLUME
```
flume-ng agent -C spark-streaming-flume-sink_2.10-1.1.1.jar -n collector_agent -c conf -f /Users/doktoray/workspace/testo/conf/flume/collectorWithKafkaService.properties
flume-ng agent -n feedback_agent -c conf -f /Users/doktoray/workspace/testo/conf/flume/feedbackService.properties
```

## in Server
```
cd /opt/flume
./bin/flume-ng agent -n collector_agent -c conf -f /root/flume/conf/collector-service-with-kafka.properties
```

## KAFKA
```
cd /usr/local/Cellar/kafka/0.10.1.0/libexec/config
zookeeper-server-start zookeeper.properties
kafka-server-start server.properties
```

```
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
kafka-topics --list --zookeeper localhost:2181
```

```
kafka-console-producer --broker-list localhost:9092 --topic test
kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning
```

## SPARK
```
spark-submit --class edu.sehir.testo.stream.TestAnalysisStreamWithKafka --master yarn ./testo-stream-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## To Compile
```
vim testo-stream/src/main/java/edu/sehir/testo/stream/JavaDirectKafkaWordCount.java
mvn clean install
spark-submit --class edu.sehir.testo.stream.TestAnalysisStreamWithKafka --master yarn testo-stream/target/testo-stream-1.0-SNAPSHOT-jar-with-dependencies.jar
```