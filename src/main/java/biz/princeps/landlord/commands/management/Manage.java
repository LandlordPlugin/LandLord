package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUI;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 19/7/17
 */
public class Manage extends LandlordCommand {

    public void onManage(Player player, String[] args) {

        // land manage
        if (args.length == 0 || (args.length == 1 && !args[0].equals("setgreet")
                && !args[0].equals("setgreetall")
                && !args[0].equals("setfarewell")
                && !args[0].equals("setfarewellall"))) {

            OwnedLand land;
            if (args.length == 0) {
                land = plugin.getWgHandler().getRegion(player.getLocation().getChunk());
            } else {
                // land manage <landid>
                land = OwnedLand.fromString(args[0]);
            }

            if (land == null) {
                player.sendMessage(lm.getString("Commands.Manage.notOwnFreeLand"));
                return;
            }

            if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.manage")) {
                player.sendMessage(lm.getString("Commands.Manage.notOwn")
                        .replace("%owner%", land.printOwners()));
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

                    for (ProtectedRegion protectedRegion : plugin.getWgHandler().getRegions(player.getUniqueId())) {
                        protectedRegion.setFlag(Flags.GREET_MESSAGE, newmsg1);
                    }

                    player.sendMessage(lm.getString("Commands.Manage.SetGreet.successful")
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
                    for (ProtectedRegion protectedRegion : plugin.getWgHandler().getRegions(player.getUniqueId())) {
                        protectedRegion.setFlag(Flags.FAREWELL_MESSAGE, newmsg);
                    }

                    player.sendMessage(lm.getString("Commands.Manage.SetFarewell.successful")
                            .replace("%msg%", newmsg));

                    break;
                case "setgreet":
                    //System.out.println("greet " + Arrays.toString(args));
                    setGreet(player, args, plugin.getLand(player.getLocation()).getWGLand(), 1);

                    break;
                case "setfarewell":
                    setFarewell(player, args, plugin.getLand(player.getLocation()).getWGLand(), 1);
                    break;
                default:
                    try {
                        World world = Bukkit.getWorld(args[0].split("_")[0]);
                        if (Bukkit.getWorlds().contains(world)) {
                            RegionManager rm = plugin.getWgHandler().getRegionManager(world);
                            if (rm != null) {
                                ProtectedRegion target = rm.getRegion(args[0]);

                                switch (args[1]) {
                                    case "setgreet":
                                        setGreet(player, args, target, 2);
                                        break;

                                    case "setfarewell":
                                        setFarewell(player, args, target, 2);
                                        break;
                                }
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        player.sendMessage(lm.getString("Commands.manage.invalidArguments"));
                    }
                    break;
            }
        }
    }


    private void setGreet(Player player, String[] args, ProtectedRegion target, int casy) {
        StringBuilder sb = new StringBuilder();
        for (int i = casy; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newmsg = sb.toString();

        if (newmsg.isEmpty()) {
            newmsg = lm.getRawString("Alerts.defaultGreeting").replace("%owner%", player.getName());
        }

        target.setFlag(Flags.GREET_MESSAGE, newmsg);
        player.sendMessage(lm.getString("Commands.Manage.SetGreet.successful")
                .replace("%msg%", newmsg));
    }

    private void setFarewell(Player player, String[] args, ProtectedRegion target, int casy) {
        StringBuilder sb = new StringBuilder();
        for (int i = casy; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String newmsg = sb.toString();
        if (newmsg.isEmpty()) {
            newmsg = lm.getRawString("Alerts.defaultFarewell").replace("%owner%", player.getName());
        }
        target.setFlag(Flags.FAREWELL_MESSAGE, newmsg);
        player.sendMessage(lm.getString("Commands.Manage.SetFarewell.successful")
                .replace("%msg%", newmsg));
    }
}
