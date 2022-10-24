package cc.invictusgames.ilib.npc.clickhandler;

import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.npc.NPC;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class OpenMenuClickHandler implements NPCClickHandler {

    private final Menu menu;

    @Override
    public void click(NPC npc, Player player) {
        menu.openMenu(player);
    }
}
