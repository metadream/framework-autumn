package com.arraywork.springfield.filewatch;

import java.io.File;

/**
 * 文件系统监听器接口
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/25
 */
public interface FileSystemListener {

    void onAdded(File file);
    void onModified(File file);
    void onDeleted(File file);

}