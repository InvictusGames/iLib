package cc.invictusgames.ilib.hologram;

import cc.invictusgames.ilib.hologram.clickhandler.HologramClickHandler;
import cc.invictusgames.ilib.hologram.packet.HologramPacketProvider;
import cc.invictusgames.ilib.hologram.packet.impl.Hologram1_7Provider;
import cc.invictusgames.ilib.hologram.packet.impl.Hologram1_8Provider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class Hologram {

    private static final Hologram1_7Provider HOLOGRAM_1_7_PROVIDER = new Hologram1_7Provider();
    private static final Hologram1_8Provider HOLOGRAM_1_8_PROVIDER = new Hologram1_8Provider();

    private int id;
    private Location location;
    private double lineSpacing;
    private HologramClickHandler clickHandler = null;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Map<Player, List<HologramLine>> currentLines = new ConcurrentHashMap<>();


    private Set<UUID> currentWatchers = new HashSet<>();

    protected Hologram(HologramBuilder builder) {
        if (builder.getLocation() == null)
            throw new IllegalArgumentException("Please provide a location for the hologram using HologramBuilder#at");

        this.location = builder.getLocation();
        this.lineSpacing = builder.getLineSpacing();
        this.id = HologramService.registerHologram(this);
        this.clickHandler = builder.getClickHandler();
    }

    public abstract List<HologramLine> getLines(Player player);

    public void spawn() {
        Bukkit.getOnlinePlayers().forEach(this::spawn);
    }

    public void spawn(Player player) {
        if (player == null || !player.isOnline())
            return;

        if (!isInDistance(player))
            return;

        HologramPacketProvider provider = getProviderFor(player);
        List<HologramLine> lines = getLines(player);

        Location start = location.clone().add(0, lines.size() * lineSpacing, 0);
        currentLines.putIfAbsent(player, new ArrayList<>());
        currentLines.get(player).clear();
        currentWatchers.add(player.getUniqueId());
        for (HologramLine line : lines) {
            if (!line.getText().isEmpty())
                provider.sendSpawnPackets(player, start, line);
            currentLines.get(player).add(line);
            start.subtract(0, lineSpacing, 0);
        }
    }

    public void destroy() {
        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    public void destroy(Player player) {
        if (!currentLines.containsKey(player))
            return;

        if (player == null || !player.isOnline())
            return;

        List<Integer> destroyList = new ArrayList<>();

        for (HologramLine line : currentLines.get(player)) {
            if (line.getHorseId() != 0)
                destroyList.add(line.getHorseId());
            if (line.getWitherId() != 0)
                destroyList.add(line.getWitherId());
            if (line.getArmorStandId() != 0)
                destroyList.add(line.getArmorStandId());
        }

        int[] destroyArray = new int[destroyList.size()];
        for (int i = 0; i < destroyList.size(); i++)
            destroyArray[i] = destroyList.get(i);

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(destroyArray);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
        currentLines.remove(player);
        currentWatchers.remove(player.getUniqueId());
    }

    public void update() {
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    public void update(Player player) {
        if (player == null || !player.isOnline())
            return;

        List<HologramLine> lines = getLines(player);
        if (!currentLines.containsKey(player)
                || (currentLines.containsKey(player)
                && currentLines.get(player).size() != lines.size())) {
            destroy(player);
            spawn(player);
            return;
        }

        if (!isInDistance(player)) {
            destroy(player);
            return;
        }

        currentWatchers.add(player.getUniqueId());

        HologramPacketProvider provider = getProviderFor(player);
        Location start = location.clone().add(0, lines.size() * lineSpacing, 0);

        int i = 0;
        for (HologramLine line : currentLines.get(player)) {
            line.setTextSilent(lines.get(i++).getText());
            if (!line.getText().isEmpty())
                provider.updateExistingEntity(player, start, line);
            else {
                List<Integer> destroyList = new ArrayList<>();
                if (line.getArmorStandId() != 0) {
                    destroyList.add(line.getArmorStandId());
                    line.setArmorStandId(0);
                }

                int[] destroyArray = new int[destroyList.size()];
                for (int j = 0; j < destroyList.size(); j++)
                    destroyArray[j] = destroyList.get(j);

                PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(destroyArray);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
            }

            start.subtract(0, lineSpacing, 0);
        }
    }

    public void setLocation(Location location) {
        this.location = location;
        destroy();
        spawn();
    }

    public boolean isEntityId(Player player, int entityId) {
        if (!currentLines.containsKey(player))
            return false;

        for (HologramLine line : currentLines.get(player)) {
            if ((line.getHorseId() != 0 && line.getHorseId() == entityId)
                    || (line.getWitherId() != 0 && line.getWitherId() == entityId)
                    || (line.getArmorStandId() != 0 && line.getArmorStandId() == entityId))
                return true;
        }

        return false;
    }

    private HologramPacketProvider getProviderFor(Player player) {
        return HOLOGRAM_1_8_PROVIDER;
    }


    public boolean isInDistance(Player player) {
        return location.getWorld() == player.getWorld() && location.distanceSquared(player.getLocation()) <= 1600.0;
    }

}
