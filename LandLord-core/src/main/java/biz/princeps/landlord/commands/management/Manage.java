package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGui;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/7/17
 * <p>
 * If you are looking for the gui, check AbstractManage!
 */
public class Manage extends LandlordCommand {

    private final IWorldGuardManager wg;

    public Manage(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Manage.name"),
                plugin.getConfig().getString("CommandSettings.Manage.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Manage.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Manage.aliases")));
        this.wg = plugin.getWGManager();
    }

    // TODO Clean this mess up
    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isConsole()) {
            return;
        }
        String[] args = arguments.get();
        Player player = properties.getPlayer();

        // land manage
        if (args.length == 0 || (args.length == 1 && !args[0].equals("setgreet")
                && !args[0].equals("setgreetall")
                && !args[0].equals("setfarewell")
                && !args[0].equals("setfarewellall"))) {

            IOwnedLand land;
            if (args.length == 0) {
                land = wg.getRegion(player.getLocation().getChunk());
            } else {
                // land manage <landid>
                land = wg.getRegion(args[0]);
            }

            if (land == null) {
                lm.sendMessage(player, lm.getString(player, "Commands.Manage.notOwnFreeLand"));
                return;
            }

            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.manage")) {
                lm.sendMessage(player, lm.getString(player, "Commands.Manage.notOwn")
                        .replace("%owner%", land.getOwnersString()));
                return;
            }

            ManageGui gui = new ManageGui(plugin, player, land);
            gui.display();

        } else {
            // land manage <allCommands>
            switch (args[0]) {
                case "setgreetall":
                    setGreet(player, args, new ArrayList<>(wg.getRegions(player.getUniqueId())), 1);
                    break;
                case "setfarewellall":
                    setFarewell(player, args, new ArrayList<>(wg.getRegions(player.getUniqueId())), 1);
                    break;
                case "setgreet":
                    //plugin.getLogger().log(Level.INFO, "greet " + Arrays.toString(args));
                    setGreet(player, args, Collections.singletonList(wg.getRegion(player.getLocation())), 1);
                    break;
                case "setfarewell":
                    setFarewell(player, args, Collections.singletonList(wg.getRegion(player.getLocation())), 1);
                    break;
                case "multisetgreet":
                    try {
                        MultiMode mode = MultiMode.valueOf(arguments.get(1).toUpperCase());
                        int radius = arguments.getInt(2);

                        setGreet(player, args, new ArrayList<>(mode.getLandsOf(radius, player.getLocation(), player.getUniqueId(), wg)), 3);
                    } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ignored) {
                    }
                    break;
                case "multisetfarewell":
                    try {
                        MultiMode mode = MultiMode.valueOf(arguments.get(1).toUpperCase());
                        int radius = arguments.getInt(2);

                        setFarewell(player, args, new ArrayList<>(mode.getLandsOf(radius, player.getLocation(), player.getUniqueId(), wg)), 3);
                    } catch (IllegalArgumentException | ArgumentsOutOfBoundsException ignored) {
                    }
                    break;
                default:
                    try {
                        IOwnedLand target = wg.getRegion(args[0]);

                        switch (args[1]) {
                            case "setgreet":
                                setGreet(player, args, Collections.singletonList(target), 2);
                                break;

                            case "setfarewell":
                                setFarewell(player, args, Collections.singletonList(target), 2);
                                break;
                        }

                    } catch (IndexOutOfBoundsException e) {
                        lm.sendMessage(player, lm.getString(player, "Commands.manage.invalidArguments"));
                    }
                    break;
            }
        }
    }


    private void setGreet(Player player, String[] args, List<IOwnedLand> lands, int casy) {
        if (lands.isEmpty() || lands.get(0) == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.Manage.notOwnFreeLand"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = casy; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newmsg = ChatColor.translateAlternateColorCodes('&', sb.toString());

        if (newmsg.isEmpty()) {
            newmsg = lm.getRawString("Alerts.defaultGreeting");
        }
        newmsg = newmsg.replace("%owner%", player.getName());

        for (IOwnedLand region : lands) {
            LandManageEvent landManageEvent = new LandManageEvent(player, region,
                    "GREET_MESSAGE", region.getGreetMessage(), newmsg);
            plugin.getServer().getPluginManager().callEvent(landManageEvent);

            region.setGreetMessage(newmsg);
        }

        lm.sendMessage(player, lm.getString(player, "Commands.Manage.SetGreet.successful")
                .replace("%msg%", newmsg));
    }

    private void setFarewell(Player player, String[] args, List<IOwnedLand> lands, int casy) {
        if (lands.isEmpty() || lands.get(0) == null) {
            lm.sendMessage(player, lm.getString(player, "Commands.Manage.notOwnFreeLand"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = casy; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newmsg = ChatColor.translateAlternateColorCodes('&', sb.toString());

        if (newmsg.isEmpty()) {
            newmsg = lm.getRawString("Alerts.defaultFarewell");
        }
        newmsg = newmsg.replace("%owner%", player.getName());

        for (IOwnedLand region : lands) {
            LandManageEvent landManageEvent = new LandManageEvent(player, region,
                    "FAREWELL_MESSAGE", region.getFarewellMessage(), newmsg);
            plugin.getServer().getPluginManager().callEvent(landManageEvent);

            region.setFarewellMessage(newmsg);
        }

        lm.sendMessage(player, lm.getString(player, "Commands.Manage.SetFarewell.successful")
                .replace("%msg%", newmsg));
    }

}
