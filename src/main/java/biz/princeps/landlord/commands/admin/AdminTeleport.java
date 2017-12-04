package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.landlord.util.UUIDFetcher;
import biz.princeps.lib.crossversion.CParticle;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AdminTeleport extends LandlordCommand {

    public void onAdminTeleport(Player sender, String target) {

        UUIDFetcher.getUUID(target, uuid -> {

            if (uuid == null) {
                // Failure
                sender.sendMessage(lm.getString("Commands.AdminTp.noPlayer").replace("%player%", target));
            } else {
                // Success

                List<ProtectedRegion> lands = new ArrayList<>();

                for (World w : Bukkit.getWorlds())
                    for (ProtectedRegion protectedRegion : plugin.getWgHandler().getWG().getRegionManager(w).getRegions().values()) {
                        if (Bukkit.getOfflinePlayer(uuid) != null) {
                            if (protectedRegion.isOwner(plugin.getWgHandler().getWG().wrapOfflinePlayer(Bukkit.getOfflinePlayer(uuid)))) {
                                lands.add(protectedRegion);
                            }
                        }
                    }
                if (lands.size() > 0) {

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            MultiPagedGUI landGui = new MultiPagedGUI(sender, 5, lm.getRawString("Commands.AdminTp.guiHeader").replace("%player%", target));

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
