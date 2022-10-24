package cc.invictusgames.ilib.scoreboard;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.scoreboard.nametag.NameTag;
import cc.invictusgames.ilib.scoreboard.nametag.NameTagAdapter;
import cc.invictusgames.ilib.scoreboard.thread.ScoreboardUpdateThread;
import cc.invictusgames.ilib.utils.logging.SimpleBukkitLogger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 21.10.2020 / 07:53
 * iLib / cc.invictusgames.ilib.scoreboard
 */

public class ScoreboardService {

    public static final long UPDATE_TICKS = 2L;
    @Getter
    private static boolean initialized = false;
    private static final List<NameTagAdapter> NAME_TAG_ADAPTERS =
            new ArrayList<>(Collections.singletonList(NameTagAdapter.DEFAULT));

    @Getter private final JavaPlugin plugin;
    @Getter private final ScoreboardAdapter adapter;
    @Getter private final Logger logger;

    public static final Map<UUID, PlayerScoreboard> SCOREBOARDS = new WeakHashMap<>();

    public ScoreboardService(JavaPlugin plugin, ScoreboardAdapter adapter) {
        if (initialized) {
            throw new IllegalStateException("ScoreboardService has already been initialized");
        }

        this.plugin = plugin;
        this.adapter = adapter;
        this.logger = SimpleBukkitLogger.getLogger(plugin, ScoreboardService.class.getSimpleName());
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(this), plugin);

        new ScoreboardUpdateThread(this).start();
        initialized = true;
    }

    public static void registerNameTagAdapter(NameTagAdapter adapter) {
        NAME_TAG_ADAPTERS.add(adapter);
        NAME_TAG_ADAPTERS.sort(Comparator.comparingInt(NameTagAdapter::getPriority).reversed());
        ILib.getInstance().getLogger().info(String.format(
                "[NameTagAdapter] Registered %s with priority %d.",
                adapter.getName(), adapter.getPriority()));
    }

    public static List<NameTagAdapter> getNameTagAdapters() {
        return NAME_TAG_ADAPTERS;
    }

    public static PlayerScoreboard getBoard(Player player) {
        return SCOREBOARDS.get(player.getUniqueId());
    }

    public void updateScoreboard(Player player) {
        PlayerScoreboard playerScoreboard = getBoard(player);
        if (playerScoreboard != null)
            playerScoreboard.update();
    }

    public static NameTag getNameTag(Player player, Player target) {
        NameTag nameTag = null;
        int index = 0;
        while (nameTag == null) {
            nameTag = ScoreboardService.getNameTagAdapters().get(index++).getNameTag(player, target);
        }
        return nameTag;
    }

    public static void forceUpdateNameTags() {
        Bukkit.getScheduler().runTaskAsynchronously(ILibBukkitPlugin.getInstance(), () ->
                SCOREBOARDS.values().forEach(playerScoreboard -> playerScoreboard.updateNameTags(true)));
    }
}
