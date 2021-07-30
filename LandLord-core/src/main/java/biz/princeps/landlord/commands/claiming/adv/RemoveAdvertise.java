package biz.princeps.landlord.commands.claiming.adv;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public class RemoveAdvertise extends LandlordCommand {

    private final IWorldGuardManager wg;

    public RemoveAdvertise(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.RemoveAdvertise.name"),
                pl.getConfig().getString("CommandSettings.RemoveAdvertise.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemoveAdvertise.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemoveAdvertise.aliases")));
        this.wg = pl.getWGManager();
    }


    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }
        if (!Options.isVaultEnabled()) {
            return;
        }

        String landname = arguments.size() == 1 ? arguments.get(0) : "this";
        Player player = properties.getPlayer();


        IOwnedLand ownedLand;
        if (landname.equals("this")) {
            ownedLand = wg.getRegion(player.getLocation().getChunk());
        } else {
            ownedLand = wg.getRegion(landname);
        }

        if (ownedLand == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.Advertise.notOwnFreeLand"));
            return;
        }
        if (isDisabledWorld(player, ownedLand.getWorld())) {
            return;
        }

        if (!ownedLand.isOwner(player.getUniqueId())) {
            lm.sendMessage(player, lm.getString(player, "Commands.Advertise.notOwn")
                    .replace("%owner%", ownedLand.getOwnersString()));
            return;
        }

        if (ownedLand.getPrice() == -1) {
            lm.sendMessage(player, lm.getString(player, "Commands.RemoveAdvertise.noAdvertise")
                    .replace("%landname%", ownedLand.getName()));
            return;
        }

        ownedLand.setPrice(-1);
        lm.sendMessage(player, lm.getString(player, "Commands.RemoveAdvertise.success")
                .replace("%landname%", landname));
    }
}
