package cc.invictusgames.ilib.tab;

import com.google.common.collect.Table;
import org.bukkit.entity.Player;

public interface TabAdapter {

    Table<Integer, Integer, TabEntry> getEntries(Player player);

    String getHeader(Player player);

    String getFooter(Player player);

}
