package com.bobobo.plugins.clb0.util;

import com.bobobo.plugins.clb0.CLb;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UP {
    private static final String VERSION_URL = "https://b0b0b0.dev/pl/cl.txt";
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void checkVersion(CLb plugin, String currentVersion) {
        try {
            String latestVersion = fetchLatestVersion();

            if (latestVersion == null) {
                logError("Failed to fetch the latest version. Skipping version check.");
                return;
            }

            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    console.sendMessage(CLb.PREFIX + " ");
                    logWarning("You are using an outdated version!");
                    logWarning("Current version: \u001B[90m" + currentVersion +
                            "\u001B[33m, latest version: \u001B[32m" + latestVersion + "\u001B[0m.");
                    logWarning("Please update the plugin. Choose one of the following links to download:");

                    console.sendMessage(CLb.PREFIX + "\u001B[36m1. Black-Minecraft: \u001B[0mhttps://black-minecraft.com/resources/clb0.10060/");
                    console.sendMessage(CLb.PREFIX + " ");
                });
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    logInfo("You are using the latest version of the plugin! Version: \u001B[32m" + currentVersion + "\u001B[0m.");
                });
            }
        } catch (Exception e) {
            logError("Error during version check: " + e.getMessage());
        }
    }

    private static String fetchLatestVersion() {
        try {
            URL url = new URL(VERSION_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return in.readLine().trim();
            }
        } catch (IOException e) {
            logError("Connection error to " + VERSION_URL + ": " + e.getMessage());
            return null;
        }
    }

    private static void logInfo(String message) {
        console.sendMessage(CLb.PREFIX + "\u001B[32m" + message + "\u001B[0m");
    }

    private static void logWarning(String message) {
        console.sendMessage(CLb.PREFIX + "\u001B[33m" + message + "\u001B[0m");
    }

    private static void logError(String message) {
        console.sendMessage(CLb.PREFIX + "\u001B[31m" + message + "\u001B[0m");
    }
}
