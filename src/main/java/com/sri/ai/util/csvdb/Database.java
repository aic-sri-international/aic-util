package com.sri.ai.util.csvdb;

import java.nio.file.Path;
import java.util.List;

public interface Database {
  /**
   * Loads the CSV file into the database.
   *
   * @param csvfile CSV file path
   * @return number of rows added to the database
   */
  int load(Path csvfile);

  /**
   * Remove the table from the schema.
   *
   * @param tableName the name of the table to remove
   * @return true if the table was found and removed, false if the table was not found
   */
  boolean delete(String tableName);

  /**
   * Determine if the schema contains a table.
   *
   * @param tableName the name of the table
   * @return true if the schema contains the table
   */
  boolean containsTable(String tableName);

  /**
   * Get the number of tables in the schema.
   *
   * @return the table count
   */
  int getTableCount();

  /**
   * Get a sorted list of the tables contained in the schema.
   *
   * @return list of table names
   */
  List<String> getTableNames();

  /**
   * Get the table for the table name.
   *
   * @param tableName the name of the table
   * @return the table, or null if the table is not in the schema
   */
  Table getTable(String tableName);
}
