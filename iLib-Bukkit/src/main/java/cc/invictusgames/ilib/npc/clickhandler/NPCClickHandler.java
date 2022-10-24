package cc.invictusgames.ilib.npc.clickhandler;

import cc.invictusgames.ilib.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface NPCClickHandler {

    NPCClickHandler COMMAND = (npc, player) -> {
        if (npc.getCommand() == null)
            return;

        Bukkit.dispatchCommand(npc.isConsoleCommand() ? Bukkit.getConsoleSender() : player,
                String.format(npc.getCommand(), player.getName()));
    };

    void click(NPC npc, Player player);

}
