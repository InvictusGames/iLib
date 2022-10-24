package cc.invictusgames.ilib.command.defaults;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.border.Border;
import cc.invictusgames.ilib.border.BorderFiller;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.chatinput.ChatInput;
import cc.invictusgames.ilib.chatinput.ChatInputChain;
import cc.invictusgames.ilib.command.CommandNode;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Flag;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.configuration.defaults.MongoConfig;
import cc.invictusgames.ilib.mongo.MongoService;
import cc.invictusgames.ilib.scoreboard.PlayerScoreboard;
import cc.invictusgames.ilib.scoreboard.ScoreboardService;
import cc.invictusgames.ilib.utils.*;
import cc.invictusgames.ilib.uuid.UUIDCache;
import cc.invictusgames.ilib.visibility.VisibilityService;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 01.04.2020 / 19:11
 * iLib / cc.invictusgames.ilib.command.defaults
 */

public class DebugCommands implements Listener {

    public DebugCommands() {
        ILibBukkitPlugin.getInstance().getServer().getPluginManager().registerEvents(this,
                ILibBukkitPlugin.getInstance());
    }

    @Command(names = {"disabledebugs"}, permission = "op", description = "Disable/Enable all debug messages")
    public boolean disableDebugs(CommandSender sender) {
        Debugger.enabled = !Debugger.enabled;
        sender.sendMessage(CC.BLUE + "You have " + CC.colorBoolean(Debugger.enabled) + CC.BLUE + " all debug messages");
        return true;
    }

    @Command(names = {"formatdate"}, permission = "op", description = "Format milliseconds into a date string")
    public boolean formatdate(CommandSender sender, @Param(name = "time") long time) {
        sender.sendMessage(CC.BLUE + TimeUtils.formatDate(time));
        return true;
    }

    @Command(names = {"formattime"}, permission = "op", description = "Format milliseconds into a time string")
    public boolean formattime(CommandSender sender, @Param(name = "time") long time) {
        sender.sendMessage(CC.BLUE + TimeUtils.formatDetailed(time));
        return true;
    }


    @Command(names = {"uuid"}, permission = "op", description = "Get the uuid of a player")
    public boolean uuid(CommandSender sender, @Param(name = "name") UUID uuid) {
        ChatMessage message = new ChatMessage(UUIDCache.getName(uuid))
                .color(ChatColor.BLUE)
                .hoverText(CC.YELLOW + "Click to copy")
                .suggestCommand(UUIDCache.getName(uuid));

        message.add(" = ").color(ChatColor.YELLOW);

        message.add(uuid.toString())
                .color(ChatColor.BLUE)
                .hoverText(CC.YELLOW + "Click to copy")
                .suggestCommand(uuid.toString());

        message.send(sender);
        return true;
    }

    @Command(names = {"commandinfo", "cmdinfo"}, permission = "op", description = "View information about a command")
    public boolean cmdinfo(CommandSender sender, @Param(name = "command", wildcard = true) String label) {
        CommandNode node;

        if (label.contains(" ")) {
            String[] split = label.split(" ");
            if (!CommandService.REGISTERED_COMMANDS.containsKey(split[0].toLowerCase())) {
                sender.sendMessage(CC.RED + "Command " + CC.YELLOW + label + CC.RED + " not found.");
                return false;
            }

            CommandNode parent = CommandService.REGISTERED_COMMANDS.get(split[0]);
            List<String> args = new ArrayList<>(Arrays.asList(split));
            args.remove(0);
            node = parent.findNode(args);
            if (node == null) {
                sender.sendMessage(CC.RED + "Command " + CC.YELLOW + label + CC.RED + " not found.");
                return false;
            }
        } else {
            if (!CommandService.REGISTERED_COMMANDS.containsKey(label.toLowerCase())) {
                sender.sendMessage(CC.RED + "Command " + CC.YELLOW + label + CC.RED + " not found.");
                return false;
            }

            node = CommandService.REGISTERED_COMMANDS.get(label.toLowerCase());
        }

        sender.sendMessage(" ");
        sender.sendMessage(CC.WHITE + node.getFullLabel() + CC.GOLD + ":");
        sender.sendMessage(CC.GOLD + "Aliases: " + CC.WHITE + StringUtils.join(node.getAliases(),
                CC.GOLD + ", " + CC.WHITE));
        sender.sendMessage(CC.GOLD + "Permission: " + CC.WHITE + node.getPermission());
        sender.sendMessage(CC.GOLD + "Player only: " + CC.WHITE + node.isPlayerOnly());
        sender.sendMessage(CC.GOLD + "Description: " + CC.WHITE + node.getDescription());
        sender.sendMessage(CC.GOLD + "Async: " + CC.WHITE + node.isAsync());
        sender.sendMessage(CC.GOLD + "Owning Class: " + CC.WHITE + (node.getObject() != null ?
                node.getObject().getClass().getName() : "Unknown"));
        if (node.getCommandCooldown() != null) {
            sender.sendMessage(CC.GOLD + (node.getCommandCooldown().global() ? "Global " : "")
                    + "Cooldown: " + CC.WHITE + node.getCommandCooldown().time() +
                    " " + WordUtils.capitalizeFully(node.getCommandCooldown().timeUnit().name()));
            sender.sendMessage(CC.GOLD + " Bypass Permission: " + CC.WHITE + node.getCommandCooldown().bypassPermission());
        }
        if (!node.getChilds().isEmpty()) {
            List<String> childs = new ArrayList<>();
            node.getChilds().forEach(child -> childs.add(child.getFullLabel()));
            sender.sendMessage(CC.GOLD + "Childs: " + CC.WHITE + StringUtils.join(childs, CC.GOLD + ", " + CC.WHITE));
        }
        return true;
    }

    @Command(names = {"fillborder"}, permission = "op")
    public boolean fillborder(
            Player sender,
            @Param(name = "material") ItemStack item,
            @Param(name = "size") int size,
            @Param(name = "height") int height,
            @Param(name = "blocksPerTick") int blocksPerTick) {

        Border border = new Border(sender.getLocation(), item.getType(), size, height);
        BorderFiller filler = new BorderFiller(border);

        sender.sendMessage(CC.YELLOW + "Loading locations...");
        Timings timings = new Timings("border-fill").startTimings();
        filler.loadLocations(() -> {
            timings.stopTimings();
            sender.sendMessage(CC.YELLOW + "Loaded locations in " + CC.BLUE + timings.calculateDifference() + "ms" + CC.YELLOW + ".");
            sender.sendMessage(CC.YELLOW + "Starting block replacement...");
            timings.startTimings();
            filler.fillLocations(blocksPerTick, filledAmount -> {
                timings.stopTimings();
                sender.sendMessage(CC.YELLOW + "Filled " + CC.BLUE + filledAmount + CC.YELLOW + " blocks in " + CC.BLUE + timings.calculateDifference() + "ms" + CC.YELLOW + ".");
            });
        });
        return true;
    }

    @Command(names = {"testvis", "visdebug"}, permission = "op",
             description = "Test the visibility context of two players")
    public boolean visdebug(CommandSender sender, @Param(name = "player") Player player, @Param(
            name = "target") Player target) {
        VisibilityService.getDebugInfo(player, target).forEach(sender::sendMessage);
        sender.sendMessage(CC.format(
                "&9Bukkit Result: &e%s %s &9see &e%s&9.",
                player.getName(),
                CC.colorBoolean(player.canSee(target), "can", "cannot"),
                target.getName()
        ));

        if (player == target)
            sender.sendMessage(CC.DRED + CC.BOLD + "!" + CC.RED
                    + " Testing a player with himself, bukkit result will always return visible");
        return true;
    }

    @Command(names = {"permadapterdebug", "permdebug"}, permission = "op", description = "Test the PermissionAdapters")
    public boolean permdebug(CommandSender sender, @Flag(names = {"msg"},
                                                         description = "Send the no permission message of the " +
                                                                 "adapter") boolean msg) {
        CommandService.getPermissionAdapters().forEach(adapter -> {
            boolean value = adapter.testSilent(sender);
            sender.sendMessage(CC.format(
                    "&9%s (%s): %s",
                    adapter.getClass().getSimpleName(),
                    adapter.getPermission(),
                    CC.colorBoolean(value, "true", "false")
            ));
            if (msg)
                adapter.test(sender);
        });
        return true;
    }

    /*@Command(names = {"genworld"}, permission = "op")
    public boolean genworld(Player sender, @Param(name = "name") String name) {
        sender.sendMessage(CC.YELLOW + "Starting world generation for world " + CC.BLUE + name + CC.YELLOW + ".");
        Bukkit.createWorld(new WorldCreator(name)
                .environment(World.Environment.NORMAL)
                .type(WorldType.NORMAL).generateStructures(true));
        sender.sendMessage(CC.YELLOW + "Done.");
        return true;
    }*/

    @Command(names = {"tpworld"}, permission = "ilib.command.tpworld")
    public boolean tpworld(Player sender, @Param(name = "world") World world) {
        sender.teleport(world.getSpawnLocation());
        sender.sendMessage(CC.BLUE + "Teleporting you to spawn location of " + CC.YELLOW + world.getName() + CC.BLUE + ".");
        return true;
    }

    /*@EventHandler
    public void onInitWorld(WorldInitEvent event) {
        //event.getWorld().getPopulators().add(new OrePopulator(Material.DIAMOND_ORE, ILib.getInstance()
        .getGeneratorConfig().getDiamondConfig()));
    }*/

    @Command(names = {"uuidcache validate"}, permission = "op", description = "Validate the uuid cache", async = true)
    public boolean validateUuidCache(CommandSender sender) {
        List<String> duplicateNames = new ArrayList<>();
        List<UUID> duplicateUuids = new ArrayList<>();
        ILib.getInstance().getUuidCache().getUuidNameMap().forEach((uuid, name) -> {
            if (!UUIDCache.getUuid(name).equals(uuid)) {
                duplicateNames.add(name);
                duplicateUuids.add(uuid);
                duplicateUuids.add(UUIDCache.getUuid(name));
                sender.sendMessage(CC.format("&9Found duplicate entry: &e%s &9(&e%s&9)", name,
                        UUIDCache.getName(uuid)));
            }
        });
        sender.sendMessage(CC.format("&9Found a total of &e%d &9duplicated entries.", duplicateNames.size()));
        sender.sendMessage(CC.BLUE + "Removing all duplicated names from cache...");
        //ILib.getInstance().getUuidCache().getUuidNameMap().entrySet().removeIf(entry -> duplicateNames.contains
        // (entry.getValue()));
        int before = ILib.getInstance().getUuidCache().getNameUuidMap().size();
        duplicateUuids.forEach(uuid -> {
            sender.sendMessage(CC.format("&9Removing &e%s &9from cache.", UUIDCache.getName(uuid)));
            ILib.getInstance().getUuidCache().getUuidNameMap().remove(uuid);
            ILib.getInstance().getRedisService().executeBackendCommand(redis -> redis.hdel("UUIDCache",
                    uuid.toString()));
        });
        sender.sendMessage(CC.format("&aDone. (&e%s&a, &e%d&a)", duplicateUuids.size(),
                before - ILib.getInstance().getUuidCache().getUuidNameMap().size()));
        sender.sendMessage(CC.BLUE + "Looking up names in Invictus database...");
        MongoService mongoService = new MongoService(new MongoConfig(), "Invictus");
        mongoService.connect();
        duplicateNames.forEach(name -> {
            Document document = mongoService.getCollection("profiles").find(Filters.eq("name", name)).first();
            if (document == null)
                sender.sendMessage(CC.format(" &c&l! &e%s &9is not present in Invictus database.", name));
            else {
                ILib.getInstance().getUuidCache().getUuidNameMap().put(UUID.fromString(document.getString("uuid")),
                        document.getString("name"));
                sender.sendMessage(CC.format("&9Found real uuid of &e%s&9.", name));
            }
        });

        sender.sendMessage(CC.GREEN + "Done.");
        sender.sendMessage(CC.BLUE + "Saving cache to redis");
        ILib.getInstance().getUuidCache().saveAll();
        sender.sendMessage(CC.GREEN + "Done.");
        return true;
    }

    @Command(names = {"uuidcache uuidnamedebug"}, permission = "op",
             description = "Debug the uuid name map (name as value)")
    public boolean uuidCacheUuidName(CommandSender sender, @Param(name = "name") String name) {
        ILib.getInstance().getUuidCache().getUuidNameMap().forEach((uuid, s) -> {
            if (s.equalsIgnoreCase(name)) {
                ChatMessage message = new ChatMessage(s)
                        .color(ChatColor.BLUE)
                        .hoverText(CC.YELLOW + "Click to copy")
                        .suggestCommand(s);

                message.add(" = ").color(ChatColor.YELLOW);

                message.add(uuid.toString())
                        .color(ChatColor.BLUE)
                        .hoverText(CC.YELLOW + "Click to copy")
                        .suggestCommand(uuid.toString());

                message.send(sender);
            }
        });
        return true;
    }

    @Command(names = {"uuidcache nameuuiddebug"}, permission = "op",
             description = "Debug the name uuid map (uuid as value)")
    public boolean uuidCacheNameUuid(CommandSender sender, @Param(name = "uuid") String uuidString) {
        if (!UUIDUtils.isUUID(uuidString))
            return false;

        ILib.getInstance().getUuidCache().getNameUuidMap().forEach((s, uuid) -> {
            if (uuid.toString().equalsIgnoreCase(uuidString)) {
                ChatMessage message = new ChatMessage(s)
                        .color(ChatColor.BLUE)
                        .hoverText(CC.YELLOW + "Click to copy")
                        .suggestCommand(s);

                message.add(" = ").color(ChatColor.YELLOW);

                message.add(uuid.toString())
                        .color(ChatColor.BLUE)
                        .hoverText(CC.YELLOW + "Click to copy")
                        .suggestCommand(uuid.toString());

                message.send(sender);
            }
        });
        return true;
    }

    @Command(names = {"scoreboarddebug", "sbdebug"},
             permission = "op",
             description = "Debug the scorebaord team names of a player")
    public boolean scoreboardDebug(CommandSender sender, @Param(name = "player", defaultValue = "@self") Player target) {
        if (!ScoreboardService.isInitialized()) {
            sender.sendMessage(CC.RED + "ILib's scoreboard service is not utilized on this server.");
            return false;
        }

        if (!ScoreboardService.SCOREBOARDS.containsKey(target.getUniqueId())) {
            sender.sendMessage(CC.format("&e%s &chas no board.", target.getName()));
            return false;
        }

        PlayerScoreboard playerScoreboard = ScoreboardService.getBoard(target.getPlayer());
        playerScoreboard.getDebugs().forEach(sender::sendMessage);
        return true;
    }

    @Command(names = {"timezonedebug"},
             permission = "op",
             description = "Test timezone parsing")
    public boolean getTimeZone(CommandSender sender, @Param(name = "input") String input, @Flag(names = "ids") boolean ids) {
        if (!ids) {
            TimeZone timeZone = TimeZone.getTimeZone(input);
            sender.sendMessage(CC.format("&9toString: &e%s", timeZone.toString()));
            sender.sendMessage(CC.format("&9displayName: &e%s", timeZone.getDisplayName()));
            sender.sendMessage(CC.format("&9currentTime: &e%s", TimeUtils.formatDate(System.currentTimeMillis(), timeZone)));
            sender.sendMessage(CC.format("&9ID: &e%s", timeZone.getID()));
            sender.sendMessage(CC.format("&9ZoneID: &e%s", timeZone.toZoneId().getId()));
            return true;
        }

        sender.sendMessage(CC.BLUE + StringUtils.join(TimeZone.getAvailableIDs(), CC.YELLOW + ", " + CC.BLUE));
        return true;
    }

    @Command(names = {"adminbypass", "adbp"},
             permission = "ilib.adminbypass",
             description = "Toggle admin bypass mode")
    public boolean adminBypass(CommandSender sender, @Param(name = "target", defaultValue = "@self") Player target) {
        AdminBypass.setBypassing(target, !AdminBypass.isBypassing(target));
        target.sendMessage(CC.format("&9Toggled admin bypass %s&9.",
                AdminBypass.isBypassing(target) ? CC.GREEN + "ON" : CC.RED + "OFF"));

        if (!sender.equals(target))
            sender.sendMessage(CC.format("&9Toggled admin bypass for &e%s %s&9.",
                    target.getName(), AdminBypass.isBypassing(target) ? CC.GREEN + "ON" : CC.RED + "OFF"));
        return true;
    }

    @Command(names = {"glowitem"})
    public boolean glowItem(Player sender,
                            @Param(name = "glow") boolean glow,
                            @Param(name = "type", defaultValue = "0") int type) {
        if (sender.getItemInHand() == null || sender.getItemInHand().getType() == Material.AIR) {
            sender.sendMessage(CC.RED + "You're not holding an item.");
            return true;
        }

        if (type == 0)
            sender.setItemInHand(new ItemBuilder(sender.getItemInHand()).glowing(glow).build());
        else if (type == 1)
            sender.getItemInHand().addUnsafeEnchantment(ItemBuilder.GLOW, 1);

        sender.updateInventory();
        sender.sendMessage(CC.format("&9Your item is %s &9glowing.",
                CC.colorBoolean(glow, "now", "no longer")));
        return true;
    }

    /*@Command(names = {"animationdebug"}, permission = "op")
    public void animation(Player sender,
                          @Param(name = "ticks") long ticks,
                          @Param(name = "fadeFrom") String fadeFrom,
                          @Param(name = "fadeTo") String fadeTo,
                          @Param(name = "text", wildcard = true) String text) {
        fadeFrom = CC.translate(fadeFrom);
        fadeTo = CC.translate(fadeTo);
        StringAnimation animation = new StringAnimation();
        animation.add(new FadeAnimation(text, fadeFrom, fadeTo, false));
        animation.add(new BlinkAnimation(text, fadeFrom, fadeTo, 3, 2));
        animation.add(new FadeAnimation(text, fadeFrom, fadeTo, true));
        animation.whenTicked(s -> BossBarService.setBossBar(sender, s, 1F));
        animation.start(ticks);
    }*/

    @Command(names = {"chatinputtest"}, permission = "op")
    public void chatinputtest(Player sender) {
        new ChatInputChain()
                .escapeMessage(CC.RED + "You cancelled the test input")
                .next(
                        new ChatInput<>(String.class)
                                .text(CC.YELLOW + "Enter something, or say cancel")
                                .accept((player, input) -> {
                                    player.sendMessage(CC.BLUE + "your input was: " + CC.YELLOW + input);
                                    return true;
                                })
                ).next(
                        new ChatInput<Integer>(Integer.class)
                                .text(
                                        CC.YELLOW + "Enter a number, or say cancel",
                                        CC.YELLOW + "The number must be between 5 and 0"
                                ).accept((player, input) -> {
                                    if (input > 5 || input < 0) {
                                        player.sendMessage(CC.RED + "Your input must be between 5 and 0.");
                                        return false;
                                    }

                                    player.sendMessage(CC.BLUE + "your input number was: " + CC.YELLOW + input);
                                    return true;
                                })
                ).start(sender);
    }

    @Command(names = {"loop"}, permission = "op", description = "Loop")
    public boolean loop(CommandSender sender,
                        @Param(name = "intervals") int intervals,
                        @Param(name = "increase") int increase,
                        @Param(name = "command", wildcard = true) String command) {
        for (int i = 1; i <= intervals; i += increase) {
            sender.sendMessage(CC.format(
                    "&9(&e%d&9/&e%d&9): &e%s",
                    i,
                    intervals,
                    String.format(command, i)
            ));
            
            Bukkit.dispatchCommand(sender, String.format(command, i));
        }

        return true;
    }
}
