package cc.invictusgames.ilib.bossbar;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.EntityUtils;
import cc.invictusgames.ilib.utils.ReflectionUtil;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarService {

    private static final Field SPAWN_A_FIELD = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "a");
    private static final Field SPAWN_B_FIELD = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "b");
    private static final Field SPAWN_C_FIELD = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "c");
    private static final Field SPAWN_D_FIELD = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "d");
    private static final Field SPAWN_E_FIELD = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "e");
    private static final Field SPAWN_L_FIELD = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "l");

    private static final Field META_A_FIELD = ReflectionUtil.getField(PacketPlayOutEntityMetadata.class, "a");

    private static final TObjectIntMap<Class<?>> CLASS_TO_ID = new TObjectIntHashMap(10, 0.5F, -1);

    private static final Map<UUID, BarData> DISPLAYING = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LAST_UPDATE = new ConcurrentHashMap<>();

    private static boolean initialized = false;

    static {
        CLASS_TO_ID.put(Byte.class, 0);
        CLASS_TO_ID.put(Short.class, 1);
        CLASS_TO_ID.put(Integer.class, 2);
        CLASS_TO_ID.put(Float.class, 3);
        CLASS_TO_ID.put(String.class, 4);
        CLASS_TO_ID.put(ItemStack.class, 5);
        CLASS_TO_ID.put(BaseBlockPosition.class, 6);
    }

    public static void init() {
        if (initialized) {
            throw new IllegalStateException("BossBarService has already been initialized");
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                removeBossBar(event.getPlayer());
            }

            public void onTeleport(PlayerTeleportEvent event) {
                Player player = event.getPlayer();
                if (!DISPLAYING.containsKey(player.getUniqueId()))
                    return;

                BarData data = DISPLAYING.get(player.getUniqueId());
                String message = data.message;
                float health = data.health;
                removeBossBar(player);
                setBossBar(player, message, health);
            }
        }, ILibBukkitPlugin.getInstance());

        Bukkit.getScheduler().runTaskTimerAsynchronously(ILibBukkitPlugin.getInstance(), () -> {
            for (UUID uuid : DISPLAYING.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null)
                    continue;

                EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

                //long lastUpdate = LAST_UPDATE.getOrDefault(player.getUniqueId(), 0L);
                /*long nextUpdate = lastUpdate + (entityPlayer.playerConnection.networkManager.getVersion() != 47
                        ? 3000L : 150);*/
                /*long nextUpdate = lastUpdate + 150L;
                long nextUpdate = lastUpdate + 50L;

                if (nextUpdate > System.currentTimeMillis())
                    continue;*/

                //LAST_UPDATE.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }, 1L, 1L);

        initialized = true;
    }

    public static void setGlobalBossBar(String message, float health) {
        for (Player player : Bukkit.getOnlinePlayers())
            setBossBar(player, message, health);
    }

    public static void setBossBar(Player player, String message, float health) {
        if (health < 0F)
            throw new IllegalArgumentException("health must be above 0.0F");

        if (health > 1F)
            throw new IllegalArgumentException("health must be below 1.0F");

        if (message == null) {
            removeBossBar(player);
            return;
        }

        if (message.length() > 64)
            message = message.substring(0, 64);

        message = CC.translate(message);

        if (!DISPLAYING.containsKey(player.getUniqueId()))
            sendSpawnPacket(player, message, health);
        else sendUpdatePacket(player, message, health);
    }

    public static void removeBossBar(Player player) {
        if (!DISPLAYING.containsKey(player.getUniqueId()))
            return;

        BarData data = DISPLAYING.get(player.getUniqueId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(data.entityId));
        DISPLAYING.remove(player.getUniqueId());
        LAST_UPDATE.remove(player.getUniqueId());
    }

    private static void sendSpawnPacket(Player player, String message, float health) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
      //  int version = entityPlayer.playerConnection.networkManager.getVersion();

        BarData data = new BarData(EntityUtils.fakeEntityId(), message, health);
        DISPLAYING.put(player.getUniqueId(), data);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        DataWatcher watcher = new DataWatcher((Entity) null);
        ReflectionUtil.setFieldValue(SPAWN_A_FIELD, packet, data.entityId);

        /*if (version != 47) {
            ReflectionUtil.setFieldValue(SPAWN_B_FIELD, packet, EntityType.ENDER_DRAGON.getTypeId());

            watcher.a(6, health * 200F);

            ReflectionUtil.setFieldValue(SPAWN_C_FIELD, packet, (int) (entityPlayer.locX * 32D));
            ReflectionUtil.setFieldValue(SPAWN_D_FIELD, packet, -6400);
            ReflectionUtil.setFieldValue(SPAWN_E_FIELD, packet, (int) (entityPlayer.locZ * 32D));
        } else {*/
        ReflectionUtil.setFieldValue(SPAWN_B_FIELD, packet, EntityType.WITHER.getTypeId());

        //watcher.a(0, (byte) (version != 47 ? 0 : 0x20));
        watcher.a(0, (byte) 0x20);
        watcher.a(6, health * 300F);
        watcher.a(20, 800);

        Location location = player.getLocation().clone()
                .getDirection()
                .multiply(32D)
                .add(player.getLocation().toVector())
                .toLocation(player.getWorld());

        ReflectionUtil.setFieldValue(SPAWN_C_FIELD, packet, (int) (location.getX() * 32D));
        ReflectionUtil.setFieldValue(SPAWN_D_FIELD, packet, (int) (location.getY() * 32D));
        ReflectionUtil.setFieldValue(SPAWN_E_FIELD, packet, (int) (location.getZ() * 32D));
        //}

      //  watcher.a(version != 47 ? 10 : 2, message);
        ReflectionUtil.setFieldValue(SPAWN_L_FIELD, packet, watcher);

        entityPlayer.playerConnection.sendPacket(packet);
    }

    private static void sendUpdatePacket(Player player, String message, float health) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
       // int version = entityPlayer.playerConnection.networkManager.getVersion();

     /*   BarData data = DISPLAYING.get(player.getUniqueId());

        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
        ReflectionUtil.setFieldValue(META_A_FIELD, packet, data.entityId);

        List<DataWatcher.WatchableObject> list = new ArrayList<>();
        if (health != data.health) {
            if (version != 47)
                list.add(createWatchableObject(6, health * 200F));
            else list.add(createWatchableObject(6, health * 300F));
        }

        if (!message.equals(data.message))
            list.add(createWatchableObject(version != 47 ? 10 : 2, message));

        ReflectionUtil.setFieldValue(META_B_FIELD, packet, list);
        entityPlayer.playerConnection.sendPacket(packet);
    }

    private static DataWatcher.WatchableObject createWatchableObject(int id, Object object) {
        return new DataWatcher.WatchableObject(CLASS_TO_ID.get(object.getClass()), id, object);
    }

    private static void updatePosition(Player player) {
        if (!DISPLAYING.containsKey(player.getUniqueId()))
            return;

        BarData data = DISPLAYING.get(player.getUniqueId());

        int x;
        int y;
        int z;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        /*int version = entityPlayer.playerConnection.networkManager.getVersion();

        if (version != 47) {
            x = (int) (entityPlayer.locX * 32D);
            y = -6400;
            z = (int) (entityPlayer.locZ * 32D);
        } else {
        Location location = player.getLocation().clone()
                .getDirection()
                .multiply(32D)
                .add(player.getLocation().toVector())
                .toLocation(player.getWorld());

        x = (int) (location.getX() * 32D);
        y = (int) (location.getY() * 32D);
        z = (int) (location.getZ() * 32D);
        //}*/

    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    private static class BarData {
        private final int entityId;
        private String message;
        private float health;
    }

}
