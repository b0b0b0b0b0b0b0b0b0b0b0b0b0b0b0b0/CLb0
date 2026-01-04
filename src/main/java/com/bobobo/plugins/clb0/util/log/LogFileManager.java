package com.bobobo.plugins.clb0.util.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

public class LogFileManager {
    private final File logFile;
    private final LogRotator rotator;
    private final LogCompressor compressor;

    public LogFileManager(File logFile, LogRotator rotator, LogCompressor compressor) {
        this.logFile = logFile;
        this.rotator = rotator;
        this.compressor = compressor;
        initializeLogFile();
    }

    private void initializeLogFile() {
        ensureParentDirectoryExists();
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                }
            } catch (IOException e) {
            }
        }
    }

    public File getCurrentLogFile() {
        ensureParentDirectoryExists();
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    throw new RuntimeException("Failed to create log file - file already exists");
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to create log file", e);
            }
        }
        return logFile;
    }

    public void rotateIfNeeded(long currentSize, boolean rotationEnabled, boolean compressEnabled, int maxFiles) {
        if (!rotationEnabled || currentSize <= rotator.getMaxFileSizeBytes()) {
            return;
        }

        rotateLogFile(compressEnabled, maxFiles);
    }

    private void rotateLogFile(boolean compressEnabled, int maxFiles) {
        File rotatedFile = rotator.generateRotatedFileName(logFile);
        
        try {
            if (logFile.exists()) {
                Files.move(logFile.toPath(), rotatedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            return;
        }

        if (compressEnabled) {
            File compressedFile = compressor.getCompressedFileName(rotatedFile);
            if (compressor.compressFile(rotatedFile, compressedFile)) {
                if (!rotatedFile.delete()) {
                }
            }
        }

        cleanupOldFiles(maxFiles, compressEnabled);
        
        try {
            if (!logFile.createNewFile()) {
            }
        } catch (IOException e) {
        }
    }

    private void cleanupOldFiles(int maxFiles, boolean compressEnabled) {
        File parentDir = logFile.getParentFile();
        if (parentDir == null || !parentDir.exists()) {
            return;
        }

        String baseName = logFile.getName();
        File[] logFiles = parentDir.listFiles((dir, name) -> {
            if (name.equals(baseName)) {
                return false;
            }
            if (name.startsWith(baseName + ".")) {
                String suffix = name.substring(baseName.length() + 1);
                if (suffix.matches("\\d+(\\.gz)?$")) {
                    return true;
                }
            }
            return false;
        });

        if (logFiles == null || logFiles.length <= maxFiles - 1) {
            return;
        }

        Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified).reversed());

        for (int i = maxFiles - 1; i < logFiles.length; i++) {
            if (!logFiles[i].delete()) {
            }
        }
    }

    private void ensureParentDirectoryExists() {
        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
}
