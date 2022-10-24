package cc.invictusgames.ilib.placeholder;

import cc.invictusgames.ilib.placeholder.adapter.PlaceholderAdapter;
import cc.invictusgames.ilib.placeholder.adapter.impl.PlayerPlaceholderAdapter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderService {

    private static final Map<String, PlaceholderAdapter> ADAPTER_MAP = new HashMap<>();

    private static final Pattern PATTERN = Pattern.compile(
            "\\{(?<identifier>\\w+):(?<placeholder>[\\w:\\-+]+)\\}",
            Pattern.CASE_INSENSITIVE
    );

    static {
        registerAdapter(new PlayerPlaceholderAdapter());
    }

    public static void registerAdapter(PlaceholderAdapter adapter) {
        ADAPTER_MAP.put(adapter.getIdentifier().toLowerCase(), adapter);
    }

    public static PlaceholderAdapter getAdapter(String identifier) {
        return ADAPTER_MAP.get(identifier.toLowerCase());
    }

    public static Collection<PlaceholderAdapter> getAdapterList() {
        return Collections.unmodifiableCollection(ADAPTER_MAP.values());
    }

    public static String replace(Player player, String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.find())
            return text;

        StringBuffer buffer = new StringBuffer();

        do {
            String identifier = matcher.group("identifier").toLowerCase();
            String placeholder = matcher.group("placeholder").toLowerCase();

            PlaceholderAdapter adapter = getAdapter(identifier);
            String requested = adapter == null ? null : adapter.getPlaceholder(player, placeholder);

            matcher.appendReplacement(buffer, requested == null ? matcher.group(0)
                    .replace("{", "")
                    .replace("}", "") : requested);

        } while (matcher.find());

        text = matcher.appendTail(buffer).toString();
        matcher = PATTERN.matcher(text);

        if (matcher.find())
            return replace(player, text);

        return text;
    }

}
