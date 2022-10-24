package cc.invictusgames.ilib.tab.packet;

import cc.invictusgames.ilib.utils.ReflectionUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PlayerInfoPacketMod {

    private final PacketPlayOutPlayerInfo packet;

    public PlayerInfoPacketMod(EntityPlayer entityPlayer,
                               PacketPlayOutPlayerInfo.EnumPlayerInfoAction action) {
        // TODO: 23.04.2022 change this to 1.8
        this.packet = new PacketPlayOutPlayerInfo(action, entityPlayer);
    /*    packet.username = name;
        packet.ping = ping;
        packet.action = action;
        packet.player = profile;*/
    }

    public void sendToPlayer(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
