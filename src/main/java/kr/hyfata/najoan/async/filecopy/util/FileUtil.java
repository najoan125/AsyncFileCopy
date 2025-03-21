package kr.hyfata.najoan.async.filecopy.util;

import kr.hyfata.najoan.async.filecopy.FileCopyProgress;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CountingInputStream;

import java.io.*;
import java.nio.file.Files;

public class FileUtil {
    public static long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += calculateDirectorySize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    public static void copyDirectoryWithProgress(File sourceFile, File targetFile, FileCopyProgress progress) throws IOException {
        FileUtils.copyDirectory(sourceFile, targetFile, file -> {
            try {
                progress.updateCopiedSize(Files.size(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            return true;
        });
    }

    public static void copyFileWithProgress(File sourceFile, File targetFile, FileCopyProgress progress) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             CountingInputStream cis = new CountingInputStream(fis);
             FileOutputStream fos = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = cis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                progress.updateCopiedSize(length);
            }
        }
    }
}
