package com.bobobo.plugins.clb0.command;

import com.bobobo.plugins.clb0.config.ConfigManager;
import com.bobobo.plugins.clb0.config.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CLbCommand implements CommandExecutor, TabCompleter {
    private final ConfigManager configManager;
    private final LanguageManager languageManager;

    public CLbCommand(ConfigManager configManager, LanguageManager languageManager) {
        this.configManager = configManager;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(languageManager.getMessage("usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("clb0.reload")) {
                sender.sendMessage(languageManager.getMessage("no-permission"));
                return true;
            }

            configManager.loadConfig();
            languageManager.reload();

            sender.sendMessage(languageManager.getMessage("plugin-reloaded"));
            return true;
        }

        sender.sendMessage(languageManager.getMessage("usage"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("clb0.reload")) {
                completions.add("reload");
            }
            return completions;
        }
        return new ArrayList<>();
    }
}
