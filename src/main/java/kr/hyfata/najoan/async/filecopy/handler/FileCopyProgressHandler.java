package kr.hyfata.najoan.async.filecopy.handler;

import kr.hyfata.najoan.async.filecopy.FileCopyProgress;
import kr.hyfata.najoan.async.filecopy.FileCopyStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileCopyProgressHandler {
    private final FileCopyProgress[] progressList;
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    int copiesLength;
    int loadedCount = 0;

    final int period = 1;
    final TimeUnit timeUnit = TimeUnit.SECONDS;

    public FileCopyProgressHandler(FileCopyProgress[] progressList, int copiesLength) {
        this.progressList = progressList;
        this.copiesLength = copiesLength;
    }

    public void build() {
        scheduledExecutorService.scheduleAtFixedRate(this::print, 0, period, timeUnit);
    }

    private double getSpeed(FileCopyProgress progress) {
        double result = 0;
        if (progress.getTemp() != 0) {
            result = (progress.getCopiedSize() - progress.getTemp()) / 1024.0 / 1024.0;
        }
        progress.setTemp(progress.getCopiedSize());
        return result;
    }

    private void print() {
        if (progressList == null) {
            System.out.println("loading...");
            return;
        } else if (loadedCount != copiesLength) {
            printLoadedCount();
            return;
        }

        int finishCount = 0;
        System.out.println();
        for (FileCopyProgress progress : progressList) {
            System.out.print(getFormattedName(progress.getFileName()) + " : " + progress.getStatus().getName());
            if (progress.getStatus() == FileCopyStatus.COPYING) {
                System.out.printf(" - %.1f%% (%.1f MB/s)", progress.getProgress(), getSpeed(progress));
            } else {
                finishCount++;
            }
            System.out.println();
        }

        if (finishCount == copiesLength) {
            scheduledExecutorService.shutdownNow();
        }
    }

    private void printLoadedCount() {
        loadedCount = 0;
        for (FileCopyProgress progress : progressList) {
            if (progress != null) {
                loadedCount++;
            }
        }
        System.out.printf("(%d/%d) loaded!\n", loadedCount, copiesLength);
    }

    private String getFormattedName(String name) {
        int maxLength = 0;
        for (FileCopyProgress progress : progressList) {
            maxLength = Math.max(progress.getFileName().length(), maxLength);
        }

        int paddingLength = maxLength - name.length();
        return name + " ".repeat(Math.max(0, paddingLength));
    }
}
