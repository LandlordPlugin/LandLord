package biz.princeps.lib.crossversion;

import biz.princeps.lib.PrincepsLib;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Stuff {

    public void sendActionBar(Player p, String text) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public void spawnPublicParticle(Location loc, Particle particle, int amt) {
        Bukkit.getScheduler().runTaskAsynchronously(PrincepsLib.getPluginInstance(), () -> loc.getWorld().spawnParticle(particle, loc, amt));
    }

    public void spawnPlayerParticle(Player p, Location loc, Particle particle, int amt) {
        Bukkit.getScheduler().runTaskAsynchronously(PrincepsLib.getPluginInstance(), () -> p.spawnParticle(particle, loc, amt));
    }
}