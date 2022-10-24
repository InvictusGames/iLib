package cc.invictusgames.ilib.deathmessage.command;

import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.deathmessage.killmessage.KillMessageMenu;
import org.bukkit.entity.Player;

public class KillMessageCommand {

    @Command(names = {"killmessage", "killtag", "killtags"},
             permission = "player",
             description = "Change your kill message")
    public boolean killMessage(Player sender) {
        new KillMessageMenu().openMenu(sender);
        return true;
    }

}
