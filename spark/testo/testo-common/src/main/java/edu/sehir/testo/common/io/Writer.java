package edu.sehir.testo.common.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by doktoray on 07/12/16.
 */
public class Writer {

    public static void saveAsTextFile(String path, String dirPrefix, String fileName, String content) {
        File pathToFile = new File(path, dirPrefix);
        if (!pathToFile.exists()) {
            pathToFile.mkdir();
        }
        String fullPath = new File(new File(path, dirPrefix).getPath(), fileName+".txt").getPath();
        try{
            PrintWriter writer = new PrintWriter(fullPath, "UTF-8");
            writer.println(content);
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }
}
