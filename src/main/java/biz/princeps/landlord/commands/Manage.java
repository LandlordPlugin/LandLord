package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.ManageGUI;
import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by spatium on 19.07.17.
 */
public class Manage extends LandlordCommand {


    public void onManage(Player player, @Nullable String[] args) {

        OwnedLand land = plugin.getWgHandler().getRegion(player.getLocation().getChunk());

        if (land == null) {
            player.sendMessage(lm.getString("Commands.Manage.notOwnFreeLand"));
            return;
        }

        if (!land.isOwner(player.getUniqueId())) {
            player.sendMessage(lm.getString("Commands.Manage.notOwn")
                    .replace("%owner%", land.printOwners()));
            return;
        }
        if (args.length == 0) {
            ManageGUI gui = new ManageGUI(player, land.getLand());
            gui.display();
            return;
        } else if (args.length >= 3) {

            World world;
            try {
                world = Bukkit.getWorld(args[0].split("_")[0]);
                if (world == null) return;
            } catch (IndexOutOfBoundsException e) {
                player.sendMessage(lm.getString("Commands.manage.invalidArguments"));
                return;
            }
            ProtectedRegion target = plugin.getWgHandler().getWG().getRegionManager(world).getRegion(args[0]);
            if (target == null) return;

            switch (args[1]) {
                case "setgreet":
                    StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String newmsg = sb.toString();

                    land.getLand().setFlag(DefaultFlag.GREET_MESSAGE, newmsg);
                    player.sendMessage(lm.getString("Commands.Manage.SetGreet.successful")
                            .replace("%msg%", newmsg));
                    break;

                case "setfarewell":
                    sb = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    newmsg = sb.toString();

                    land.getLand().setFlag(DefaultFlag.FAREWELL_MESSAGE, newmsg);
                    player.sendMessage(lm.getString("Commands.Manage.SetFarewell.successful")
                            .replace("%msg%", newmsg));
                    break;
            }

        }


    }
}
