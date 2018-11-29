package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUI;
import biz.princeps.landlord.guis.ManageGUIAll;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.Material;
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

        List<OwnedLand> lands = plugin.getWgHandler().getRegionsAsOL(target.getUuid());

        if (lands.size() > 0) {

            String mode = plugin.getConfig().getString("CommandSettings.ListLands.mode");

            if (mode.equals("gui")) {
                MultiPagedGUI landGui = new MultiPagedGUI(sender, 5,
                        plugin.getLangManager().getRawString("Commands.ListLands.gui.header")
                                .replace("%player%", target.getName()));

                for (OwnedLand land : lands) {
                    List<String> loreRaw = plugin.getLangManager().getStringList("Commands.ListLands.gui.lore");
                    List<String> lore = new ArrayList<>();

                    for (String s : loreRaw) {
                        if (s.contains("%flags%")) {
                            String flagFormat = lm.getRawString("Commands.ListLands.gui.flagformat");
                            //       flagformat: '&a%flagname%: &f%flagvalue%'
                            land.getWGLand().getFlags().forEach((flag, value) -> {
                                lore.add(flagFormat.replace("%flagname%", flag.getName())
                                        .replace("%flagvalue%", value.toString()));
                            });

                        } else {
                            lore.add(s.replace("%name%", land.getName())
                                    .replace("%realx%", String.valueOf(land.getChunk().getX() * 16))
                                    .replace("%realz%", String.valueOf(land.getChunk().getZ() * 16))
                                    .replace("%members%", land.printMembers())
                            );
                        }
                    }

                    Icon icon = new Icon(new ItemStack(Material.GRASS));
                    icon.setName(lm.getRawString("Commands.ListLands.gui.itemname")
                            .replace("%name%", land.getName()));
                    icon.setLore(lore);
                    icon.addClickAction((p) -> {
                        ManageGUI manageGUI = new ManageGUI(sender, landGui, land);
                        manageGUI.display();
                    });


                    landGui.addIcon(icon);
                }

                landGui.setIcon(52, new Icon(new ItemStack(Material.BEACON))
                        .setName(lm.getRawString("Commands.ListLands.gui.manageAll"))
                        .addClickAction((p) -> {
                            ManageGUIAll manageGUIAll = new ManageGUIAll(sender, landGui, lands);
                            manageGUIAll.display();
                        }));

                landGui.display();

            } else {
                // Chat based system

                List<String> formatted = new ArrayList<>();

                String segment = lm.getRawString("Commands.ListLands.chat.segment");

                lands.forEach(land -> {
                    formatted.add(segment.replace("%landname%", land.getName())
                            .replace("%members%", land.printMembers()));
                });

                String prev = lm.getRawString("Commands.ListLands.chat.previous");
                String next = lm.getRawString("Commands.ListLands.chat.next");


                MultiPagedMessage message = new MultiPagedMessage("/land list",
                        plugin.getLangManager().getRawString("Commands.ListLands.gui.header")
                                .replace("%player%", target.getName()),
                        plugin.getConfig().getInt("CommandSettings.ListLands.landsPerPage"),
                        formatted, prev, next, page);

                sender.spigot().sendMessage(message.create());
            }
        } else {
            lm.sendMessage(sender, plugin.getLangManager().getString("Commands.ListLands.noLands"));
        }
    }


}
