package edu.sehir.testo.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cenk on 12/08/15.
 */
public class Utils {

  public static String datedName(String name) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("-yyyyMMddHHmmss");
    Date date = new Date();
    return name.concat(dateFormat.format(date));
  }
}
