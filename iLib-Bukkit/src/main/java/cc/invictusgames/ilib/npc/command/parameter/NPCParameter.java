package cc.invictusgames.ilib.npc.command.parameter;

import cc.invictusgames.ilib.ILibBukkit;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.hologram.statics.StaticHologram;
import cc.invictusgames.ilib.npc.NPC;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class NPCParameter implements ParameterType<NPC> {

    private final ILibBukkit instance;

    @Override
    public NPC parse(CommandSender sender, String source) {
        NPC npc = instance.getNpcService().getNpc(source);

        if (npc != null)
            return npc;

        Integer id = CommandService.getParameter(Integer.class).parse(sender, source);
        if (id == null) {
            sender.sendMessage(CC.format("&cNPC with name or id &e%s &cnot found.", source));
            return null;
        }

        npc = instance.getNpcService().getNpc(id);
        if (npc == null)
            sender.sendMessage(CC.format("&cNPC with name or id &e%s &cnot found.", source));

        return npc;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (NPC npc : instance.getNpcService().getSerializedNpcs()) {
            if (npc.getName() != null)
                completions.add(npc.getName());
            else completions.add(String.valueOf(npc.getId()));
        }
        return completions;
    }
}
