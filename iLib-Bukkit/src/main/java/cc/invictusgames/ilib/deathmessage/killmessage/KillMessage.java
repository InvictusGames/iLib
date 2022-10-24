package cc.invictusgames.ilib.deathmessage.killmessage;

import cc.invictusgames.ilib.deathmessage.damage.Damage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public enum KillMessage {

    DEFAULT("Default") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was slain by " + damager;
        }

        @Override
        public boolean canAccess(Player player) {
            return true;
        }
    },

    SMOKED("Smoked") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was smoked by " + damager;
        }
    },

    ROCKED("Rocked") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was rocked by " + damager;
        }
    },

    QUICKDROPPED("Quickdropped") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was quickdropped by " + damager;
        }
    },

    QUICKIED("Quickied") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was quickied by " + damager;
        }
    },

    PACKED("Packed") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was packed by " + damager;
        }
    },

    BOMBED("Bombed") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was bombed by " + damager;
        }
    },

    BANISHED("Banished") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was banished by " + damager;
        }
    },

    COMBOED("Comboed") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was comboed by " + damager;
        }
    },

    KNOCKED_OUT("Knocked-out") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was knocked-out by " + damager;
        }
    },

    DROPPED("Dropped") {
        @Override
        public String formatPvP(String damaged, String damager) {
            return damaged + Damage.getColor() + " was dropped by " + damager;
        }
    };

    private final String display;

    public abstract String formatPvP(String damaged, String damager);

    public String getPermission() {
        return "ilib.killmessage." + name().toLowerCase();
    }

    public boolean canAccess(Player player) {
        return player.hasPermission(getPermission());
    }
}
