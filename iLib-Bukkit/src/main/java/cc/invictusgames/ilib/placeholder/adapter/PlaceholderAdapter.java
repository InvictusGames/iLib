package cc.invictusgames.ilib.placeholder.adapter;

import org.bukkit.entity.Player;

public interface PlaceholderAdapter {

    String getIdentifier();

    String getPlaceholder(Player player, String placeholder);

}
