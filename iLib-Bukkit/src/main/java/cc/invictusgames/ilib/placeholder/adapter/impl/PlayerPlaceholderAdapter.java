package cc.invictusgames.ilib.placeholder.adapter.impl;

import cc.invictusgames.ilib.placeholder.adapter.PlaceholderAdapter;
import org.bukkit.entity.Player;

public class PlayerPlaceholderAdapter implements PlaceholderAdapter {

    @Override
    public String getIdentifier() {
        return "player";
    }

    @Override
    public String getPlaceholder(Player player, String placeholder) {
        if (placeholder.equalsIgnoreCase("name"))
            return player.getName();

        if (placeholder.equalsIgnoreCase("displayName"))
            return player.getDisplayName();

        if (placeholder.equalsIgnoreCase("uuid") || placeholder.equalsIgnoreCase("uniqueId"))
            return player.getUniqueId().toString();

        return null;
    }
}
