package com.sri.ai.util.csvdb;

import java.nio.file.Path;
import java.util.function.Predicate;

final class Utils {
  static final Predicate<Object> csvFilter = o -> o.toString().endsWith(".csv");

  /**
   * Get a table name from a file.
   *
   * @param filePath file path
   * @return the table name representation of the filename
   */
  public static String toTableName(Path filePath) {
    String filename = filePath.getFileName().toString();
    int ix = filename.lastIndexOf('.');
    return ix > 0 ? filename.substring(0, ix) : filename;
  }
}
