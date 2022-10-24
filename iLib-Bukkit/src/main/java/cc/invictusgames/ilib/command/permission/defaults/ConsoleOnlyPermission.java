package cc.invictusgames.ilib.command.permission.defaults;

import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.command.permission.PermissionAdapter;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.03.2021 / 09:10
 * iLib / cc.invictusgames.ilib.command.permission.defaults
 */

public class ConsoleOnlyPermission extends PermissionAdapter {

    public ConsoleOnlyPermission() {
        super("console");
    }

    public boolean test(CommandSender sender) {
        boolean b = testSilent(sender);
        if (!b) {
            if (sender.isOp())
                sender.sendMessage(CC.RED + "This command can only be used from console.");
            else sender.sendMessage(CommandService.NO_PERMISSION_MESSAGE);
        }
        return b;
    }

    @Override
    public boolean testSilent(CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}
