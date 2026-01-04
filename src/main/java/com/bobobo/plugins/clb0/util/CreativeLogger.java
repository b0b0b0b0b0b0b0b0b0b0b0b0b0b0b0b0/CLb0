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
import java.util.UUID;
import java.util.logging.Level;

public class CreativeLogger {
    private final CLb plugin;
    private final ConfigManager configManager;
    private final LogEntryFormatter formatter;
    private final LogRotationConfig rotationConfig;
    private final boolean debug;
    private final Map<UUID, LogFileManager> fileManagers;
    private LogFileManager defaultFileManager;

    public CreativeLogger(CLb plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.debug = configManager.isDebug();
        this.formatter = new LogEntryFormatter(configManager.getLogFormat(), configManager.getDateFormat());
        this.rotationConfig = configManager.getLogRotationConfig();
        this.fileManagers = new HashMap<>();

        if (!configManager.isLogByPlayer()) {
            String logFilePath = configManager.getLogFile();
            File logFile = new File(plugin.getDataFolder(), logFilePath);
            LogRotator rotator = new LogRotator(rotationConfig.getMaxFileSizeBytes());
            LogCompressor compressor = new LogCompressor();
            defaultFileManager = new LogFileManager(logFile, rotator, compressor);
        }
    }

    public boolean isDebug() {
        return debug;
    }

    private LogFileManager getFileManagerForPlayer(Player player) {
        if (!configManager.isLogByPlayer()) {
            return defaultFileManager;
        }

        UUID uuid = player.getUniqueId();
        return fileManagers.computeIfAbsent(uuid, k -> {
            String logFilePath = configManager.getLogFile();
            String playerId = configManager.getLogPlayerFormat().equals("uuid") ? uuid.toString() : player.getName();
            String fileName = logFilePath.replace("{player}", playerId);
            File logFile = new File(plugin.getDataFolder(), fileName);
            LogRotator rotator = new LogRotator(rotationConfig.getMaxFileSizeBytes());
            LogCompressor compressor = new LogCompressor();
            return new LogFileManager(logFile, rotator, compressor);
        });
    }

    public void logCreativeItem(Player player, ItemStack item) {
        if (debug) {
            plugin.getLogger().info(" ");
            plugin.getLogger().info("\u001B[36mlogCreativeItem called - Player: " + player.getName() +
                " Item: " + (item != null ? item.getType() : "null") + "\u001B[0m");
            plugin.getLogger().info(" ");
        }

        if (!configManager.isEnabled()) {
            if (debug) {
                plugin.getLogger().info("\u001B[33mPlugin disabled in config\u001B[0m");
            }
            return;
        }

        if (configManager.isPlayerBypassed(player.getName())) {
            if (debug) {
                plugin.getLogger().info("\u001B[33mPlayer " + player.getName() + " is bypassed\u001B[0m");
            }
            return;
        }

        if (item == null || item.getType() == Material.AIR) {
            if (debug) {
                plugin.getLogger().info("\u001B[33mItem is null or AIR\u001B[0m");
            }
            return;
        }

        LogFileManager fileManager = getFileManagerForPlayer(player);
        File logFile = fileManager.getCurrentLogFile();

        if (debug) {
            plugin.getLogger().info("\u001B[36mLog file path: " + logFile.getAbsolutePath() + "\u001B[0m");
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
            plugin.getLogger().info("\u001B[36mLog entry: " + logEntry + "\u001B[0m");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(logEntry);
            if (debug) {
                plugin.getLogger().info("\u001B[32mSuccessfully wrote to log file\u001B[0m");
                plugin.getLogger().info(" ");
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to write to log file", e);
        }
    }
}
