package cc.invictusgames.ilib.command;

import cc.invictusgames.ilib.command.annotation.CommandCooldown;
import cc.invictusgames.ilib.command.data.Data;
import cc.invictusgames.ilib.command.data.FlagData;
import cc.invictusgames.ilib.command.data.ParameterData;
import cc.invictusgames.ilib.command.permission.PermissionAdapter;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ChatMessage;
import cc.invictusgames.ilib.utils.TimeUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 19.01.2020 / 22:31
 * iLib / cc.invictusgames.ilib.commandapi
 */

@Getter
@Setter
@NoArgsConstructor
public class CommandNode {

    private static HashMap<String, CommandNode> commands = new HashMap<>();

    private Method method;
    private Object object;
    private String label = "";
    private List<String> aliases = new ArrayList<>();
    private String permission;
    private String description = "N/A";
    private boolean async;
    private boolean hidden;
    private boolean playerOnly;
    private CommandCooldown commandCooldown;
    private CommandNode parent;
    private List<CommandNode> childs = new ArrayList<>();
    private List<Data> parameters = new ArrayList<>();
    private List<String> flags = new ArrayList<>();

    private Map<Player, Long> cooldowns = new HashMap<>();
    private long globalCooldown = -1;

    public void registerChild(CommandNode child) {
        child.setParent(this);
        childs.add(child);
    }

    public CommandNode getChild(String name) {
        for (CommandNode child : childs) {
            if (child.getLabel().equalsIgnoreCase(name)
                    || child.getAliases().contains(name))
                return child;
        }

        return null;
    }

    public boolean canUse(CommandSender sender) {
        if (permission == null) {
            return true;
        }

        PermissionAdapter adapter = CommandService.getPermissionAdapter(permission);
        if (adapter != null)
            return adapter.testSilent(sender);

        if (permission.equals("")) {
            return !playerOnly || sender instanceof Player;
        }

        return playerOnly ? sender instanceof Player && sender.hasPermission(permission) :
                sender.hasPermission(permission);
    }

    public CommandNode findNode(List<String> args) {
        if (args.size() > 0) {
            String s = args.get(0);
            if (getChild(s) != null) {
                args.remove(s);
                return getChild(s).findNode(args);
            }
        }
        return this;
    }

    public ChatMessage getUsage(String realLabel) {
        ChatMessage usage = new ChatMessage("Usage: " + realLabel + " ")
                .color(ChatColor.RED)
                .hoverText(CC.YELLOW + description);

        List<FlagData> flags = new ArrayList<>();
        for (Data parameter : parameters) {
            if (!(parameter instanceof FlagData))
                continue;

            FlagData data = (FlagData) parameter;
            if (data.isHidden())
                continue;

            flags.add(data);
        }

        List<ParameterData> params = new ArrayList<>();
        for (Data parameter : parameters) {
            if (!(parameter instanceof ParameterData))
                continue;

            params.add((ParameterData) parameter);
        }

        if (!params.isEmpty()) {
            int index = 0;
            for (ParameterData param : params) {
                boolean required = param.getDefaultValue().isEmpty();
                boolean wildcard = param.isWildCard();
                usage.add((required ? "<" : "[") + param.getName() + (wildcard ? "..." : "") + (required ? ">" : "]") +
                        (index != params.size() - 1 ? " " : "")).color(ChatColor.RED);
                usage.hoverText(CC.YELLOW + description);
                index++;
            }
        }

        boolean firstFlag = true;

        if (!flags.isEmpty()) {

            usage.add(" (").color(ChatColor.RED);
            usage.hoverText(CC.YELLOW + description);

            for (FlagData flag : flags) {
                if (!firstFlag) {
                    usage.add(" | ").color(ChatColor.RED);
                    usage.hoverText(CC.YELLOW + description);
                }
                firstFlag = false;
                usage.add("-" + flag.getNames().get(0)).color(ChatColor.AQUA);
                usage.hoverText(CC.GRAY + flag.getDescription());
            }

            usage.add(")").color(ChatColor.RED);
            usage.hoverText(CC.YELLOW + description);
        }

        flags.clear();
        params.clear();
        return usage;
    }

    public boolean invoke(CommandSender sender, List<String> args, List<String> flags) {
        if (method == null) {
            if (!childs.isEmpty()) {

                Set<CommandNode> usable = new HashSet<>();
                for (CommandNode node : childs)
                    if (node.canUse(sender) && !node.isHidden())
                        usable.add(node);

                if (usable.isEmpty()) {
                    if (hidden) {
                        sender.sendMessage(SpigotConfig.unknownCommandMessage);
                        return true;
                    }

                    sender.sendMessage(CommandService.NO_PERMISSION_MESSAGE);
                    return true;
                }

                sender.sendMessage(CC.CHAT_BAR);
                usable.forEach(node ->
                        sender.sendMessage(CC.RED + " " + (sender instanceof Player ? "/" : "")
                                + node.getUsage(node.getFullLabel()).build().toLegacyText().substring(9) +
                                (node.getDescription().isEmpty() || node.getDescription().equalsIgnoreCase("N/A")
                                        ? "" : CC.GRAY + " - " + node.getDescription()))
                );
                sender.sendMessage(CC.CHAT_BAR);
                return true;
            }
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
            return true;
        }

        List<ParameterData> realParameters = new ArrayList<>();
        for (Data data : parameters) {
            if (data instanceof ParameterData) {
                realParameters.add((ParameterData) data);
            }
        }
        List<String> unusedArguments = new ArrayList<>(args);
        List<ParameterData> defaultsUsed = new ArrayList<>();
        Map<Integer, Object> objects = new HashMap<>();
        objects.put(0, sender);
        int index = 0;
        for (Data parameter : parameters) {
            if (parameter instanceof FlagData) {
                FlagData data = (FlagData) parameter;
                boolean value = data.isDefaultValue();
                for (String name : data.getNames()) {
                    if (flags.contains(name.toLowerCase())) {
                        value = !value;
                    }
                }
                objects.put(data.getIndex(), value);
            } else if (parameter instanceof ParameterData) {
                ParameterData data = (ParameterData) parameter;
                String argument;
                if ((!data.getDefaultValue().isEmpty()) && (args.size() < realParameters.size())) {
                    argument = data.getDefaultValue();
                    Object parsed = data.getParameterType().parse(sender, argument);
                    if (parsed == null) {
                        return true;
                    }
                    objects.put(data.getIndex() + 1, parsed);
                    defaultsUsed.add(data);
                    continue;
                } else {
                    try {
                        argument = args.get(index);
                    } catch (Exception e) {
                        return false;
                    }
                }
                unusedArguments.remove(argument);
                if ((data.isWildCard()) && (!args.isEmpty()) /*|| (argument.equals(data.getDefaultValue()))*/) {
                    argument = StringUtils.join(args.toArray(new String[0]), " ", index, args.size());
                }
                if (data.getParameterType() == null) {
                    sender.sendMessage(CC.RED + "Could not find a ParameterType to parse " + data.getType().getName() + "." +
                            " Please contact the server administration if this continues to happen.");
                    return true;
                }
                Object parsed = data.getParameterType().parse(sender, argument);
                if (parsed == null) {
                    return true;
                }
                objects.put(data.getIndex() + 1, parsed);
            }
            index++;
        }

        index = 0;
        for (ParameterData data : defaultsUsed) {
            String argument;
            try {
                argument = unusedArguments.get(index);
            } catch (Exception e) {
                continue;
            }
            Object parsed = data.getParameterType().parse(sender, argument);
            if (parsed == null) {
                continue;
            }
            objects.put(data.getIndex() + 1, parsed);
            index++;
        }


        /*if ((!hasWildCard) && (args.size() > flagsUsed + parametersUsed)) {
            return false;
        }*/

        if (sender instanceof Player
                && hasCooldown((Player) sender)) {
            if (commandCooldown.global())
                sender.sendMessage(CC.format("&cThis command is on a global cooldown for another &e%s&c.",
                        formatRemainingCooldown((Player) sender)));
            else sender.sendMessage(CC.format("&cYou cannot use this command for another &e%s&c.",
                    formatRemainingCooldown((Player) sender)));
            return true;
        }

        try {
            if (method.getReturnType() == Boolean.TYPE) {
                boolean success = (Boolean) method.invoke(object, objects.values().toArray());
                if ((success) && (commandCooldown != null) && (sender instanceof Player)) {
                    if (commandCooldown.global())
                        globalCooldown = System.currentTimeMillis();
                    else cooldowns.put((Player) sender, System.currentTimeMillis());
                }
            } else {
                method.invoke(object, objects.values().toArray());
            }
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getFullLabel() {
        return parent != null ? parent.getFullLabel() + " " + label : label;
    }

    private boolean hasCooldown(Player player) {
        if (commandCooldown == null) {
            return false;
        }

        if (commandCooldown.global()) {
            if (!commandCooldown.bypassPermission().isEmpty() && player.hasPermission(commandCooldown.bypassPermission())) {
                return false;
            }

            return globalCooldown + commandCooldown.timeUnit().toMillis(commandCooldown.time()) >= System.currentTimeMillis();
        }

        if (!cooldowns.containsKey(player)) {
            return false;
        }

        if (!commandCooldown.bypassPermission().isEmpty() && player.hasPermission(commandCooldown.bypassPermission())) {
            return false;
        }

        return cooldowns.get(player) + commandCooldown.timeUnit().toMillis(commandCooldown.time()) >= System.currentTimeMillis();
    }

    private String formatRemainingCooldown(Player player) {
        long end = (commandCooldown.global() ? globalCooldown : cooldowns.get(player))
                + commandCooldown.timeUnit().toMillis(commandCooldown.time());
        return TimeUtils.formatDetailed(end - System.currentTimeMillis());
    }

}
