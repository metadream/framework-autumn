package com.arraywork.springfield.filewatch;

import java.io.File;
import java.time.Duration;
import java.util.Set;

import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * 目录监视器
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

    // 初始化监视器参数
    public DirectoryWatcher(long pollInterval, long quietPeriod, FileSystemListener listener) {
        this.pollInterval = Duration.ofSeconds(pollInterval);
        this.quietPeriod = Duration.ofSeconds(quietPeriod);
        this.listener = listener;
    }

    // 启动监视指定目录（可重复调用以更改目录）
    public void start(String rootDirectory) {
        stop();
        watcher = new FileSystemWatcher(true, pollInterval, quietPeriod);
        watcher.addSourceDirectory(new File(rootDirectory));
        watcher.addListener(new FileChangeListener() {

            @Override
            public void onChange(Set<ChangedFiles> changeSet) {
                for (ChangedFiles files : changeSet) {
                    for (ChangedFile changedFile : files.getFiles()) {
                        File file = changedFile.getFile();

                        switch (changedFile.getType()) {
                            case ADD:
                                listener.onAdded(file);
                                break;
                            case MODIFY:
                                listener.onModified(file);
                                break;
                            case DELETE:
                                listener.onDeleted(file);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

        });

        watcher.start();
        log.info("Directory watcher is watching on {}", rootDirectory);
    }

    // 中止监听线程
    public void stop() {
        if (watcher != null) {
            watcher.stop();
            watcher = null;
            log.info("Directory watcher stopped.");
        }
    }

}