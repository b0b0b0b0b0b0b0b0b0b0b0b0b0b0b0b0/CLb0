package com.bobobo.plugins.clb0.config;

import com.bobobo.plugins.clb0.CLb;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {
    private final CLb plugin;
    private final ConfigManager configManager;
    private FileConfiguration languageConfig;
    private String currentLanguage;

    public LanguageManager(CLb plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        extractAllLanguageFiles();
        this.currentLanguage = configManager.getLanguage();
        loadLanguage();
    }

    private void extractAllLanguageFiles() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        String[] languages = {"ru", "en"};
        for (String lang : languages) {
            File languageFile = new File(langFolder, "lang_" + lang + ".yml");
            if (!languageFile.exists()) {
                plugin.saveResource("lang/lang_" + lang + ".yml", false);
            }
        }
    }

    public void loadLanguage() {
        this.currentLanguage = configManager.getLanguage();
        File langFolder = new File(plugin.getDataFolder(), "lang");
        File languageFile = new File(langFolder, "lang_" + currentLanguage + ".yml");

        if (!languageFile.exists()) {
            extractAllLanguageFiles();
        }

        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        InputStream defaultStream = plugin.getResource("lang/lang_" + currentLanguage + ".yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            languageConfig.setDefaults(defaultConfig);
        }
    }

    public String getMessage(String key) {
        return languageConfig.getString(key, key);
    }

    public void reload() {
        loadLanguage();
    }
}
