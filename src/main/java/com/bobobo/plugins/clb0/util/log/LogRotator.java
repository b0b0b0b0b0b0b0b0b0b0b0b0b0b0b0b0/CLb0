package com.bobobo.plugins.clb0.util.log;

import java.io.File;

public class LogRotator {
    private final long maxFileSizeBytes;

    public LogRotator(long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public File generateRotatedFileName(File originalFile) {
        File parent = originalFile.getParentFile();
        String baseName = originalFile.getName();
        
        int counter = 1;
        File rotatedFile;
        
        do {
            String newName = baseName + "." + counter;
            rotatedFile = new File(parent, newName);
            counter++;
        } while (rotatedFile.exists() && counter < 10000);
        
        return rotatedFile;
    }
}
