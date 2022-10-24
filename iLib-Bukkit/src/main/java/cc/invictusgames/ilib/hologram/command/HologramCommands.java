package cc.invictusgames.ilib.hologram.command;

import cc.invictusgames.ilib.ILibBukkit;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.hologram.HologramBuilder;
import cc.invictusgames.ilib.hologram.HologramLine;
import cc.invictusgames.ilib.hologram.statics.StaticHologram;
import cc.invictusgames.ilib.menu.menu.TextEditMenu;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class HologramCommands {

    private final ILibBukkit instance;

    @Command(names = {"hologram create", "holo create"},
             permission = "ilib.holograms",
             description = "Create a new hologram",
             playerOnly = true)
    public boolean create(Player sender,
                          @Param(name = "name") String name,
                          @Param(name = "text", wildcard = true) String text) {
        try {
            Integer.parseInt(name);
            sender.sendMessage(CC.RED + "Cannot create hologram with int name.");
            return false;
        } catch (NumberFormatException ignored) { }

        StaticHologram hologram = new HologramBuilder()
                .at(sender.getLocation())
                .staticHologram()
                .addLines(CC.translate(text))
                .build();

        hologram.setName(name);
        hologram.spawn();

        instance.getHologramService().register(hologram);
        instance.getHologramService().save();
        sender.sendMessage(CC.format("&9Hologram &e#%d &9has been created.", hologram.getId()));
        return true;
    }

    @Command(names = {"hologram delete", "hologram remove"},
             permission = "ilib.holograms",
             description = "Delete a hologram")
    public boolean delete(CommandSender sender, @Param(name = "id") StaticHologram hologram) {
        instance.getHologramService().remove(hologram);
        instance.getHologramService().save();
        sender.sendMessage(CC.format("&9Deleted hologram &e#%d&9.", hologram.getId()));
        return true;
    }

    @Command(names = {"hologram list"},
             permission = "ilib.holograms",
             description = "List all holograms")
    public boolean list(CommandSender sender) {
        if (instance.getHologramService().getSerializedHolograms().size() <= 0) {
            sender.sendMessage(CC.RED + "There are no active holograms.");
            return true;
        }

        for (StaticHologram hologram : instance.getHologramService().getSerializedHolograms()) {
            String location = String.format("[%.1f, %.1f, %.1f]",
                    hologram.getLocation().getX(), hologram.getLocation().getY(), hologram.getLocation().getZ());

            List<String> hover = new ArrayList<>();

            hover.add(CC.GREEN + "Location: " + location);
            hover.add(CC.YELLOW + "Click to teleport");
            hover.add(" ");

            int i = 0;
            for (HologramLine line : hologram.getCurrentLines())
                hover.add(CC.GRAY + (++i) + ". " + CC.RESET + line.getText());

            new ChatMessage(hologram.getName() + " - #" + hologram.getId())
                    .color(ChatColor.RED)
                    .hoverText(String.join("\n", hover))
                    .runCommand("/hologram tpto " + hologram.getId())
                    .send(sender);
        }
        return true;
    }

    @Command(names = {"hologram addline"},
             permission = "ilib.holograms",
             description = "Add a line to a hologram")
    public boolean addLine(CommandSender sender,
                           @Param(name = "id") StaticHologram hologram,
                           @Param(name = "text", wildcard = true) String text) {
        if (text.equalsIgnoreCase("{empty}"))
            text = "";
        else text = CC.translate(text);

        hologram.addLines(text);
        instance.getHologramService().save();
        sender.sendMessage(CC.format("&9Added '&r%s&9' to hologram &e#%d&9.", text, hologram.getId()));
        return true;
    }

    @Command(names = {"hologram removeline"},
             permission = "ilib.holograms",
             description = "Remove a line from a hologram")
    public boolean removeLine(CommandSender sender,
                           @Param(name = "id") StaticHologram hologram,
                           @Param(name = "index") int line) {
        List<HologramLine> lines = new ArrayList<>(hologram.getCurrentLines());
        if (--line < 0 || line > lines.size() - 1) {
            sender.sendMessage(CC.format("&cInvalid index. (&e1&c-&e%d&c)", lines.size()));
            return false;
        }

        HologramLine removed = lines.remove(line);

        List<String> strings = new ArrayList<>();
        for (HologramLine hologramLine : lines) {
            strings.add(hologramLine.getText());
        }

        hologram.setLines(strings);
        instance.getHologramService().save();

        strings.clear();

        sender.sendMessage(CC.format("&9Removed '&r%s&9' from hologram &e#%d&9.",
                removed.getText(), hologram.getId()));
        return true;
    }

    @Command(names = {"hologram setline"},
             permission = "ilib.holograms",
             description = "Set the line of a hologram")
    public boolean setLine(CommandSender sender,
                           @Param(name = "id") StaticHologram hologram,
                           @Param(name = "index") int line,
                           @Param(name = "text", wildcard = true) String text) {
        if (--line < 0 || line > hologram.getCurrentLines().size() - 1) {
            sender.sendMessage(CC.format("&cInvalid index. (&e1&c-&e%d&c)", hologram.getCurrentLines().size()));
            return false;
        }

        if (text.equalsIgnoreCase("{empty}"))
            text = "";
        else text = CC.translate(text);

        hologram.setLine(line, text);
        instance.getHologramService().save();
        sender.sendMessage(CC.format("&9Set line &e%d &9to '&r%s&9' on hologram &e#%d&9.",
                line + 1, text, hologram.getId()));
        return true;
    }

    @Command(names = {"hologram insertbefore"},
             permission = "ilib.holograms",
             description = "Insert a line before another line")
    public boolean insertBefore(CommandSender sender,
                           @Param(name = "id") StaticHologram hologram,
                           @Param(name = "index") int line,
                           @Param(name = "text", wildcard = true) String text) {
        List<HologramLine> lines = new ArrayList<>(hologram.getCurrentLines());
        if (--line < 0 || line > lines.size() - 1) {
            sender.sendMessage(CC.format("&cInvalid index. (&e1&c-&e%d&c)", lines.size()));
            return false;
        }

        if (text.equalsIgnoreCase("{empty}"))
            text = "";
        else text = CC.translate(text);

        lines.add(line, new HologramLine(text));

        List<String> strings = new ArrayList<>();
        for (HologramLine hologramLine : lines) {
            strings.add(hologramLine.getText());
        }

        hologram.setLines(strings);
        instance.getHologramService().save();

        strings.clear();

        sender.sendMessage(CC.format("&9Inserted '&r%s&9' at position &e%d &9on hologram &e#%d&9.",
                text, line + 1, hologram.getId()));
        return true;
    }

    @Command(names = {"hologram insertafter"},
             permission = "ilib.holograms",
             description = "Insert a line after another line")
    public boolean insertAfter(CommandSender sender,
                                @Param(name = "id") StaticHologram hologram,
                                @Param(name = "index") int line,
                                @Param(name = "text", wildcard = true) String text) {
        List<HologramLine> lines = new ArrayList<>(hologram.getCurrentLines());
        if (--line < 0 || line > lines.size() - 1) {
            sender.sendMessage(CC.format("&cInvalid index. (&e1&c-&e%d&c)", lines.size()));
            return false;
        }

        if (text.equalsIgnoreCase("{empty}"))
            text = "";
        else text = CC.translate(text);

        lines.add(line + 1, new HologramLine(text));

        List<String> strings = new ArrayList<>();
        for (HologramLine hologramLine : lines) {
            strings.add(hologramLine.getText());
        }

        hologram.setLines(strings);
        instance.getHologramService().save();

        strings.clear();

        sender.sendMessage(CC.format("&9Inserted '&r%s&9' at position &e%d &9on hologram &e#%d&9.",
                text, line + 2, hologram.getId()));
        return true;
    }

    @Command(names = {"hologram tphere", "hologram movehere"},
             permission = "ilib.holograms",
             description = "Teleport a hologram to your position",
             playerOnly = true)
    public boolean tphere(Player sender, @Param(name = "id") StaticHologram hologram) {
        hologram.setLocation(sender.getLocation());
        instance.getHologramService().save();
        sender.sendMessage(CC.format("&9Teleported hologram &e#%d &9to your location.", hologram.getId()));
        return true;
    }

    @Command(names = {"hologram tpto"},
             permission = "ilib.holograms",
             description = "Teleport to a hologram",
             playerOnly = true)
    public boolean tpto(Player sender, @Param(name = "id") StaticHologram hologram) {
        sender.teleport(hologram.getLocation());
        sender.sendMessage(CC.format("&9Teleported to hologram &e#%d&9.", hologram.getId()));
        return true;
    }

    @Command(names = {"hologram edit"},
             permission = "ilib.holograms",
             description = "Open the hologram editor",
             playerOnly = true)
    public boolean edit(Player sender, @Param(name = "id") StaticHologram hologram) {
        List<String> lines = new ArrayList<>();

        for (HologramLine line : hologram.getCurrentLines())
            lines.add(line.getText());

        new TextEditMenu(
                lines,
                strings -> {
                    hologram.setLines(strings);
                    instance.getHologramService().save();
                },
                "Editing: " + hologram.getName()
        ).openMenu(sender);
        return true;
    }

}
