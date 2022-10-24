package cc.invictusgames.ilib.utils.callback;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.03.2020 / 17:43
 * iLib / cc.invictusgames.ilib.utils.callback
 */

public interface ReturnCallable<T, T1> {

    T1 callback(T t);
}
