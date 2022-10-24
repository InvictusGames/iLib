package cc.invictusgames.ilib.task.impl;


import cc.invictusgames.ilib.task.TaskChain;
import cc.invictusgames.ilib.task.runnable.ChainCallback;

import java.util.concurrent.*;

public class AsynchronousTaskChain implements TaskChain {

    private final ExecutorService executor;

    public AsynchronousTaskChain() {
        this.executor = new ThreadPoolExecutor(2, Integer.MAX_VALUE,
                30L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    public AsynchronousTaskChain(int poolSize) {
        this.executor = new ThreadPoolExecutor(poolSize, Integer.MAX_VALUE,
                30L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    public AsynchronousTaskChain(boolean singleThreaded) {
        if (singleThreaded) {
            this.executor = Executors.newSingleThreadExecutor();
        } else {
            this.executor = new ThreadPoolExecutor(1, Integer.MAX_VALUE,
                    30L, TimeUnit.SECONDS, new SynchronousQueue<>());
        }
    }

    public void run(Runnable runnable) {
        executor.execute(runnable);
    }

    public <T> T runAsync(Callable<T> callable) throws ExecutionException, InterruptedException {
        return executor.submit(callable).get();
    }

    public void runCallback(ChainCallback chainCallback) {
        chainCallback.getRunnables().forEach(executor::execute);
    }
}
