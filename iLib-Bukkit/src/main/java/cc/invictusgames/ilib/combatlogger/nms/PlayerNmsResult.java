package cc.invictusgames.ilib.combatlogger.nms;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 07.03.2021 / 06:36
 * iLib / cc.invictusgames.ilib.combatlogger.nms
 */

@RequiredArgsConstructor
@Getter
public class PlayerNmsResult {

    public static PlayerNmsResult get(UUID uuid, World world) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) {
            return null;
        }

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        EntityPlayer entity = new EntityPlayer(
                server,
                ((CraftWorld) world).getHandle(),
                new GameProfile(uuid, offlinePlayer.getName()),
                new PlayerInteractManager(server.getWorld()));
        Player player = entity.getBukkitEntity();

        if (player != null) {
            player.loadData();
            return new PlayerNmsResult(player, entity);
        }

        return null;
    }

    private final Player player;
    private final EntityPlayer entityPlayer;

}
