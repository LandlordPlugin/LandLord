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
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;


/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 20/05/20
 */
public class Regenerate extends LandlordCommand {

    private final IWorldGuardManager wg;

    public Regenerate(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Regenerate.name"),
                pl.getConfig().getString("CommandSettings.Regenerate.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Regenerate.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Regenerate.aliases")));
        this.wg = pl.getWGManager();


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
            ConfirmationGUI confi = new ConfirmationGUI(player,
                    lm.getRawString("Commands.Regenerate.confirmation").replace("%cost%", costString),
                    (p1) -> {
                        boolean flag = true;
                        if (Options.isVaultEnabled()) {
                            if (plugin.getVaultManager().hasBalance(player, cost)) {
                                plugin.getVaultManager().take(player, cost);
                            } else {
                                lm.sendMessage(player, lm.getString("Commands.Regenerate.notEnoughMoney")
                                        .replace("%cost%", costString)
                                        .replace("%name%", finalLand.getName()));
                                flag = false;
                            }
                        }
                        if (flag) {
                            Bukkit.getScheduler().runTask(plugin.getPlugin(), () -> {
                                LandManageEvent landManageEvent = new LandManageEvent(player, finalLand,
                                        null, "REGENERATE", "REGENERATE");
                                Bukkit.getPluginManager().callEvent(landManageEvent);
                            });

                            plugin.getRegenerationManager().regenerateChunk(finalLand.getALocation());
                            lm.sendMessage(player, lm.getString("Commands.Regenerate.success")
                                    .replace("%land%", finalLand.getName()));
                            player.closeInventory();
                        }

                    }, (p2) -> lm.sendMessage(player, lm.getString("Commands.Regenerate.abort")
                    .replace("%land%", finalLand.getName())), null);

            confi.setConfirm(lm.getRawString("Confirmation.accept"));
            confi.setDecline(lm.getRawString("Confirmation.decline"));

            confi.display();
        }
    }
}
