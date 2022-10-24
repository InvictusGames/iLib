package cc.invictusgames.ilib;

import cc.invictusgames.ilib.config.MainConfig;
import cc.invictusgames.ilib.configuration.ConfigurationService;
import cc.invictusgames.ilib.configuration.JsonConfigurationService;
import cc.invictusgames.ilib.redis.RedisService;
import cc.invictusgames.ilib.task.impl.AsynchronousTaskChain;
import cc.invictusgames.ilib.uuid.UUIDCache;
import lombok.Getter;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 25.10.2020 / 04:15
 * iLib / cc.invictusgames.ilib
 */

public abstract class ILib {

    public static final AsynchronousTaskChain TASK_CHAIN = new AsynchronousTaskChain(true);

    @Getter private static ILib instance;

    @Getter private final Logger logger;
    @Getter private final ConfigurationService configurationService;
    @Getter private final MainConfig mainConfig;
    @Getter private final RedisService redisService;
    @Getter private final UUIDCache uuidCache;

    public ILib(Logger logger, File dataFolder) {
        if (ILib.instance != null)
            throw new IllegalStateException("Already Initialized");

        ILib.instance = this;

        this.logger = logger;
        this.configurationService = new JsonConfigurationService();
        this.mainConfig = configurationService.loadConfiguration(MainConfig.class, new File(dataFolder, "config.json"));
        this.redisService = new RedisService("ilib", mainConfig.getRedisConfig());
        redisService.subscribe();
        this.uuidCache = new UUIDCache(redisService);

        init();
    }

    public abstract void init();

    public abstract void runTask(Runnable runnable);

    public abstract void runTaskAsync(Runnable runnable);

}
