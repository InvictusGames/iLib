package cc.invictusgames.ilib.border;

import cc.invictusgames.ilib.cuboid.Cuboid;
import cc.invictusgames.ilib.cuboid.CuboidDirection;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 12.07.2020 / 08:18
 * iLib / cc.invictusgames.ilib.border
 */

@Getter
public class Border {

    private static final Map<World, Border> borderMap = new HashMap<>();

    private final Location origin;
    private Material material;
    private int size;
    private int height;
    private Cuboid cuboid;

    public Border(Location origin, Material material, int size, int height) {
        this.origin = origin;
        this.material = material;
        this.size = size;
        this.height = height;
        this.cuboid = new Cuboid(
                origin.clone().add(size + 1D, origin.getWorld().getMaxHeight() - origin.getY(), size + 1D),
                origin.clone().subtract(size + 1D, origin.getY(), size + 1D)
        );
        borderMap.put(origin.getWorld(), this);
    }

    public Cuboid shrink(int amount) {
        size -= amount;
        Cuboid previous = cuboid.clone();
        cuboid = cuboid.inset(CuboidDirection.HORIZONTAL, amount);
        return previous;
    }

    public Cuboid expand(int amount) {
        size += amount;
        Cuboid previous = cuboid.clone();
        cuboid = cuboid.outset(CuboidDirection.HORIZONTAL, amount);
        return previous;
    }

    public boolean contains(Location location) {
        return cuboid.contains(location);
    }

    public boolean contains(Entity entity) {
        return cuboid.contains(entity);
    }

    public boolean contains(Block block) {
        return cuboid.contains(block);
    }

    public static Border getBorder(World world) {
        return borderMap.get(world);
    }

    public static Border getBorder(Location location) {
        return borderMap.get(location.getWorld());
    }

}
