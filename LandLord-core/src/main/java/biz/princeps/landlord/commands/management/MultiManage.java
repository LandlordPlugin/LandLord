package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGuiAll;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MultiManage extends LandlordCommand {

    private final IWorldGuardManager wg;

    public MultiManage(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.MultiManage.name"),
                plugin.getConfig().getString("CommandSettings.MultiManage.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MultiManage.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MultiManage.aliases")));
        this.wg = plugin.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            properties.sendMessage("Player command only!");
            return;
        }
        if (arguments.size() != 2) {
            properties.sendUsage();
            return;
        }
        MultiMode mode;
        int radius;
        try {
            mode = MultiMode.valueOf(arguments.get(0).toUpperCase());
            radius = arguments.getInt(1);
        } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ex) {
            properties.sendUsage();
            return;
        }

        Player player = properties.getPlayer();

        List<IOwnedLand> lands = new ArrayList<>(mode.getLandsOf(radius, player.getLocation(), player.getUniqueId(), wg));

        if (lands.isEmpty()) {
            lm.sendMessage(player, plugin.getLangManager().getString("Commands.ListLands.noLands"));
            return;
        }

        ManageGuiAll gui = new ManageGuiAll(
                plugin, player, lands, ManageMode.MULTI, mode, radius);
        gui.display();
    }
}
