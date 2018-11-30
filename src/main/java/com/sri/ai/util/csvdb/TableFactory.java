package com.sri.ai.util.csvdb;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public final class TableFactory {
  /**
   * Load a CSV file into a {@link Table}
   *
   * <p>An appropriate error message will be logged if an error is encountered when loading or
   * processing the CSV file.
   *
   * @param csvFilePath path to the CSV file
   * @return the table
   * @throws RuntimeException if the CSV file cannot be processed
   */
  public static Table toTable(Path csvFilePath) {
    Table table;

    try {
      table = toTable_(csvFilePath);
    } catch (Exception e) {
      throw new RuntimeException(String.format("Error processing csv file '%s'", csvFilePath), e);
    }

    return table;
  }

  private static Table toTable_(Path csvFilePath) throws Exception {
    Table table;
    int columnCount;

    CSVParser parser;
    try (Reader in = new FileReader(csvFilePath.toFile())) {
      parser =
          CSVFormat.EXCEL
              .withHeader()
              .withIgnoreEmptyLines(true)
              .withIgnoreSurroundingSpaces(true)
              .parse(in);

      table = new TableImpl(csvFilePath, parser.getHeaderMap());
      columnCount = table.getColumnCount();

      for (CSVRecord record : parser) {
        List<String> row = new ArrayList<>(columnCount);

        for (int i = 0; i < columnCount; ++i) {
          row.add(record.get(i));
        }

        table.addRow(row);
      }
    }

    return table;
  }
}
