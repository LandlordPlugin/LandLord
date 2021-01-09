package biz.princeps.lib.command;

import org.bukkit.ChatColor;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 2/5/18
 * <p>
 * This enum is responsible for handling all proprietary messages I'm using in order to depropriete them :3
 * If you want to set different messages in your own plugin simply call
 * CommandMessage.FOOBAR.setMessage("FOOOOOOOO BARRRR QUAGGAN");
 * <p>
 * All messages referencing FOOBAR will be using the new String now
 */
public enum CommandMessage {

    // Initial values assigned here
    NO_PERMS("&cYou don't have the permission to execute %cmd%!");

    private String message;

    CommandMessage(String string) {
        this.message = string;
    }

    /**
     * Sets a new message to a existing constant
     *
     * @param newMsg the new message
     */
    public void setMessage(String newMsg) {
        this.message = newMsg;
    }

    /**
     * @return the message as a (formatted) bukkit string
     */
    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
