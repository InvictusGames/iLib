package cc.invictusgames.ilib.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 21.10.2020 / 07:46
 * iLib / cc.invictusgames.ilib.scoreboard
 */

public interface ScoreboardAdapter {

    String getTitle(Player player);

    List<String> getLines(Player player);

    boolean showHealth(Player player);

}
