package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUIAll;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 18/7/17
 */
public class ListLands extends LandlordCommand {

    public void onListLands(Player sender, LPlayer target, int page) {

        List<ProtectedRegion> lands = new ArrayList<>(plugin.getWgHandler().getRegions(target.getUuid()));

        if (lands.size() > 0) {

            String mode = plugin.getConfig().getString("CommandSettings.ListLands.mode");

            if (mode.equals("gui")) {
                MultiPagedGUI landGui = new MultiPagedGUI(sender, 5, plugin.getLangManager().getRawString("Commands.ListLands.header").replace("%player%", target.getName()));

                lands.forEach(land -> landGui.addIcon(new Icon(new ItemStack(Material.GRASS))
                                .setName(land.getId())
                       /*
                        * ATTENTION; THIS IS A HUGE EXPLOIT!!!!! PLAYERS ARE ABLE TO MANAGE ANY LAND BY OPENING THE LAND LIST GUI
                        .addClickAction((p, ic) -> {
                                    ManageGUI manageGui = new ManageGUI(sender, landGui, plugin.getWgHandler().getRegion(land));
                                    manageGui.setTitle(manageGui.getRawTitle().replace("%realZ", plugin.getLand(land).getChunk().getZ() * 16 + "").replace("%realX", plugin.getLand(land).getChunk().getX() * 16 + ""));
                                    manageGui.display();
                                }
                        )
                        */
                ));

                landGui.setIcon(52, new Icon(new ItemStack(Material.BEACON))
                        .setName(lm.getRawString("Commands.ListLands.manageAll"))
                        .addClickAction((p, ic2) -> {
                            ManageGUIAll manageGUIAll = new ManageGUIAll(sender, landGui, plugin.getWgHandler().getRegionsAsOL(target.getUuid()));
                            manageGUIAll.display();
                        }));

                landGui.display();

            } else {
                // Chat based system

                List<String> formatted = new ArrayList<>();

                String segment = lm.getRawString("Commands.ListLands.chat.segment");

                lands.forEach(land -> {
                    OwnedLand ol = plugin.getLand(land);
                    formatted.add(segment.replace("%landname%", ol.getName()).replace("%members%", ol.printMembers()));
                });

                String prev = lm.getRawString("Commands.ListLands.chat.previous");
                String next = lm.getRawString("Commands.ListLands.chat.next");


                MultiPagedMessage message = new MultiPagedMessage("/land list",
                        plugin.getLangManager().getRawString("Commands.ListLands.header").replace("%player%",
                                target.getName()), plugin.getConfig().getInt("CommandSettings.ListLands.landsPerPage"),
                        formatted, prev, next, page);

                sender.spigot().sendMessage(message.create());
            }
        } else {
            sender.sendMessage(plugin.getLangManager().getString("Commands.ListLands.noLands"));
        }
    }


}
