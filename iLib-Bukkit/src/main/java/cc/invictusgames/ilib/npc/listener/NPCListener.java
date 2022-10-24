package cc.invictusgames.ilib.npc.listener;

import cc.invictusgames.ilib.ILibBukkit;
import cc.invictusgames.ilib.npc.NPC;
import cc.invictusgames.ilib.npc.NPCService;
import cc.invictusgames.ilib.protocol.TinyProtocol;
import cc.invictusgames.ilib.scoreboard.packet.ScoreboardTeamPacketMod;
import cc.invictusgames.ilib.timebased.TimeBasedContainer;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class NPCListener implements Listener, TinyProtocol {

    private final ILibBukkit instance;

    private final TimeBasedContainer<UUID> clickCooldown = new TimeBasedContainer<>(500, TimeUnit.MILLISECONDS);

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        instance.getNpcService().loadWorld(event.getWorld());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new ScoreboardTeamPacketMod(NPC.NAMETAG_KEY, "", "", new ArrayList<>(), 0)
                .sendToPlayer(event.getPlayer());

        NPCService.getNpcs().forEach(npc -> npc.spawn(event.getPlayer()));
    }

    @Override
    public Packet handleIncomingPacket(Player player, Packet packet, ChannelHandlerContext ctx) {
        if (!(packet instanceof PacketPlayInUseEntity))
            return packet;

        PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
        Entity entity = useEntity.a(((CraftPlayer) player).getHandle().world);
        if (entity == null || entity.getUniqueID() == null)
            return packet;

        NPC npc = NPCService.getNpc(entity.getUniqueID());
        if (npc != null) {
            if (npc.getClickHandler() != null
                    && !clickCooldown.contains(player.getUniqueId())){
                npc.getClickHandler().click(npc, player);
                clickCooldown.add(player.getUniqueId());
            }

            return null;
        }

        return packet;
    }

    @Override
    public Packet handleOutgoingPacket(Player player, Packet packet, ChannelHandlerContext ctx) {
        return packet;
    }
}
