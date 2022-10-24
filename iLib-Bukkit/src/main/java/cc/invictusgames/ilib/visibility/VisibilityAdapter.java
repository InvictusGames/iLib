package cc.invictusgames.ilib.visibility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 24.10.2020 / 06:44
 * iLib / cc.invictusgames.ilib.visibility
 */

@AllArgsConstructor
@Getter
public abstract class VisibilityAdapter {

    public static final VisibilityAdapter DEFAULT = new VisibilityAdapter("Default Adapter", 0) {
        @Override
        public VisibilityAction canSee(Player player, Player target) {
            return VisibilityAction.SHOW;
        }
    };

    private final String name;
    private final int priority;

    public abstract VisibilityAction canSee(Player player, Player target);
}
