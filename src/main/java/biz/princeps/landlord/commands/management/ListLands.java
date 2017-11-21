package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUI;
import biz.princeps.landlord.guis.ManageGUIAll;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.chat.MultiPagedComponentMessage;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spatium on 18.07.17.
 */
public class ListLands extends LandlordCommand {

    private String header = plugin.getLangManager().getRawString("Commands.ListLands.header");

    public void onListLands(Player player, int page) {

        List<ProtectedRegion> lands = new ArrayList<>();

        for (ProtectedRegion protectedRegion : plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegions().values()) {
            if (protectedRegion.isOwner(plugin.getWgHandler().getWG().wrapPlayer(player))) {
                lands.add(protectedRegion);
            }
        }
        if (lands.size() > 0) {

            String mode = plugin.getConfig().getString("CommandSettings.ListLands.mode");
            List<OwnedLand> landsOfPlayer = new ArrayList<>();

            for (World world : Bukkit.getWorlds()) {
                for (ProtectedRegion pr : plugin.getWgHandler().getRegions(player.getUniqueId(), world)) {
                    landsOfPlayer.add(plugin.getLand(pr));
                }
            }

            if (mode.equals("gui")) {
                MultiPagedGUI landGui = new MultiPagedGUI(player, 5, header);

                lands.forEach(land -> landGui.addIcon(new Icon(new ItemStack(Material.GRASS))
                        .setName(land.getId())
                        .addClickAction((p, ic) -> {
                                    ManageGUI manageGui = new ManageGUI(player, landGui, plugin.getWgHandler().getRegion(land));
                                    manageGui.setTitle(manageGui.getRawTitle().replace("%realZ", plugin.getLand(land).getChunk().getZ() * 16 + "").replace("%realX", plugin.getLand(land).getChunk().getX() * 16 + ""));
                                    manageGui.display();
                                }
                        )
                ));

                landGui.setIcon(52, new Icon(new ItemStack(Material.BEACON))
                        .setName(lm.getRawString("Commands.ListLands.manageAll"))
                        .addClickAction((p, ic2) -> {


                            ManageGUIAll manageGUIAll = new ManageGUIAll(player, landGui, landsOfPlayer);
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


                MultiPagedMessage message = new MultiPagedMessage("/land list", header, plugin.getConfig().getInt("CommandSettings.ListLands.landsPerPage"),
                        formatted, prev, next, page);

                player.spigot().sendMessage(message.create());
            }
        } else {
            player.sendMessage(plugin.getLangManager().getString("Commands.ListLands.noLands"));
        }
    }


}
