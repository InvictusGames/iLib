package cc.invictusgames.ilib.stringanimation.impl;

import cc.invictusgames.ilib.stringanimation.AnimationType;

import java.util.ArrayList;
import java.util.List;

public class StaticAnimation implements AnimationType {

    private final String[] strings;
    private int index = 0;

    public StaticAnimation(String text, int internalDelay) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < internalDelay; i++)
            list.add(text);

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
