package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
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

    public void onAdminTeleport(Player sender, String target) {

        plugin.getPlayerManager().getOfflinePlayerAsync(target, lplayer -> {

            if (lplayer == null) {
                // Failure
                lm.sendMessage(sender, lm.getString("Commands.AdminTp.noPlayer").replace("%player%", target));
            } else {
                // Success

                Set<ProtectedRegion> lands = plugin.getWgHandler().getRegions(lplayer.getUuid());
                if (lands.size() > 0) {

                    MultiPagedGUI landGui = new MultiPagedGUI(sender, 5,
                            lm.getRawString("Commands.AdminTp.guiHeader").replace("%player%", target));

                    lands.forEach(land -> landGui.addIcon(new Icon(new ItemStack(Material.GRASS))
                            .setName(land.getId())
                            .addClickAction((p) -> {
                                        Location toTp = OwnedLand.getLocationFromName(land.getId());
                                        sender.teleport(toTp);
                                        OwnedLand.highlightLand(sender, Particle.VILLAGER_HAPPY);
                                    }
                            )
                    ));

                    landGui.display();
                } else {
                    lm.sendMessage(sender, lm.getString("Commands.AdminTp.noLands").replace("%player%", target));
                }
            }
        });
    }
}
