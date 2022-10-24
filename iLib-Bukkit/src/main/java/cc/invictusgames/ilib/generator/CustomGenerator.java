package cc.invictusgames.ilib.generator;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldGenerator;

import java.util.Random;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 16.07.2020 / 11:19
 * iLib / cc.invictusgames.ilib.generator
 */

public class CustomGenerator extends WorldGenerator {

    @Override
    public boolean generate(World world, Random random, BlockPosition blockPosition) {
        return false;
    }
}
