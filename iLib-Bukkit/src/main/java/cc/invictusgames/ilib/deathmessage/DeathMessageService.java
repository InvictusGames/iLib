package cc.invictusgames.ilib.deathmessage;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.deathmessage.command.LastDamageCommand;
import cc.invictusgames.ilib.deathmessage.damage.Damage;
import cc.invictusgames.ilib.deathmessage.damage.PlayerDamage;
import cc.invictusgames.ilib.deathmessage.damage.UnkownDamage;
import cc.invictusgames.ilib.deathmessage.listener.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:17
 * iLib / cc.invictusgames.ilib.deathmessage
 */

public class DeathMessageService {

    @Getter
    @Setter
    private static DeathMessageSettings settings;
    private final static Map<UUID, List<Damage>> LAST_DAMAGE = new HashMap<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized)
            throw new IllegalStateException("DeathMessageService has already been initialized");

        settings = DeathMessageSettings.DEFAULT;
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ArrowListener(), ILibBukkitPlugin.getInstance());
        pluginManager.registerEvents(new EntityListener(), ILibBukkitPlugin.getInstance());
        pluginManager.registerEvents(new FallListener(), ILibBukkitPlugin.getInstance());
        pluginManager.registerEvents(new FireListener(), ILibBukkitPlugin.getInstance());
        pluginManager.registerEvents(new GeneralListener(), ILibBukkitPlugin.getInstance());
        pluginManager.registerEvents(new PvPListener(), ILibBukkitPlugin.getInstance());
        pluginManager.registerEvents(new VoidListener(), ILibBukkitPlugin.getInstance());
        pluginManager.registerEvents(new PlayerDeathListener(), ILibBukkitPlugin.getInstance());
        CommandService.register(ILibBukkitPlugin.getInstance(), new LastDamageCommand());
        initialized = true;
    }

    public static void addDamage(UUID uuid, Damage damage) {
        LAST_DAMAGE.putIfAbsent(uuid, new ArrayList<>());
        List<Damage> damages = LAST_DAMAGE.get(uuid);
        while (damages.size() > 30)
            damages.remove(0);

        damages.add(damage);
    }

    public static List<Damage> getDamage(UUID uuid) {
        return LAST_DAMAGE.getOrDefault(uuid, new ArrayList<>());
    }

    public static void clearDamage(UUID uuid) {
        LAST_DAMAGE.remove(uuid);
    }

    public static PlayerDamage getLastPlayerDamage(UUID uuid) {
        List<Damage> damageList = getDamage(uuid);

        PlayerDamage playerDamage = null;
        for (Damage damage : damageList) {
            if (!(damage instanceof PlayerDamage))
                continue;

            if (playerDamage == null) {
                playerDamage = (PlayerDamage) damage;
                continue;
            }

            if (playerDamage.getTimeAgo() > damage.getTimeAgo())
                playerDamage = (PlayerDamage) damage;
        }

        return playerDamage;
    }

    public static Damage getLastDamageCause(Player player) {
        List<Damage> damages = getDamage(player.getUniqueId());
        Damage cause;
        if (!damages.isEmpty()) {
            cause = damages.get(damages.size() - 1);
            if (cause.getTimeAgo() > TimeUnit.SECONDS.toMillis(30))
                cause = new UnkownDamage(player.getUniqueId(), 1D);
        } else cause = new UnkownDamage(player.getUniqueId(), 1D);

        return cause;
    }

    public static void broadcastPlayerDeath(Player player) {
        Damage cause = getLastDamageCause(player);
        UUID uuid = player.getUniqueId();
        UUID killerUuid = player.getKiller() == null ? null : player.getKiller().getUniqueId();
        Bukkit.getConsoleSender().sendMessage(cause.getDeathMessage(null));
        for (Player current : Bukkit.getOnlinePlayers()) {
            if (DeathMessageService.getSettings().shouldShow(uuid, killerUuid, current.getUniqueId()))
                current.sendMessage(cause.getDeathMessage(current.getUniqueId()));
        }
    }

}
