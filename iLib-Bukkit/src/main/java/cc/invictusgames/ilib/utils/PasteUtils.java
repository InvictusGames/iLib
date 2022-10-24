package cc.invictusgames.ilib.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 16.06.2020 / 18:55
 * Invictus / cc.invictusgames.invictus.spigot.utils
 */

public class PasteUtils {

    private static final String PASTE_ENDPOINT = "https://www.toptal.com/developers/hastebin/documents";
    private static final String PASTE_URL = "https://www.toptal.com/developers/hastebin/";
    private static final String RAW_PASTE_URL = "https://www.toptal.com/developers/hastebin/raw/";

    public static String paste(String content, boolean raw) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(PASTE_ENDPOINT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "ILib Hastebin API");
            connection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
            connection.setUseCaches(false);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(content.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder();
            reader.lines().forEach(json::append);

            JsonObject object = Statics.JSON_PARSER.parse(json.toString()).getAsJsonObject();
            return (raw ? RAW_PASTE_URL : PASTE_URL) + object.get("key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

}
