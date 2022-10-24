package cc.invictusgames.ilib.hologram;

import lombok.Data;

@Data
public class HologramLine {

    private String text;
    private int horseId = 0;
    private int witherId = 0;
    private int armorStandId = 0;

    public HologramLine(String text) {
        this.text = text;
    }

    public void setText(String text) {
        horseId = 0;
        witherId = 0;
        armorStandId = 0;
        this.text = text;
    }

    public void setTextSilent(String text) {
        this.text = text;
    }
}
