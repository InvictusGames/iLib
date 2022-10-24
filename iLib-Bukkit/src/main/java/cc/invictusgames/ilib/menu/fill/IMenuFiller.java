package cc.invictusgames.ilib.menu.fill;

import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.12.2020 / 21:20
 * iLib / cc.invictusgames.ilib.menu.fill
 */

public interface IMenuFiller {

    void fill(Menu menu, Player player, Map<Integer, Button> buttons, int size);

}
