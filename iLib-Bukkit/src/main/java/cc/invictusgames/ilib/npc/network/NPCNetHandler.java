package cc.invictusgames.ilib.npc.network;

import net.minecraft.server.v1_8_R3.*;

public class NPCNetHandler extends PlayerConnection {

    public NPCNetHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }

    @Override
    public void sendPacket(Packet packet) {
    }
}
