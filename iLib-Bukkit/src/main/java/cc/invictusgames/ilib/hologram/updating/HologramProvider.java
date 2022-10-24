package cc.invictusgames.ilib.hologram.updating;

import org.bukkit.entity.Player;

import java.util.List;

public interface HologramProvider {

    List<String> getRawLines(Player player);

}
