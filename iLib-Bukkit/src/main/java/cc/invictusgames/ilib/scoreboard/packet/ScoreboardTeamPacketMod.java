package cc.invictusgames.ilib.scoreboard.packet;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ScoreboardTeamPacketMod {

    private final PacketPlayOutScoreboardTeam packet;

    public ScoreboardTeamPacketMod(String name, String prefix,
                                   String suffix, Collection<?> players, int paramInt,
                                   ScoreboardTeamBase.EnumNameTagVisibility visibility) {
        this.packet = new PacketPlayOutScoreboardTeam();
        try {
            packet.setName(name);
            packet.setAction(paramInt);
            if (packet.getAction() == 0 || packet.getAction() == 2) {
                packet.setName(name);
                packet.setPrefix(prefix);
                packet.setSuffix(suffix);
                packet.setPackOptionData(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        packet.setNameTagVisibility(visibility);

        if (paramInt == 0) {
            this.addAll(players);
        }
    }

    public ScoreboardTeamPacketMod(String name, String prefix,
                                   String suffix, Collection<?> players, int paramInt) {
        this.packet = new PacketPlayOutScoreboardTeam();
        try {
            packet.setName(name);
            packet.setAction(paramInt);
            if (packet.getAction() == 0 || packet.getAction() == 2) {
                packet.setName(name);
                packet.setPrefix(prefix);
                packet.setSuffix(suffix);
                packet.setPackOptionData(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (paramInt == 0) {
            this.addAll(players);
        }
    }

    public ScoreboardTeamPacketMod(String name, Collection<?> players, int paramInt,
                                   ScoreboardTeamBase.EnumNameTagVisibility visibility) {
        this.packet = new PacketPlayOutScoreboardTeam();
        try {
            packet.setPackOptionData(3);
            packet.setName(name);
            packet.setAction(paramInt);
            packet.setNameTagVisibility(visibility);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.addAll(players);
    }

    public ScoreboardTeamPacketMod(String name, Collection<?> players, int paramInt) {
        this.packet = new PacketPlayOutScoreboardTeam();
        try {
            packet.setPackOptionData(3);
            packet.setName(name);
            packet.setAction(paramInt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        if (packet != null && bukkitPlayer != null) {
            ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(this.packet);
        }
    }

    private void addAll(Collection<?> col) {
        if (col == null) {
            return;
        }
        try {
            this.packet.addPlayers((Collection<String>) col);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
