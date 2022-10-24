package cc.invictusgames.ilib.border;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.utils.callback.Callable;
import cc.invictusgames.ilib.utils.callback.TypeCallable;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 12.07.2020 / 09:06
 * iLib / cc.invictusgames.ilib.border
 */

@RequiredArgsConstructor
public class BorderFiller {

    private static final List<Material> AIR_BLOCKS;

    private final Border border;

    private final Queue<Location> southWall = new ArrayDeque<>();
    private final Queue<Location> westWall = new ArrayDeque<>();
    private final Queue<Location> northWall = new ArrayDeque<>();
    private final Queue<Location> eastWall = new ArrayDeque<>();

    public void loadLocations(Callable callable) {
        southWall.clear();
        westWall.clear();
        northWall.clear();
        eastWall.clear();

        ILib.getInstance().runTaskAsync(() -> {
            int originX = border.getOrigin().getBlockX();
            int originZ = border.getOrigin().getBlockZ();

            int posX = originX + border.getSize();
            int negX = originX - border.getSize();

            int posZ = originZ + border.getSize();
            int negZ = originZ - border.getSize();

            loop(posX, negX, posZ, false, southWall);
            loop(posZ, negZ, negX, true, westWall);
            loop(posX, negX, negZ, false, northWall);
            loop(posZ, negZ, posX, true, eastWall);

            callable.callback();
        });
    }

    /**
     * @param posCoord  the positive corner coordinate
     * @param negCoord  the negative corner coordinate
     * @param wallCoord the coordinate the wall is on
     * @param b         if the wall coordinate is an x or z coordinate (true=x, false=z)
     * @param queue     the queue to add the locations in
     */
    private void loop(int posCoord, int negCoord, int wallCoord, boolean b, Queue<Location> queue) {
        World world = border.getCuboid().getWorld();
        for (int i = posCoord; i >= negCoord; i--) {
            int min = world.getHighestBlockYAt(b ? wallCoord : i, b ? i : wallCoord);
            int max = min + border.getHeight();

            for (int y = min; y < max && y < world.getMaxHeight(); y++) {
                Location location = new Location(world, b ? wallCoord : i, y, b ? i : wallCoord);
                queue.add(location);
            }
        }
    }

    public void fillLocations(int blocksPerTick, TypeCallable<Long> callable) {
        if (blocksPerTick < 1) {
            throw new IllegalArgumentException("blocksPerTick must be positive (" + blocksPerTick + ")");
        }

        long totalBlocks = southWall.size() + westWall.size() + northWall.size() + eastWall.size();
        AtomicBoolean done = new AtomicBoolean(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < blocksPerTick; i++) {
                    Arrays.asList(southWall, westWall, northWall, eastWall).forEach(queue -> {
                        if (!queue.isEmpty()) {
                            queue.poll().getBlock().setTypeIdAndData(border.getMaterial().getId(), (byte) 0, false);
                            done.set(false);
                        }
                    });

                    if (done.get()) {
                        cancel();
                        callable.callback(totalBlocks);
                        return;
                    }
                }
            }
        }.runTaskTimer(ILibBukkitPlugin.getInstance(), 0L, 1L);
    }

    static {
        AIR_BLOCKS = new ArrayList<>(Arrays.asList(
                Material.AIR,
                Material.STATIONARY_WATER,
                Material.WATER,
                Material.STATIONARY_LAVA,
                Material.LAVA,
                Material.LOG,
                Material.LOG_2,
                Material.LEAVES,
                Material.LEAVES_2,
                Material.HUGE_MUSHROOM_1,
                Material.HUGE_MUSHROOM_2
        ));
        AIR_BLOCKS.addAll(Arrays.stream(Material.values())
                .filter(material -> !material.isSolid() || material.isTransparent())
                .collect(Collectors.toList()));
    }
}
