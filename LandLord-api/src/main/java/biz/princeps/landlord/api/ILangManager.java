package biz.princeps.landlord.api;

import org.bukkit.entity.Player;

import java.util.List;

public interface ILangManager {

    /**
     * Reloads the config from the disk.
     */
    void reload();

    /**
     * Returns the string for a specific path. Also appends the tag in front of the string like
     * [Tag] I am a cool string from the config
     * with
     * Tag = [Tag]
     * path points to "I am a ..."
     *
     * @param path the path
     * @return the string
     */
    String getString(String path);

    /**
     * Gets the tag defined in the lang file (Tag)
     *
     * @return the tag
     */
    String getTag();

    /**
     * Gets a stringlist from the config
     *
     * @param path the path
     * @return the list
     */
    List<String> getStringList(String path);

    /**
     * Returns the string for a specific path.
     * Does not append the tag!
     *
     * @param path the path
     * @return the string
     */
    String getRawString(String path);

    /**
     * Send a message to a player. Also replaces color codes with chatcolors.
     *
     * @param player the player
     * @param msg    the message
     */
    void sendMessage(Player player, String msg);
}
