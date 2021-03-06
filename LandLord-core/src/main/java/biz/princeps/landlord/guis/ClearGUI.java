package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ClearType;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IMultiTaskManager;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.multi.MultiClearTask;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class ClearGUI extends AbstractGUI {

    private final ILandLord plugin;
    private final ILangManager lm;
    private final IWorldGuardManager wg;
    private final IMultiTaskManager multiTaskManager;

    public ClearGUI(ILandLord plugin, Player player) {
        super(plugin, player, 9, plugin.getLangManager().getRawString("Commands.Clear.gui.title"));
        this.plugin = plugin;
        this.lm = plugin.getLangManager();
        this.wg = plugin.getWGManager();
        this.multiTaskManager = plugin.getMultiTaskManager();
    }

    @Override
    protected void create() {
        IOwnedLand land = wg.getRegion(player.getLocation());
        /*
         * Clear Options:
         * 1. Clear all for player x        (target==x || player stands inside x claim)
         * 2. Clear only specific claim     (target==null)
         * 3. Clear entire world            (target==null)
         */
        int pos = 0;
        if (land != null) {
            if (player.hasPermission("landlord.admin.clear.land")) {
                // Only clear this land
                Icon i1 = new Icon(new ItemStack(plugin.getMaterialsManager().getGrass()));
                i1.setName(lm.getRawString("Commands.Clear.gui.clearcurrentland.name"));
                i1.setLore(Arrays.asList(lm.getRawString("Commands.Clear.gui.clearcurrentland.desc").split("\\|")));
                i1.addClickAction((player1) -> {
                    ConfirmationGUI confirm = new ConfirmationGUI(plugin, player1,
                            lm.getRawString("Commands.Clear.gui.clearcurrentland.confirm"),
                            (a) -> {
                                clearLand(land);
                                a.closeInventory();
                            },
                            (d) -> {
                                lm.sendMessage(d, lm.getString(player, "Commands.Clear.gui.clearcurrentland.abort"));
                                d.closeInventory();
                            }, this);
                    confirm.display();
                });
                this.setIcon(pos++, i1);
            }

            // Clear all for owner of current land
            if (player.hasPermission("landlord.admin.clear.player")) {
                Icon i2 = new Icon(plugin.getMaterialsManager().getPlayerHead(land.getOwner()));
                i2.setName(lm.getRawString("Commands.Clear.gui.clearplayer.name"));
                i2.setLore(Arrays.asList(lm.getRawString("Commands.Clear.gui.clearplayer.desc").split("\\|")));
                i2.addClickAction((player1) -> {
                    ConfirmationGUI confirm = new ConfirmationGUI(plugin, player1,
                            lm.getRawString("Commands.Clear.gui.clearplayer.confirm"),
                            (a) -> {
                                clearPlayer(land.getOwner());
                                a.closeInventory();
                            },
                            (d) -> {
                                lm.sendMessage(d, lm.getString(player, "Commands.Clear.gui.clearplayer.abort"));
                                d.closeInventory();
                            }, this);
                    confirm.display();
                });
                this.setIcon(pos++, i2);
            }
        }

        if (player.hasPermission("landlord.admin.clear.world")) {
            // Clear all lands in a world
            Icon i3 = new Icon(new ItemStack(plugin.getMaterialsManager().getFireCharge()));
            i3.setName(lm.getRawString("Commands.Clear.gui.clearworld.name"));
            i3.setLore(Arrays.asList(lm.getRawString("Commands.Clear.gui.clearworld.desc").split("\\|")));
            i3.addClickAction((player1) -> {
                ConfirmationGUI confirm = new ConfirmationGUI(plugin, player1,
                        lm.getRawString("Commands.Clear.gui.clearworld.confirm").replace("%world%",
                                player.getWorld().getName()),
                        (a) -> {
                            clearWorld(a.getWorld());
                            a.closeInventory();
                        },
                        (d) -> {
                            lm.sendMessage(d, lm.getString(player, "Commands.Clear.gui.clearworld.abort"));
                            d.closeInventory();
                        }, this);
                confirm.display();
            });
            this.setIcon(pos++, i3);
        }
    }

    private void clearLand(IOwnedLand land) {
        //TODO We already check during menu creation, it's enough, this check is useless ?
        if (!player.hasPermission("landlord.admin.clear.land")) {
            lm.sendMessage(player, lm.getString(player, "noPermissions"));
        }

        wg.unclaim(Collections.singleton(land));
        lm.sendMessage(player, lm.getString(player, "Commands.Clear.gui.clearcurrentland.success")
                .replace("%land%", land.getName()));
    }

    private void clearWorld(World world) {
        //TODO We already check during menu creation, it's enough, this check is useless ?
        if (!player.hasPermission("landlord.admin.clear.world")) {
            lm.sendMessage(player, lm.getString(player, "noPermissions"));
        }

        multiTaskManager.enqueueTask(new MultiClearTask(plugin, player, wg.getRegions(world), world.getName(), ClearType.WORLD));
    }

    private void clearPlayer(UUID id) {
        //TODO We already check during menu creation, it's enough, this check is useless ?
        if (!player.hasPermission("landlord.admin.clear.player")) {
            lm.sendMessage(player, lm.getString(player, "noPermissions"));
        }

        plugin.getPlayerManager().getOffline(id, (lPlayer) -> {
            if (lPlayer == null) {
                // Failure
                lm.sendMessage(player, lm.getString(player, "Commands.Clear.noPlayer")
                        .replace("%players%", id.toString()));
            } else {
                // Success
                multiTaskManager.enqueueTask(new MultiClearTask(plugin, player, wg.getRegions(lPlayer.getUuid()), lPlayer.getName(), ClearType.PLAYER));
            }
        });
    }

}
