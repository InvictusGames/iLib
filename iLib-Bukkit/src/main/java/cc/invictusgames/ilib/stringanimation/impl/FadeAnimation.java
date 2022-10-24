package cc.invictusgames.ilib.stringanimation.impl;

import cc.invictusgames.ilib.reboot.RebootService;
import cc.invictusgames.ilib.stringanimation.AnimationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FadeAnimation implements AnimationType {

    private final String[] strings;
    private int index = 0;

    public FadeAnimation(String text, String fromColor, String toColor, boolean reversed) {
        List<String> list = new ArrayList<>();

        list.add(fromColor + text);
        for (int i = 0; i < text.length(); i++) {
            StringBuilder line = new StringBuilder();

            line.append(toColor);
            boolean colorAppended = false;
            for (int j = 0; j < text.length(); j++) {
                if (j > i && !colorAppended) {
                    line.append(fromColor);
                    colorAppended = true;
                }

                char c = text.charAt(j);
                line.append(c);
            }

            list.add(line.toString());
        }

        if (reversed)
            Collections.reverse(list);

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
