package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.gui.ConfirmationGUI;
import com.google.common.collect.Sets;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 20/05/20
 */
public class Regenerate extends LandlordCommand {

    private final IWorldGuardManager wg;

    public Regenerate(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Regenerate.name"),
                plugin.getConfig().getString("CommandSettings.Regenerate.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Regenerate.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Regenerate.aliases")));
        this.wg = plugin.getWGManager();


    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }

        Player player = properties.getPlayer();
        if (isDisabledWorld(player)) {
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        IOwnedLand land = wg.getRegion(chunk);

        if (arguments.size() == 1) {
            land = wg.getRegion(arguments.get(0));
        }

        if (land == null || !land.isOwner(player.getUniqueId())) {
            // Unclaimed
            plugin.getLangManager().sendMessage(player, "Commands.Regenerate.notOwn");
        } else {
            // actual regenerate
            double cost = plugin.getConfig().getDouble("ResetCost");
            String costString = (Options.isVaultEnabled() ? plugin.getVaultManager().format(cost) : "-1");

            IOwnedLand finalLand = land;
            ConfirmationGUI confi = new ConfirmationGUI(plugin.getPlugin(), player,
                    lm.getRawString("Commands.Regenerate.confirmation").replace("%cost%", costString),
                    (p1) -> {
                        boolean flag = true;
                        if (Options.isVaultEnabled()) {
                            if (plugin.getVaultManager().hasBalance(player, cost)) {
                                plugin.getVaultManager().take(player, cost);
                            } else {
                                lm.sendMessage(player, lm.getString(player, "Commands.Regenerate.notEnoughMoney")
                                        .replace("%cost%", costString)
                                        .replace("%name%", finalLand.getName()));
                                flag = false;
                            }
                        }
                        if (flag) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    LandManageEvent landManageEvent = new LandManageEvent(player, finalLand,
                                            null, "REGENERATE", "REGENERATE");
                                    plugin.getPlugin().getServer().getPluginManager().callEvent(landManageEvent);
                                }
                            }.runTask(plugin.getPlugin());

                            plugin.getRegenerationManager().regenerateChunk(finalLand.getALocation());
                            lm.sendMessage(player, lm.getString(player, "Commands.Regenerate.success")
                                    .replace("%land%", finalLand.getName()));
                            player.closeInventory();
                        }

                    }, (p2) -> lm.sendMessage(player, lm.getString(player, "Commands.Regenerate.abort")
                    .replace("%land%", finalLand.getName())), null);

            confi.setConfirm(lm.getRawString("Confirmation.accept"));
            confi.setDecline(lm.getRawString("Confirmation.decline"));

            confi.display();
        }
    }
}
