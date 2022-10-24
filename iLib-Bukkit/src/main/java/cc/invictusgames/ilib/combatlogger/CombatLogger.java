package cc.invictusgames.ilib.combatlogger;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.combatlogger.command.DespawnCombatLoggerCommand;
import cc.invictusgames.ilib.combatlogger.event.CombatLoggerDespawnEvent;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.utils.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.07.2020 / 01:10
 * iLib / cc.invictusgames.ilib.combatlogger
 */

@Getter
public class CombatLogger {

    public static final String METADATA = "CombatLogger";

    @Getter private static final Map<UUID, CombatLogger> loggerMap = new HashMap<>();
    private static boolean initialized = false;

    private final String playerName;
    private final UUID playerUuid;
    private final ItemStack[] armor;
    private final ItemStack[] items;
    private final double health;
    private CombatLoggerType loggerType = CombatLoggerType.VILLAGER;
    private long despawnTime = 30;
    private LivingEntity spawnedEntity;
    private BukkitRunnable runnable;

    public CombatLogger(Player player) {
        if (!initialized) {
            throw new IllegalStateException("CombatLogger has not been initialized");
        }

        this.playerName = player.getName();
        this.playerUuid = player.getUniqueId();
        this.armor = player.getInventory().getArmorContents();
        this.items = player.getInventory().getContents();
        this.health = player.getHealth();
    }

    public CombatLogger setLoggerType(CombatLoggerType loggerType) {
        this.loggerType = loggerType;
        return this;
    }

    public CombatLogger setDespawnTime(long despawnTime, TimeUnit timeUnit) {
        this.despawnTime = timeUnit.toSeconds(despawnTime);
        return this;
    }

    public LivingEntity spawn(Location location) {
        LivingEntity entity = loggerType.spawn(location, playerUuid);
        entity.setMetadata(CombatLogger.METADATA, new FixedMetadataValue(ILibBukkitPlugin.getInstance(),
                playerUuid.toString()));
        entity.setCustomName(CC.YELLOW + playerName);
        entity.setCustomNameVisible(true);
        entity.setCanPickupItems(false);
        entity.setMaxHealth(Math.min(health, 20D));
        entity.setHealth(health);
        entity.getEquipment().setArmorContents(armor);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100), true);

        loggerMap.put(playerUuid, this);
        loggerMap.put(entity.getUniqueId(), this);

        runnable = new CombatLoggerRunnable(this);
        runnable.runTaskLater(ILibBukkitPlugin.getInstance(), despawnTime * 20);

        return spawnedEntity = entity;
    }

    public void despawn(CombatLoggerDespawnEvent.Cause cause) {
        if (!spawnedEntity.isDead() && spawnedEntity.isValid()) {
            Bukkit.getPluginManager().callEvent(new CombatLoggerDespawnEvent(this, cause));

            spawnedEntity.remove();

            if (runnable != null) {
                runnable.cancel();
                runnable = null;
            }

            CombatLogger.getLoggerMap().remove(playerUuid);
            CombatLogger.getLoggerMap().remove(spawnedEntity.getUniqueId());
            spawnedEntity = null;
        }
    }

    public static void init(JavaPlugin plugin) {
        if (initialized) {
            throw new IllegalStateException("CombatLogger has already been initialized");
        }

        CombatLoggerType.registerCustomEntities();
        Bukkit.getPluginManager().registerEvents(new CombatLoggerListener(), plugin);
        CommandService.register(plugin, new DespawnCombatLoggerCommand());

        initialized = true;
    }

    public static void disable() {
        for (CombatLogger combatLogger : loggerMap.values())
            combatLogger.despawn(CombatLoggerDespawnEvent.Cause.DISABLED);
    }
}
