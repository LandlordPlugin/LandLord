package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Borders extends LandlordCommand {

    private HashMap<Player, BukkitTask> tasks;

    public Borders() {
        tasks = new HashMap<>();
    }

    public void onToggleBorder(Player p) {

        if (!Options.enabled_borders()) {
            return;
        }

        if (tasks.get(p) == null) {

            ComponentBuilder cp = new ComponentBuilder(lm.getString("Commands.Borders.activated")).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll borders")
            );
            p.spigot().sendMessage(cp.create());

            this.tasks.put(p, new BukkitRunnable() {
                int counter = 0;

                @Override
                public void run() {
                    if (counter <= 360 / plugin.getConfig().getInt("Borders.refreshRate")) {
                        if (plugin.getConfig().getBoolean("Particles.borders.enabled"))
                            OwnedLand.highlightLand(p, Particle.valueOf(plugin.getConfig().getString("Particles.borders.particle")));
                    } else
                        cancel();
                    counter++;
                }
            }.runTaskTimer(plugin, 0, plugin.getConfig().getInt("Borders.refreshRate") * 20));
        } else {
            lm.sendMessage(p, lm.getString("Commands.Borders.deactivated"));
            tasks.get(p).cancel();
            tasks.remove(p);
        }
    }
}
