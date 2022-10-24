package cc.invictusgames.ilib.utils.logging;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.03.2021 / 06:57
 * iLib / cc.invictusgames.ilib.utils.logging
 */

@RequiredArgsConstructor
public class BukkitLogFactory implements LogFactory {

    private final Plugin parent;

    @Override
    public Logger newLogger(String name) {
        return SimpleBukkitLogger.getLogger(parent, name);
    }
}
