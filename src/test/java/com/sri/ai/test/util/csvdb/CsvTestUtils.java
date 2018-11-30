package com.sri.ai.test.util.csvdb;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public final class CsvTestUtils {
  static final String CSV_RELATIVE_PATH = "./csv_tmp_dir.tmp";
  static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
  static final String MONTHS_ARRAY[]
      = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  static final String MONTH = "month";
  static final String RAINFALL = "rainfall";
  static final String TEMPERATURE = "temperature";
  static final String[] MONTH_RAIN_TEMP_HEADERS_ARRAY = {MONTH, RAINFALL, TEMPERATURE};
  static final double INITIAL_RAINFALL = .10;
  static final int INITIAL_TEMPERATURE = 120;
  static final double RAINFALL_INCR = .10;
  static final int TEMPERATURE_DECR = 10;
  
  
  static void removeCsvTmpDirIfExists() {
    if (Files.isDirectory(Paths.get(CSV_RELATIVE_PATH))) {
      try {
        deleteDirectoryRecursion(Paths.get(CSV_RELATIVE_PATH));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static void deleteDirectoryRecursion(Path path) throws IOException {
    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
      try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
        for (Path entry : entries) {
          deleteDirectoryRecursion(entry);
        }
      }
    }
    Files.delete(path);
  }

  private static CSVPrinter getPrinter(Path filePath) {
    try {
      return new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.EXCEL);
    } catch (IOException e) {
      throw new RuntimeException("Error creating CSVPrinter for " + filePath, e);
    }
  }

  static Path getTableFilePath(String tableName) {
    Path csvDir = Paths.get(CSV_RELATIVE_PATH);
    try {
      Files.createDirectories(csvDir);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return csvDir.resolve(tableName + ".csv");
  }


  static Path createCsvFile(String tableName, String[] headers, Object[][] rows) {
    Path filePath = getTableFilePath(tableName);

    try (CSVPrinter printer = getPrinter(filePath)){
      printer.printRecord(Arrays.asList(headers));

      for (Object[] row : rows) {
          printer.printRecord(row);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error from CSVPrinter", e);
    }

    return filePath;
  }

  // Rainfall starts a .10 and increase by .10 per month
  // Temperature starts a 120 and decrease by 10 per month
  static Path createCsvFileDateRainTemp(String tableName, int rowCount) {
    Object[][] rows = new Object[rowCount][3];
    double rainfall = INITIAL_RAINFALL;
    int temperature = INITIAL_TEMPERATURE;
    int j = 0;
    for (int i = 0; i < rowCount; ++i) {
      Object[] cols = {MONTHS_ARRAY[j], DECIMAL_FORMAT.format(rainfall), temperature};
      rows[i] = cols;
      rainfall += RAINFALL_INCR;
      temperature -= TEMPERATURE_DECR;
      if (++j >= MONTHS_ARRAY.length) {
        j = 0;
      }
    }

    return createCsvFile(tableName, MONTH_RAIN_TEMP_HEADERS_ARRAY, rows);
  }

  @BeforeClass
  public static void initialize() {
    removeCsvTmpDirIfExists();
  }

  @AfterClass
  public static void cleanup() {
    removeCsvTmpDirIfExists();
  }

  @Test
  public void createCsvFileDateRainTempTest() {
    String tableName = "DateRainTempTable";
    int rowCount = 13;
    Path csvFile = createCsvFileDateRainTemp(tableName, rowCount);
    List<String> rows;
    try {
      rows = Files.readAllLines(csvFile);
    } catch (IOException e) {
      throw new RuntimeException("Cannot read table: " + csvFile);
    }

    Assert.assertEquals("CSV row count not as expected:", rowCount + 1, rows.size());
  }
}
