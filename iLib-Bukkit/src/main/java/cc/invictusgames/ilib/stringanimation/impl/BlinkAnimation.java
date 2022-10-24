package cc.invictusgames.ilib.stringanimation.impl;

import cc.invictusgames.ilib.stringanimation.AnimationType;

import java.util.ArrayList;
import java.util.List;

public class BlinkAnimation implements AnimationType {

    private final String[] strings;
    private int index = 0;

    public BlinkAnimation(String text, String fromColor, String toColor, int times, int internalDelay) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < times; i++) {
            for (int j = 0; j < internalDelay; j++)
                list.add(fromColor + text);

            if (i < times - 1) {
                for (int j = 0; j < internalDelay; j++)
                    list.add(toColor + text);
            }
        }

        strings = list.toArray(new String[0]);
    }

    @Override
    public String next() {
        return strings[index++];
    }

    @Override
    public boolean done() {
        return index >= strings.length;
    }

    @Override
    public void reset() {
        index = 0;
    }
}
