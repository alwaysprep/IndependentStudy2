package edu.sehir.testo.stream.spark.context;

import edu.sehir.testo.common.Utils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;

/**
 * Created by cenk on 06/02/15.
 */
public class TunedSparkContext implements Serializable {

  final private String master;
  final private String appName;

  public TunedSparkContext(String master, String name) {
    this.master = master;
    this.appName = Utils.datedName(name);
  }

  public JavaSparkContext getJavaSparkContext() {
    SparkConf sparkConf = new TunedSparkConf(master, appName);
    return new JavaSparkContext(sparkConf);
  }

}
