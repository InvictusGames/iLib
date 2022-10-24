package cc.invictusgames.ilib.hologram.packet;

import cc.invictusgames.ilib.hologram.HologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface HologramPacketProvider {

    void sendSpawnPackets(Player player, Location location, HologramLine line);

    void updateExistingEntity(Player player, Location location, HologramLine line);

}
