package cc.invictusgames.ilib.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 03.01.2020 / 21:38
 * iLib / cc.invictusgames.ilib.serialization
 */

public class JsonSerializer {

    private static final Gson GSON = new Gson();

    private static final JsonParser JSON_PARSER = new JsonParser();

    public static String serialize(JsonSerializable serializable) {
        return GSON.toJson(serializable);
    }

    public static <T extends JsonSerializable> T deserialize(String jsonString, Class<? extends T> clazz) {
        JsonObject json = JSON_PARSER.parse(jsonString).getAsJsonObject();
        return GSON.fromJson(json, clazz);
    }

}
