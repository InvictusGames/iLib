package cc.invictusgames.ilib.scoreboard;

import cc.invictusgames.ilib.scoreboard.nametag.NameTag;
import cc.invictusgames.ilib.scoreboard.packet.ScoreboardTeamPacketMod;
import cc.invictusgames.ilib.utils.CC;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.jafama.FastMath;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 21.10.2020 / 07:49
 * iLib / cc.invictusgames.ilib.scoreboard
 */

public class PlayerScoreboard {

    private static final StringBuilder SEPARATE_SCORE_BUILDER = new StringBuilder();

    private final UUID uuid;
    private final ScoreboardService service;
    private final Map<String, Integer> displayedScores = new HashMap<>();
    private final Map<String, String> scorePrefixes = new HashMap<>();
    private final Map<String, String> scoreSuffixes = new HashMap<>();
    private final Map<String, NameTag> knownNameTags = new HashMap<>();
    private final Set<String> sentTeamCreates = new HashSet<>();
    private final List<String> usedScores = new ArrayList<>();
    private final List<String> separateScores = new ArrayList<>();
    private final String[] prefixScoreSuffix = new String[3];
    private final Objective sidebar;
    private final Objective health;

    @Getter
    private boolean running;
    private long lastNameTagUpdate;

    @Getter
    private boolean visible = true;

    public PlayerScoreboard(ScoreboardService service, UUID uuid) {
        this.uuid = uuid;
        this.service = service;

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        sidebar = board.registerNewObjective("sidebar", "sidebar");

        String title = CC.translate(service.getAdapter().getTitle(toPlayer()));
        if (title.length() > 32) {
            service.getLogger().warning(String.format("Title on board of %s is too long! (%d)",
                    toPlayer().getName(), title.length()));
            title = title.substring(0, 32);
        }
        sidebar.setDisplayName(title);
        sidebar.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);

        health = board.registerNewObjective("health", "health");
        health.setDisplayName(CC.HEART);
        health.setDisplaySlot(service.getAdapter().showHealth(toPlayer()) ? DisplaySlot.BELOW_NAME : null);
        toPlayer().setScoreboard(board);
    }

    public void update() {
        running = true;

        if (System.currentTimeMillis() - lastNameTagUpdate > TimeUnit.SECONDS.toMillis(1))
            updateNameTags(false);

        String title = CC.translate(service.getAdapter().getTitle(toPlayer()));
        if (title.length() > 32) {
            service.getLogger().warning(String.format("Title on board of %s is too long! Input: %s",
                    toPlayer().getName(), title));
            title = title.substring(0, 32);
        }

        List<String> lines = service.getAdapter().getLines(toPlayer());
        while (lines.size() > 16)
            lines.remove(lines.size() - 1);

        boolean currentHealth = health.getDisplaySlot() != null;
        boolean showHealth = service.getAdapter().showHealth(toPlayer());
        if (showHealth != currentHealth)
            health.setDisplaySlot(showHealth ? DisplaySlot.BELOW_NAME : null);

        if (!sidebar.getDisplayName().equals(title))
            sidebar.setDisplayName(title);

        usedScores.clear();

        int position = lines.size();
        List<String> added = new ArrayList<>();

        for (String line : lines) {
            if (48 <= line.length()) {
                service.getLogger().warning(String.format("Line on board of %s is too long! Line: %s",
                        toPlayer().getName(), line));
                line = line.substring(0, 48);
            }

            String[] separated = this.separate(line, usedScores);
            String prefix = separated[0];
            String score = separated[1];
            String suffix = separated[2];
            added.add(score);

            if (!this.sentTeamCreates.contains(score)) {
                this.createAndAddMember(score);
            }
            if (!this.displayedScores.containsKey(score)
                    || this.displayedScores.get(score) != position) {
                this.setScore(score, position);
            }
            if (!this.scorePrefixes.containsKey(score)
                    || !this.scorePrefixes.get(score).equals(prefix) || !this.scoreSuffixes.get(score).equals(suffix)) {
                this.updateScore(score, prefix, suffix);
            }
            --position;
        }

        for (String displayedScore : ImmutableSet.copyOf(this.displayedScores.keySet())) {
            if (added.contains(displayedScore))
                continue;
            this.removeScore(displayedScore);
        }

        running = false;
    }


    public void updateNameTags(boolean force) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            NameTag nameTag = ScoreboardService.getNameTag(toPlayer(), target);
            String nameTagKey = "§§" + nameTag.getName().substring(0, FastMath.min(nameTag.getName().length(), 14));

            boolean createNew = !knownNameTags.containsKey(nameTagKey);
            if (!createNew) {
                NameTag currentNameTag = knownNameTags.get(nameTagKey);
                if (!currentNameTag.getPrefix().equals(nameTag.getPrefix())
                        || !currentNameTag.getSuffix().equals(nameTag.getSuffix())) {
                    new ScoreboardTeamPacketMod(nameTagKey, "", "", new ArrayList<>(), 1);
                    createNew = true;
                }
            }

            if (createNew)
                new ScoreboardTeamPacketMod(nameTagKey, nameTag.getPrefix(),
                        nameTag.getSuffix(), new ArrayList<>(), 0).sendToPlayer(toPlayer());

            new ScoreboardTeamPacketMod(nameTagKey, Collections.singletonList(target.getName()), 3)
                    .sendToPlayer(toPlayer());
            knownNameTags.put(nameTagKey, nameTag);
        }
        lastNameTagUpdate = System.currentTimeMillis();
    }

    private void setField(Packet packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndAddMember(String scoreTitle) {
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(scoreTitle, "_", "_", ImmutableList.of(), 0);
        ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(scoreTitle, ImmutableList.of(scoreTitle), 3);
        scoreboardTeamAdd.sendToPlayer(this.toPlayer());
        scoreboardTeamAddMember.sendToPlayer(this.toPlayer());
        this.sentTeamCreates.add(scoreTitle);
    }


    private void setScore(String score, int value) {
        PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();
        this.setField(scoreboardScorePacket, "a", score);
        this.setField(scoreboardScorePacket, "b", this.sidebar.getName());
        this.setField(scoreboardScorePacket, "c", value);
//        this.setField(scoreboardScorePacket, "d", 0);
        this.setField(scoreboardScorePacket, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
        this.displayedScores.put(score, value);
        ((CraftPlayer) this.toPlayer()).getHandle().playerConnection.sendPacket(scoreboardScorePacket);
    }

    private void removeScore(String score) {
        this.displayedScores.remove(score);
        this.scorePrefixes.remove(score);
        this.scoreSuffixes.remove(score);
        ((CraftPlayer) this.toPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutScoreboardScore(score));
    }

    private void updateScore(String score, String prefix, String suffix) {
        this.scorePrefixes.put(score, prefix);
        this.scoreSuffixes.put(score, suffix);
        (new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2)).sendToPlayer(this.toPlayer());
    }


    //Stolen from FrozenOrb (https://frozenorb.net) TriHard
    private String[] separate(String line, final Collection<String> usedBaseScores) {
        line = ChatColor.translateAlternateColorCodes('&', line);
        String prefix = "";
        String score = "";
        String suffix = "";
        this.separateScores.clear();
        SEPARATE_SCORE_BUILDER.setLength(0);
        for (int i = 0; i < line.length(); ++i) {
            final char c = line.charAt(i);
            if (c == '*' || (SEPARATE_SCORE_BUILDER.length() == 16 && this.separateScores.size() < 3)) {
                this.separateScores.add(SEPARATE_SCORE_BUILDER.toString());
                SEPARATE_SCORE_BUILDER.setLength(0);
                if (c == '*') {
                    continue;
                }
            }
            SEPARATE_SCORE_BUILDER.append(c);
        }
        this.separateScores.add(SEPARATE_SCORE_BUILDER.toString());
        switch (this.separateScores.size()) {
            case 1: {
                score = this.separateScores.get(0);
                break;
            }
            case 2: {
                score = this.separateScores.get(0);
                suffix = this.separateScores.get(1);
                break;
            }
            case 3: {
                prefix = this.separateScores.get(0);
                score = this.separateScores.get(1);
                suffix = this.separateScores.get(2);
                break;
            }
            default: {
                service.getLogger().warning("Failed to separate scoreboard line. Input: " + line);
                break;
            }
        }
        if (usedBaseScores.contains(score)) {
            if (score.length() <= 14) {
                for (final ChatColor chatColor : ChatColor.values()) {
                    final String possibleScore = chatColor + score;
                    if (!usedBaseScores.contains(possibleScore)) {
                        score = possibleScore;
                        break;
                    }
                }

                /*if (usedBaseScores.contains(score))
                    service.getLogger().warning("Failed to find alternate color code for: " + score);*/
            } /*else
                service.getLogger().warning("Found a scoreboard base collision to shift: " + score);*/
        }

        if (prefix.length() > 16)
            prefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";

        if (score.length() > 16)
            score = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";

        if (suffix.length() > 16)
            suffix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + ">16";

        usedBaseScores.add(score);
        this.prefixScoreSuffix[0] = prefix;
        this.prefixScoreSuffix[1] = score;
        this.prefixScoreSuffix[2] = suffix;
        return this.prefixScoreSuffix;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        sidebar.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }

    public List<String> getDebugs() {
        List<String> lines = service.getAdapter().getLines(toPlayer());
        List<String> used = new ArrayList<>();
        List<String> debugs = new ArrayList<>();
        for (String line : lines) {
            String[] separate = separate(line, used);
            debugs.add(String.join(ChatColor.WHITE + ",", separate));
        }

        return debugs;
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}