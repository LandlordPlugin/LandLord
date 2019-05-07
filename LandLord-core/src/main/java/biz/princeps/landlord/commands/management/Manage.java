package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/7/17
 * <p>
 * If you are looking for the gui, check AbstractManage!
 */
public class Manage extends LandlordCommand {

    // TODO Clean this mess up
    public void onManage(Player player, String[] args) {

        // land manage
        if (args.length == 0 || (args.length == 1 && !args[0].equals("setgreet")
                && !args[0].equals("setgreetall")
                && !args[0].equals("setfarewell")
                && !args[0].equals("setfarewellall"))) {

            IOwnedLand land;
            if (args.length == 0) {
                land = plugin.getWgproxy().getRegion(player.getLocation().getChunk());
            } else {
                // land manage <landid>
                land = plugin.getWgproxy().getRegion(args[0]);
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

            ManageGUI gui = new ManageGUI(player, land);
            gui.display();

        } else {
            // land manage <allCommands>
            switch (args[0]) {
                case "setgreetall":
                    StringBuilder sb1 = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb1.append(args[i]).append(" ");
                    }
                    String newmsg1 = sb1.toString();
                    if (newmsg1.isEmpty()) {
                        newmsg1 = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", player.getName());
                    }

                    for (IOwnedLand region : plugin.getWgproxy().getRegions(player.getUniqueId())) {
                        LandManageEvent landManageEvent = new LandManageEvent(player, region,
                                "GREET_MESSAGE", region.getFlagValue("GREET_MESSAGE"), newmsg1);
                        Bukkit.getPluginManager().callEvent(landManageEvent);

                        region.setFlagValue("GREET_MESSAGE", newmsg1);
                    }

                    lm.sendMessage(player, lm.getString("Commands.Manage.SetGreet.successful")
                            .replace("%msg%", newmsg1));

                    break;
                case "setfarewellall":
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String newmsg = sb.toString();
                    if (newmsg.isEmpty()) {
                        newmsg = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", player.getName());
                    }
                    for (IOwnedLand region : plugin.getWgproxy().getRegions(player.getUniqueId())) {
                        LandManageEvent landManageEvent = new LandManageEvent(player, region,
                                "FAREWELL_MESSAGE", region.getFlagValue("FAREWELL_MESSAGE"), newmsg);
                        Bukkit.getPluginManager().callEvent(landManageEvent);

                        region.setFlagValue("FAREWELL_MESSAGE", newmsg);
                    }

                    lm.sendMessage(player, lm.getString("Commands.Manage.SetFarewell.successful")
                            .replace("%msg%", newmsg));

                    break;
                case "setgreet":
                    //System.out.println("greet " + Arrays.toString(args));
                    setGreet(player, args, plugin.getWgproxy().getRegion(player.getLocation()), 1);

                    break;
                case "setfarewell":
                    setFarewell(player, args, plugin.getWgproxy().getRegion(player.getLocation()), 1);
                    break;
                default:
                    try {
                        IOwnedLand target = plugin.getWgproxy().getRegion(args[0]);

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
        StringBuilder sb = new StringBuilder();
        for (int i = casy; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newmsg = sb.toString();

        if (newmsg.isEmpty()) {
            newmsg = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", player.getName());
        }

        LandManageEvent landManageEvent = new LandManageEvent(player, target,
                "GREET_MESSAGE", target.getFlagValue("GREET_MESSAGE"), newmsg);
        Bukkit.getPluginManager().callEvent(landManageEvent);

        target.setFlagValue("GREET_MESSAGE", newmsg);

        lm.sendMessage(player, lm.getString("Commands.Manage.SetGreet.successful")
                .replace("%msg%", newmsg));
    }

    private void setFarewell(Player player, String[] args, IOwnedLand target, int casy) {
        StringBuilder sb = new StringBuilder();
        for (int i = casy; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newmsg = sb.toString();
        if (newmsg.isEmpty()) {
            newmsg = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", player.getName());
        }

        LandManageEvent landManageEvent = new LandManageEvent(player, target,
                "FAREWELL_MESSAGE", target.getFlagValue("FAREWELL_MESSAGE"), newmsg);
        Bukkit.getPluginManager().callEvent(landManageEvent);

        target.setFlagValue("FAREWELL_MESSAGE", newmsg);
        lm.sendMessage(player, lm.getString("Commands.Manage.SetFarewell.successful")
                .replace("%msg%", newmsg));
    }
}
