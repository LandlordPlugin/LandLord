package biz.princeps.landlord.commands.management.borders;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class Borders extends LandlordCommand implements Listener {

    private final Map<UUID, BordersTask> tasks;

    public Borders(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Borders.name"),
                plugin.getConfig().getString("CommandSettings.Borders.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Borders.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Borders.aliases")));
        this.tasks = new HashMap<>();

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

        if (!tasks.containsKey(p.getUniqueId())) {
            ComponentBuilder cp = new ComponentBuilder(lm.getString(p, "Commands.Borders.activated")).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, PrincepsLib.getCommandManager()
                            .getCommand(Landlordbase.class).getCommandString(Borders.class))
            );
            plugin.getUtilsManager().sendBasecomponent(p, cp.create());

            BordersTask bordersTask = new BordersTask(plugin, p);
            tasks.put(p.getUniqueId(), bordersTask);
            bordersTask.start();
        } else {
            lm.sendMessage(p, lm.getString(p, "Commands.Borders.deactivated"));
            tasks.remove(p.getUniqueId()).cancel();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisconnect(PlayerQuitEvent e) {
        BordersTask bordersTask = tasks.remove(e.getPlayer().getUniqueId());
        if (bordersTask != null) {
            bordersTask.cancel();
        }
    }
}
