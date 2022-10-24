package cc.invictusgames.ilib.playersetting.command;

import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.playersetting.menu.SettingsMenu;
import org.bukkit.entity.Player;

public class SettingsCommands {

    @Command(names = {"settings", "options", "preferences", "prefs"},
             description = "Open the settings menu")
    public boolean settings(Player sender) {
        new SettingsMenu().openMenu(sender);
        return true;
    }

}
