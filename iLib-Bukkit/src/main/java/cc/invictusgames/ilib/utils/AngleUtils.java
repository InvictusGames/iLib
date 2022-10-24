package cc.invictusgames.ilib.utils;

import org.bukkit.block.BlockFace;

import java.util.EnumMap;
import java.util.Map;

/**
 * All credits for this class belong to FrozenOrb (https://frozenorb.net)
 *
 * @author Emilxyz (langgezockt@gmail.com)
 * 08.11.2020 / 12:09
 * Nitrogen / cc.invictusgames.practice
 */

public class AngleUtils {

    private static final Map<BlockFace, Integer> NOTCHES = new EnumMap<>(BlockFace.class);
    private static final BlockFace[] RADIALS = {
            BlockFace.SOUTH,
            BlockFace.SOUTH_WEST,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST,
            BlockFace.WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH,
            BlockFace.NORTH,
            BlockFace.NORTH_EAST,
            BlockFace.NORTH_EAST,
            BlockFace.WEST,
            BlockFace.WEST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH
    };

    static {
        BlockFace[] radials = {
                BlockFace.WEST,
                BlockFace.NORTH_WEST,
                BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST
        };

        for (int i = 0; i < radials.length; i++) {
            NOTCHES.put(radials[i], i);
        }
    }

    public static BlockFace pitchToFace(float pitch) {

        if (pitch >= 67.5 && pitch <= 90)
            return BlockFace.UP;

        if (pitch <= -67.5 && pitch >= -90)
            return BlockFace.DOWN;

        Debugger.debug("pitch=" + pitch);
        return null;
    }

    public static BlockFace yawToFace(float yaw) {
        yaw = (yaw < 0 ? -yaw : yaw);
        yaw = (yaw < 0 ? 0 : yaw);
        yaw = (yaw > 360 ? 360 : yaw);
        if (yaw >= 0 && yaw < 22.5 || yaw >= 337.5 && yaw < 360)
            return BlockFace.SOUTH;

        if (yaw >= 22.5 && yaw < 67.5)
            return BlockFace.SOUTH_WEST;

        if (yaw >= 67.5 && yaw < 112.5)
            return BlockFace.WEST;

        if (yaw >= 112.5 && yaw < 157.5)
            return BlockFace.NORTH_WEST;

        if (yaw >= 157.5 && yaw < 202.5)
            return BlockFace.NORTH;

        if (yaw >= 202.5 && yaw < 247.5)
            return BlockFace.NORTH_EAST;

        if (yaw >= 247.5 && yaw < 292.5)
            return BlockFace.EAST;

        if (yaw >= 292.5 && yaw < 337.5)
            return BlockFace.SOUTH_EAST;

        return null;
    }

    public static int faceToYaw(BlockFace face) {
        return wrapAngle(45 * NOTCHES.getOrDefault(face, 0));
    }

    private static int wrapAngle(int angle) {
        int wrappedAngle = angle;

        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }

        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }

        return wrappedAngle;
    }

}
