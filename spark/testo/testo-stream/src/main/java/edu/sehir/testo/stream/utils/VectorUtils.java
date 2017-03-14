package edu.sehir.testo.stream.utils;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

/**
 * Created by emrullah on 14/11/16.
 */
public class VectorUtils {

    public static Vector normalizeVector(Vector vector) {
        double[] sarray = vector.toArray();
        double[] values = new double[sarray.length];
        for (int i = 0; i < sarray.length; i++) {
            if (sarray[i] == 0.0) {
                values[i] = 0.0;
            } else {
                for (int j = i; j < sarray.length; j++) {
                    if (sarray[j] == 0.0) {
                        values[j] = 1.0;
                    }
                    else {
                        values[j] = sarray[j];;
                    }
                }
                break;
            }
        }
        return Vectors.dense(values);
    }


    public static Vector concatenateVectors(Vector vector1, Vector vector2) {
        double[] sarray1 = vector1.toArray();
        double[] sarray2 = vector2.toArray();
        double[] values = new double[sarray1.length];
        for (int i = 0; i < sarray1.length; i++) {
            values[i] = sarray1[i] + sarray2[i];
        }
        return Vectors.dense(values);
    }

}
