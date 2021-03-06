package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Borders extends LandlordCommand implements Listener {

    private final HashMap<UUID, BukkitTask> tasks;
    private final IWorldGuardManager wg;

    public Borders(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Borders.name"),
                pl.getConfig().getString("CommandSettings.Borders.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Borders.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Borders.aliases")));
        tasks = new HashMap<>();
        this.wg = pl.getWGManager();

        this.plugin.getPlugin().getServer().getPluginManager().registerEvents(this, plugin.getPlugin());
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        if (!Options.enabled_borders()) {
            return;
        }

        Player p = properties.getPlayer();

        if (tasks.get(p.getUniqueId()) == null) {

            ComponentBuilder cp = new ComponentBuilder(lm.getString(p, "Commands.Borders.activated")).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, PrincepsLib.getCommandManager()
                            .getCommand(Landlordbase.class).getCommandString(Borders.class))
            );
            plugin.getUtilsManager().sendBasecomponent(p, cp.create());


            int refreshRate = plugin.getConfig().getInt("Borders.refreshRate");
            this.tasks.put(p.getUniqueId(), new BukkitRunnable() {
                int counter = 0;
                final int timeout = plugin.getConfig().getInt("Borders.timeout");

                @Override
                public void run() {
                    if (counter * refreshRate <= timeout) {
                        if (plugin.getConfig().getBoolean("Particles.borders.enabled")) {
                            final IOwnedLand ownedLand = wg.getRegion(p.getLocation());

                            if (ownedLand == null) {
                                wg.highlightLand(p.getLocation().getChunk(), p,
                                        Particle.valueOf(plugin.getConfig().getString("Particles.borders.unclaimed")), 1, false);
                            } else {
                                plugin.getPlayerManager().getOffline(ownedLand.getOwner(), (owner) -> {
                                    if (plugin.getPlayerManager().isInactive(owner.getLastSeen())) {
                                        wg.highlightLand(p.getLocation().getChunk(), p,
                                                Particle.valueOf(plugin.getConfig().getString("Particles.borders.inactive")), 1, false);
                                    } else {
                                        wg.highlightLand(p.getLocation().getChunk(), p,
                                                Particle.valueOf(plugin.getConfig().getString("Particles.borders.claimed")), 1, false);
                                    }
                                });
                            }
                        }
                    } else {
                        cancel();
                        counter++;
                    }
                }
            }.runTaskTimer(plugin.getPlugin(), 0, refreshRate * 20L));
        } else {
            lm.sendMessage(p, lm.getString(p, "Commands.Borders.deactivated"));
            tasks.get(p.getUniqueId()).cancel();
            tasks.remove(p.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisconnect(PlayerQuitEvent e) {
        BukkitTask bukkitTask = this.tasks.get(e.getPlayer().getUniqueId());
        if (bukkitTask != null) {
            bukkitTask.cancel();
            this.tasks.remove(e.getPlayer().getUniqueId());
        }
    }
}
