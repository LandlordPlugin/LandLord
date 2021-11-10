package biz.princeps.landlord.util;

import biz.princeps.landlord.api.ILandLord;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleScoreboard {

    private final ILandLord plugin;
    private Scoreboard scoreboard;

    private final String title;
    private final List<String> scores;
    private final Player player;
    private BukkitRunnable runnable;

    /**
     * Creates a new scoreboard with a specific title for one player
     *
     * @param title the title to be displayed
     * @param p     the player the scoreboard should be displayed for
     */
    public SimpleScoreboard(ILandLord plugin, String title, Player p) {
        Objects.requireNonNull(p);
        this.plugin = plugin;
        this.scoreboard = plugin.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
        this.title = title;
        this.scores = new ArrayList<>();
        this.player = p;
    }

    /**
     * Adds a line of text to the bottom of the scoreboard
     *
     * @param text the text
     */
    public void add(String text) {
        Preconditions.checkArgument(text.length() < 48, "text cannot be over 48 characters in length");
        add(text, scores.size());
    }

    /**
     * Replaces or inserts text into the line number given with score
     *
     * @param text  the text
     * @param score the line number
     */
    public void add(String text, Integer score) {
        Preconditions.checkArgument(text.length() < 48, "text cannot be over 48 characters in length");
        //text = fixDuplicates(text);
        scores.add(score, text);
    }

    private String fixDuplicates(String text) {
        while (scores.contains(text))
            text += "§r";
        if (text.length() > 48)
            text = text.substring(0, 47);
        return text;
    }

    /**
     * Builds the scoreboard (create the objectives...)
     */
    private void build() {
        String s = (title.length() > 16 ? title.substring(0, 15) : title);
        Objective obj = scoreboard.getObjective(s);
        if (obj == null) {
            obj = scoreboard.registerNewObjective(s, "dummy");
        }
        obj.setDisplayName(title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < scores.size(); i++) {
            obj.getScore(scores.get(scores.size() - 1 - i)).setScore(i);
        }
    }

    /**
     * Resets the current scoreboard.
     * 1) Clear the current score list
     * 2) Display an empty scoreboard to the player
     */
    public void reset() {
        scores.clear();
        this.scoreboard = plugin.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
        player.setScoreboard(this.scoreboard);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * Builds and sets this scoreboard to the player
     */
    public void send() {
        build();
        player.setScoreboard(scoreboard);
    }

    /**
     * Schedules an update task which regularly updates the the scoreboard with a given runnable
     *
     * @param plugin plugin reference
     * @param run    runnable
     * @param delay  ~ delay
     * @param timer  the repeating time
     */
    public void scheduleUpdate(JavaPlugin plugin, Runnable run, long delay, long timer) {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                run.run();
            }
        };
        this.runnable.runTaskTimer(plugin, delay, timer);
    }

    /**
     * Deactivates this scoreboard
     * 1) Stops a probable update Task
     * 2) Sets an empty scoreboard to the player
     */
    //TODO here may be some logic to restore the old scoreboard for featherboard. But i dunno, never got to check this out
    public void deactivate() {
        if (runnable != null)
            this.runnable.cancel();
        this.scoreboard = plugin.getPlugin().getServer().getScoreboardManager().getNewScoreboard();
        player.setScoreboard(this.scoreboard);

    }
}