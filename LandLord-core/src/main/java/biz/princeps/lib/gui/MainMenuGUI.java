package biz.princeps.lib.gui;

import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by spatium on 21.07.17.
 */
public class MainMenuGUI extends AbstractGUI {

    /**
     * Creates a new main menu
     *
     * @param plugin   the plugin instance
     * @param player   the player which want to see the menu
     * @param rowCount a value between 0 and 6
     * @param title    the name of the menu - ChatColor allowed!
     */
    public MainMenuGUI(JavaPlugin plugin, Player player, int rowCount, String title) {
        super(plugin, player, rowCount * 9, title);
    }


    /**
     * If you want to add some menu items, you can do that here. Make sure to that a appropriate clickaction
     *
     * @param slot The slot, in which the icon should be placed
     * @param icon the icon
     * @return itsself to add more buttons builder-pattern alike
     */
    public MainMenuGUI addButton(int slot, Icon icon) {
        this.setIcon(slot, icon);
        return this;
    }


    public String getTitle() {
        return title;
    }

    @Override
    public void create() {

    }
}
