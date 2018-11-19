package com.sri.ai.util.csvdb;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TableImpl implements Table {
  private List<List<String>> rows = new ArrayList<>();
  private Path filePath;
  private Map<String, Integer> headerMap;
  private String[] headers;

  public TableImpl(Path filePath, Map<String, Integer> headerMap) {
    this.filePath = filePath;
    this.headerMap = headerMap;
    headers = new String[headerMap.size()];
    for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
      headers[entry.getValue()] = entry.getKey();
    }
  }

  @Override
  public List<String> getColumnNames() {
    return Arrays.asList(headers);
  }

  @Override
  public int getColumnCount() {
    return headerMap.size();
  }

  @Override
  public int getRowCount() {
    return rows.size();
  }

  @Override
  public void addRow(List<String> row) {
    if (headers.length != row.size()) {
      throw new IllegalArgumentException(
          String.format(
              "Table has %d columns, row has %d columns, counts must match",
              headers.length, row.size()));
    }
    this.rows.add(row);
  }

  @Override
  public SearchResult search(Map<String, ?> columnNameToValueMap, int limit) {
    if (!headerMap.keySet().containsAll(columnNameToValueMap.keySet())) {
      Set<String> set = new HashSet<>(columnNameToValueMap.keySet());
      set.removeAll(headerMap.keySet());
      throw new IllegalArgumentException(
          "columnNameToValueMap contained column names not in the table: " + set);
    }

    if (limit <= 0) {
      limit = Integer.MAX_VALUE;
    }

    List<List<String>> foundRows = new ArrayList<>();
    List<String> currentRow;

    for (List<String> row : rows) {
      currentRow = row;

      for (Entry<String, ?> entry : columnNameToValueMap.entrySet()) {
        if (!columnMatches(entry.getKey(), entry.getValue(), currentRow)) {
          currentRow = null;
          break;
        }
      }

      if (currentRow != null) {
        foundRows.add(new ArrayList<>(currentRow));
        if (--limit <= 0) {
          break;
        }
      }
    }

    return new SearchResultImpl(headerMap, foundRows);
  }

  private boolean columnMatches(String columnName, Object columnValue, List rowValues) {
    Object rowValue = rowValues.get(headerMap.get(columnName));
    return rowValue == columnValue || rowValue != null && rowValue.equals(columnValue);
  }

  @Override
  public String getName() {
    return Utils.toTableName(filePath);
  }

  @Override
  public String format() {
    StringBuilder sb = new StringBuilder();
    sb.append(getName()).append(System.lineSeparator());
    sb.append(headerMap.keySet()).append(System.lineSeparator());
    rows.forEach(row -> sb.append(row).append(System.lineSeparator()));
    return sb.toString();
  }
}
