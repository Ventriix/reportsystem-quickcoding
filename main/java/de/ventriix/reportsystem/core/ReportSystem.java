package de.ventriix.reportsystem.core;

import de.ventriix.reportsystem.commands.Report_CMD;
import de.ventriix.reportsystem.listener.LeaveListener;
import de.ventriix.reportsystem.utils.ReportHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class ReportSystem extends Plugin {

    private static ReportSystem instance;

    /* Variables */
    private Configuration configuration;
    private ReportHandler reportHandler;
    private static String prefix = "§f[§cReport§f] §7";

    @Override
    public void onEnable() {
        try {
            instance = this;
            /* Config */
            File configFolder = new File("plugins/ReportSystem");
            if(!configFolder.exists()) configFolder.mkdir();

            File configFile = new File("plugins/ReportSystem/configuration.yml");
            if (!configFile.exists()) configFile.createNewFile();
            configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);

            addDefault("Info", "Coming soon!");

            saveConfig();
            /* Other stuff */
            reportHandler = new ReportHandler();
            reportHandler.schedule();

            ProxyServer.getInstance().getPluginManager().registerCommand(this, new Report_CMD());
            ProxyServer.getInstance().getPluginManager().registerListener(this, new LeaveListener());

            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(prefix + "§fPlugin §aenabled§f, have fun!"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }

    public static ReportSystem getInstance() {
        return instance;
    }

    public void saveConfig() {
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, new File("plugins/ReportSystem/configuration.yml"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addDefault(String key, Object value) {
        if(configuration.get(key) == null) configuration.set(key, value);
    }

    public boolean hasPermission(ProxiedPlayer proxiedPlayer, String permission) {
        if(proxiedPlayer.hasPermission(permission) || proxiedPlayer.hasPermission("*") || proxiedPlayer.hasPermission("reportsystem.*")) {
            return true;
        } else {
            proxiedPlayer.sendMessage(new TextComponent(prefix + "§cDazu hast du keine Rechte."));
            return false;
        }
    }
    public ReportHandler getReportHandler() {
        return reportHandler;
    }

    public static String getPrefix() {
        return prefix;
    }
}
