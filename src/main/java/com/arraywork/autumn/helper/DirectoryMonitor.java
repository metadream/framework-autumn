package com.arraywork.autumn.helper;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * Directory Monitor
 *
 * @author Marco
 * @copyright ArrayWork Inc.
 * @since 2025/03/24
 */
public class DirectoryMonitor {

    private FileAlterationMonitor monitor;
    private FileAlterationObserver observer;
    private FileAlterationListener listener;
    private boolean isRunning;

    public DirectoryMonitor(long interval, FileAlterationListener listener) {
        this.listener = listener;
        monitor = new FileAlterationMonitor(interval);
    }

    public synchronized void start(String directory) throws Exception {
        stop();
        observer = new FileAlterationObserver(directory);
        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
        isRunning = true;
    }

    public synchronized void stop() throws Exception {
        if (isRunning) {
            monitor.stop();
            if (observer != null) {
                monitor.removeObserver(observer);
            }
            isRunning = false;
        }
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public static class DirectoryListener implements FileAlterationListener {

        @Override
        public void onStart(FileAlterationObserver observer) { }

        @Override
        public void onDirectoryCreate(File directory) { }

        @Override
        public void onDirectoryChange(File directory) { }

        @Override
        public void onDirectoryDelete(File directory) { }

        @Override
        public void onFileCreate(File file) { }

        @Override
        public void onFileChange(File file) { }

        @Override
        public void onFileDelete(File file) { }

        @Override
        public void onStop(FileAlterationObserver observer) { }

    }

    public static void main(String[] args) throws Exception {
        DirectoryMonitor dm = new DirectoryMonitor(1000, new DirectoryMonitor.DirectoryListener() {
            @Override
            public void onFileCreate(File file) {
                System.out.println(file.getAbsoluteFile() + " was created.");
            }

            @Override
            public void onFileChange(File file) {
                System.out.println(file.getAbsoluteFile() + " was modified.");
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println(file.getAbsoluteFile() + " was deleted.");
            }
        });

        dm.start("C:\\Users\\Administrator\\Pictures\\test2");

        Thread.sleep(10000);
        dm.stop();
        System.out.println("Watcher stopped.");

        Thread.sleep(10000);
        dm.start("C:\\Users\\Administrator\\Pictures\\test");
        System.out.println("Watcher started again.");
    }

}