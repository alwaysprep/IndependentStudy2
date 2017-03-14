package edu.sehir.testo.stream;

import edu.sehir.testo.common.io.Writer;
import edu.sehir.testo.stream.spark.context.TunedSparkContext;
import edu.sehir.testo.stream.utils.VectorUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;
import scala.Tuple2;

import java.util.Collections;
import java.util.Date;


public final class TestAnalysisStream {

    public static void main(String[] args) throws Exception {
        JavaSparkContext jsc = new TunedSparkContext("local[4]", "test").getJavaSparkContext();
        JavaStreamingContext jssc = new JavaStreamingContext(jsc, Globals.SPARK_STREAM_BATCH_DURATION);

        JavaReceiverInputDStream<SparkFlumeEvent> flumeStream =
                FlumeUtils.createPollingStream(jssc, Globals.FLUME_SPARK_STREAM_HOST, Globals.FLUME_SPARK_STREAM_PORT);

        JavaDStream<String> lines = flumeStream.map(new Function<SparkFlumeEvent, String>() {
//            {"headers": {"file": "\/Users\/doktoray\/workspace\/testo\/Datasets\/testers\/tester2\/tester_2-1.txt"}, "body": {"bytes": "tester2-1,1,1,1,1"}}
            public String call(SparkFlumeEvent flumeEvent) throws Exception {
                  return new String(flumeEvent.event().getBody().array());
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
                        Writer.saveAsTextFile(
                                Globals.HDFS_PROCESSED_FILE_DIR,
                                stringVectorTuple2._1(),
                                Globals.TEST_FILE_NAME_FORMAT.format(new Date()),
                                stringVectorTuple2._2().toString()
                        );
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