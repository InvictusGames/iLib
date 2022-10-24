package cc.invictusgames.ilib.timebased;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TimeBasedContainer<E> {

    private final long expireTime;
    private final Map<E, Long> timeStamps;

    public TimeBasedContainer(long time, TimeUnit timeUnit) {
        this(time, timeUnit, new ConcurrentHashMap<>());
    }

    public TimeBasedContainer(long time, TimeUnit timeUnit, Map<E, Long> timeStamps) {
        this.expireTime = timeUnit.toMillis(time);

        if (!timeStamps.isEmpty())
            throw new IllegalStateException("Time stamps map must be empty");

        this.timeStamps = timeStamps;
    }

    public boolean contains(E e) {
        validate();
        return timeStamps.containsKey(e);
    }

    public void add(E e) {
        validate();
       timeStamps.put(e, System.currentTimeMillis());
    }

    public void remove(E e) {
        validate();
        timeStamps.remove(e);
    }

    public long getExpiryTime(E e) {
        validate();
        return timeStamps.containsKey(e) ? timeStamps.get(e) + expireTime : -1;
    }

    private void validate() {
        timeStamps.entrySet().removeIf(entry -> System.currentTimeMillis() > entry.getValue() + expireTime);
    }

}
