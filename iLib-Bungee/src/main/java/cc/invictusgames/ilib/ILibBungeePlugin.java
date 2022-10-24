package cc.invictusgames.ilib;

import cc.invictusgames.pluginannotation.annotation.BungeePlugin;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 25.10.2020 / 08:24
 * iLib / cc.invictusgames.ilib
 */

@BungeePlugin(name = "iLib",
              version = "${git.build.version}-${git.commit.id.abbrev}",
              gitReplacements = true)
public class ILibBungeePlugin extends Plugin {

    @Getter private static ILibBungeePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        new ILibBungee(this);
    }
}
