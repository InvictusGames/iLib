package cc.invictusgames.ilib.uuid;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.redis.RedisService;
import cc.invictusgames.ilib.uuid.packets.UUIDUpdatePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 12.02.2020 / 21:36
 * iLib / cc.invictusgames.ilib.uuid
 */

public class UUIDCache {

    private static final Map<String, UUID> nameUuidMap = new HashMap<>();
    private static final Map<UUID, String> uuidNameMap = new HashMap<>();

    private static UUIDCache instance;
    private final RedisService redisService;

    public UUIDCache(RedisService redisService) {
        if (instance != null)
            throw new IllegalStateException("Already Initialized");

        this.redisService = redisService;
        instance = this;
        //Bukkit.getPluginManager().registerEvents(this, plugin);
        this.redisService.executeCommand(redis -> {
            for (Map.Entry<String, String> entry : redis.hgetAll("UUIDCache").entrySet()) {
                UUID uuid = UUID.fromString(entry.getKey());
                String name = entry.getValue();

                nameUuidMap.put(name.toLowerCase(), uuid);
                uuidNameMap.put(uuid, name);
            }
            return null;
        });
    }

    public static UUID getUuid(String name) {
        return nameUuidMap.get(name.toLowerCase());
    }

    public static UUID uuid(String name) {
        return nameUuidMap.get(name.toLowerCase());
    }

    public static String getName(UUID uuid) {
        return uuidNameMap.get(uuid);
    }

    public static String name(UUID uuid) {
        return uuidNameMap.get(uuid);
    }

    public void update(UUID uuid, String name, boolean async) {
        String oldName = uuidNameMap.getOrDefault(uuid, null);
        if (oldName != null)
            nameUuidMap.remove(oldName.toLowerCase());

        nameUuidMap.put(name.toLowerCase(), uuid);
        uuidNameMap.put(uuid, name);

        Runnable runnable;
        runnable = () ->
                this.redisService.executeBackendCommand(redis -> {
                    redis.hset("UUIDCache", uuid.toString(), name);
                    return null;
                });

        if (async)
            ILib.TASK_CHAIN.run(runnable);
        else runnable.run();

        ILib.getInstance().getRedisService().publish(new UUIDUpdatePacket(uuid, oldName, name));
    }

    public static void updateLocally(UUID uuid, String oldName, String newName) {
        nameUuidMap.put(newName.toLowerCase(), uuid);
        if (oldName != null)
            nameUuidMap.remove(oldName.toLowerCase());
        uuidNameMap.put(uuid, newName);
    }

    public void saveAll() {
        this.redisService.executeBackendCommand(redis -> {
            uuidNameMap.forEach((uuid, s) -> redis.hset("UUIDCache", uuid.toString(), s));
            return null;
        });
    }

    public int getCachedAmount() {
        return uuidNameMap.size();
    }

    public Map<String, UUID> getNameUuidMap() {
        return nameUuidMap;
    }

    public Map<UUID, String> getUuidNameMap() {
        return uuidNameMap;
    }
}
