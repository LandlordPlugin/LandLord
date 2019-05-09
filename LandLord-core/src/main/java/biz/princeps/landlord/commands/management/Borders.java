package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
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

    public Borders(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Borders.name"),
                pl.getConfig().getString("CommandSettings.Borders.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Borders.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Borders.aliases")));
        tasks = new HashMap<>();
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

        if (tasks.get(p) == null) {

            ComponentBuilder cp = new ComponentBuilder(lm.getString("Commands.Borders.activated")).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll borders")
            );
            plugin.getUtilsProxy().send_basecomponent(p, cp.create());

            this.tasks.put(p, new BukkitRunnable() {
                int counter = 0;

                @Override
                public void run() {
                    if (counter <= 360 / plugin.getConfig().getInt("Borders.refreshRate")) {
                        if (plugin.getConfig().getBoolean("Particles.borders.enabled")) {
                            IOwnedLand ol = plugin.getWGProxy().getRegion(p.getLocation().getChunk());
                            ol.highlightLand(p, Particle.valueOf(plugin.getConfig().getString("Particles.borders.particle")));
                        }
                    } else {
                        cancel();
                        counter++;
                    }
                }
            }.runTaskTimer(plugin.getPlugin(), 0, plugin.getConfig().getInt("Borders.refreshRate") * 20));
        } else {
            lm.sendMessage(p, lm.getString("Commands.Borders.deactivated"));
            tasks.get(p).cancel();
            tasks.remove(p);
        }
    }
}
