package cc.invictusgames.ilib.command.defaults;

import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.mongo.MongoService;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.TimeUtils;
import org.bukkit.command.CommandSender;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 04.03.2020 / 18:57
 * iLib / cc.invictusgames.ilib.command.defaults
 */

public class MongoCommand {

    @Command(names = {"mongo"}, permission = "op",
             description = "View information about all Mongo-Services running on the server")
    public boolean mongo(CommandSender sender) {
        sender.sendMessage(CC.YELLOW + "Status: " + (MongoService.isDown() ? CC.RED + "down" : CC.GREEN + "up"));
        sender.sendMessage(CC.YELLOW + "Last command: " + CC.AQUA + (MongoService.getLastExecution() == -1 ?
                "Never?" : TimeUtils.formatDetailed(System.currentTimeMillis() - MongoService.getLastExecution()) +
                " ago"));
        sender.sendMessage(CC.YELLOW + "Last error: " + CC.AQUA + (MongoService.getLastError() == -1 ?
                "Never (wohoo)" :
                TimeUtils.formatDetailed(System.currentTimeMillis() - MongoService.getLastError()) + " ago"));
        sender.sendMessage(CC.YELLOW + "Last latency: " + CC.AQUA + MongoService.getLastLatency() + "ms");
        sender.sendMessage(CC.YELLOW + "Average latency: " + CC.AQUA + MongoService.getAverageLatency() + "ms");
        sender.sendMessage(CC.YELLOW + "Services registered: " + CC.AQUA + MongoService.getServices().size());
        return true;
    }
}
