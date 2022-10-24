package cc.invictusgames.ilib.utils;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class AdminBypass {

    public static final String METADATA = "iLib:AdminBypass";

    public static boolean isBypassing(Player player) {
        return player.hasMetadata(METADATA);
    }

    public static void setBypassing(Player player, boolean bypassing) {
        if (!bypassing)
            player.removeMetadata(METADATA, ILibBukkitPlugin.getInstance());
        else player.setMetadata(METADATA, new FixedMetadataValue(ILibBukkitPlugin.getInstance(), bypassing));
    }

}
