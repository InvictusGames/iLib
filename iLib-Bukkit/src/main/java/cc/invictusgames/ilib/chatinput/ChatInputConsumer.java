package cc.invictusgames.ilib.chatinput;

import org.bukkit.entity.Player;

public interface ChatInputConsumer<T> {

    boolean accept(Player player, T input);

}
