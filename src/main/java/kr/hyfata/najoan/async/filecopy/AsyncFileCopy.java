package kr.hyfata.najoan.async.filecopy;

import kr.hyfata.najoan.async.filecopy.handler.AsyncFileCopyHandler;
import kr.hyfata.najoan.async.filecopy.handler.FileCopyProgressHandler;

public class AsyncFileCopy {
    public static void main(String[] args) {
        String jsonFilePath = args.length != 1 ? "copy_paths.json" : args[0];

        AsyncFileCopyHandler handler = new AsyncFileCopyHandler(jsonFilePath);
        handler.build();

        FileCopyProgressHandler progressHandler = new FileCopyProgressHandler(handler.getProgressList());
        progressHandler.build();
    }
}