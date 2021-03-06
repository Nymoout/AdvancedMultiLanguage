package me.smessie.MultiLanguage.bungeecord;

import me.smessie.MultiLanguage.bungeecord.commands.English;
import me.smessie.MultiLanguage.main.Cache;
import me.smessie.MultiLanguage.main.Languages;
import me.smessie.MultiLanguage.main.MySQL;
import me.smessie.MultiLanguage.main.Settings;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends Plugin {

    public static Main plugin;

    public static String defaultLanguage;

    public static boolean useMysql;

    public static String ip = null;
    public static int port = 3306;
    public static String username = null;
    public static String password = null;
    public static String db = null;

    public void onEnable() {
        plugin = this;

        DataFile.setupConfig(plugin);

        File configFile = new File("plugins/AdvancedMultiLanguage", "config.yml");
        Configuration config = null;
        try {
            config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        defaultLanguage = config.getString("defaultLanguage");
        useMysql = config.getBoolean("use-mysql");
        if (useMysql) {
            connectMysql();
            try (MySQL mySQL = connectMysql()) {
                mySQL.createTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            DataFile.setupData(plugin);
        }
        Settings.mode = "BungeeCord";
        Settings.useMysql = useMysql;
        Settings.table = config.getString("mysql.table");
        Settings.createMysqlTable = config.getBoolean("create-mysqlTable-ifNotExist");
        Settings.defaultLanguage = config.getString("defaultLanguage");

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new English());

        Languages.addSupportedLanguages();

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            int caching = configuration.getInt("caching");
            if (caching > 0) {
                Cache.setCaching(caching);
            } else {
                configuration.set("caching", 7200000);
                ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
                Cache.setCaching(7200000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        plugin = null;
    }

    public MySQL connectMysql() {

        File configFile = new File("plugins/AdvancedMultiLanguage", "config.yml");
        Configuration config = null;
        try {
            config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ip == null) {
            ip = config.getString("mysql.host");
            port = config.getInt("mysql.port");
            username = config.getString("mysql.user");
            password = config.getString("mysql.password");
            db = config.getString("mysql.database");
        }

        MySQL mysql = new MySQL(ip, port, username, password, db);

        System.out.println("Succesfully connected to mysql database!");
        return mysql;
    }

}