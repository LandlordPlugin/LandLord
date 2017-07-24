package biz.princeps.landlord.commands;

import biz.princeps.landlord.guis.ManageGUI;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Created by spatium on 19.07.17 / 11:55.
 */
class Manage extends LandlordCommand {


    void onManage(Player player, @Nullable String[] args) {

        OwnedLand land = plugin.getWgHandler().getRegion(player.getLocation().getChunk());

        if (land == null) {
            player.sendMessage(lm.getString("Commands.Manage.notOwnFreeLand"));
            return;
        }

        if (!land.isOwner(player.getUniqueId()) && !player.hasPermission("landlord.admin.manage")) {
            player.sendMessage(lm.getString("Commands.Manage.notOwn")
                    .replace("%owner%", land.printOwners()));
            return;
        }

        // land manage
        if (args.length == 0) {
            ManageGUI gui = new ManageGUI(player, land.getLand());
            gui.display();
        } else
            // land manage landid cmd (args[1])
            if (args.length >= 2) {

                World world;
                try {
                    world = Bukkit.getWorld(args[0].split("_")[0]);
                } catch (IndexOutOfBoundsException e) {
                    player.sendMessage(lm.getString("Commands.manage.invalidArguments"));
                    return;
                }
                if (!Bukkit.getWorlds().contains(world)) {
                    if (args[0].equals("setgreetall")) {
                        StringBuilder sb1 = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb1.append(args[i]).append(" ");
                        }
                        String newmsg1 = sb1.toString();

                        for (ProtectedRegion protectedRegion : plugin.getWgHandler().getRegions(player.getUniqueId(), player.getWorld())) {
                            protectedRegion.setFlag(DefaultFlag.GREET_MESSAGE, newmsg1);
                        }

                        player.sendMessage(lm.getString("Commands.Manage.SetGreet.successful")
                                .replace("%msg%", newmsg1));

                    } else if (args[0].equals("setfarewellall")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        String newmsg = sb.toString();

                        for (ProtectedRegion protectedRegion : plugin.getWgHandler().getRegions(player.getUniqueId(), player.getWorld())) {
                            protectedRegion.setFlag(DefaultFlag.FAREWELL_MESSAGE, newmsg);
                        }

                        player.sendMessage(lm.getString("Commands.Manage.SetFarewell.successful")
                                .replace("%msg%", newmsg));
                    }
                    return;
                }

                ProtectedRegion target = plugin.getWgHandler().getWG().getRegionManager(world).getRegion(args[0]);

                switch (args[1]) {
                    case "setgreet":
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        String newmsg = sb.toString();

                        target.setFlag(DefaultFlag.GREET_MESSAGE, newmsg);
                        player.sendMessage(lm.getString("Commands.Manage.SetGreet.successful")
                                .replace("%msg%", newmsg));
                        break;

                    case "setfarewell":
                        sb = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        newmsg = sb.toString();

                        target.setFlag(DefaultFlag.FAREWELL_MESSAGE, newmsg);
                        player.sendMessage(lm.getString("Commands.Manage.SetFarewell.successful")
                                .replace("%msg%", newmsg));
                        break;
                }
            }


    }
}
