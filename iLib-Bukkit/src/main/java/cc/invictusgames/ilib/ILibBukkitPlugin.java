package cc.invictusgames.ilib;

import cc.invictusgames.ilib.combatlogger.CombatLogger;
import cc.invictusgames.ilib.configuration.JsonConfigurationService;
import cc.invictusgames.ilib.scoreboard.thread.ScoreboardUpdateThread;
import cc.invictusgames.ilib.utils.json.adapter.ItemStackAdapter;
import cc.invictusgames.ilib.utils.json.adapter.LocationAdapter;
import cc.invictusgames.ilib.utils.json.adapter.UUIDAdapter;
import cc.invictusgames.pluginannotation.annotation.BukkitPlugin;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 25.10.2020 / 05:07
 * iLib / cc.invictusgames.ilib
 */

public class ILibBukkitPlugin extends JavaPlugin {

    @Getter
    private static ILibBukkitPlugin instance;

    @Override
    public void onLoad() {
        JsonConfigurationService.setGson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeHierarchyAdapter(UUID.class, new UUIDAdapter())
                .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .disableHtmlEscaping()
                .create());
    }

    @Override
    public void onEnable() {
        ILibBukkitPlugin.instance = this;

        new ILibBukkit(this);
    }

    @Override
    public void onDisable() {
        ScoreboardUpdateThread.getRunning().set(false);
        CombatLogger.disable();
    }
}
