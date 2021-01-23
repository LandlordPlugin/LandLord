package biz.princeps.landlord.commands.friends;


import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 18/07/18
 */
public class ListFriends extends LandlordCommand {

    private final IWorldGuardManager wg;

    public ListFriends(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Listfriends.name"),
                pl.getConfig().getString("CommandSettings.Listfriends.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Listfriends.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Listfriends.aliases")));
        this.wg = pl.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isPlayer()) {
            String[] args = arguments.get();

            String landname;
            if (args.length == 0) {
                IOwnedLand region = wg.getRegion(properties.getPlayer().getLocation());
                if (region != null) {
                    landname = region.getName();
                } else {
                    landname = null;
                }
            } else {
                landname = args[0];
            }

            onListFriends(properties.getPlayer(), landname);
        }
    }

    private void onListFriends(Player player, String landname) {
        if (isDisabledWorld(player)) return;

        if (landname == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.Listfriends.freeLand"));
            return;
        }
        if (!wg.isLLRegion(landname)) {
            lm.sendMessage(player, lm.getString(player, "Commands.Listfriends.invalidGeneral"));
            return;
        }

        IOwnedLand land = wg.getRegion(landname);

        if (land == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.Listfriends.freeLand"));
            return;
        }
        if (land.getFriends().size() > 0)
            lm.sendMessage(player, lm.getString(player, "Commands.Listfriends.friends")
                    .replace("%friends%", land.getMembersString()));
        else {
            lm.sendMessage(player, lm.getString(player, "Commands.Listfriends.noFriends"));
        }

    }
}
