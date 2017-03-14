package edu.sehir.testo.stream.spark.context;

import org.apache.spark.SparkConf;

/**
 * Created by cenk on 12/08/15.
 */
public class TunedSparkConf extends SparkConf {

  public TunedSparkConf(String master, String appName) {
    super(true);

    this.setMaster(master).setAppName(appName);
  }

}
