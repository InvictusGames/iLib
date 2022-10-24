package cc.invictusgames.ilib.redis;

import redis.clients.jedis.Jedis;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 12.02.2020 / 22:11
 * iLib / cc.invictusgames.ilib.redis
 */

public interface RedisCommand<T> {

    public T execute(Jedis redis);

}
