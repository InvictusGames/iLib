package cc.invictusgames.ilib.command.defaults;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Flag;
import cc.invictusgames.ilib.redis.RedisService;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.03.2020 / 17:05
 * iLib / cc.invictusgames.ilib.command.defaults
 */

@RequiredArgsConstructor
public class RedisCommand {

    private final ILib instance;

    @Command(names = {"redis"}, permission = "op",
             description = "View information about all Redis-Services running on the server")
    public boolean redis(CommandSender sender, @Flag(names = {"r", "reconnect"},
                                                     description = "Reconnect all running Redis-Services") boolean reconnect) {
        /*if (reconnect) {
            sender.sendMessage(CC.YELLOW + "Please type " + CC.GREEN + "confirm" + CC.YELLOW + " to confirm or " + CC
            .RED + "cancel" + CC.YELLOW + " to cancel.");
            new ChatInput(sender, callback -> {
                if (callback.equalsIgnoreCase("confirm")) {
                    int amount = 0;
                    int success = 0;
                    int failed = 0;
                    for (RedisService service : RedisService.getServices()) {
                        amount++;
                        sender.sendMessage(CC.YELLOW + "Attempting to reconnect service #" + amount + ".");
                        if (service.reconnect()) {
                            success++;
                            sender.sendMessage(CC.GREEN + "Successfully reconnected service #" + amount + ".");
                        } else {
                            failed ++;
                            sender.sendMessage(CC.RED + "Failed to reconnect service #" + amount + ". Check console
                            for further information.");
                        }
                    }
                    sender.sendMessage(CC.YELLOW + "Done " + CC.GREEN + success + CC.YELLOW + " | " + CC.RED + failed
                     + CC.YELLOW + " (" + amount + ")");
                    return true;
                } else if (callback.equalsIgnoreCase("cancel")) {
                    sender.sendMessage(CC.RED + "Cancelled.");
                    return true;
                } else {
                    sender.sendMessage(CC.RED + "Unrecognized input.");
                    sender.sendMessage(CC.YELLOW + "Please type " + CC.GREEN + "confirm" + CC.YELLOW + " to confirm
                    or " + CC.RED + "cancel" + CC.YELLOW + " to cancel.");
                    return false;
                }
            });
            return true;
        }*/

        sender.sendMessage(CC.YELLOW + "Status: " + (RedisService.isDown() ? CC.RED + "down" : CC.GREEN + "up"));
        sender.sendMessage(CC.YELLOW + "Last command: " + CC.AQUA + (RedisService.getLastExecution() == -1 ?
                "Never?" : TimeUtils.formatDetailed(System.currentTimeMillis() - RedisService.getLastExecution()) +
                " ago"));
        sender.sendMessage(CC.YELLOW + "Last packet: " + CC.AQUA + (RedisService.getLastPacket() == -1 ?
                "Never?" : TimeUtils.formatDetailed(System.currentTimeMillis() - RedisService.getLastPacket()) + " " +
                "ago") +
                " (" + RedisService.getLastPacketName() + ")");
        sender.sendMessage(CC.YELLOW + "Last error: " + CC.AQUA + (RedisService.getLastError() == -1 ?
                "Never (wohoo)" :
                TimeUtils.formatDetailed(System.currentTimeMillis() - RedisService.getLastError()) + " ago"));
        sender.sendMessage(CC.YELLOW + "Cached UUID's: " + CC.AQUA + instance.getUuidCache().getCachedAmount());
        sender.sendMessage(CC.YELLOW + "Services registered: " + CC.AQUA + RedisService.getServices().size());
        return true;
    }
}
