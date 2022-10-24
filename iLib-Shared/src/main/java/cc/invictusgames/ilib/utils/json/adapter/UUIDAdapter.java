package cc.invictusgames.ilib.utils.json.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 31.12.2020 / 17:39
 * iLib / cc.invictusgames.ilib.utils.json.adapter
 */

public class UUIDAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (element == null || element.isJsonNull())
            return null;

        return UUID.fromString(element.getAsString());
    }

    @Override
    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext context) {
        if (uuid == null)
            return null;

        return new JsonPrimitive(uuid.toString());
    }
}
