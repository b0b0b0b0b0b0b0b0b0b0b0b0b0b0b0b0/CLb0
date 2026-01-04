package com.bobobo.plugins.clb0.util;

import com.bobobo.plugins.clb0.CLb;
import com.bobobo.plugins.clb0.config.ConfigManager;
import com.bobobo.plugins.clb0.config.LogRotationConfig;
import com.bobobo.plugins.clb0.util.formatter.LogEntryFormatter;
import com.bobobo.plugins.clb0.util.log.LogCompressor;
import com.bobobo.plugins.clb0.util.log.LogFileManager;
import com.bobobo.plugins.clb0.util.log.LogRotator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CreativeLogger {
    private final CLb plugin;
    private final ConfigManager configManager;
    private final LogEntryFormatter formatter;
    private final LogRotationConfig rotationConfig;
    private final boolean debug;
    private final Map<String, LogFileManager> fileManagers;

    public CreativeLogger(CLb plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.debug = configManager.isDebug();
        this.formatter = new LogEntryFormatter(configManager.getLogFormat(), configManager.getDateFormat());
        this.rotationConfig = configManager.getLogRotationConfig();
        this.fileManagers = new HashMap<>();
    }

    public boolean isDebug() {
        return debug;
    }

    private LogFileManager getFileManagerForPlayer(Player player) {
        if (!configManager.isLogByPlayer()) {
            String defaultKey = "default";
            return fileManagers.computeIfAbsent(defaultKey, k -> {
                String logFilePath = configManager.getLogFile();
                File logFile = new File(plugin.getDataFolder(), logFilePath);
                LogRotator rotator = new LogRotator(rotationConfig.getMaxFileSizeBytes());
                LogCompressor compressor = new LogCompressor();
                return new LogFileManager(logFile, rotator, compressor);
            });
        }

        String playerIdentifier;
        String format = configManager.getLogPlayerFormat();
        
        if (format.equalsIgnoreCase("uuid")) {
            playerIdentifier = player.getUniqueId().toString();
        } else {
            playerIdentifier = player.getName();
        }

        return fileManagers.computeIfAbsent(playerIdentifier, k -> {
            String logFilePath = configManager.getLogFile();
            String fileName = logFilePath;
            
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                String name = fileName.substring(0, lastDot);
                String extension = fileName.substring(lastDot);
                fileName = name + "-" + playerIdentifier + extension;
            } else {
                fileName = fileName + "-" + playerIdentifier;
            }

            File logFile = new File(plugin.getDataFolder(), fileName);
            LogRotator rotator = new LogRotator(rotationConfig.getMaxFileSizeBytes());
            LogCompressor compressor = new LogCompressor();
            return new LogFileManager(logFile, rotator, compressor);
        });
    }

    public void logCreativeItem(Player player, ItemStack item) {
        if (debug) {
            plugin.getLogger().info("DEBUG [CreativeLogger] logCreativeItem called - Player: " + player.getName() + 
                " Item: " + (item != null ? item.getType() : "null"));
        }
        
        if (!configManager.isEnabled()) {
            if (debug) {
                plugin.getLogger().info("DEBUG [CreativeLogger] Plugin disabled in config");
            }
            return;
        }

        if (configManager.isPlayerBypassed(player.getName())) {
            if (debug) {
                plugin.getLogger().info("DEBUG [CreativeLogger] Player " + player.getName() + " is bypassed");
            }
            return;
        }

        if (item == null || item.getType() == Material.AIR) {
            if (debug) {
                plugin.getLogger().info("DEBUG [CreativeLogger] Item is null or AIR");
            }
            return;
        }

        LogFileManager fileManager = getFileManagerForPlayer(player);
        File logFile = fileManager.getCurrentLogFile();
        
        if (debug) {
            plugin.getLogger().info("DEBUG [CreativeLogger] Log file path: " + logFile.getAbsolutePath());
        }
        
        long currentSize = logFile.length();

        if (rotationConfig.isEnabled()) {
            fileManager.rotateIfNeeded(
                currentSize,
                rotationConfig.isEnabled(),
                rotationConfig.isCompressOldLogs(),
                rotationConfig.getMaxFiles()
            );
            logFile = fileManager.getCurrentLogFile();
        }

        String logEntry = formatter.format(player, item);
        if (debug) {
            plugin.getLogger().info("DEBUG [CreativeLogger] Log entry: " + logEntry);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(logEntry);
            if (debug) {
                plugin.getLogger().info("DEBUG [CreativeLogger] Successfully wrote to log file");
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to write to log file", e);
        }
    }
}
