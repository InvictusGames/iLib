package cc.invictusgames.ilib.reboot;

import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.command.parameter.defaults.Duration;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class RebootCommands {

    @Command(names = {"reboot"},
             permission = "ilib.command.reboot",
             description = "Start the reboot timer")
    public boolean reboot(CommandSender sender, @Param(name = "duration") Duration duration) {
        if (duration.isPermanent()) {
            sender.sendMessage(CC.RED + "Cannot start permanent reboot task.");
            return false;
        }

        if (RebootService.isRebooting()) {
            sender.sendMessage(CC.RED + "Reboot already in progress.");
            return false;
        }

        RebootService.reboot(duration.getDuration());
        sender.sendMessage(CC.format("&9Rebooting in &e%s&9.", TimeUtils.formatDetailed(duration.getDuration())));
        return true;
    }

    @Command(names = {"reboot cancel"},
             permission = "ilib.command.reboot",
             description = "Cancel the reboot timer")
    public boolean cancel(CommandSender sender) {
        if (!RebootService.isRebooting()) {
            sender.sendMessage(CC.RED + "Not rebooting.");
            return false;
        }

        RebootService.cancel();
        return true;
    }

}
