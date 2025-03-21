package kr.hyfata.najoan.async.filecopy.handler;

import kr.hyfata.najoan.async.filecopy.FileCopyProgress;
import kr.hyfata.najoan.async.filecopy.FileCopyStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileCopyProgressHandler {
    private final FileCopyProgress[] progressList;
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private int period = 1;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public FileCopyProgressHandler(FileCopyProgress[] progressList) {
        this.progressList = progressList;
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
        if (progressList == null || progressList[0] == null) {
            return;
        }
        int finishCount = 0;
        System.out.println();
        for (FileCopyProgress progress : progressList) {
            System.out.print(progress.getFileName() + " : " + progress.getStatus().getName());
            if (progress.getStatus() == FileCopyStatus.COPYING) {
                System.out.printf(" - %.1f%% (%.1f MB/s)", progress.getProgress(), getSpeed(progress));
            } else {
                finishCount++;
            }
            System.out.println();
        }

        if (finishCount == progressList.length) {
            scheduledExecutorService.shutdownNow();
        }
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
