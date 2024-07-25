package com.arraywork.springforce.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;

import com.arraywork.springforce.util.FileUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Directory Watcher
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
@Slf4j
public class DirectoryWatcher {

    private final Duration pollInterval;
    private final Duration quietPeriod;
    private final DirectoryListener listener;
    private FileSystemWatcher watcher;
    private Path observePath;

    /** Initialize the watcher parameters */
    public DirectoryWatcher(long pollInterval, long quietPeriod, DirectoryListener listener) {
        this.pollInterval = Duration.ofSeconds(pollInterval);
        this.quietPeriod = Duration.ofSeconds(quietPeriod);
        this.listener = listener;
    }

    /**
     * Start watch the specified directory
     * can be called repeatedly to change the directory
     */
    public void start(String rootDirectory) {
        stop();
        observePath = Path.of(rootDirectory);
        watcher = new FileSystemWatcher(true, pollInterval, quietPeriod);
        watcher.addSourceDirectory(observePath.toFile());
        watcher.addListener(changeSet -> {

            for (ChangedFiles files : changeSet) {
                Set<ChangedFile> changedFiles = files.getFiles();
                int count = 0, total = changedFiles.size();

                for (ChangedFile changedFile : changedFiles) {
                    File file = changedFile.getFile();
                    count++;

                    switch (changedFile.getType()) {
                        case ADD -> listener.onAdd(file, count, total);
                        case MODIFY -> listener.onModify(file, count, total);
                        case DELETE -> listener.onDelete(file, count, total);
                    }
                }
            }
        });

        watcher.start();
        log.info("Directory watcher is watching on {}", rootDirectory);
    }

    /** Scan all files manually */
    public void scan() throws IOException {
        List<File> files = FileUtils.walk(observePath);
        int count = 0, total = files.size();
        for (File file : files) {
            listener.onScan(file, ++count, total);
        }
    }

    /** Kill the listening process */
    public void stop() {
        if (watcher != null) {
            watcher.stop();
            watcher = null;
            log.info("Directory watcher stopped.");
        }
    }

}