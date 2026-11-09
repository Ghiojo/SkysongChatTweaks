package org.skysongdev.skysongchattweaks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import openrp.OpenRP;
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
import org.skysongdev.skysongchattweaks.injury.InjuryAwardAnnouncer;
import org.skysongdev.skysongchattweaks.injury.InjuryTagListener;
import org.skysongdev.skysongchattweaks.injury.InjuryTagRenderer;
import org.skysongdev.skysongchattweaks.injury.InjuryTeacher;
import org.skysongdev.skysongchattweaks.listeners.*;
import org.skysongdev.skysongchattweaks.stats.ApiVersionCheck;
import org.skysongdev.skysongstats.api.AnnouncerRegistration;
import org.skysongdev.skysongstats.api.SkysongStatsApi;

import java.util.Optional;
import java.util.OptionalInt;

public final class SkysongChatTweaks extends JavaPlugin {

    private static SkysongChatTweaks plugin;
    public MiniMessage mm = MiniMessage.miniMessage();
    private static OpenRP openrp;
    public static final String PLUGIN_TAG = "<dark_gray>[<gold>SkysongChat<dark_gray>] ";
    private SkysongStatsApi stats;
    private AnnouncerRegistration awardAnnouncements;


    public MiniMessage getMiniMessage() { return mm; }

    public static OpenRP getOpenRP() { return openrp; }


    /** The SkysongStats API, set once {@link #onEnable()} has checked its version. */
    public SkysongStatsApi getStats() { return stats; }


    //TODO: Add a listener that looks for [c_<condition>] to replace the text with a condition
    //REMEMBER TO CHANGE OPENRP TO STRIP ABUSABLE FORMATS FROM KYORI

    public static SkysongChatTweaks getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        if (getServer().getPluginManager().isPluginEnabled("OpenRP")) {
            openrp = (OpenRP) getServer().getPluginManager().getPlugin("OpenRP");
        } else {
            getLogger().info("OpenRP 2.3.5 is required for the Addon to work.");
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

    @Override
    public void onDisable() {
        // SkysongStats also drops it when we disable; ending it here keeps that explicit.
        if (awardAnnouncements != null) {
            awardAnnouncements.unregister();
            awardAnnouncements = null;
        }
    }
}
