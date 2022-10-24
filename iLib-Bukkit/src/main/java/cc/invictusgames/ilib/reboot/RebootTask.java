package cc.invictusgames.ilib.reboot;

import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
public class RebootTask extends BukkitRunnable {

    private static final List<? extends Number> ANNOUNCE_TIMES = Arrays.asList(
            5 * 60,
            4 * 60,
            3 * 60,
            2 * 60,
            60,
            30,
            15,
            10,
            5
    );

    private int secondsRemaining;

    @Override
    public void run() {
        if (ANNOUNCE_TIMES.contains(secondsRemaining)) {
            Bukkit.broadcastMessage(RebootService.CHAT_BAR);
            Bukkit.broadcastMessage(CC.RED + "Server rebooting in "
                    + TimeUtils.formatDetailed(secondsRemaining, TimeUnit.SECONDS) + ".");
            Bukkit.broadcastMessage(RebootService.CHAT_BAR);
        }

        if (secondsRemaining == 0) {
            cancel();
            Bukkit.getServer().shutdown();
            return;
        }

        --secondsRemaining;
    }


}
