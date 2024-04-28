package com.arraywork.springforce.filewatch;

import java.io.File;

/**
 * File System Listener Interface
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
public interface FileSystemListener {

    void onStarted(File file, int count, int total);
    void onAdded(File file);
    void onModified(File file);
    void onDeleted(File file);

}