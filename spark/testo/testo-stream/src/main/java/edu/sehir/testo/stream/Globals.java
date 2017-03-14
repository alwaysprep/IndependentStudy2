package edu.sehir.testo.stream;

import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Created by doktoray on 07/12/16.
 */
public class Globals {

    public static final Pattern TESTER_SPLIT_BY = Pattern.compile("-");
    public static final Pattern VECTOR_SPLIT_BY = Pattern.compile(",");

    public static final Integer PAUSE_INTERVAL_FOR_TEST_FILE_GENERATION = 10;

    public static final SimpleDateFormat TEST_FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String FLUME_SPARK_STREAM_HOST = "localhost";
    public static final Integer FLUME_SPARK_STREAM_PORT = 9999;

    public static final String KAFKA_SPARK_STREAM_BROKERS = "SrvT2C2Work01:9092,SrvT2C2Work02:9092,SrvT2C2Work03:9092";
//    public static final String KAFKA_SPARK_STREAM_BROKERS = "localhost:9092";
    public static final String KAFKA_SPARK_STREAM_TOPICS = "test";

    public static final Duration SPARK_STREAM_BATCH_DURATION = Durations.seconds(10);
    public static final Duration SPARK_STREAM_WINDOW_LENGTH = Durations.seconds(30);
    public static final Duration SPARK_STREAM_SLIDE_INTERVAL = Durations.seconds(10);

    public static final String HDFS_PROCESSED_FILE_DIR = "/flume/data/processed";
//    public static final String HDFS_PROCESSED_FILE_DIR = "/Users/doktoray/workspace/testo/Datasets/hdfs/processed";

        public static final String SPARK_MASTER = "spark://SrvT2C2Master:7077";
//    public static final String SPARK_MASTER = "local[4]";
    public static final String SPARK_APPNAME = "test";

}
