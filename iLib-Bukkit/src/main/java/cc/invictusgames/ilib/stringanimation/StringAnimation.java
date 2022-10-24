package cc.invictusgames.ilib.stringanimation;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StringAnimation {

    private final List<AnimationType> animations = new ArrayList<>();
    private final List<Consumer<String>> whenTicked = new ArrayList<>();

    private int animationIndex = 0;

    private BukkitRunnable runnable = null;

    public void add(AnimationType type) {
        animations.add(type);
    }

    public void whenTicked(Consumer<String> consumer) {
        whenTicked.add(consumer);
    }

    public void start(long ticks) {
        if (runnable != null)
            return;

        if (animations.isEmpty())
            return;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                AnimationType animation = animations.get(animationIndex);
                if (animation.done()) {
                    animation.reset();
                    if (++animationIndex >= animations.size())
                        animationIndex = 0;

                    animation = animations.get(animationIndex);
                }

                for (Consumer<String> consumer : whenTicked)
                    consumer.accept(animation.next());
            }
        };
        runnable.runTaskTimerAsynchronously(ILibBukkitPlugin.getInstance(), ticks, ticks);
    }

    public void cancel() {
        if (runnable == null)
            return;

        runnable.cancel();
        for (AnimationType animation : animations)
            animation.reset();
        animationIndex = 0;
    }

}
