package com.bobobo.plugins.clb0;

import com.bobobo.plugins.clb0.config.ConfigManager;
import com.bobobo.plugins.clb0.config.LanguageManager;
import com.bobobo.plugins.clb0.listener.CreativeListener;
import com.bobobo.plugins.clb0.util.CreativeLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class CLb extends JavaPlugin {
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private CreativeLogger creativeLogger;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this, configManager);
        creativeLogger = new CreativeLogger(this, configManager);

        getServer().getPluginManager().registerEvents(new CreativeListener(creativeLogger, this), this);
    }

    @Override
    public void onDisable() {

    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public CreativeLogger getCreativeLogger() {
        return creativeLogger;
    }
}
