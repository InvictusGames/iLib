package cc.invictusgames.ilib.redis;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.configuration.defaults.RedisConfig;
import cc.invictusgames.ilib.redis.packet.Packet;
import cc.invictusgames.ilib.redis.packet.PacketPubSub;
import cc.invictusgames.ilib.redis.subscriber.ListenerPubSub;
import cc.invictusgames.ilib.redis.subscriber.RedisListener;
import cc.invictusgames.ilib.redis.subscriber.RedisSubscriber;
import cc.invictusgames.ilib.utils.Statics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 31.12.2019 / 03:20
 * iLib / cc.invictusgames.ilib.redis
 */

public class RedisService {

    @Getter
    private static final List<RedisService> services = new ArrayList<>();
    @Getter
    private static long lastExecution = -1;
    @Getter
    private static long lastPacket = -1;
    @Getter
    private static String lastPacketName = "N/A";
    @Getter
    private static long lastError = -1;
    @Getter
    private static boolean down = false;

    private static RedisConfig backendConfig = null;
    private static JedisPool backendPool = null;

    private final String channel;
    private final JedisPool pool;

    private Jedis subscribeClient;
    private Thread subscribeThread;

    private final RedisConfig config;
    private boolean subscribed = false;

    public RedisService(String channel, RedisConfig config) {
        this.channel = channel;
        this.config = config;

        if (backendPool == null) {
            backendConfig = ILib.getInstance().getMainConfig().getRedisConfig();
            if (backendConfig.getDbId() != 0)
                throw new IllegalArgumentException("dbId of backend redis connection (ILib config) must be 0 ("
                        + backendConfig.getDbId() + ")");
            backendPool = new JedisPool(backendConfig.getHost(), backendConfig.getPort());
        }

        this.pool = new JedisPool(config.getHost(), config.getPort());
        services.add(this);
    }


    public void publish(Packet packet) {
        this.executeCommand(redis -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("packet", packet.getClass().getName());
            jsonObject.addProperty("data", Statics.PLAIN_GSON.toJson(packet));
            lastPacketName = packet.getClass().getSimpleName();
            redis.publish(this.channel, jsonObject.toString());
            return null;
        }, true, pool, config);
    }

    public void publish(String channel, String message) {
        executeCommand(redis -> {
            redis.publish(channel, message);
            return null;
        });
    }

    public void publish(String channel, JsonElement json) {
        executeCommand(redis -> {
            redis.publish(channel, json.toString());
            return null;
        });
    }

    public void subscribe() {
        if (this.subscribeThread != null) {
            return;
        }

        this.subscribeThread = new Thread(() -> {
            if (subscribeClient == null) {
                subscribeClient = pool.getResource();
                if (config.isAuthEnabled())
                    subscribeClient.auth(config.getAuthPassword());
            }

            this.subscribed = true;
            JedisPubSub pubSub = new PacketPubSub();
            subscribeClient.subscribe(pubSub, this.channel);
        }, "iLib - Redis Subscriber");
        this.subscribeThread.setDaemon(true);
        this.subscribeThread.start();
    }

    public void addListener(RedisListener... listeners) {
        Arrays.stream(listeners).forEach(listener ->
                Arrays.stream(listener.getClass().getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(RedisSubscriber.class)
                                && method.getParameterCount() < 3
                                && method.getParameterCount() > 0)
                        .forEach(method -> new Thread(() -> {
                            if (subscribeClient == null) {
                                subscribeClient = pool.getResource();
                                if (config.isAuthEnabled())
                                    subscribeClient.auth(config.getAuthPassword());
                            }

                            RedisSubscriber annotation = method.getAnnotation(RedisSubscriber.class);
                            subscribeClient.subscribe(new ListenerPubSub(listener, method), annotation.channels());
                        }).start()));
    }

    public <T> T executeCommand(RedisCommand<T> command) {
        return this.executeCommand(command, false, pool, config);
    }

    public <T> T executeBackendCommand(RedisCommand<T> command) {
        return this.executeCommand(command, false, backendPool, backendConfig);
    }

    private <T> T executeCommand(RedisCommand<T> command, boolean packet, JedisPool pool, RedisConfig config) {
        if (packet)
            lastPacket = System.currentTimeMillis();
        else
            lastExecution = System.currentTimeMillis();

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            if (config.isAuthEnabled())
                jedis.auth(config.getAuthPassword());

            jedis.select(config.getDbId());
            down = false;
            return command.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            lastError = System.currentTimeMillis();
            down = true;
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
                jedis = null;
            }
        }
    }

    /*public boolean reconnect() {
        try {
            if (!this.pool.isClosed()) {
                this.pool.close();
            }

            if (subscribeClient != null)
                subscribeClient.close();

            this.pool = new JedisPool(this.config.getHost(), this.config.getPort());
            if (this.subscribed) {
                if (this.subscribeThread.isAlive()) {
                    this.subscribeThread.stop();
                }
                this.subscribeThread = null;
                this.subscribe();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/

}
