package cc.invictusgames.ilib.tab;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class TabEntry {

    private final String text;
    private int ping = 0;
    private String texture = PlayerTab.BLANK_SKIN_TEXTURE;
    private String signature = PlayerTab.BLANK_SKIN_SIGNATURE;

    public TabEntry setPing(int ping) {
        this.ping = ping;
        return this;
    }

    public TabEntry setTexture(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;
        return this;
    }

    public TabEntry adaptPlayer(Player player) {
        this.ping = ((CraftPlayer) player).getHandle().ping;

        PropertyMap properties = ((CraftPlayer) player).getProfile().getProperties();
        if (properties.size() < 1)
            return null;

        Property texture = properties.get("textures").toArray(new Property[0])[0];
        this.texture = texture.getValue();
        this.signature = texture.getSignature();
        return this;
    }

}
