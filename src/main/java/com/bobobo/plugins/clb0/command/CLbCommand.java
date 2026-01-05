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
            return false;
        }

        String commandName = args[0].toLowerCase();

        switch (commandName) {
            case "reload":
                if (!sender.hasPermission("clb0.reload")) {
                    sender.sendMessage(languageManager.getMessage("no-permission"));
                    return true;
                }

                configManager.loadConfig();
                languageManager.reload();
                sender.sendMessage(languageManager.getMessage("plugin-reloaded"));
                return true;

            case "bypass":
                if (!sender.hasPermission("clb0.bypass")) {
                    sender.sendMessage(languageManager.getMessage("no-permission"));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(languageManager.getMessage("bypass-usage"));
                    return true;
                }

                String subCommand = args[1].toLowerCase();

                switch (subCommand) {
                    case "add":
                        if (args.length < 3) {
                            sender.sendMessage(languageManager.getMessage("bypass-add-usage"));
                            return true;
                        }

                        String playerName = args[2];
                        if (configManager.addBypassPlayer(playerName)) {
                            sender.sendMessage(languageManager.getMessage("bypass-added").replace("{player}", playerName));
                        } else {
                            sender.sendMessage(languageManager.getMessage("bypass-already-exists").replace("{player}", playerName));
                        }
                        return true;

                    case "remove":
                        if (args.length < 3) {
                            sender.sendMessage(languageManager.getMessage("bypass-remove-usage"));
                            return true;
                        }

                        playerName = args[2];
                        if (configManager.removeBypassPlayer(playerName)) {
                            sender.sendMessage(languageManager.getMessage("bypass-removed").replace("{player}", playerName));
                        } else {
                            sender.sendMessage(languageManager.getMessage("bypass-not-found").replace("{player}", playerName));
                        }
                        return true;

                    case "list":
                        List<String> bypassList = configManager.getBypassPlayers();
                        if (bypassList.isEmpty()) {
                            sender.sendMessage(languageManager.getMessage("bypass-list-empty"));
                        } else {
                            sender.sendMessage(languageManager.getMessage("bypass-list-header"));
                            for (String player : bypassList) {
                                sender.sendMessage("  - " + player);
                            }
                        }
                        return true;

                    default:
                        sender.sendMessage(languageManager.getMessage("bypass-usage"));
                        return true;
                }

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("clb0.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("clb0.bypass")) {
                completions.add("bypass");
            }
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("bypass") && sender.hasPermission("clb0.bypass")) {
            List<String> completions = new ArrayList<>();
            completions.add("add");
            completions.add("remove");
            completions.add("list");
            return completions;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("bypass") && sender.hasPermission("clb0.bypass")) {
            if (args[1].equalsIgnoreCase("remove")) {
                return new ArrayList<>(configManager.getBypassPlayers());
            }
        }

        return new ArrayList<>();
    }
}
