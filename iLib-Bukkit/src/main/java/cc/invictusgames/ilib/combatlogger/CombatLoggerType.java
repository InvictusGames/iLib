package cc.invictusgames.ilib.combatlogger;

import cc.invictusgames.ilib.combatlogger.type.SkeletonCombatLogger;
import cc.invictusgames.ilib.combatlogger.type.VillagerCombatLogger;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 07.03.2021 / 06:19
 * iLib / cc.invictusgames.ilib.combatlogger
 */

public enum CombatLoggerType {

    VILLAGER(EntityType.VILLAGER, VillagerCombatLogger.class, VillagerCombatLogger::new),
    SKELETON(EntityType.SKELETON, SkeletonCombatLogger.class, SkeletonCombatLogger::new);

    @Getter private final String name;
    private final int id;
    private final Class<? extends EntityInsentient> customClass;
    private final BiFunction<Location, UUID, EntityInsentient> spawnFunction;

    CombatLoggerType(EntityType entityType,
                     Class<? extends EntityInsentient> customClass,
                     BiFunction<Location, UUID, EntityInsentient> spawnFunction) {
        this.name = entityType.getName();
        this.id = entityType.getTypeId();
        this.customClass = customClass;
        this.spawnFunction = spawnFunction;
    }

    public LivingEntity spawn(Location location, UUID uuid) {
        return (LivingEntity) spawnFunction.apply(location, uuid).getBukkitEntity();
    }

    public static void registerCustomEntities() {
        for (CombatLoggerType value : values()) {
            registerCustomEntity(value.customClass, value.getName(), value.id);
        }
    }

    private static void registerCustomEntity(Class<?> entityClass, String name, int id) {
        setFieldPrivateStaticMap("d", entityClass, name);
        setFieldPrivateStaticMap("f", entityClass, id);
    }

    private static void setFieldPrivateStaticMap(String fieldName, Object key, Object value) {
        try {
            Field ex = EntityTypes.class.getDeclaredField(fieldName);
            ex.setAccessible(true);
            Map map = (Map) ex.get(null);
            map.put(key, value);
            ex.set(null, map);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException var5) {
            var5.printStackTrace();
        }
    }

}
