package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.landlord.util.UUIDFetcher;
import biz.princeps.lib.crossversion.CParticle;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdminTeleport extends LandlordCommand {

    public void onAdminTeleport(Player sender, String target) {

        plugin.getPlayerManager().getOfflinePlayer(target, lplayer -> {

            if (lplayer == null) {
                // Failure
                sender.sendMessage(lm.getString("Commands.AdminTp.noPlayer").replace("%player%", target));
            } else {
                // Success

                Set<ProtectedRegion> lands = plugin.getWgHandler().getRegions(lplayer.getUuid());
                if (lands.size() > 0) {

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            MultiPagedGUI landGui = new MultiPagedGUI(sender, 5,
                                    lm.getRawString("Commands.AdminTp.guiHeader").replace("%player%", target));

                            lands.forEach(land -> landGui.addIcon(new Icon(new ItemStack(Material.GRASS))
                                    .setName(land.getId())
                                    .addClickAction((p, icon) -> {
                                                Location toTp = OwnedLand.getLocationFromName(land.getId());
                                                sender.teleport(toTp);
                                                OwnedLand.highlightLand(sender, CParticle.VILLAGERHAPPY);
                                            }
                                    )
                            ));

                            landGui.display();
                        }
                    }.runTask(plugin);

                } else {
                    sender.sendMessage(lm.getString("Commands.AdminTp.noLands").replace("%player%", target));
                }
            }
        });
    }
}
