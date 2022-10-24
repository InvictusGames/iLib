package cc.invictusgames.ilib.utils;

import cc.invictusgames.ilib.utils.json.adapter.UUIDAdapter;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.util.TimeZone;
import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 15.01.2020 / 21:29
 * iLib / cc.invictusgames.ilib.utils
 */

public class Statics {

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("America/New_York");
    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(UUID.class, new UUIDAdapter())
            .disableHtmlEscaping()
            .create();
    public static final Gson PLAIN_GSON = new GsonBuilder().create();
    public static Joiner SPACE_JOINER = Joiner.on(" ");
    public static Joiner COMMA_JOINER = Joiner.on(", ");

}
