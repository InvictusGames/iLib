package cc.invictusgames.ilib.npc.network;

import cc.invictusgames.ilib.utils.ReflectionUtil;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;

import java.lang.reflect.Field;
import java.net.SocketAddress;

public class NPCNetworkManager extends NetworkManager {

    private static final Field NETWORK_ADDRESS = ReflectionUtil.getField(NetworkManager.class, "l");

    public NPCNetworkManager(EnumProtocolDirection enumProtocolDirection) {
        super(enumProtocolDirection);

        this.channel = new NPCChannel(null);
        ReflectionUtil.setFieldValue(NETWORK_ADDRESS, this, new SocketAddress() { });
    }
}
