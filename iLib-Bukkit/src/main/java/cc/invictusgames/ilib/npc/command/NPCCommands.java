package cc.invictusgames.ilib.npc.command;

import cc.invictusgames.ilib.ILibBukkit;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Flag;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.npc.NPC;
import cc.invictusgames.ilib.npc.NPCBuilder;
import cc.invictusgames.ilib.npc.equipment.EquipmentSlot;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ChatMessage;
import cc.invictusgames.ilib.utils.ItemUtils;
import cc.invictusgames.ilib.utils.Statics;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class NPCCommands {

    private final ILibBukkit instance;

    @Command(names = {"npc create"},
             permission = "ilib.npcs",
             description = "Create a new npc",
             playerOnly = true)
    public boolean create(Player sender, @Param(name = "name") String name) {
        try {
            Integer.parseInt(name);
            sender.sendMessage(CC.RED + "Cannot create npc with int name.");
            return false;
        } catch (NumberFormatException ignored) { }

        NPC npc = new NPCBuilder()
                .at(sender.getLocation())
                .buildAndSpawn();

        npc.setName(name);
        instance.getNpcService().register(npc);
        instance.getNpcService().save();
        sender.sendMessage(CC.format("&9NPC &e#%d &9has been created.", npc.getId()));
        return true;
    }

    @Command(names = {"npc delete", "npc remove"},
             permission = "ilib.npcs",
             description = "Delete a npc")
    public boolean delete(CommandSender sender, @Param(name = "id") NPC npc) {
        instance.getNpcService().remove(npc);
        instance.getNpcService().save();
        sender.sendMessage(CC.format("&9Deleted npc &e#%d&9.", npc.getId()));
        return true;
    }

    @Command(names = {"npc list"},
             permission = "ilib.npcs",
             description = "List all npcs")
    public boolean list(CommandSender sender) {
        if (instance.getNpcService().getSerializedNpcs().size() <= 0) {
            sender.sendMessage(CC.RED + "There are no npcs.");
            return true;
        }

        for (NPC npc : instance.getNpcService().getSerializedNpcs()) {
            String location = String.format("[%.1f, %.1f, %.1f]",
                    npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ());

            List<String> hover = new ArrayList<>();

            hover.add(CC.GREEN + "Location: " + location);
            hover.add(CC.YELLOW + "Click to teleport");

            if (npc.getCommand() != null) {
                hover.add(" ");
                hover.add(CC.BLUE + "Command: " + CC.YELLOW + npc.getCommand());
                if (npc.isConsoleCommand())
                    hover.add(CC.GRAY + "(Console Command)");
            }

            new ChatMessage(npc.getName() + " - #" + npc.getId())
                    .color(ChatColor.RED)
                    .hoverText(String.join("\n", hover))
                    .runCommand("/npc tpto " + npc.getId())
                    .send(sender);
        }

        return true;
    }

    @Command(names = {"npc setname", "npc name"},
             permission = "ilib.npcs",
             description = "Set the display name of a npc")
    public boolean setName(CommandSender sender,
                        @Param(name = "id") NPC npc,
                        @Param(name = "displayName", wildcard = true) String displayName) {
        displayName = CC.translate(displayName);

        npc.setDisplayName(displayName);
        npc.despawn();
        npc.spawn();

        instance.getNpcService().save();

        sender.sendMessage(CC.format("&9Set display name of npc &e#%d &9to '&r%s&9'.",
                npc.getId(), displayName));
        return true;
    }

    @Command(names = {"npc command"},
             permission = "ilib.npcs",
             description = "Set the command of a npc")
    public boolean command(CommandSender sender,
                           @Param(name = "id") NPC npc,
                           @Param(name = "command", wildcard = true) String command,
                           @Flag(names = {"-console"}, description = "Executes the command from the console")
                                       boolean consoleCommand) {
        npc.setCommand(command);
        npc.setConsoleCommand(consoleCommand);

        instance.getNpcService().save();

        sender.sendMessage(CC.format("&9Set the command of npc &e#%d &9to &e%s&9.%s",
                npc.getId(), command, consoleCommand ? CC.GRAY + " (Console Command)" : ""));
        return true;
    }

    @Command(names = {"npc removecommand"},
             permission = "ilib.npcs",
             description = "Remove the command of a npc")
    public boolean removeCommand(CommandSender sender, @Param(name = "id") NPC npc) {
        npc.setCommand(null);
        npc.setConsoleCommand(false);

        instance.getNpcService().save();

        sender.sendMessage(CC.format("&9You removed the command of npc &e#%d&9.", npc.getId()));
        return true;
    }

    @Command(names = {"npc skin"},
             permission = "ilib.npcs",
             description = "Set the skin of a npc",
             async = true)
    public boolean skin(CommandSender sender, @Param(name = "id") NPC npc, @Param(name = "skin") String skin) {
        try {
            String response = getResponse("https://api.minetools.eu/uuid/" + skin);
            JsonObject parsed = Statics.JSON_PARSER.parse(response).getAsJsonObject();
            String uuid = parsed.get("id").getAsString();

            response = getResponse("https://api.minetools.eu/profile/" + uuid);
            parsed = Statics.JSON_PARSER.parse(response).getAsJsonObject();
            JsonObject properties = parsed.get("raw").getAsJsonObject()
                    .get("properties").getAsJsonArray().get(0).getAsJsonObject();

            npc.setSkin(new String[] {
                    properties.get("value").getAsString(),
                    properties.get("signature").getAsString()
            });
        } catch (Exception e)  {
            npc.setSkin(null);
            e.printStackTrace();
        }

        instance.getNpcService().save();

        sender.sendMessage(CC.format("&9NPC &e#%d &9now has the skin of &e%s&9.", npc.getId(), skin));
        Bukkit.getScheduler().runTask(ILibBukkitPlugin.getInstance(), () -> {
            npc.despawn();
            npc.spawn();
        });
        return true;
    }

    @Command(names = {"npc tphere"},
             permission = "ilib.npcs",
             description = "Teleport a npc to your location",
             playerOnly = true)
    public boolean tphere(Player sender, @Param(name = "id") NPC npc) {
        npc.setLocation(sender.getLocation());
        npc.despawn();
        npc.spawn();

        instance.getNpcService().save();

        sender.sendMessage(CC.format("&9Teleported npc &e#%d &9to your location.", npc.getId()));
        return true;
    }

    @Command(names = {"npc tpto"},
             permission = "ilib.npcs",
             description = "Teleport to a npc",
             playerOnly = true)
    public boolean tpto(Player sender, @Param(name = "id") NPC npc) {
        sender.teleport(npc.getLocation());
        sender.sendMessage(CC.format("&9Teleported to npc &e#%d&9.", npc.getId()));
        return true;
    }

    @Command(names = {"npc equipment"},
             permission = "ilib.npcs",
             description = "Set the equipment of a npc",
             playerOnly = true)
    public boolean equipment(Player sender, @Param(name = "id") NPC npc, @Param(name = "slot") EquipmentSlot slot) {
        npc.setEquipment(slot, sender.getItemInHand() == null || sender.getItemInHand().getType() == Material.AIR
                ? null : sender.getItemInHand().clone());

        instance.getNpcService().save();

        sender.sendMessage(CC.format(
                "&9Set equipment in slot &e%s &9of npc &e#%d &9to &e%s&9.",
                slot.name(),
                npc.getId(),
                ItemUtils.getName(sender.getItemInHand())
        ));
        return true;

    }

    private static String getResponse(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().forEach(response::append);
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
