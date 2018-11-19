package com.sri.ai.util.csvdb;

import com.sri.ai.util.csvdb.DirectoryWatcher.DirChangeEntry.TYPE;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DirectoryWatcher {
  private static final Logger LOG = LoggerFactory.getLogger(DirectoryWatcher.class);
  private final List<DirChangeEntry> dirChangeQueue = new ArrayList<>();
  private final ExecutorService dirChangeQueueExecutor = Executors.newSingleThreadExecutor();
  private final Runnable dirChangeQueueRunnable;
  private final Path watchDir;
  private WatchService watchService;

  DirectoryWatcher(
      final Path watchDir,
      final boolean flushQueueOnOverflow,
      final DirectoryChanged directoryChanged) {
    this.watchDir = watchDir;

    dirChangeQueueRunnable =
        () -> {
          try {
            TimeUnit.MILLISECONDS.sleep(2000);

            List<DirChangeEntry> prod;
            List<DirChangeEntry> tmp;

            synchronized (dirChangeQueue) {
              if (dirChangeQueue.isEmpty()) {
                LOG.debug("dirChangeQueue is empty");
                return;
              }

              if (dirChangeQueue.size() == 1) {
                prod = new ArrayList<>(dirChangeQueue);
                tmp = Collections.emptyList();
              } else {
                tmp = new ArrayList<>(dirChangeQueue);
                prod = new ArrayList<>();

                if (flushQueueOnOverflow) {
                  for (DirChangeEntry dirChangeEntry : tmp) {
                    if (dirChangeEntry.getType() == TYPE.OVERFLOW) {
                      prod.add(dirChangeEntry);
                      tmp = Collections.emptyList();
                      break;
                    }
                  }
                }
              }
              dirChangeQueue.clear();
            }

            LOG.debug("dirChangeQueue length: {}", Math.max(tmp.size(), prod.size()));
            if (prod.isEmpty()) {
              for (int i = 0; i < tmp.size(); ++i) {
                DirChangeEntry cur = tmp.get(i);
                DirChangeEntry next = i < tmp.size() - 1 ? tmp.get(i + 1) : null;
                if (next == null || !cur.getFile().equals(next.getFile())) {
                  prod.add(cur);
                } else if (cur.getType() == DirChangeEntry.TYPE.CREATE
                    && next.getType() == DirChangeEntry.TYPE.MODIFY) {
                  tmp.set(i + 1, cur);
                } else if (cur.getType() != next.getType()) {
                  prod.add(cur);
                }
              }
            }

            prod.forEach(
                e -> {
                  switch (e.getType()) {
                    case CREATE:
                      break;
                    case DELETE:
                      break;
                    case MODIFY:
                      break;
                    case OVERFLOW:
                      break;
                    default:
                      LOG.warn("Unknown event type: {}", e.getType());
                      return;
                  }

                  directoryChanged.fileEntryChange(e);
                });
          } catch (Exception e) {
            if (!(e instanceof InterruptedException)) {
              LOG.warn("Error from dirChangeQueueRunnable: {}", e.toString());
            }
          }
        };

    try {
      watchService = FileSystems.getDefault().newWatchService();
      LOG.debug("newWatchService completed");
    } catch (Exception e) {
      LOG.error("Cannot create WatchService", e);
      return;
    }

    try {
      watchDir.register(
          watchService,
          StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_DELETE,
          StandardWatchEventKinds.ENTRY_MODIFY);
    } catch (Exception e) {
      LOG.error("Error attempting to register WatchService on {}", watchDir, e);
      return;
    }

    Thread thread =
        new Thread(
            () -> {
              try {
                LOG.info("WatchService thread for {} started", watchDir);
                loop();
              } catch (Exception e) {
                LOG.info("WatchService thread for {} terminated", watchDir, e.toString());
              }
            });
    thread.setName("DirectoryWatcher");
    thread.start();
  }

  private void loop() throws InterruptedException {
    WatchKey key;
    List<DirChangeEntry> localEntries = new ArrayList<>();

    while ((key = watchService.take()) != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        DirChangeEntry dirChangeEntry = new DirChangeEntry();
        if (event.kind() != StandardWatchEventKinds.OVERFLOW) {
          dirChangeEntry.setFile(event.context().toString());
        }

        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
          dirChangeEntry.setType(DirChangeEntry.TYPE.CREATE);
        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
          dirChangeEntry.setType(DirChangeEntry.TYPE.MODIFY);
        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
          dirChangeEntry.setType(DirChangeEntry.TYPE.DELETE);
        } else if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
          dirChangeEntry.setType(DirChangeEntry.TYPE.OVERFLOW);
        }

        if (dirChangeEntry.getType() != null) {
          localEntries.add(dirChangeEntry);
        }
      }
      key.reset();

      if (!localEntries.isEmpty()) {
        boolean isFirst;

        synchronized (dirChangeQueue) {
          isFirst = dirChangeQueue.isEmpty();
          dirChangeQueue.addAll(localEntries);
        }
        localEntries.clear();
        if (isFirst) {
          dirChangeQueueExecutor.submit(dirChangeQueueRunnable);
        }
      }
    }
  }

  void stop() {
    if (watchService != null) {
      try {
        watchService.close();
      } catch (IOException e) {
        LOG.info("Error attempting to close WatchService for {}", watchDir, e);
      }
      dirChangeQueueExecutor.shutdownNow();
    }
  }

  public interface DirectoryChanged {
    void fileEntryChange(DirChangeEntry dirChangeEntry);
  }

  static class DirChangeEntry {
    private TYPE type;
    private String file;

    public TYPE getType() {
      return type;
    }

    public DirChangeEntry setType(TYPE type) {
      this.type = type;
      return this;
    }

    /**
     * Get the file related to the event.
     *
     * @return the file or null if the event type is OVERFLOW
     */
    public String getFile() {
      return file;
    }

    public DirChangeEntry setFile(String file) {
      this.file = file;
      return this;
    }

    @Override
    public String toString() {
      return "DirChangeEntry{" + "type=" + type + ", file='" + file + '\'' + '}';
    }

    enum TYPE {
      CREATE,
      DELETE,
      MODIFY,
      /** Events may have been lost or discarded. */
      OVERFLOW,
    }
  }
}
