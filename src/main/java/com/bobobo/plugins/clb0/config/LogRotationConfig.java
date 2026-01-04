package com.bobobo.plugins.clb0.config;

public class LogRotationConfig {
    private final boolean enabled;
    private final long maxFileSizeBytes;
    private final int maxFiles;
    private final boolean compressOldLogs;

    public LogRotationConfig(boolean enabled, long maxFileSizeBytes, int maxFiles, boolean compressOldLogs) {
        this.enabled = enabled;
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.maxFiles = maxFiles;
        this.compressOldLogs = compressOldLogs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public boolean isCompressOldLogs() {
        return compressOldLogs;
    }
}
