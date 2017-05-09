package edu.sehir.testo.common;

import java.io.File;

public class Path {
    public static String getPath(String path, String dirPrefix, String fileName) {
        return new File(new File(path, dirPrefix).getPath(), fileName+".txt").getPath();
    }
}
