package com.sri.ai.util.csvdb;

public interface SearchResult {

  /**
   * Get the number of rows in the result
   *
   * @return the number of rows, which may be zero
   */
  int rowCount();

  /**
   * Get a result for a column at the specified row index in the results.
   *
   * @param rowIx index of result row (zero relative)
   * @param columnName name of column
   * @return the value found for the column at the row, which may be null
   */
  String getResult(int rowIx, String columnName);
}
