package cc.invictusgames.ilib.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 08.06.2020 / 16:38
 * iLib / cc.invictusgames.ilib.command.annotation
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandCooldown {

    int time();

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    String bypassPermission() default "";

    boolean global() default false;

}
