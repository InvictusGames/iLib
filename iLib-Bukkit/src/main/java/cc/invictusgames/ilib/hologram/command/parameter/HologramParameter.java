package cc.invictusgames.ilib.hologram.command.parameter;

import cc.invictusgames.ilib.ILibBukkit;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.hologram.statics.StaticHologram;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class HologramParameter implements ParameterType<StaticHologram> {

    private final ILibBukkit instance;

    @Override
    public StaticHologram parse(CommandSender sender, String source) {
        StaticHologram hologram = instance.getHologramService().getHologram(source);

        if (hologram != null)
            return hologram;

        Integer id = CommandService.getParameter(Integer.class).parse(sender, source);
        if (id == null) {
            sender.sendMessage(CC.format("&cHologram with name or id &e%s &cnot found.", source));
            return null;
        }

        hologram = instance.getHologramService().getHologram(id);
        if (hologram == null)
            sender.sendMessage(CC.format("&cHologram with name or id &e%s &cnot found.", source));

        return hologram;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (StaticHologram hologram : instance.getHologramService().getSerializedHolograms()) {
            if (hologram.getName() != null)
                completions.add(hologram.getName());
            else completions.add(String.valueOf(hologram.getId()));
        }
        return completions;
    }
}
