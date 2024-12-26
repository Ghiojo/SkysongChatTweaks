package org.skysongdev.skysongchattweaks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import openrp.OpenRP;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.skysongdev.skysongchattweaks.commands.Conditions;
import org.skysongdev.skysongchattweaks.commands.Injuries;
import org.skysongdev.skysongchattweaks.commands.Play;
import org.skysongdev.skysongchattweaks.commands.Reload;
import org.skysongdev.skysongchattweaks.database.Database;
import org.skysongdev.skysongchattweaks.database.Pinger;
import org.skysongdev.skysongchattweaks.database.Profile;
import org.skysongdev.skysongchattweaks.listeners.*;
import org.skysongdev.skysongchattweaks.utils.Condition;
import org.skysongdev.skysongchattweaks.utils.Injury;
import org.skysongdev.skysongstats.SkysongStats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SkysongChatTweaks extends JavaPlugin {

    private static SkysongChatTweaks plugin;
    public MiniMessage mm = MiniMessage.miniMessage();
    private static OpenRP openrp;
    public static final String PLUGIN_TAG = "<dark_gray>[<gold>SkysongChat<dark_gray>] ";
    private static LuckPerms luckPerms;
    private static SkysongStats skysongStats;
    public static NamespacedKey optionKey;
    public static Database database;

    public Database getDatabase() { return database; }

    public MiniMessage getMiniMessage() { return mm; }

    public static OpenRP getOpenRP() { return openrp; }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }
    public static SkysongStats getSkysongStats() { return skysongStats; }

    public ArrayList<Condition> conditions;
    public ArrayList<Injury> injuries;
    public ArrayList<Profile> profiles;

    //TODO: Add a listener that looks for [c_<condition>] to replace the text with a condition
    //REMEMBER TO CHANGE OPENRP TO STRIP ABUSABLE FORMATS FROM KYORI

    public static SkysongChatTweaks getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        optionKey = new NamespacedKey(plugin, "option");
        profiles = new ArrayList<Profile>();
        if (getServer().getPluginManager().isPluginEnabled("OpenRP")) {
            openrp = (OpenRP) getServer().getPluginManager().getPlugin("OpenRP");
        } else {
            getLogger().info("OpenRP 2.3.5 is required for the Addon to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            luckPerms = LuckPermsProvider.get();
        } else {
            getLogger().info("LuckPerms is required for the plugin to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if(getServer().getPluginManager().isPluginEnabled("SkysongStats")){
            skysongStats = (SkysongStats) getServer().getPluginManager().getPlugin("SkysongStats");
        } else {
            getLogger().info("SkysongStats is required for the plugin to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        InitializeCommands();
        InitializeListeners();

        InitializeConditions();
        InitializeInjuries();
        saveDefaultConfig();
        InitializeDatabase();

        Bukkit.getScheduler().runTaskTimer(this, new Pinger(), 0L, 6000L);

    }

    public void InitializeCommands(){
        getCommand("play").setExecutor(new Play(this));
        getCommand("injury").setExecutor(new Injuries());
        getCommand("injuries").setExecutor(new Injuries());
        getCommand("condition").setExecutor(new Conditions());
        getCommand("conditions").setExecutor(new Conditions());
        getCommand("screload").setExecutor(new Reload());
    }

    public void InitializeListeners(){
        getServer().getPluginManager().registerEvents(new LinkChecker(), this);
        getServer().getPluginManager().registerEvents(new InjuryChecker(), this);
        getServer().getPluginManager().registerEvents(new ConditionChecker(), this);
        getServer().getPluginManager().registerEvents(new ConditionGUIListener(), this);
        getServer().getPluginManager().registerEvents(new InjuryGUIListener(), this);
        getServer().getPluginManager().registerEvents(new ProfileUpdateListener(), this);
    }


    public void InitializeConditions(){
        conditions = new ArrayList<>();
        List<String> set = getConfig().getConfigurationSection("conditions").getKeys(false).stream().toList();
        for(int i = 0; i < set.size(); i++){
            String name = getConfig().getString("conditions." + set.get(i) + ".name");
            String description = getConfig().getString("conditions." + set.get(i) + ".description");
            String item = getConfig().getString("conditions." + set.get(i) + ".item");
            conditions.add(new Condition(set.get(i), name, description, item));
        }
    }

    public void InitializeInjuries(){
        injuries = new ArrayList<>();
        List<String> set = getConfig().getConfigurationSection("injuries").getKeys(false).stream().toList();
        for(int i = 0; i < set.size(); i++){
            String name = getConfig().getString("injuries." + set.get(i) + ".name");
            String description = getConfig().getString("injuries." + set.get(i) + ".description");
            String item = getConfig().getString("injuries." + set.get(i) + ".item");
            List<String> permissions = getConfig().getStringList("injuries." + set.get(i) + ".permissions");
            String severity = getConfig().getString("injuries." + set.get(i) + ".severity");
            injuries.add(new Injury(set.get(i), name, description, item, permissions, severity));
            for(String perm : permissions){
                if(Bukkit.getPluginManager().getPermission(perm) == null){
                    Bukkit.getPluginManager().addPermission(new org.bukkit.permissions.Permission(perm));
                }
            }
        }
    }

    public Profile getProfile(String string, String uuid){
        for(Profile profile : profiles){
            if(profile.getUUID().equals(uuid) && profile.getProfile().equals(string)){
                return profile;
            }
        }
        Profile profile = new Profile(uuid, string, new ArrayList<>());
        profiles.add(profile);
        try {
            getDatabase().addProfile(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }

    public void InitializeDatabase(){
        try{
            this.database = new Database(
                    getConfig().getString("database.host"),
                    getConfig().getString("database.port"),
                    getConfig().getString("database.user"),
                    getConfig().getString("database.password"),
                    getConfig().getString("database.database_name"));
            database.initializeDatabase();
            database.dumpDatabaseData();
        }catch(SQLException e){
            Bukkit.getLogger().warning("[SkysongStats] Unable to connect to database and create tables!");
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
