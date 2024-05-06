package com.arraywork.springforce.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.InputStreamSource;

/**
 * Directory and File Utilities
 * @author AiChen
 * @copyright ArrayWork Inc.
 * @since 2024/04/28
 */
public class Files {

    // Get the file name without extension
    public static String getName(String filename) {
        if (filename == null) return null;
        int i = filename.lastIndexOf(".");
        return i > -1 ? filename.substring(0, i) : filename;
    }

    // Get the file extension
    public static String getExtension(String filename) {
        if (filename == null) return null;
        int i = filename.lastIndexOf(".");
        return i > -1 ? filename.substring(i) : "";
    }

    // Walk directory and add files to argument List<File>
    public static void walk(File dir, List<File> files) {
        File[] entries = dir.listFiles();
        for (File entry : entries) {
            if (entry.isFile()) {
                files.add(entry);
            } else if (entry.isDirectory()) {
                walk(entry, files);
            }
        }
    }

    // Determine input stream is image format or not
    public static boolean isImageFormat(InputStreamSource source) throws IOException {
        byte[] header = new byte[4];
        InputStream inputStream = source.getInputStream();
        int read = inputStream.read(header);
        inputStream.close();

        if (read != -1) {
            String fileCode = Strings.toHexadecimal(header).toUpperCase();
            return fileCode.matches("(FFD8FF|89504E|47494638|52494646).+"); // jpg|png|gif|webp
        }
        return false;
    }

}