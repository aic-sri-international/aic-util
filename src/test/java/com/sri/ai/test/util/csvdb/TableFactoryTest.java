package com.sri.ai.test.util.csvdb;

import com.sri.ai.util.csvdb.SearchResult;
import com.sri.ai.util.csvdb.Table;
import com.sri.ai.util.csvdb.TableFactory;
import static com.sri.ai.test.util.csvdb.CsvTestUtils.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TableFactoryTest {
  @BeforeClass
  public static void initialize() {
    removeCsvTmpDirIfExists();
  }

  @AfterClass
  public static void cleanup() {
    removeCsvTmpDirIfExists();
  }

  @Test
  public void csvFileLoaderTest() {
    String tableName = "test table name";
    int rowCount = 13;
    Path csvFile = createCsvFileDateRainTemp(tableName, rowCount);

    Table table = TableFactory.toTable(csvFile);
    System.out.println(table.format());
    System.out.flush();

    // Test that the table loaded correctly

    Assert.assertEquals("Table name", tableName, table.getName());

    Assert.assertEquals("Table row count", rowCount, table.getRowCount());
    Assert.assertEquals("Table column count",
        MONTH_RAIN_TEMP_HEADERS_ARRAY.length, table.getColumnCount());

    String[] columnNames = table.getColumnNames().toArray(new String[0]);
    Assert.assertArrayEquals("Headers", MONTH_RAIN_TEMP_HEADERS_ARRAY, columnNames);

    //--- Test a table search

    String dec = MONTHS_ARRAY[11];
    Map<String, String>  map = new HashMap<>();
    map.put(MONTH, MONTHS_ARRAY[0]);

    SearchResult result = table.search(map, 1);
    Assert.assertEquals("Result row count", 1, result.rowCount());
    Assert.assertEquals("Result month", MONTHS_ARRAY[0], result.getResult(0, MONTH));
    Assert.assertEquals("Result rain",
        DECIMAL_FORMAT.format(INITIAL_RAINFALL), result.getResult(0, RAINFALL));
    Assert.assertEquals("Result rain",
        INITIAL_TEMPERATURE + "", result.getResult(0, TEMPERATURE));

    // Same search criteria, increase the limit
    result = table.search(map, 2);
    Assert.assertEquals("Result row count", 2, result.rowCount());

    // Validate values in last row
    int multiplier = rowCount - 1;
    Assert.assertEquals("Result rain",
        DECIMAL_FORMAT.format(INITIAL_RAINFALL + (RAINFALL_INCR * multiplier)),
        result.getResult(1, RAINFALL));

    Assert.assertEquals("Result temperature",
        (INITIAL_TEMPERATURE - (TEMPERATURE_DECR * multiplier)) + "",
        result.getResult(1, TEMPERATURE));

    // validate search sepcifing all column value
    multiplier = rowCount - 2;
    String expectedRain =  DECIMAL_FORMAT.format(INITIAL_RAINFALL + (RAINFALL_INCR * multiplier));
    String expectedTemp = INITIAL_TEMPERATURE - (TEMPERATURE_DECR * multiplier) + "";
    String expectedMonth = MONTHS_ARRAY[multiplier];

    map = new HashMap<>();
    map.put(MONTH, expectedMonth);
    map.put(TEMPERATURE, expectedTemp);
    map.put(RAINFALL, expectedRain);

    result = table.search(map, 0);
    Assert.assertEquals("Result row count", 1, result.rowCount());
    result = table.search(map, 0);
    Assert.assertEquals("Result month", expectedMonth, result.getResult(0, MONTH));
    Assert.assertEquals("Result temperature", expectedTemp, result.getResult(0, TEMPERATURE));
    Assert.assertEquals("Result rainfall", expectedRain, result.getResult(0, RAINFALL));
  }
}
