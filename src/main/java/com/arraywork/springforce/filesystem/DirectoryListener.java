package com.arraywork.springforce.filesystem;

import java.io.File;

/**
 * Directory Listener Interface
 *
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
public interface DirectoryListener {

    void onScan(File file, int count, int total);
    void onAdd(File file, int count, int total);
    void onModify(File file, int count, int total);
    void onDelete(File file, int count, int total);

}