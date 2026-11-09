package org.skysongdev.skysongchattweaks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import openrp.OpenRP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.skysongdev.skysongchattweaks.chat.ChatAudience;
import org.skysongdev.skysongchattweaks.commands.Play;
import org.skysongdev.skysongchattweaks.commands.Reload;
import org.skysongdev.skysongchattweaks.condition.ConditionTagListener;
import org.skysongdev.skysongchattweaks.condition.ConditionTagRenderer;
import org.skysongdev.skysongchattweaks.concurrent.BukkitMainThread;
import org.skysongdev.skysongchattweaks.concurrent.MainThread;
import org.skysongdev.skysongchattweaks.config.ChatTweaksSettings;
import org.skysongdev.skysongchattweaks.database.Database;
import org.skysongdev.skysongchattweaks.database.Pinger;
import org.skysongdev.skysongchattweaks.database.Profile;
import org.skysongdev.skysongchattweaks.injury.InjuryAwardAnnouncer;
import org.skysongdev.skysongchattweaks.injury.InjuryTagListener;
import org.skysongdev.skysongchattweaks.injury.InjuryTagRenderer;
import org.skysongdev.skysongchattweaks.injury.InjuryTeacher;
import org.skysongdev.skysongchattweaks.listeners.*;
import org.skysongdev.skysongchattweaks.stats.ApiVersionCheck;
import org.skysongdev.skysongstats.SkysongStats;
import org.skysongdev.skysongstats.api.AnnouncerRegistration;
import org.skysongdev.skysongstats.api.SkysongStatsApi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public final class SkysongChatTweaks extends JavaPlugin {

    private static SkysongChatTweaks plugin;
    public MiniMessage mm = MiniMessage.miniMessage();
    private static OpenRP openrp;
    public static final String PLUGIN_TAG = "<dark_gray>[<gold>SkysongChat<dark_gray>] ";
    private static LuckPerms luckPerms;
    private static SkysongStats skysongStats;
    private SkysongStatsApi stats;
    private AnnouncerRegistration awardAnnouncements;
    public static Database database;

    public Database getDatabase() { return database; }

    public MiniMessage getMiniMessage() { return mm; }

    public static OpenRP getOpenRP() { return openrp; }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }
    public static SkysongStats getSkysongStats() { return skysongStats; }

    /** The SkysongStats API, set once {@link #onEnable()} has checked its version. */
    public SkysongStatsApi getStats() { return stats; }

    public ArrayList<Profile> profiles;

    //TODO: Add a listener that looks for [c_<condition>] to replace the text with a condition
    //REMEMBER TO CHANGE OPENRP TO STRIP ABUSABLE FORMATS FROM KYORI

    public static SkysongChatTweaks getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
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

        if (!connectStatsApi()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        InitializeCommands();
        InitializeListeners();

        saveDefaultConfig();
        InitializeDatabase();

        Bukkit.getScheduler().runTaskTimer(this, new Pinger(), 0L, 6000L);

    }

    /** Gets the SkysongStats API once and checks that it is new enough. */
    private boolean connectStatsApi() {
        SkysongStatsApi api;
        try {
            api = SkysongStatsApi.get();
        } catch (IllegalStateException notEnabled) {
            getLogger().severe(notEnabled.getMessage());
            return false;
        }
        Optional<String> problem = ApiVersionCheck.problemWith(api.apiVersion());
        if (problem.isPresent()) {
            getLogger().severe(problem.get());
            return false;
        }
        stats = api;
        return true;
    }

    public void InitializeCommands(){
        getCommand("play").setExecutor(new Play(this));
        getCommand("screload").setExecutor(new Reload());
    }

    public void InitializeListeners(){
        getServer().getPluginManager().registerEvents(new LinkChecker(), this);
        MainThread mainThread = new BukkitMainThread(this);
        ChatAudience audience = new ChatAudience(getServer()::getOnlinePlayers, this::channelRange);
        InjuryTeacher teacher = new InjuryTeacher(audience, stats.profiles(), stats.injuries());
        InjuryTagRenderer injuryTags = new InjuryTagRenderer(stats.injuries(), stats.profiles(), mainThread);
        getServer().getPluginManager().registerEvents(new InjuryTagListener(injuryTags, teacher, mainThread), this);
        ChatTweaksSettings settings = new ChatTweaksSettings(this::getConfig);
        getServer().getPluginManager().registerEvents(
                new InjuryAwardAnnouncer(settings, audience, teacher, getServer()::getPlayer), this);
        // From here on SkysongStats leaves award messages to us instead of sending them privately.
        awardAnnouncements = stats.injuries().announceAwards(this);
        getServer().getPluginManager().registerEvents(
                new ConditionTagListener(new ConditionTagRenderer(stats.conditions())), this);
    }

    /** A channel's {@code range} in OpenRP's chat config; empty when it has none, which OpenRP reads as server-wide. */
    private OptionalInt channelRange(String channel) {
        FileConfiguration chat = openrp.getChat().getConfig();
        String path = "channels." + channel + ".range";
        return chat.isSet(path) ? OptionalInt.of(chat.getInt(path)) : OptionalInt.empty();
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
        // SkysongStats also drops it when we disable; ending it here keeps that explicit.
        if (awardAnnouncements != null) {
            awardAnnouncements.unregister();
            awardAnnouncements = null;
        }
    }
}
