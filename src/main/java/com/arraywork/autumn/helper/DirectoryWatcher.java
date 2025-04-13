package com.arraywork.autumn.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

/**
 * Directory Watcher (something unexpected)
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2024/12/13
 */
@Slf4j
@Deprecated
public class DirectoryWatcher implements Runnable {

    private final Map<String, LinkedList<ScheduledFuture<?>>> taskMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ChangeListener listener;

    private Thread thread;
    private WatchService watcher;
    private Path directory;
    private boolean recursive;

    public DirectoryWatcher(ChangeListener listener) {
        this.listener = listener;
    }

    /** Start the watching thread */
    public synchronized void start(Path directory, boolean recursive) throws IOException {
        if (running.get()) {
            stop();
        }
        this.directory = directory;
        this.recursive = recursive;
        watcher = FileSystems.getDefault().newWatchService();
        register(directory, recursive);

        running.set(true);
        thread = new Thread(this);
        thread.start();
    }

    /** Stop the watching thread */
    public synchronized void stop() throws IOException {
        if (running.get()) {
            running.set(false);
            if (thread != null) {
                thread.interrupt();
            }
            if (watcher != null) {
                watcher.close();
            }
        }
    }

    /** Process change events */
    @Override
    public void run() {
        log.info("Started watching directory: {}", directory);
        try {
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (ClosedWatchServiceException e) {
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path name = (Path) event.context();
                    Path changed = ((Path) key.watchable()).resolve(name);
                    String eventName = kind.name();

                    // Cancel tasks with the same file and event
                    String fileEventKey = eventName + ":" + changed;
                    LinkedList<ScheduledFuture<?>> taskList = taskMap.get(fileEventKey);
                    if (taskList != null) {
                        for (ScheduledFuture<?> task : taskList) {
                            task.cancel(false);
                        }
                        taskList.clear();
                        taskMap.remove(fileEventKey);
                    }

                    // Delayed triggering of monitoring events
                    ScheduledFuture<?> scheduledTask = scheduler.schedule(() -> {
                        processEvent(eventName, changed);
                    }, 1, TimeUnit.SECONDS);

                    // Add tasks to list
                    taskMap.computeIfAbsent(fileEventKey, k -> new LinkedList<>()).add(scheduledTask);
                }

                if (!key.reset()) {
                    log.warn("WatchKey no longer valid, stopping watcher for directory: {}", directory);
                    break;
                }
            }
        } finally {
            log.info("Stopped watching directory: {}", directory);
        }
    }

    /** Process changed path event */
    private void processEvent(String eventName, Path changed) {
        File file = changed.toFile();
        switch (eventName) {
            case "ENTRY_DELETE" -> listener.onDelete(file);
            case "ENTRY_MODIFY" -> {
                // If the file is deleted during copying,
                // the modify event will not be triggered.
                if (file.exists()) {
                    listener.onModify(file);
                }
            }
            case "ENTRY_CREATE" -> {
                listener.onCreate(file);
                // Watching added subdirectory
                if (recursive && file.isDirectory()) {
                    try {
                        register(changed, recursive);
                    } catch (IOException e) {
                        log.error("Error while watching directory: {}", directory, e);
                    }
                }
            }
        }
    }

    /** Watch specified directory or its subdirectories */
    private void register(Path directory, boolean recursive) throws IOException {
        if (!recursive) {
            register(directory);
            return;
        }
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /** Watch specified directory */
    private void register(Path directory) throws IOException {
        directory.register(watcher,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE);
    }

    @PreDestroy
    public void onDestroy() throws IOException {
        log.info("Stopping DirectoryWatcher via @PreDestroy...");
        stop();
    }

    /** Callback interface */
    public interface ChangeListener {
        default void onScan(List<File> files, Object options) { }
        default void onCreate(File file) { }
        default void onModify(File file) { }
        default void onDelete(File file) { }
    }

    static class MyListener implements ChangeListener {
        @Override
        public void onCreate(File file) {
            if (file.isFile()) System.out.println("File created: " + file);
            else System.out.println("Directory created: " + file);
        }

        @Override
        public void onModify(File file) {
            if (file.isFile()) System.out.println("File modified: " + file);
            else System.out.println("Directory modified: " + file);
        }

        @Override
        public void onDelete(File file) {
            if (file.isFile()) System.out.println("File deleted: " + file);
            else System.out.println("Directory deleted: " + file);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path root = Path.of("/home/xehu/Documents/test");
        DirectoryWatcher watcher = new DirectoryWatcher(new MyListener());
        watcher.start(root, true);

        //        Thread.sleep(10000);
        //        watcher.stop();
        //        System.out.println("Watcher stopped.");
        //
        //        Thread.sleep(10000);
        //        watcher.start(Path.of("/home/xehu/Documents/test2/aaa"), true);
        //        System.out.println("Watcher started again.");
    }

}