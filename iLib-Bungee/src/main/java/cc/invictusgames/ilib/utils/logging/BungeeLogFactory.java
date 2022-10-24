package cc.invictusgames.ilib.utils.logging;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.03.2021 / 06:57
 * iLib / cc.invictusgames.ilib.utils.logging
 */

@RequiredArgsConstructor
public class BungeeLogFactory implements LogFactory {

    private final Plugin parent;

    @Override
    public Logger newLogger(String name) {
        return SimpleBungeeLogger.getLogger(parent, name);
    }
}
