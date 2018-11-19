package com.sri.ai.util.csvdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResultImpl implements SearchResult {
  private Map<String, Integer> columnNameToIndexMap;
  // With the current implementation, each row will have a value for all columns, however,
  // a column's value may be null.
  private List<List<String>> resultRows = new ArrayList<>();

  SearchResultImpl(Map<String, Integer> columnNameToIndexMap, List<List<String>> resultRows) {
    this.columnNameToIndexMap = columnNameToIndexMap;
    this.resultRows = resultRows;
  }

  @Override
  public int rowCount() {
    return resultRows.size();
  }

  @Override
  public String getResult(int rowIx, String columnName) {
    Integer colIndex = columnNameToIndexMap.get(columnName);

    if (colIndex == null) {
      throw new IllegalArgumentException(
          String.format(
              "Column '%s' is not in the table, table columns=%s",
              columnName, columnNameToIndexMap.keySet()));
    }

    int rowCount = rowCount();
    if (rowIx < 0 || rowIx >= rowCount) {
      throw new IndexOutOfBoundsException(
          String.format("rowIx=%d, rowCount=%d", rowIx, rowCount()));
    }

    return resultRows.get(rowIx).get(colIndex);
  }

  @Override
  public String toString() {
    return "SearchResultImpl{"
        + "columnNameToIndexMap="
        + columnNameToIndexMap
        + ", resultRows="
        + resultRows
        + '}';
  }
}
