package cc.invictusgames.ilib.hologram.updating;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.hologram.Hologram;
import cc.invictusgames.ilib.hologram.HologramLine;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class UpdatingHologram extends Hologram {

    private final long interval;
    private final HologramProvider provider;
    private BukkitRunnable updateTask = null;

    protected UpdatingHologram(UpdatingHologramBuilder builder) {
        super(builder);
        if (builder.getInterval() <= 0)
            throw new IllegalArgumentException("Please provide a update interval using UpdatingHologramBuilder#interval");
        if (builder.getProvider() == null)
            throw new IllegalArgumentException("Please provide a provider using UpdatingHologramBuilder#provider");
        this.interval = builder.getInterval();
        this.provider = builder.getProvider();
    }

    public void start() {
        if (updateTask != null)
            return;

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        };
        updateTask.runTaskTimerAsynchronously(ILibBukkitPlugin.getInstance(), 0L, interval);
    }

    public void cancel() {
        if (updateTask == null)
            return;

        updateTask.cancel();
        updateTask = null;
    }

    @Override
    public List<HologramLine> getLines(Player player) {
        List<HologramLine> lines = new ArrayList<>();
        for (String line : provider.getRawLines(player))
            lines.add(new HologramLine(line));
        return lines;
    }
}
