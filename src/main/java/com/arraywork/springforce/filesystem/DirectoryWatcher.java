package com.arraywork.springforce.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Directory Watcher
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/12/13
 */
@Slf4j
@Component
public class DirectoryWatcher implements Runnable {

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
                    Path child = ((Path) key.watchable()).resolve(name);
                    File file = child.toFile();

                    switch (kind.name()) {
                        case "ENTRY_MODIFY" -> listener.onModify(file);
                        case "ENTRY_DELETE" -> listener.onDelete(file);
                        case "ENTRY_CREATE" -> {
                            listener.onCreate(file);
                            if (recursive && file.isDirectory()) {
                                register(child, recursive);
                            }
                        }
                    }
                }
                if (!key.reset()) {
                    log.warn("WatchKey no longer valid, stopping watcher for directory: {}", directory);
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error while watching directory: {}", directory, e);
        } finally {
            log.info("Stopped watching directory: {}", directory);
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
    interface ChangeListener {
        default void onCreate(File file) { }
        default void onModify(File file) { }
        default void onDelete(File file) { }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path root = Path.of("/home/xehu/Documents/test");
        DirectoryWatcher watcher = new DirectoryWatcher(new MyListener());
        watcher.start(root, true);

        Thread.sleep(10000);
        watcher.stop();
        System.out.println("Watcher stopped.");

        Thread.sleep(10000);
        watcher.start(Path.of("/home/xehu/Documents/test2/aaa"), true);
        System.out.println("Watcher started again.");
    }

    static class MyListener implements ChangeListener {
        @Override
        public void onCreate(File file) {
            if (file.isDirectory()) System.out.println("Directory created: " + file);
            else System.out.println("File created: " + file);
        }

        @Override
        public void onModify(File file) {
            if (file.isDirectory()) System.out.println("Directory modified: " + file);
            else System.out.println("File modified: " + file);
        }

        @Override
        public void onDelete(File file) {
            if (file.isDirectory()) System.out.println("Directory deleted: " + file);
            else System.out.println("File deleted: " + file);
        }
    }

}