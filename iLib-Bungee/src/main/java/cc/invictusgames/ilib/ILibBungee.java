package cc.invictusgames.ilib;

import cc.invictusgames.ilib.uuid.UUIDCacheListener;

import java.util.concurrent.ExecutionException;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 25.10.2020 / 08:23
 * iLib / cc.invictusgames.ilib
 */

public class ILibBungee extends ILib {

    public ILibBungee(ILibBungeePlugin plugin) {
        super(plugin.getLogger(), plugin.getDataFolder());
    }

    @Override
    public void init() {
        ILibBungeePlugin.getInstance().getProxy().getPluginManager().registerListener(ILibBungeePlugin.getInstance(),
                new UUIDCacheListener(getUuidCache()));
    }

    @Override
    public void runTask(Runnable runnable) {
        runTaskAsync(runnable);
    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        try {
            ILib.TASK_CHAIN.runAsync(() -> runnable);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
