package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ClearGUI extends AbstractGUI {

    private Landlord plugin = Landlord.getInstance();
    private LangManager lm = plugin.getLangManager();

    public ClearGUI(Player player) {
        super(player, 9, Landlord.getInstance().getLangManager().getRawString("Commands.ClearWorld.gui.title"));
    }

    @Override
    protected void create() {
        IOwnedLand land = plugin.getWgproxy().getRegion(player.getLocation());
        /*
         * Clear Options:
         * 1. Clear all for player x        (target==x || player stands inside x claim)
         * 2. Clear only specific claim     (target==null)
         * 3. Clear entire world            (target==null)
         */
        int pos = 0;
        if (land != null) {
            // Only clear this land
            Icon i1 = new Icon(new ItemStack(plugin.getMaterialsProxy().getGrass()));
            i1.setName(lm.getRawString("Commands.ClearWorld.gui.clearcurrentland.name"));
            i1.setLore(Arrays.asList(lm.getRawString("Commands.ClearWorld.gui.clearcurrentland.desc").split("\\|")));
            i1.addClickAction((player1) -> {
                ConfirmationGUI confirm = new ConfirmationGUI(player1,
                        lm.getRawString("Commands.ClearWorld.gui.clearcurrentland.confirm"),
                        (a) -> {
                            clearLand(land);
                            a.closeInventory();
                        },
                        (d) -> {
                            lm.sendMessage(d, lm.getString("Commands.ClearWorld.gui.clearcurrentland.abort"));
                            d.closeInventory();
                        }, this);
                confirm.display();
            });
            this.setIcon(pos++, i1);

            // Clear all for owner of current land
            Icon i2 = new Icon(plugin.getMaterialsProxy().getPlayerHead(land.getOwner()));
            i2.setName(lm.getRawString("Commands.ClearWorld.gui.clearplayer.name"));
            i2.setLore(Arrays.asList(lm.getRawString("Commands.ClearWorld.gui.clearplayer.desc").split("\\|")));
            i2.addClickAction((player1) -> {
                ConfirmationGUI confirm = new ConfirmationGUI(player1,
                        lm.getRawString("Commands.ClearWorld.gui.clearplayer.confirm"),
                        (a) -> {
                            clearPlayer(land.getOwner());
                            a.closeInventory();
                        },
                        (d) -> {
                            lm.sendMessage(d, lm.getString("Commands.ClearWorld.gui.clearplayer.abort"));
                            d.closeInventory();
                        }, this);
                confirm.display();
            });
            this.setIcon(pos++, i2);
        }
        // Clear all lands in a world
        Icon i3 = new Icon(new ItemStack(plugin.getMaterialsProxy().getFireCharge()));
        i3.setName(lm.getRawString("Commands.ClearWorld.gui.clearworld.name"));
        i3.setLore(Arrays.asList(lm.getRawString("Commands.ClearWorld.gui.clearworld.desc").split("\\|")));
        i3.addClickAction((player1) -> {
            ConfirmationGUI confirm = new ConfirmationGUI(player1,
                    lm.getRawString("Commands.ClearWorld.gui.clearworld.confirm").replace("%world%", player.getWorld().getName()),
                    (a) -> {
                        clearWorld(a.getWorld());
                        a.closeInventory();
                    },
                    (d) -> {
                        lm.sendMessage(d, lm.getString("Commands.ClearWorld.gui.clearworld.abort"));
                        d.closeInventory();
                    }, this);
            confirm.display();
        });
        this.setIcon(pos++, i3);

    }

    private void clearLand(IOwnedLand land) {
        handleUnclaim(Sets.newHashSet(land));
        lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearcurrentland.success")
                .replace("%land%", land.getName()));
    }

    private void clearWorld(World world) {
        Set<IOwnedLand> regions = plugin.getWgproxy().getRegions(world);
        int count = handleUnclaim(regions);

        lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearworld.success")
                .replace("%count%", String.valueOf(count))
                .replace("%world%", world.getName()));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPluginInstance(), () -> plugin.getMapManager().updateAll());

    }

    private int handleUnclaim(Set<IOwnedLand> regions) {
        int count = regions.size();

        for (IOwnedLand region : regions) {
            plugin.getOfferManager().removeOffer(region.getName());
            plugin.getWgproxy().unclaim(region);
        }
        return count;
    }

    private void clearPlayer(UUID id) {
        plugin.getPlayerManager().getOfflinePlayerAsync(id, lPlayer -> {

            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString("Commands.ClearWorld.noPlayer")
                        .replace("%players%", id.toString()));
            } else {
                // Success
                Set<IOwnedLand> regions = plugin.getWgproxy().getRegions(lPlayer.getUuid());
                int amt = handleUnclaim(regions);

                lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearplayer.success")
                        .replace("%count%", String.valueOf(amt))
                        .replace("%player%", lPlayer.getName()));

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin.getPluginInstance(), () -> plugin.getMapManager().updateAll());
            }
        });
    }
}
