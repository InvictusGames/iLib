package cc.invictusgames.ilib.hologram.clickhandler;

import cc.invictusgames.ilib.hologram.Hologram;
import org.bukkit.entity.Player;

public interface HologramClickHandler {

    void click(Player player, Hologram hologram, ClickType clickType);

    enum ClickType {
        LEFT_CLICK,
        RIGHT_CLICK
    }

}
