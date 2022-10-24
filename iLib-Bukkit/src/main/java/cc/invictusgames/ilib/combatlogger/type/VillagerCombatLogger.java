package cc.invictusgames.ilib.combatlogger.type;

import cc.invictusgames.ilib.combatlogger.nms.CombatLoggerNms;
import cc.invictusgames.ilib.combatlogger.nms.PlayerNmsResult;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityAgeable;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 07.03.2021 / 06:24
 * iLib / cc.invictusgames.ilib.combatlogger.type
 */

public class VillagerCombatLogger extends EntityVillager {

    private final UUID playerUuid;

    public VillagerCombatLogger(Location location, UUID playerUuid) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.playerUuid = playerUuid;
        setPositionRotation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        retrack();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float damage) {
        if (!CombatLoggerNms.damageEntity(
                this,
                PlayerNmsResult.get(playerUuid, world.getWorld()),
                damagesource,
                damage))
            return false;

        return super.damageEntity(damagesource, damage);
    }

    @Override
    public void die(DamageSource damageSource) {
        CombatLoggerNms.die(
                this,
                this.combatTracker,
                PlayerNmsResult.get(playerUuid, world.getWorld()),
                damageSource,
                super::die
        );
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return null;
    }

    @Override
    public void move(double d0, double d1, double d2) {
        super.move(0, d1, 0);
    }


    @Override
    protected void dropDeathLoot(boolean flag, int i) {
    }

    @Override
    public boolean a(EntityHuman entityHuman) {
        return false;
    }

    @Override
    public void h() {
        super.h();
    }

    @Override
    public CraftEntity getBukkitEntity() {
        return super.getBukkitEntity();
    }
}