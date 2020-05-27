package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGui;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/7/17
 * <p>
 * If you are looking for the gui, check AbstractManage!
 */
public class Manage extends LandlordCommand {

    private final IWorldGuardManager wg;

    public Manage(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Manage.name"),
                pl.getConfig().getString("CommandSettings.Manage.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Manage.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Manage.aliases")));
        this.wg = pl.getWGManager();
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
                lm.sendMessage(player, lm.getString("Commands.Manage.notOwnFreeLand"));
                return;
            }

            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.manage")) {
                lm.sendMessage(player, lm.getString("Commands.Manage.notOwn")
                        .replace("%owner%", land.getOwnersString()));
                return;
            }

            ManageGui gui = new ManageGui(plugin, player, land);
            gui.display();

        } else {
            // land manage <allCommands>
            switch (args[0]) {
                case "setgreetall":
                    StringBuilder sb1 = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb1.append(args[i]).append(" ");
                    }
                    String newmsg1 = ChatColor.translateAlternateColorCodes('&', sb1.toString());

                    if (newmsg1.isEmpty()) {
                        newmsg1 = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", player.getName());
                    }

                    for (IOwnedLand region : wg.getRegions(player.getUniqueId())) {
                        LandManageEvent landManageEvent = new LandManageEvent(player, region,
                                "GREET_MESSAGE", region.getGreetMessage(), newmsg1);
                        Bukkit.getPluginManager().callEvent(landManageEvent);

                        region.setGreetMessage(newmsg1);
                    }

                    lm.sendMessage(player, lm.getString("Commands.Manage.SetGreet.successful")
                            .replace("%msg%", newmsg1));

                    break;
                case "setfarewellall":
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String newmsg = ChatColor.translateAlternateColorCodes('&', sb.toString());

                    if (newmsg.isEmpty()) {
                        newmsg = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", player.getName());
                    }
                    for (IOwnedLand region : wg.getRegions(player.getUniqueId())) {
                        LandManageEvent landManageEvent = new LandManageEvent(player, region,
                                "FAREWELL_MESSAGE", region.getFarewellMessage(), newmsg);
                        Bukkit.getPluginManager().callEvent(landManageEvent);

                        region.setFarewellMessage(newmsg);
                    }

                    lm.sendMessage(player, lm.getString("Commands.Manage.SetFarewell.successful")
                            .replace("%msg%", newmsg));

                    break;
                case "setgreet":
                    //System.out.println("greet " + Arrays.toString(args));
                    setGreet(player, args, wg.getRegion(player.getLocation().getChunk()), 1);
                    break;
                case "setfarewell":
                    setFarewell(player, args, wg.getRegion(player.getLocation().getChunk()), 1);
                    break;
                default:
                    try {
                        IOwnedLand target = wg.getRegion(args[0]);

                        switch (args[1]) {
                            case "setgreet":
                                setGreet(player, args, target, 2);
                                break;

                            case "setfarewell":
                                setFarewell(player, args, target, 2);
                                break;
                        }

                    } catch (IndexOutOfBoundsException e) {
                        lm.sendMessage(player, lm.getString("Commands.manage.invalidArguments"));
                    }
                    break;
            }
        }
    }


    private void setGreet(Player player, String[] args, IOwnedLand target, int casy) {
        if (target == null) {
            lm.sendMessage(player, lm.getString("Commands.Manage.notOwnFreeLand"));
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


        LandManageEvent landManageEvent = new LandManageEvent(player, target,
                "GREET_MESSAGE", target.getGreetMessage(), newmsg);
        Bukkit.getPluginManager().callEvent(landManageEvent);

        target.setGreetMessage(newmsg);

        lm.sendMessage(player, lm.getString("Commands.Manage.SetGreet.successful")
                .replace("%msg%", newmsg));
    }

    private void setFarewell(Player player, String[] args, IOwnedLand target, int casy) {
        if (target == null) {
            lm.sendMessage(player, lm.getString("Commands.Manage.notOwnFreeLand"));
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

        LandManageEvent landManageEvent = new LandManageEvent(player, target,
                "FAREWELL_MESSAGE", target.getFarewellMessage(), newmsg);
        Bukkit.getPluginManager().callEvent(landManageEvent);

        target.setFarewellMessage(newmsg);
        lm.sendMessage(player, lm.getString("Commands.Manage.SetFarewell.successful")
                .replace("%msg%", newmsg));
    }
}
