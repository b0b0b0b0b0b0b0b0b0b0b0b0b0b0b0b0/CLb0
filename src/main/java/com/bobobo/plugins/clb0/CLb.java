package com.bobobo.plugins.clb0;

import com.bobobo.plugins.clb0.command.CLbCommand;
import com.bobobo.plugins.clb0.config.ConfigManager;
import com.bobobo.plugins.clb0.config.LanguageManager;
import com.bobobo.plugins.clb0.listener.CreativePacketListener;
import com.bobobo.plugins.clb0.util.CreativeLogger;
import com.bobobo.plugins.clb0.util.UP;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CLb extends JavaPlugin {
    public static final String PREFIX = "\u001B[37m[\u001B[90mCLb0\u001B[37m]\u001B[0m ";

    private ConfigManager configManager;
    private LanguageManager languageManager;
    private CreativeLogger creativeLogger;

    @Override
    public void onLoad() {
        getLogger().info("\u001B[36mInitializing PacketEvents...\u001B[0m");
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this, configManager);
        creativeLogger = new CreativeLogger(this, configManager);

        PacketEvents.getAPI().getEventManager().registerListener(new CreativePacketListener(creativeLogger, this));

        CLbCommand command = new CLbCommand(configManager, languageManager);
        getCommand("clb0").setExecutor(command);
        getCommand("clb0").setTabCompleter(command);

        if (configManager.isCheckUpdate()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () ->
                UP.checkVersion(this, getDescription().getVersion()), 60L);
        }
    }

    @Override
    public void onDisable() {
        try {
            PacketEvents.getAPI().terminate();
        } catch (Exception | LinkageError e) {
        }
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
