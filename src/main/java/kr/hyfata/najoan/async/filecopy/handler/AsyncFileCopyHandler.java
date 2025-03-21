package kr.hyfata.najoan.async.filecopy.handler;

import kr.hyfata.najoan.async.filecopy.FileCopyProgress;
import kr.hyfata.najoan.async.filecopy.FileCopyStatus;
import kr.hyfata.najoan.async.filecopy.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncFileCopyHandler {
    private FileCopyProgress[] progressList;
    private final String jsonFilePath;

    public AsyncFileCopyHandler(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    public FileCopyProgress[] getProgressList() {
        return progressList;
    }

    public void build() {
        try {
            JsonParser jsonParser = new JsonParser(jsonFilePath);
            int copiesLength = jsonParser.getCopiesLength();
            progressList = new FileCopyProgress[copiesLength];
            ExecutorService executorService = Executors.newFixedThreadPool(copiesLength);

            // submit to executor service
            for (int i = 0; i < copiesLength; i++) {
                int index = i;
                String source = jsonParser.getSourcePath(index);
                String destination = jsonParser.getDestinationPath(index);
                executorService.execute(() -> copyFiles(source, destination, index));
            }

            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace(System.err);
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
            e.printStackTrace(System.err);
        }
    }
}
