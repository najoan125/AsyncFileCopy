package kr.hyfata.najoan.async.filecopy;

import kr.hyfata.najoan.async.filecopy.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

public class FileCopyProgress {
    private final long totalSize;
    private final AtomicLong copiedSize = new AtomicLong(0);
    private final String fileName;
    private FileCopyStatus status = FileCopyStatus.COPYING;
    private long temp;

    public FileCopyProgress(Path source) {
        this.fileName = source.getFileName().toString();
        if (Files.isDirectory(source)) {
            this.totalSize = FileUtil.calculateDirectorySize(source.toFile());
        } else {
            try {
                this.totalSize = Files.size(source);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateCopiedSize(long size) {
        copiedSize.addAndGet(size);
    }

    public double getProgress() {
        double progress = (double) copiedSize.get() / totalSize * 100;
        return Math.min(progress, 100.0);
    }

    public long getCopiedSize() {
        return copiedSize.get();
    }

    public long getTotalSize() {
        return totalSize;
    }

    public String getFileName() {
        return fileName;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(long temp) {
        this.temp = temp;
    }

    public FileCopyStatus getStatus() {
        return status;
    }

    public void setStatus(FileCopyStatus status) {
        this.status = status;
    }
}