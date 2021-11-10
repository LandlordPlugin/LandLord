package biz.princeps.lib.manager;

import biz.princeps.lib.chat.ConfirmationDialog;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Action;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfirmationManager {

    private final JavaPlugin plugin;

    private STATE state;
    private int timout = 10;

    public ConfirmationManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public void setTimout(int timout) {
        this.timout = timout;
    }

    public void drawGUI(Player p, String message, Action onAccept, Action onDeny, AbstractGUI main) {
        ConfirmationGUI confirmationGUI = new ConfirmationGUI(plugin, p, message, onAccept, onDeny, main);
        confirmationGUI.display();
    }

    public void drawChat(Player p, String message, Action onAccept, Action onDeny, String confirmCommand, int timout) {
        ConfirmationDialog cd = new ConfirmationDialog(message, confirmCommand, onAccept, onDeny, timout);
        cd.display(p);
    }

    public void draw(Player p, String guiMessage, String chatMessage, Action onAccept, Action onDeny, String confirmCommand, int timout, AbstractGUI main) {
        switch (state) {

            case GUI:
                drawGUI(p, guiMessage, onAccept, onDeny, main);
                break;
            case CHAT:
                drawChat(p, chatMessage, onAccept, onDeny, confirmCommand, timout);
                break;
        }
    }

    public void draw(Player p, String guiMessage, String chatMessage, Action onAccept, Action onDeny, String confirmCommand, AbstractGUI main) {
        this.draw(p, guiMessage, chatMessage, onAccept, onDeny, confirmCommand, timout, main);
    }

    public void draw(Player p, String guiMessage, String chatMessage, Action onAccept, Action onDeny, String confirmCommand) {
        this.draw(p, guiMessage, chatMessage, onAccept, onDeny, confirmCommand, timout, null);
    }

    public enum STATE {GUI, CHAT}
}

