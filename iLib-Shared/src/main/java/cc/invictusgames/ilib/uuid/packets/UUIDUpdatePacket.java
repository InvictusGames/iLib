package cc.invictusgames.ilib.uuid.packets;

import cc.invictusgames.ilib.redis.packet.Packet;
import cc.invictusgames.ilib.uuid.UUIDCache;

import java.util.UUID;

public class UUIDUpdatePacket implements Packet {

    public UUIDUpdatePacket() {
    }

    public UUIDUpdatePacket(UUID uuid, String oldName, String newName) {
        this.uuid = uuid;
        this.oldName = oldName;
        this.newName = newName;
    }

    private UUID uuid;
    private String oldName;
    private String newName;

    @Override
    public void receive() {
        UUIDCache.updateLocally(uuid, oldName, newName);
    }
}
