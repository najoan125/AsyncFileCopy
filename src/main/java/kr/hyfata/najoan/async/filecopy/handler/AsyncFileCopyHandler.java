package kr.hyfata.najoan.async.filecopy.handler;

import kr.hyfata.najoan.async.filecopy.FileCopyProgress;
import kr.hyfata.najoan.async.filecopy.FileCopyStatus;
import kr.hyfata.najoan.async.filecopy.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AsyncFileCopyHandler {
    private FileCopyProgress[] progressList;
    private final String jsonFilePath;
    private int copiesLength;

    public AsyncFileCopyHandler(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public FileCopyProgress[] getProgressList() {
        return progressList;
    }

    public int getCopiesLength() {
        return copiesLength;
    }

    public void build() {
        try {
            JsonParser jsonParser = new JsonParser(jsonFilePath);
            copiesLength = jsonParser.getCopiesLength();
            progressList = new FileCopyProgress[copiesLength];

            for (int i = 0; i < copiesLength; i++) {
                int index = i;
                String source = jsonParser.getSourcePath(index);
                String destination = jsonParser.getDestinationPath(index);
                CompletableFuture.runAsync(()-> copyFiles(source, destination, index));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFiles(String source, String destination, int index) {
        File sourceFile = new File(source);
        File targetFile = new File(destination);
        FileCopyProgress progress = new FileCopyProgress(sourceFile.toPath());
        progressList[index] = progress;

        try {
            if (sourceFile.isDirectory()) {
                FileUtil.copyDirectoryWithProgress(sourceFile, targetFile, progress);
            } else {
                FileUtil.copyFileWithProgress(sourceFile, targetFile, progress);
            }
            progress.setStatus(FileCopyStatus.COMPLETE);
        } catch (IOException e) {
            progress.setStatus(FileCopyStatus.FAILED);
            if (e instanceof FileNotFoundException) {
                System.err.println("File not found: " + e.getMessage());
            } else {
                System.err.println("IOException occurred: " + e.getMessage());
            }
        }
    }
}
