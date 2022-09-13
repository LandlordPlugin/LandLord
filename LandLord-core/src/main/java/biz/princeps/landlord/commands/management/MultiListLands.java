package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.guis.ManageGui;
import biz.princeps.landlord.guis.ManageGuiAll;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.lib.PrincepsLib;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MultiListLands extends LandlordCommand {

    private final IWorldGuardManager wg;

    public MultiListLands(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.MultiListLands.name"),
                plugin.getConfig().getString("CommandSettings.MultiListLands.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MultiListLands.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.MultiListLands.aliases")));
        this.wg = plugin.getWGManager();
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        if (properties.isPlayer()) {
            String target = null;
            int page = 0;
            MultiMode mode;
            int radius;
            try {
                // check arguments for different sub sub commands like /ll list <mode> <radius> <name> <pagenr>
                switch (arguments.size()) {
                    case 4:
                        target = arguments.get(2);
                        page = arguments.getInt(3);
                        break;
                    case 3:
                        target = arguments.get(2);
                        break;
                }

                mode = MultiMode.valueOf(arguments.get(0).toUpperCase());
                radius = arguments.getInt(1);
            } catch (ArgumentsOutOfBoundsException ignored) {
                properties.sendUsage();
                return;
            }

            // Want to know own lands
            if (target == null) {
                onListLands(properties.getPlayer(), mode, radius,
                        plugin.getPlayerManager().get(properties.getPlayer().getUniqueId()), page);
            } else if (properties.getPlayer().hasPermission("landlord.admin.list")) {
                // Admin, Other lands, need to lookup their names

                String finalTarget = target;
                int finalPage = page;
                plugin.getPlayerManager().getOffline(target, (lPlayer) -> {
                    if (lPlayer == null) {
                        // Failure
                        properties.getPlayer().sendMessage(lm.getString(properties.getPlayer(), "Commands.MultiListLands.noPlayer")
                                .replace("%player%", finalTarget));
                    } else {
                        // Success
                        onListLands(properties.getPlayer(), mode, radius, lPlayer, finalPage);
                    }
                });
            }
        }
    }

    private void onListLands(Player sender, MultiMode mode, int radius, IPlayer target, int page) {
        if (isDisabledWorld(sender)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                List<IOwnedLand> lands = new ArrayList<>(mode.getLandsOf(radius, sender.getLocation(), target.getUuid(), wg));

                if (lands.isEmpty()) {
                    lm.sendMessage(sender, plugin.getLangManager().getString("Commands.MultiListLands.noLands"));
                    return;
                }

                String confirmMode = plugin.getConfig().getString("CommandSettings.MultiListLands.mode");

                if (confirmMode.equals("gui")) {
                    MultiPagedGUI landGui = new MultiPagedGUI(plugin, sender, 5,
                            plugin.getLangManager().getRawString("Commands.MultiListLands.gui.header")
                                    .replace("%player%", target.getName())) {
                        @Override
                        protected void generateStaticIcons() {
                            setIcon(52, new Icon(new ItemStack(Material.BEACON))
                                    .setName(lm.getRawString("Commands.MultiListLands.gui.multiManage"))
                                    .addClickAction((p) -> {
                                        ManageGuiAll manageGUIAll = new ManageGuiAll(plugin, sender, this, lands, ManageMode.MULTI, mode, radius);
                                        manageGUIAll.display();
                                    }));
                        }
                    };

                    for (IOwnedLand land : lands) {
                        List<String> loreRaw = plugin.getLangManager().getStringList("Commands.MultiListLands.gui.lore");
                        List<String> lore = new ArrayList<>();

                        for (String s : loreRaw) {
                            if (s.contains("%flags%")) {
                                String flagFormat = lm.getRawString("Commands.MultiListLands.gui.flagformat");
                                //       flagformat: '&a%flagname%: &f%flagvalue%'

                                for (ILLFlag flag : land.getFlags()) {
                                    lore.add(flagFormat
                                            .replace("%flagname%", flag.getName())
                                            .replace("%friend%", formatState(flag.getFriendStatus()))
                                            .replace("%all%", formatState(flag.getAllStatus())));
                                }
                            } else {
                                lore.add(s.replace("%name%", land.getName())
                                        .replace("%realx%", String.valueOf(land.getChunkX() << 4))
                                        .replace("%realz%", String.valueOf(land.getChunkZ() << 4))
                                        .replace("%members%", land.getMembersString())
                                );
                            }
                        }

                        Icon icon = new Icon(new ItemStack(plugin.getMaterialsManager().getWorldGrass(land.getWorld())));
                        icon.setName(lm.getRawString("Commands.MultiListLands.gui.itemname")
                                .replace("%name%", land.getName()));
                        icon.setLore(lore);
                        icon.addClickAction((p) -> {
                            boolean hasPerm = false;
                            for (String s : Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Manage.permissions"))) {
                                if (p.hasPermission(s)) {
                                    hasPerm = true;
                                    break;
                                }
                            }
                            if (hasPerm) {
                                ManageGui manageGUI = new ManageGui(plugin, sender, landGui, land);
                                manageGUI.display();
                            }
                        });

                        landGui.addIcon(icon);
                    }

                    if (sender.isValid()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                landGui.display();
                            }
                        }.runTask(plugin);
                    }
                } else {
                    // Chat based system
                    List<String> formatted = new ArrayList<>();
                    String segment = lm.getRawString("Commands.MultiListLands.chat.segment");

                    for (IOwnedLand land : lands) {
                        formatted.add(segment.replace("%landname%", land.getName())
                                .replace("%members%", land.getMembersString()));
                    }

                    String prev = lm.getRawString("Commands.MultiListLands.chat.previous");
                    String next = lm.getRawString("Commands.MultiListLands.chat.next");

                    MultiPagedMessage message = new MultiPagedMessage(PrincepsLib.getCommandManager()
                            .getCommand(Landlordbase.class).getCommandString(ListLands.class),
                            plugin.getLangManager().getRawString("Commands.MultiListLands.gui.header")
                                    .replace("%player%", target.getName()),
                            plugin.getConfig().getInt("CommandSettings.MultiListLands.landsPerPage"),
                            formatted, prev, next, page);

                    if (sender.isValid()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                plugin.getUtilsManager().sendBasecomponent(sender, message.create());
                            }
                        }.runTask(plugin);
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private String formatState(boolean bool) {
        if (!bool) {
            return lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.deny");
        } else {
            return lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.allow");
        }
    }

}
