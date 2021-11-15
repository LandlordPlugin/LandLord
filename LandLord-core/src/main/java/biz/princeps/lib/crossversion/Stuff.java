package biz.princeps.lib.crossversion;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Stuff {

    private final JavaPlugin plugin;

    public Stuff(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendActionBar(Player p, String text) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public void spawnPublicParticle(Location loc, Particle particle, int amt) {
        new BukkitRunnable() {
            @Override
            public void run() {
                loc.getWorld().spawnParticle(particle, loc, amt);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void spawnPlayerParticle(Player p, Location loc, Particle particle, int amt) {
        new BukkitRunnable() {
            @Override
            public void run() {
                p.spawnParticle(particle, loc, amt);
            }
        }.runTaskAsynchronously(plugin);
    }

}