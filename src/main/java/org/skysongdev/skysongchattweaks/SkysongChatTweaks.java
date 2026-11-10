package org.skysongdev.skysongchattweaks;

import openrp.OpenRP;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.skysongdev.skysongchattweaks.chat.ChatAudience;
import org.skysongdev.skysongchattweaks.commands.PlayCommand;
import org.skysongdev.skysongchattweaks.commands.ReloadCommand;
import org.skysongdev.skysongchattweaks.concurrent.BukkitMainThread;
import org.skysongdev.skysongchattweaks.concurrent.MainThread;
import org.skysongdev.skysongchattweaks.condition.ConditionTagListener;
import org.skysongdev.skysongchattweaks.condition.ConditionTagRenderer;
import org.skysongdev.skysongchattweaks.config.ChatTweaksSettings;
import org.skysongdev.skysongchattweaks.injury.InjuryAwardAnnouncer;
import org.skysongdev.skysongchattweaks.injury.InjuryTagListener;
import org.skysongdev.skysongchattweaks.injury.InjuryTagRenderer;
import org.skysongdev.skysongchattweaks.injury.InjuryTeacher;
import org.skysongdev.skysongchattweaks.link.LinkTagListener;
import org.skysongdev.skysongchattweaks.stats.ApiVersionCheck;
import org.skysongdev.skysongstats.api.AnnouncerRegistration;
import org.skysongdev.skysongstats.api.SkysongStatsApi;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Wires the plugin together and nothing else: every other class gets what it needs through
 * its constructor.
 */
public final class SkysongChatTweaks extends JavaPlugin {

    private AnnouncerRegistration awardAnnouncements;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // plugin.yml depends on both, so they are loaded; they may still have failed to enable.
        if (!(getServer().getPluginManager().getPlugin("OpenRP") instanceof OpenRP openrp) || !openrp.isEnabled()) {
            getLogger().severe("SkysongChatTweaks needs OpenRP enabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Optional<SkysongStatsApi> stats = connectStatsApi();
        if (stats.isEmpty()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands(openrp);
        registerChat(openrp, stats.get());
    }

    @Override
    public void onDisable() {
        // SkysongStats also drops it when we disable; ending it here keeps that explicit.
        if (awardAnnouncements != null) {
            awardAnnouncements.unregister();
            awardAnnouncements = null;
        }
    }

    /** Gets the SkysongStats API once and checks that it is new enough. */
    private Optional<SkysongStatsApi> connectStatsApi() {
        SkysongStatsApi api;
        try {
            api = SkysongStatsApi.get();
        } catch (IllegalStateException notEnabled) {
            getLogger().severe(notEnabled.getMessage());
            return Optional.empty();
        }
        Optional<String> problem = ApiVersionCheck.problemWith(api.apiVersion());
        problem.ifPresent(getLogger()::severe);
        return problem.isPresent() ? Optional.empty() : Optional.of(api);
    }

    private void registerCommands(OpenRP openrp) {
        setExecutor("play", new PlayCommand(openrp.getChat()::sendMessage, getLogger()));
        setExecutor("screload", new ReloadCommand(this::reloadConfig));
    }

    private void registerChat(OpenRP openrp, SkysongStatsApi stats) {
        MainThread mainThread = new BukkitMainThread(this);
        ChatAudience audience = new ChatAudience(getServer()::getOnlinePlayers,
                channel -> channelRange(openrp.getChat().getConfig(), channel));
        InjuryTeacher teacher = new InjuryTeacher(audience, stats.profiles(), stats.injuries());
        ChatTweaksSettings settings = new ChatTweaksSettings(this::getConfig);

        register(new LinkTagListener());
        register(new InjuryTagListener(
                new InjuryTagRenderer(stats.injuries(), stats.profiles(), mainThread), teacher, mainThread));
        register(new ConditionTagListener(new ConditionTagRenderer(stats.conditions())));
        register(new InjuryAwardAnnouncer(settings, audience, teacher, getServer()::getPlayer));
        // From here on SkysongStats leaves award messages to us instead of sending them privately.
        awardAnnouncements = stats.injuries().announceAwards(this);
    }

    /** A channel's {@code range} in OpenRP's chat config; empty when it has none, which OpenRP reads as server-wide. */
    private static OptionalInt channelRange(FileConfiguration chat, String channel) {
        String path = "channels." + channel + ".range";
        return chat.isSet(path) ? OptionalInt.of(chat.getInt(path)) : OptionalInt.empty();
    }

    private void setExecutor(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            throw new IllegalStateException("plugin.yml is missing the /" + name + " command");
        }
        command.setExecutor(executor);
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
