package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.events.LandChangeEvent;
import biz.princeps.lib.PrincepsLib;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.TextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.function.BiConsumer;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;

public class LandChangeListener extends BasicListener {
    private final BiConsumer<String, Player> sendMessage;

    public LandChangeListener(ILandLord plugin) {
        super(plugin);
        MessageDisplay type = MessageDisplay.valueOf(plugin.getConfig().getString("LandMessage"));
        // that would be super cool with switch expressions :(
        switch (type) {
            case ActionBar:
                sendMessage = (message, player) -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                break;
            case Chat:
                sendMessage = (message, player) -> plugin.getLangManager().sendMessage(player, message);
                break;
            case Title:
                sendMessage = (message, player) -> player.sendTitle(translateAlternateColorCodes('&', message), null, 10, 70, 20);
                break;
            case Disabled:
            default:
                sendMessage = (message, player) -> {};
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLandChange(LandChangeEvent event) {
        if (event.getNewLand() == null) {
            // exited
            this.sendMessage.accept(event.getPreviousLand().getFarewellMessage(), event.getPlayer());
            return;
        }
        // entered
        this.sendMessage.accept(event.getNewLand().getGreetMessage(), event.getPlayer());
    }

}
