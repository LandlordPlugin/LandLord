package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.google.common.collect.Sets;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class AdminTeleport extends LandlordCommand {

    public AdminTeleport(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.AdminTP.name"),
                plugin.getConfig().getString("CommandSettings.AdminTP.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.AdminTP.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.AdminTP.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player sender = properties.getPlayer();
        String target;
        try {
            target = arguments.get(0);
        } catch (ArgumentsOutOfBoundsException e) {
            properties.sendUsage();
            return;
        }

        plugin.getPlayerManager().getOffline(target, (offline) -> {
            if (offline == null) {
                // Failure
                lm.sendMessage(sender, lm.getString(sender, "Commands.AdminTp.noPlayer").replace("%player%", target));
            } else {
                // Success
                Set<IOwnedLand> lands = plugin.getWGManager().getRegions(offline.getUuid());
                if (lands.size() > 0) {
                    MultiPagedGUI landGui = new MultiPagedGUI(plugin.getPlugin(), sender, 5,
                            lm.getRawString("Commands.AdminTp.guiHeader").replace("%player%", target));

                    for (IOwnedLand land : lands) {
                        landGui.addIcon(new Icon(new ItemStack(plugin.getMaterialsManager().getGrass()))
                                .setName(land.getName())
                                .addClickAction((p) -> {
                                            Location toTp = land.getALocation();
                                            sender.teleport(toTp);
                                            land.highlightLand(sender, Particle.VILLAGER_HAPPY);
                                        }
                                )
                        );
                    }
                    landGui.display();
                } else {
                    lm.sendMessage(sender, lm.getString(sender, "Commands.AdminTp.noLands").replace("%player%", target));
                }
            }
        });
    }
}
