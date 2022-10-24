package cc.invictusgames.ilib.generator.populator;

import cc.invictusgames.ilib.config.generator.OreGeneratorEntry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 16.07.2020 / 12:25
 * iLib / cc.invictusgames.ilib.generator.populator
 */

public class OrePopulator extends BlockPopulator {

    private final Material material;
    private final OreGeneratorEntry entry;

    public OrePopulator(Material material, OreGeneratorEntry entry) {
        this.material = material;
        this.entry = entry;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (!entry.isEnabled()) {
            return;
        }

        for (int i = 0; i < entry.getAttempts(); i++) {
            if (random.nextInt(100) >= entry.getChance()) {
                continue;
            }

            int x = chunk.getX() * 16 + random.nextInt(16);
            int y = entry.getMinY() + random.nextInt(entry.getMaxY() - entry.getMinY());
            int z = chunk.getX() * 16 + random.nextInt(16);

            Bukkit.broadcastMessage(x + " " + y + " " + z);

            Block block = chunk.getBlock(x, y, z);
            if (!block.getType().equals(Material.STONE) || (entry.isMustTouchAir() && !isTouchingAir(block))) {
                Bukkit.broadcastMessage(block.getType().name() + " is not stone or not touching air");
                return;
            }

            boolean shouldContinue = true;
            int veinSize = 0;
            while (shouldContinue) {
                chunk.getBlock(x, y, z).setType(material);
                veinSize++;
                if (random.nextInt(100) < 40 && veinSize <= entry.getMaxSize()) {
                    int j = random.nextInt(5);
                    Bukkit.broadcastMessage("expanding vein towards " + j);
                    switch (j) {
                        case 0:
                            x++;
                        case 1:
                            y++;
                        case 2:
                            z++;
                        case 3:
                            x--;
                        case 4:
                            y--;
                        case 5:
                            z--;
                            shouldContinue = chunk.getBlock(x, y, z).getType().equals(Material.STONE);
                    }
                } else {
                    shouldContinue = false;
                    Bukkit.broadcastMessage("not expanding");
                }
            }
            Bukkit.broadcastMessage("veinSize=" + veinSize);
        }
    }

    private boolean isTouchingAir(Block block) {
        for (BlockFace face : BlockFace.values()) {
            if (block.getRelative(face).getType().equals(Material.AIR)) {
                return true;
            }
        }

        return false;
    }
}
