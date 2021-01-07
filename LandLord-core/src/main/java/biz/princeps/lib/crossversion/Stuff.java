package biz.princeps.lib.crossversion;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Stuff {

    public void sendActionBar(Player p, String text) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public void spawnPublicParticle(Location loc, Particle particle, int amt) {
        loc.getWorld().spawnParticle(particle, loc, amt);
    }

    public void spawnPlayerParticle(Player p, Location loc, Particle particle, int amt) {
        p.spawnParticle(particle, loc, amt);
    }
}