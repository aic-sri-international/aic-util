package com.sri.ai.util.csvdb;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvDatabase implements Database {
  private Map<String, Table> schema = Collections.synchronizedMap(new HashMap<>());

  @Override
  public int load(Path csvfile) {
    Table table = TableFactory.toTable(csvfile);
    schema.put(table.getName(), table);
    return table.getRowCount();
  }

  @Override
  public boolean delete(String tableName) {
    Table table = schema.remove(tableName);
    return table != null;
  }

  @Override
  public boolean containsTable(String tableName) {
    return schema.containsKey(tableName);
  }

  @Override
  public int getTableCount() {
    return schema.size();
  }

  @Override
  public List<String> getTableNames() {
    List<String> sorted = new ArrayList<>(schema.keySet());
    sorted.sort(Comparator.naturalOrder());
    return sorted;
  }

  @Override
  public Table getTable(String tableName) {
    return schema.get(tableName);
  }
}
