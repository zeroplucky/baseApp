package com.minda.logger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.minda.logger.Utils.checkNotNull;


/**
 * Abstract class that takes care of background threading the file log operation on Android.
 * implementing classes are free to directly perform I/O operations there.
 * <p>
 * Writes all logs to the disk with CSV format.
 */
public class DiskLogStrategy implements LogStrategy {

    @NonNull
    private final Handler handler;

    public DiskLogStrategy(@NonNull Handler handler) {
        this.handler = checkNotNull(handler);
    }

    @Override
    public void log(int level, @Nullable String tag, @NonNull String message) {
        checkNotNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        String obtainMessage = tag + "i0i" + message;
        handler.sendMessage(handler.obtainMessage(level, obtainMessage));
    }

    static class WriteHandler extends Handler {

        @NonNull
        private final String folder;
        private final int maxFileSize;
        private String tag = "no_tag";

        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize, String tag) {
            super(checkNotNull(looper));
            this.folder = checkNotNull(folder);
            this.maxFileSize = maxFileSize;
            this.tag = tag;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;
            String tag = "no";
            String log = "no";
            try {
                String[] conents = content.split("i0i");
                tag = conents[0];
                log = conents[1];
            } catch (Exception e) {
                log = content;
            }
            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, tag);

            try {
                fileWriter = new FileWriter(logFile, true);
                writeLog(fileWriter, log);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            checkNotNull(fileWriter);
            checkNotNull(content);

            fileWriter.append(content);
        }

        private File getLogFile(@NonNull String folderName, @NonNull String fileName) {
            checkNotNull(folderName);
            checkNotNull(fileName);

            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }

//            int newFileCount = 0;
            File newFile;
//            File existingFile = null;

            newFile = new File(folder, String.format("%s.txt", fileName));
//            while (newFile.exists()) {
//                existingFile = newFile;
//                newFileCount++;
//                newFile = new File(folder, String.format("%s_%s.txt", fileName, newFileCount));
//            }
//
//            if (existingFile != null) {
//                if (existingFile.length() >= maxFileSize) {
//                    return newFile;
//                }
//                return existingFile;
//            }

            return newFile;
        }
    }
}
