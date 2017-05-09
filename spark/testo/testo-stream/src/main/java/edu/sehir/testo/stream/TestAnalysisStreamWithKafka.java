package edu.sehir.testo.stream;

import edu.sehir.testo.common.Path;
import edu.sehir.testo.stream.spark.context.TunedSparkContext;
import edu.sehir.testo.stream.utils.VectorUtils;
import kafka.serializer.StringDecoder;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;


public final class TestAnalysisStreamWithKafka {

    private transient static JavaSparkContext jsc;


    public static void main(String[] args) throws Exception {
        jsc = new TunedSparkContext(Globals.SPARK_MASTER, Globals.SPARK_APPNAME).getJavaSparkContext();
        JavaStreamingContext jssc = new JavaStreamingContext(jsc, Globals.SPARK_STREAM_BATCH_DURATION);

        Set<String> topicsSet = new HashSet<String>(Arrays.asList(Globals.KAFKA_SPARK_STREAM_TOPICS.split(",")));
        Map<String, String> kafkaParams = new HashMap<String, String>();
        kafkaParams.put("metadata.broker.list", Globals.KAFKA_SPARK_STREAM_BROKERS);

        // Create direct kafka stream with brokers and topics
        JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topicsSet
        );

        // Get the lines
        JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {
            public String call(Tuple2<String, String> tuple2) {
                return tuple2._2();
            }
        });
        System.out.println("lines...");
        lines.print();

        JavaPairDStream<String, Vector> vectors = lines.flatMapToPair(new PairFlatMapFunction<String, String, Vector>() {
            public Iterable<Tuple2<String, Vector>> call(String s) throws Exception {
                String[] testerAndArray = Globals.TESTER_SPLIT_BY.split(s);
                String testerID = testerAndArray[0];
                String vector = testerAndArray[1];
                String[] sarray = Globals.VECTOR_SPLIT_BY.split(vector);
                double[] values = new double[sarray.length];
                for (int i = 0; i < sarray.length; i++) {
                    values[i] = Double.parseDouble(sarray[i]);
                }
                return Collections.singletonList(new Tuple2<String, Vector>(testerID, Vectors.dense(values)));
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

        windowedVectors.foreachRDD(new Function<JavaPairRDD<String, Vector>, Void>() {
            public Void call(JavaPairRDD<String, Vector> stringVectorJavaPairRDD) throws Exception {
                stringVectorJavaPairRDD.foreach(new VoidFunction<Tuple2<String, Vector>>() {
                    public void call(Tuple2<String, Vector> stringVectorTuple2) throws Exception {
                        String value = "";
                        if (stringVectorTuple2 != null) {
                            value = stringVectorTuple2._2().toString();
                        }
                        JavaRDD<String> testerValue = jsc.parallelize(Arrays.asList(value));
                        testerValue.saveAsTextFile(Path.getPath(Globals.HDFS_PROCESSED_FILE_DIR, stringVectorTuple2._1(), Globals.TEST_FILE_NAME_FORMAT.format(new Date())));
//                        Writer.saveAsTextFile(
//                                Globals.HDFS_PROCESSED_FILE_DIR,
//                                stringVectorTuple2._1(),
//                                Globals.TEST_FILE_NAME_FORMAT.format(new Date()),
//                                stringVectorTuple2._2().toString()
//                        );
                    }
                });
                return null;
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