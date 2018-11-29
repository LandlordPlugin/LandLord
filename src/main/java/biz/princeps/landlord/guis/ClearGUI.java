package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
        super(player, 9, Landlord.getInstance().getLangManager()
                .getRawString("Commands.ClearWorld.gui.title"));
    }

    @Override
    protected void create() {
        OwnedLand land = plugin.getLand(player.getLocation());
        /*
         * Clear Options:
         * 1. Clear all for player x        (target==x || player stands inside x claim)
         * 2. Clear only specific claim     (target==null)
         * 3. Clear entire world            (target==null)
         */
        int pos = 0;
        if (land != null) {
            // Only clear this land
            Icon i1 = new Icon(new ItemStack(Material.GRASS));
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
            ItemStack skull = new ItemStack(Material.SKULL_ITEM);
            SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
            itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(land.getOwner()));
            skull.setItemMeta(itemMeta);
            Icon i2 = new Icon(skull);
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
        Icon i3 = new Icon(new ItemStack(Material.FIREBALL));
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

    private void clearLand(OwnedLand land) {
        RegionManager rgm = plugin.getWgHandler().getRegionManager(land.getWorld());
        rgm.removeRegion(land.getName());

        lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearcurrentland.success")
                .replace("%land%", land.getName()));
    }

    private void clearWorld(World world) {
        RegionManager regionManager = plugin.getWgHandler().getRegionManager(world);

        Map<String, ProtectedRegion> regions = new HashMap<>(regionManager.getRegions());

        regions.keySet().removeIf(key -> !plugin.isLLRegion(key));

        int count = regions.size();

        regions.keySet().forEach(regionManager::removeRegion);

        lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearworld.success")
                .replace("%count%", String.valueOf(count))
                .replace("%world%", world.getName()));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getMapManager().updateAll());

    }

    private void clearPlayer(UUID id) {
        plugin.getPlayerManager().getOfflinePlayerAsync(id, lPlayer -> {

            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString("Commands.ClearWorld.noPlayer")
                        .replace("%players%", id.toString()));
            } else {
                // Success
                int amt = 0;
                for (World world : Bukkit.getWorlds()) {
                    // Only count enabled worlds
                    if (!Landlord.getInstance().getConfig().getStringList("disabled-worlds").contains(world.getName())) {
                        List<ProtectedRegion> rgs = plugin.getWgHandler().getRegions(lPlayer.getUuid(), world);
                        amt += rgs.size();
                        Set<String> toDelete = new HashSet<>();
                        for (ProtectedRegion protectedRegion : rgs) {
                            if (plugin.isLLRegion(protectedRegion.getId()))
                                toDelete.add(protectedRegion.getId());
                        }
                        RegionManager rgm = plugin.getWgHandler().getRegionManager(world);
                        for (String s : toDelete) {
                            plugin.getOfferManager().removeOffer(s);
                            rgm.removeRegion(s);
                        }
                    }
                }

                lm.sendMessage(player, lm.getString("Commands.ClearWorld.gui.clearplayer.success")
                        .replace("%count%", String.valueOf(amt))
                        .replace("%player%", lPlayer.getName()));

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getMapManager().updateAll());
            }
        });
    }
}
