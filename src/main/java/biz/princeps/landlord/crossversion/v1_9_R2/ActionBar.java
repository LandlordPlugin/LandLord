package biz.princeps.landlord.crossversion.v1_9_R2;

import biz.princeps.landlord.crossversion.IActionBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 28.07.17.
 */
public class ActionBar implements IActionBar {

    @Override
    public void sendActionBar(Player player, String msg) {
        BaseComponent text = new TextComponent(msg);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }
}
