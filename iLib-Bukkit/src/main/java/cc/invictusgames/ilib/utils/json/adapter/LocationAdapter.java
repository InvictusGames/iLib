package cc.invictusgames.ilib.utils.json.adapter;

import cc.invictusgames.ilib.utils.json.JsonBuilder;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 31.12.2020 / 17:43
 * iLib / cc.invictusgames.ilib.utils.json.adapter
 */

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (element == null || element.isJsonNull() || !element.isJsonObject())
            return null;

        JsonObject object = element.getAsJsonObject();
        return new Location(
                Bukkit.getWorld(object.get("world").getAsString()),
                object.get("x").getAsDouble(),
                object.get("y").getAsDouble(),
                object.get("z").getAsDouble(),
                object.get("yaw").getAsFloat(),
                object.get("pitch").getAsFloat()
        );
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        if (location == null)
            return null;

        return new JsonBuilder()
                .add("world", location.getWorld().getName())
                .add("x", location.getX())
                .add("y", location.getY())
                .add("z", location.getZ())
                .add("yaw", location.getYaw())
                .add("pitch", location.getPitch())
                .build();
    }
}
