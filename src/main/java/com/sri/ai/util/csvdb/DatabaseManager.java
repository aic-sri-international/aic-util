package com.sri.ai.util.csvdb;

import static com.sri.ai.util.csvdb.Utils.csvFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sri.ai.util.csvdb.DirectoryWatcher.DirChangeEntry;
import com.sri.ai.util.csvdb.DirectoryWatcher.DirChangeEntry.TYPE;

/** Creates and manages a database that is populated from CSV files. */
public class DatabaseManager {
  private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
  private final Path csvDirPath;
  private final Database database = new CsvDatabase();
  private final CsvDirScanner csvDirScanner;
  private DirectoryWatcher directoryWatcher;

  /**
   * Create an instance of a DatabaseManager.
   *
   * <p>The directory for {@code relativePathToCsvDir} will be created if it does not already exist.
   *
   * <p>Subdirectories will not be traversed.
   *
   * <p>All .csv files contained within the directory will be parsed and loaded into the database.
   * The name of the csv file (without the .csv extension) is used as the table name.
   *
   * <p>Call {@link #getDatabase}, then {@link Database#getTable(String)}, then {@link
   * Table#search(Map, int)} to search for values in the database.
   *
   * <p>Calling {@link #startDirectoryWatchService()} to update the database on filesystem changes.
   *
   * @param relativePathToCsvDir the relative path that contains (or will contain) CSV files
   */
  public DatabaseManager(String relativePathToCsvDir) {
    csvDirPath = Paths.get(".", relativePathToCsvDir).toAbsolutePath().normalize();
    this.csvDirScanner = new CsvDirScanner(csvDirPath);
    List<Path> csvFilePaths = csvDirScanner.findCsvFiles();
    loadCsvFileIntoDatabaseSchema(csvFilePaths);
  }

  /**
   * Start the CSV directory file watcher.
   *
   * <p>Watches the csv directory for new, changed, or deleted files and updates the database
   * accordingly.
   *
   * <p>If you call this method you should call {@link #stopDirectoryWatchService()} to stop threads
   * started by this service before you shutdown the application that is using the {@link
   * DatabaseManager}.
   */
  public void startDirectoryWatchService() {
    if (directoryWatcher != null) {
      LOG.info("The CSV directory file watch service was already started.");
      return;
    }

    directoryWatcher = new DirectoryWatcher(csvDirPath, true, this::processedDirectoryChange);
  }

  /**
   * Get the database.
   *
   * @return the database
   */
  public Database getDatabase() {
    return database;
  }

  /**
   * Stop the CSV directory file watcher.
   *
   * <p>Stops the threads started by {@link #startDirectoryWatchService()} and releases its
   * resources.
   */
  public void stopDirectoryWatchService() {
    if (directoryWatcher != null) {
      directoryWatcher.stop();
      directoryWatcher = null;
    }
  }

  private void loadCsvFileIntoDatabaseSchema(List<Path> csvFilePaths) {
    long startTime = System.currentTimeMillis();
    LOG.info("Starting to load database schema. CSV file count={}", csvFilePaths.size());
    for (Path csvFilePath : csvFilePaths) {
      try {
        int rowCount = database.load(csvFilePath);
        LOG.debug("Loaded CSV file. RowCount={}. File={}", rowCount, csvFilePath);
      } catch (Exception e) {
        LOG.error("Cannot load CSV file {}", csvFilePath, e);
      }
    }
    LOG.info(
        "Completed loading database schema in {} millis, file count={}",
        System.currentTimeMillis() - startTime,
        csvFilePaths.size());
  }

  private void processedDirectoryChange(DirChangeEntry dirChangeEntry) {
    if (dirChangeEntry.getType() == TYPE.OVERFLOW) {
      List<Path> csvFilePaths = null;
      try {
        csvFilePaths = csvDirScanner.findCsvFiles();
      } catch (Exception e) {
        LOG.error("Error when trying to scan the CSV directory after an OVERFLOW event", e);
      }

      if (csvFilePaths != null) {
        loadCsvFileIntoDatabaseSchema(csvFilePaths);
      }
      return;
    }

    Path filePath = Paths.get(csvDirPath.toString(), dirChangeEntry.getFile());
    if (!csvFilter.test(filePath)) {
      LOG.debug("Notification from dir watcher about a non-csv file {}", dirChangeEntry);
      return;
    }

    if (dirChangeEntry.getType() == TYPE.DELETE) {
      String tableName = Utils.toTableName(filePath);
      if (database.delete(tableName)) {
        LOG.debug(
            "Deleted table '{}' from schema because file was deleted: {}", tableName, filePath);
      } else {
        LOG.debug(
            "Received delete event for file but table was not in schema: " + "table='{}', file={}",
            tableName,
            filePath);
      }
    } else {
      loadCsvFileIntoDatabaseSchema(Collections.singletonList(filePath));
    }
  }
}
