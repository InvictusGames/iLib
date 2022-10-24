package cc.invictusgames.ilib.command.permission.defaults;

import cc.invictusgames.ilib.command.permission.PermissionAdapter;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.03.2021 / 09:10
 * iLib / cc.invictusgames.ilib.command.permission.defaults
 */

public class PlayerOnlyPermission extends PermissionAdapter {

    public PlayerOnlyPermission() {
        super("player");
    }

    public boolean test(CommandSender sender) {
        boolean b = testSilent(sender);
        if (!b)
            sender.sendMessage(CC.RED + "This command can only be used as a player.");
        return b;
    }

    @Override
    public boolean testSilent(CommandSender sender) {
        return sender instanceof Player;
    }
}
