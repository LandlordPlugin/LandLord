package biz.princeps.landlord.crossversion.v1_8_R3;

import biz.princeps.landlord.crossversion.IActionBar;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by spatium on 28.07.17.
 */
public class ActionBar implements IActionBar {

    @Override
    public void sendActionBar(Player player, String msg) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(msg), (byte)2);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
