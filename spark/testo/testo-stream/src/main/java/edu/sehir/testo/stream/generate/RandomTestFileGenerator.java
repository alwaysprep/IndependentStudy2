package edu.sehir.testo.stream.generate;

import edu.sehir.testo.stream.Globals;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by doktoray on 07/12/16.
 */
public class RandomTestFileGenerator {

    private String path;
    private String testerName;
    private int testSize;

    public RandomTestFileGenerator(String path, String testerName, int testSize) {
        this.path = path;
        this.testerName = testerName;
        this.testSize = testSize;

        this.createDirectories();
    }

    private void createDirectories() {
//        File pathToFile = new File(this.path, this.testerName);
        File pathToFile = new File(this.path);
        if (!pathToFile.exists()) {
            pathToFile.mkdirs();
            System.out.println("Directories are created for "+this.testerName);
        }
    }

    public void generateRandomTestFile() {
        String fileName = Globals.TEST_FILE_NAME_FORMAT.format(new Date());
//        String fullPath = new File(new File(this.path, this.testerName).getPath(), fileName+".txt").getPath();
        String fullPath = new File(this.path, fileName+".txt").getPath();
        try{
            PrintWriter writer = new PrintWriter(fullPath, "UTF-8");
            writer.println(this.testerName + Globals.TESTER_SPLIT_BY.pattern() + this.generateRandomTests());
            writer.close();
            System.out.println("Random Test File is generated for "+this.testerName);
        } catch (IOException e) {
            System.out.println("File creation is failed!");
        }
    }

    private String generateRandomTests() {
        String[] values = new String[this.testSize];
        Random random = new Random();
        for (int i = 0; i < this.testSize; i++) {
            values[i] = Integer.toString(random.nextInt(2));
        }
        return StringUtils.join(values, Globals.VECTOR_SPLIT_BY.pattern());
    }

    public static void main(String[] args) {
        RandomTestFileGenerator tester1RandomTestFileGenerator = new RandomTestFileGenerator(
                "/Users/doktoray/workspace/testo/Datasets/testers/tester1/input", "tester1", 5);
        RandomTestFileGenerator tester2RandomTestFileGenerator = new RandomTestFileGenerator(
                "/Users/doktoray/workspace/testo/Datasets/testers/tester2/input", "tester2", 5);
        RandomTestFileGenerator tester3RandomTestFileGenerator = new RandomTestFileGenerator(
                "/Users/doktoray/workspace/testo/Datasets/testers/tester3/input", "tester3", 5);

        int fileCount = 0;
        while (fileCount < 12){
            tester1RandomTestFileGenerator.generateRandomTestFile();
            tester2RandomTestFileGenerator.generateRandomTestFile();
            tester3RandomTestFileGenerator.generateRandomTestFile();
            fileCount++;
            try {
                TimeUnit.SECONDS.sleep(Globals.PAUSE_INTERVAL_FOR_TEST_FILE_GENERATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
