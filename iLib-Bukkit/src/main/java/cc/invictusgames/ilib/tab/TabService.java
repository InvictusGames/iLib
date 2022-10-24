package cc.invictusgames.ilib.tab;

import cc.invictusgames.ilib.tab.thread.TabUpdateThread;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TabService {

    private static boolean initialized = false;

    @Getter
    @Setter
    private static Function<Player, String> playerNameGetter = HumanEntity::getName;

    @Getter
    private final TabAdapter adapter;

    @Getter
    private final Map<UUID, PlayerTab> tabs = new ConcurrentHashMap<>();



    public TabService(JavaPlugin plugin, TabAdapter adapter) {
        if (initialized)
            throw new IllegalStateException("TabService has already been initialized");

        this.adapter = adapter;

        Bukkit.getPluginManager().registerEvents(new TabListener(this), plugin);

        new TabUpdateThread(this).start();
        initialized = true;
    }

}
