package com.arraywork.springforce.filewatch;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;

import com.arraywork.springforce.util.Files;

import lombok.extern.slf4j.Slf4j;

/**
 * Directory Watcher
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
@Slf4j
public class DirectoryWatcher {

    private Duration pollInterval;
    private Duration quietPeriod;
    private FileSystemListener listener;
    private FileSystemWatcher watcher;

    // Initialize the water parameters
    public DirectoryWatcher(long pollInterval, long quietPeriod, FileSystemListener listener) {
        this.pollInterval = Duration.ofSeconds(pollInterval);
        this.quietPeriod = Duration.ofSeconds(quietPeriod);
        this.listener = listener;
    }

    // Start watch the specified directory
    // (can be called repeatedly to change the directory)
    public void start(String rootDirectory) {
        stop();
        File rootEntry = new File(rootDirectory);
        watcher = new FileSystemWatcher(true, pollInterval, quietPeriod);
        watcher.addSourceDirectory(rootEntry);
        watcher.addListener(new FileChangeListener() {

            @Override
            public void onChange(Set<ChangedFiles> changeSet) {
                for (ChangedFiles files : changeSet) {
                    for (ChangedFile changedFile : files.getFiles()) {
                        File file = changedFile.getFile();

                        switch (changedFile.getType()) {
                            case ADD -> listener.onAdded(file);
                            case MODIFY -> listener.onModified(file);
                            case DELETE -> listener.onDeleted(file);
                        }
                    }
                }
            }

        });

        watcher.start();
        log.info("Directory watcher is watching on {}", rootDirectory);

        // Scan all files after starting
        List<File> files = new ArrayList<>();
        Files.walk(rootEntry, files);
        int count = 1, total = files.size();
        for (File file : files) {
            listener.onStarted(file, count, total);
            count++;
        }
    }

    // Kill the listening process
    public void stop() {
        if (watcher != null) {
            watcher.stop();
            watcher = null;
            log.info("Directory watcher stopped.");
        }
    }

}