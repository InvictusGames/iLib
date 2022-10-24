package cc.invictusgames.ilib.task.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChainCallback {

    private Runnable apply = () -> System.out.println("Apply runnable wasn't set"),
            callback = () -> System.out.println("Callback runnable wasn't set");

    public ChainCallback apply(Runnable runnable) {
        this.apply = runnable;
        return this;
    }

    public ChainCallback thenCallback(Runnable runnable) {
        this.callback = runnable;
        return this;
    }

    public List<Runnable> getRunnables() {
        return new ArrayList<>(Arrays.asList(this.apply, this.callback));
    }
}
