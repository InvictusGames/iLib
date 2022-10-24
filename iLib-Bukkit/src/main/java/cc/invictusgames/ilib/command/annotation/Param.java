package cc.invictusgames.ilib.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 03.06.2020 / 10:08
 * iLib / cc.invictusgames.ilib.commandapi.parameter
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String name();

    String defaultValue() default "";

    boolean wildcard() default false;

    String[] completionFlags() default {};

}
