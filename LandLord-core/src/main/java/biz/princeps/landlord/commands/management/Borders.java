package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
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
    private IWorldGuardManager wg;

    public Borders(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Borders.name"),
                pl.getConfig().getString("CommandSettings.Borders.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Borders.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Borders.aliases")));
        tasks = new HashMap<>();
        this.wg = pl.getWGManager();
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
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, PrincepsLib.getCommandManager()
                            .getCommand(Landlordbase.class).getCommandString(Borders.class))
            );
            plugin.getUtilsManager().sendBasecomponent(p, cp.create());


            int refreshRate = plugin.getConfig().getInt("Borders.refreshRate");
            this.tasks.put(p, new BukkitRunnable() {
                int counter = 0;
                int timeout = plugin.getConfig().getInt("Borders.timeout");

                @Override
                public void run() {
                    if (counter * refreshRate <= timeout) {
                        if (plugin.getConfig().getBoolean("Particles.borders.enabled")) {
                            wg.highlightLand(p.getLocation().getChunk(), p,
                                    Particle.valueOf(plugin.getConfig().getString("Particles.borders.particle")), 1);
                        }
                    } else {
                        cancel();
                        counter++;
                    }
                }
            }.runTaskTimer(plugin.getPlugin(), 0, refreshRate * 20));
        } else {
            lm.sendMessage(p, lm.getString("Commands.Borders.deactivated"));
            tasks.get(p).cancel();
            tasks.remove(p);
        }
    }
}
