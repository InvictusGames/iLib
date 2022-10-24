package cc.invictusgames.ilib.command.permission.defaults;

import cc.invictusgames.ilib.command.permission.PermissionAdapter;
import org.bukkit.command.CommandSender;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.03.2021 / 09:10
 * iLib / cc.invictusgames.ilib.command.permission.defaults
 */

public class OpOnlyPermission extends PermissionAdapter {

    public OpOnlyPermission() {
        super("op");
    }

    @Override
    public boolean testSilent(CommandSender sender) {
        return sender.isOp();
    }
}
