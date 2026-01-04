package com.bobobo.plugins.clb0.config;

import com.bobobo.plugins.clb0.CLb;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final CLb plugin;
    private FileConfiguration config;

    public ConfigManager(CLb plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getLanguage() {
        return config.getString("lang", "en");
    }

    public String getLogFile() {
        return config.getString("log-file", "logs/creative-items.log");
    }

    public String getLogFormat() {
        return config.getString("log-format", "[{date}] {player} took {item} x{amount} from creative menu");
    }

    public String getDateFormat() {
        return config.getString("date-format", "yyyy-MM-dd HH:mm:ss");
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled", true);
    }

    public boolean isDebug() {
        return config.getBoolean("debug", false);
    }

    public List<String> getBypassPlayers() {
        List<String> bypassList = config.getStringList("bypass-players");
        if (bypassList == null) {
            return new ArrayList<>();
        }
        return bypassList;
    }

    public boolean isPlayerBypassed(String playerName) {
        List<String> bypassList = getBypassPlayers();
        if (bypassList.isEmpty()) {
            return false;
        }
        String lowerName = playerName.toLowerCase();
        for (String bypassPlayer : bypassList) {
            if (bypassPlayer != null && bypassPlayer.toLowerCase().equals(lowerName)) {
                return true;
            }
        }
        return false;
    }

    public LogRotationConfig getLogRotationConfig() {
        boolean enabled = config.getBoolean("rotation.enabled", true);
        int maxFileSizeMb = config.getInt("rotation.max-file-size-mb", 10);
        int maxFiles = config.getInt("rotation.max-files", 5);
        boolean compressOldLogs = config.getBoolean("rotation.compress-old-logs", true);

        long maxFileSizeBytes = maxFileSizeMb * 1024L * 1024L;

        return new LogRotationConfig(enabled, maxFileSizeBytes, maxFiles, compressOldLogs);
    }
}
