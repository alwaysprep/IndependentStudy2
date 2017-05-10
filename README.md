## FLUME
Collector Service
```
/opt/flume/bin/flume-ng agent -n collector_agent -c conf -f /root/IndependentStudy2/flume/conf/hdfs/collector-service.properties
```
Feedback Service
```
/opt/flume/bin/flume-ng agent -n feedback_agent -c conf -f /root/IndependentStudy2/flume/conf/hdfs/feedback-service.properties
```


## KAFKA
To Create Topic (for collection)
```
/opt/kafka/bin/kafka-topics.sh --create --zookeeper 10.100.8.55:2181 --replication-factor 1 --partitions 1 --topic test
```
To Create Topic (for feedback)
<br> 
example; for tester1
```
/opt/kafka/bin/kafka-topics.sh --create --zookeeper 10.100.8.55:2181 --replication-factor 1 --partitions 1 --topic tester1
```
To list existing topics
```
/opt/kafka/bin/kafka-topics.sh --list --zookeeper 10.100.8.55:2181
```
To monitor topic records
<br> 
example; for "test" topic
```
/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
```

To start zookeeper (In Master Server)
```
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties

```
To start kafka clients (In Slave Server(s))
```
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties

```

## SPARK
To Compile
```
mvn clean install
```
To Run
```
spark-submit --class edu.sehir.testo.stream.TestAnalysisStreamWithKafka \
             --master yarn \ 
             /root/IndependentStudy2/spark/testo/testo-stream/target/testo-stream-1.0-SNAPSHOT-jar-with-dependencies.jar
```
