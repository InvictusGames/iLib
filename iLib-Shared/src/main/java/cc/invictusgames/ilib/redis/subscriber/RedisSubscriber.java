package cc.invictusgames.ilib.redis.subscriber;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 14.03.2021 / 12:30
 * iLib / cc.invictusgames.ilib.redis.subscriber
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisSubscriber {

    String[] channels();

}
