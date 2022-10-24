package cc.invictusgames.ilib.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 03.06.2020 / 10:00
 * iLib / cc.invictusgames.ilib.commandapi
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Flag {
    Pattern FLAG_PATTERN = Pattern.compile("(--?)([a-zA-Z])([\\w]*)?");

    String[] names();

    String description() default "Default Description";

    boolean defaultValue() default false;

    boolean hidden() default false;

}
