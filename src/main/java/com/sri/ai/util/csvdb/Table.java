package com.sri.ai.util.csvdb;

import java.util.List;
import java.util.Map;

public interface Table {
  /** Get the list of column names */
  List<String> getColumnNames();

  /**
   * Get the number of columns in the table.
   *
   * @return column count
   */
  int getColumnCount();

  /**
   * The number of rows in the table.
   *
   * @return the row count
   */
  int getRowCount();

  /**
   * Add a row to the table.
   *
   * @param row row of data to add to the table
   */
  void addRow(List<String> row);

  /**
   * Get the table name
   *
   * @return the name of the table
   */
  String getName();

  /**
   * Search for a row
   *
   * @param columnNameToValueMap col name/values to search for
   * @param limit maximum number of rows to return. if limit is <= 0, the limit is {@link
   *     Integer#MAX_VALUE}
   * @return a {@link SearchResult}
   * @throws IllegalArgumentException if all column names passed in the columnNameToValueMap are not
   *     contained in the table
   */
  SearchResult search(Map<String, ?> columnNameToValueMap, int limit);

  /**
   * Get a formatted representation of the table and its data.
   *
   * @return formatted table
   */
  String format();
}
