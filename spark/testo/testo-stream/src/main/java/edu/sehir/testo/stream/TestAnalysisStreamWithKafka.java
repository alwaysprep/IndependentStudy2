package edu.sehir.testo.stream;

import edu.sehir.testo.common.io.Writer;
import edu.sehir.testo.stream.spark.context.TunedSparkContext;
import edu.sehir.testo.stream.utils.VectorUtils;
import kafka.serializer.StringDecoder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import scala.Tuple2;
import org.apache.kafka.common.serialization.StringDeserializer;


import java.util.*;


public final class TestAnalysisStreamWithKafka {

    public static void main(String[] args) throws Exception {
        JavaSparkContext jsc = new TunedSparkContext(Globals.SPARK_MASTER, Globals.SPARK_APPNAME).getJavaSparkContext();
        JavaStreamingContext jssc = new JavaStreamingContext(jsc, Globals.SPARK_STREAM_BATCH_DURATION);

        Collection<String> topics = Arrays.asList("test");

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", Globals.KAFKA_SPARK_STREAM_BROKERS);

        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "group_1");
        kafkaParams.put("auto.offset.reset", "latest");
        //kafkaParams.put("enable.auto.commit", false);
        //kafkaParams.put("partition.assignment.strategy",  "org.apache.kafka.clients.consumer.RangeAssignor");

        // Create direct kafka stream with brokers and topics
        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(

                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)

        );

        // Get the lines
        JavaDStream<String> lines = messages.map(
                new Function<ConsumerRecord<String, String>, String>() {
                    @Override
                    public String call(ConsumerRecord<String, String> record) {
                        return record.value();
                    }
                });
        System.out.println("lines...");
        lines.print();

        JavaPairDStream<String, Vector> vectors = lines.flatMapToPair(new PairFlatMapFunction<String, String, Vector>() {
            public Iterator<Tuple2<String, Vector>> call(String s) throws Exception {
                String[] testerAndArray = Globals.TESTER_SPLIT_BY.split(s);
                String testerID = testerAndArray[0];
                String vector = testerAndArray[1];
                String[] sarray = Globals.VECTOR_SPLIT_BY.split(vector);
                double[] values = new double[sarray.length];
                for (int i = 0; i < sarray.length; i++) {
                    values[i] = Double.parseDouble(sarray[i]);
                }
                return Collections.singletonList(new Tuple2<>(testerID, Vectors.dense(values))).iterator();
            }
        });
        System.out.println("vectors...");
        vectors.print();

        JavaPairDStream<String, Vector> windowedVectors = vectors.reduceByKeyAndWindow(new Function2<Vector, Vector, Vector>() {
            public Vector call(Vector vector1, Vector vector2) throws Exception {
                Vector nVector1 = VectorUtils.normalizeVector(vector1);
                Vector nVector2 = VectorUtils.normalizeVector(vector2);
                return VectorUtils.concatenateVectors(nVector1, nVector2);
            }
        }, Globals.SPARK_STREAM_WINDOW_LENGTH, Globals.SPARK_STREAM_SLIDE_INTERVAL);
        System.out.println("windowed vectors...");
        windowedVectors.print();

        windowedVectors.foreachRDD(new VoidFunction<JavaPairRDD<String,Vector>>() {
            public void call(JavaPairRDD<String, Vector> stringVectorJavaPairRDD) throws Exception {
                stringVectorJavaPairRDD.foreach(new VoidFunction<Tuple2<String, Vector>>() {
                    public void call(Tuple2<String, Vector> stringVectorTuple2) throws Exception {
                        Writer.saveAsTextFile(
                                Globals.HDFS_PROCESSED_FILE_DIR,
                                stringVectorTuple2._1(),
                                Globals.TEST_FILE_NAME_FORMAT.format(new Date()),
                                stringVectorTuple2._2().toString()
                        );
                    }
                });
//                return null;
            }
        });

//        StreamingKMeans model = new StreamingKMeans();
//        model.setK(2);
//        model.setDecayFactor(.5);
//        model.setRandomCenters(5, 0.0, Utils.random().nextLong());
//
//        System.out.println("prediction results...");
//        JavaPairDStream predictionResults = model.predictOnValues(windowedVectors);
//        predictionResults.print();

        // Start the computation
        jssc.start();
        jssc.awaitTermination();
    }
}
