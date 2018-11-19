package com.sri.ai.util.csvdb;

import static com.sri.ai.util.csvdb.Utils.csvFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CsvDirScanner {
  private static final Logger LOG = LoggerFactory.getLogger(CsvDirScanner.class);
  private final Path csvDirPath;

  CsvDirScanner(Path csvDirPath) {
    this.csvDirPath = csvDirPath;
    createDirectories(csvDirPath, "CSV file");
  }

  private static void createDirectories(Path dirPath, String description) {
    String msg = String.format("%s directory '%s'", description, dirPath);
    if (Files.notExists(dirPath)) {
      try {
        Files.createDirectories(dirPath);
        LOG.info("Created " + msg);
      } catch (Exception ex) {
        throw new RuntimeException("Cannot create " + msg, ex);
      }
    } else {
      LOG.info("Using " + msg);
    }
  }

  List<Path> findCsvFiles() {
    try {
      return Files.list(csvDirPath).filter(csvFilter).collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(
          String.format("Error accessing csv files in directory: %s", csvDirPath), e);
    }
  }
}
