package com.sri.ai.test.util.csvdb;

import static com.sri.ai.test.util.csvdb.CsvTestUtils.removeCsvTmpDirIfExists;
import static com.sri.ai.test.util.csvdb.CsvTestUtils.createCsvFileDateRainTemp;

import com.sri.ai.util.csvdb.Database;
import com.sri.ai.util.csvdb.DatabaseManager;
import com.sri.ai.util.csvdb.Table;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseManagerTest {
  private static DatabaseManager databaseManager;
  private static String TABLE_EXIST_ONE = "table exist one";
  private static String TABLE_EXIST_TWO = "table exist two";
  private static String TABLE_ONE = "table one";
  private static String TABLE_TWO = "table two";

  @BeforeClass
  public static void initialize() {
    removeCsvTmpDirIfExists();

    // Add 2 csv files
    createCsvFileDateRainTemp(TABLE_EXIST_ONE, 2);
    createCsvFileDateRainTemp(TABLE_EXIST_TWO, 40);

    // Create DatabaseManager, it should load the above 2 files
    databaseManager = new DatabaseManager(CsvTestUtils.CSV_RELATIVE_PATH);
    Database db = databaseManager.getDatabase();
    Assert.assertEquals("pre-existing tables not loaded at db start-up", 2, db.getTableCount());
    List<String> tableNames = db.getTableNames();
    tableNames.removeAll(Arrays.asList(TABLE_EXIST_ONE, TABLE_EXIST_TWO));
    Assert.assertTrue("Database.getTableNames", tableNames.isEmpty());
  }

  @AfterClass
  public static void cleanup() {
    databaseManager.stopDirectoryWatchService();
    removeCsvTmpDirIfExists();
  }

  private static void pause(long secs) {
    try {
      Thread.sleep(secs);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // Replace path passed to ctor with one on your system that contains a flat dir of csv files
  // to determine load time from DatabaseManager console output. The path must be a relative path !
  // @Test
  public void loadTimeTest() {
    String relativePath = "../../CSV data/flat";

    if (!Files.isDirectory(Paths.get(relativePath))) {
      throw new IllegalArgumentException(String.format("'%s + does not exist", relativePath));
    }

    new DatabaseManager(relativePath);
  }

  // Test can take a bit of time, so don't run it by default
//  @Test
  public void testWatcher() {
    databaseManager.startDirectoryWatchService();
    Database db = databaseManager.getDatabase();

    Table tableOne = null;

    Path tableOnePath = createCsvFileDateRainTemp(TABLE_ONE, 24);
    createCsvFileDateRainTemp(TABLE_TWO, 24);
    for (int i = 0; i < 15; ++i) {
      pause(2000);
      if (db.containsTable(TABLE_ONE) && db.containsTable(TABLE_TWO)) {
        tableOne = db.getTable(TABLE_ONE);
        break;
      }
    }
    Assert.assertTrue("Tables not found by table watcher",
        db.containsTable(TABLE_ONE) && db.containsTable(TABLE_TWO));


    createCsvFileDateRainTemp(TABLE_ONE, 10);
    for (int i = 0; i < 15; ++i) {
      pause(2000);
      if ((tableOne = db.getTable(TABLE_ONE)).getRowCount() == 10) {
        break;
      }
    }
    Assert.assertEquals("Table not updated", 10, tableOne.getRowCount());

    try {
      Files.delete(tableOnePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < 15; ++i) {
      pause(2000);
      if (!db.containsTable(TABLE_ONE)) {
        break;
      }
    }
    Assert.assertFalse("Table not deleted", db.containsTable(TABLE_ONE));

    databaseManager.stopDirectoryWatchService();
  }
}
