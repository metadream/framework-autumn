package com.arraywork.springforce.filewatch;

import java.io.File;

/**
 * 文件系统监听器接口
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
public interface FileSystemListener {

    void onStarted(File file);
    void onAdded(File file);
    void onModified(File file);
    void onDeleted(File file);

}