package cc.invictusgames.ilib.redis.packet;

import cc.invictusgames.ilib.utils.Statics;
import com.google.gson.JsonObject;
import redis.clients.jedis.JedisPubSub;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 31.12.2019 / 03:37
 * iLib / cc.invictusgames.ilib.redis
 */

public class PacketPubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String redisMessage) {
        JsonObject redisJson = Statics.JSON_PARSER.parse(redisMessage).getAsJsonObject();
        String packetClassName = redisJson.get("packet").getAsString();
        String packetJson = redisJson.get("data").getAsString();
        Class<?> packetClass;
        try {
            packetClass = Class.forName(packetClassName);
        } catch (ClassNotFoundException e) {
            return;
        }
        Packet packet = (Packet) Statics.PLAIN_GSON.fromJson(packetJson, packetClass);
        try {
            packet.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
