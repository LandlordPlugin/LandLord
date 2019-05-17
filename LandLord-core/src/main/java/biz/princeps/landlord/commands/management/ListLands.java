package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGui;
import biz.princeps.landlord.guis.ManageGuiAll;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.google.common.collect.Sets;
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

    public ListLands(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.ListLands.name"),
                pl.getConfig().getString("CommandSettings.ListLands.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ListLands.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ListLands.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isPlayer()) {
            String target = null;
            int page = 0;
            try {
                // check arguments for different sub sub commands like /ll list <name> <pagenr>
                switch (arguments.size()) {
                    case 2:
                        target = arguments.get(0);
                        page = arguments.getInt(1);
                        break;
                    case 1:
                        target = arguments.get(0);
                        break;
                    case 0:
                        break;
                }

            } catch (ArgumentsOutOfBoundsException ignored) {
                properties.sendUsage();
            }

            // Want to know own lands
            if (target == null) {
                onListLands(properties.getPlayer(),
                        plugin.getPlayerManager().get(properties.getPlayer().getUniqueId()), page);
            } else if (properties.getPlayer().hasPermission("landlord.admin.list")) {
                // Admin, Other lands, need to lookup their names
                int finalPage = page;
                String finalTarget = target;
                plugin.getPlayerManager().getOfflinePlayerAsync(target, lPlayer -> {
                    if (lPlayer == null) {
                        // Failure
                        properties.getPlayer().sendMessage(lm.getString("Commands.ListLands.noPlayer")
                                .replace("%player%", finalTarget));
                    } else {
                        // Success
                        onListLands(properties.getPlayer(), (LPlayer) lPlayer, finalPage);
                    }
                });
            }
        }
    }

    private void onListLands(Player sender, IPlayer target, int page) {

        List<IOwnedLand> lands = new ArrayList<>(plugin.getWGManager().getRegions(target.getUuid()));

        if (lands.size() == 0) {
            lm.sendMessage(sender, plugin.getLangManager().getString("Commands.ListLands.noLands"));
            return;
        }

        String mode = plugin.getConfig().getString("CommandSettings.ListLands.mode");

        if (mode.equals("gui")) {
            MultiPagedGUI landGui = new MultiPagedGUI(sender, 5,
                    plugin.getLangManager().getRawString("Commands.ListLands.gui.header")
                            .replace("%player%", target.getName()));

            for (IOwnedLand land : lands) {
                List<String> loreRaw = plugin.getLangManager().getStringList("Commands.ListLands.gui.lore");
                List<String> lore = new ArrayList<>();

                for (String s : loreRaw) {
                    if (s.contains("%flags%")) {
                        String flagFormat = lm.getRawString("Commands.ListLands.gui.flagformat");
                        //       flagformat: '&a%flagname%: &f%flagvalue%'

                        land.getFlags().forEach((flag) -> lore.add(flagFormat
                                .replace("%flagname%", flag.getName())
                                .replace("%friend%", this.formatState(flag.getFriendStatus()))
                                .replace("%all%", this.formatState(flag.getAllStatus()))));
                    } else {
                        lore.add(s.replace("%name%", land.getName())
                                .replace("%realx%", String.valueOf(land.getChunk().getX() * 16))
                                .replace("%realz%", String.valueOf(land.getChunk().getZ() * 16))
                                .replace("%members%", land.getMembersString())
                        );
                    }
                }

                Icon icon = new Icon(new ItemStack(plugin.getMaterialsManager().getGrass()));
                icon.setName(lm.getRawString("Commands.ListLands.gui.itemname")
                        .replace("%name%", land.getName()));
                icon.setLore(lore);
                icon.addClickAction((p) -> {
                    ManageGui manageGUI = new ManageGui(plugin, sender, landGui, land);
                    manageGUI.display();
                });


                landGui.addIcon(icon);
            }

            landGui.setIcon(52, new Icon(new ItemStack(Material.BEACON))
                    .setName(lm.getRawString("Commands.ListLands.gui.manageAll"))
                    .addClickAction((p) -> {
                        ManageGuiAll manageGUIAll = new ManageGuiAll(plugin, sender, landGui, lands);
                        manageGUIAll.display();
                    }));

            landGui.display();

        } else {
            // Chat based system

            List<String> formatted = new ArrayList<>();

            String segment = lm.getRawString("Commands.ListLands.chat.segment");

            lands.forEach(land -> formatted.add(segment.replace("%landname%", land.getName())
                    .replace("%members%", land.getMembersString())));

            String prev = lm.getRawString("Commands.ListLands.chat.previous");
            String next = lm.getRawString("Commands.ListLands.chat.next");


            MultiPagedMessage message = new MultiPagedMessage("/land list",
                    plugin.getLangManager().getRawString("Commands.ListLands.gui.header")
                            .replace("%player%", target.getName()),
                    plugin.getConfig().getInt("CommandSettings.ListLands.landsPerPage"),
                    formatted, prev, next, page);

            plugin.getUtilsManager().sendBasecomponent(sender, message.create());
        }
    }

    private String formatState(boolean bool) {
        if (bool) {
            return lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.deny");
        } else {
            return lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.allow");
        }
    }
}
