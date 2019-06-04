package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.*;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AManage extends AbstractGUI {

    private List<IOwnedLand> regions;
    private ILangManager lm;
    private ILandLord plugin;
    private int flagPage = 0;

    private Set<String> toggleMobs;

    private IMaterialsManager mats;

    AManage(ILandLord pl, Player player, String header, List<IOwnedLand> land) {
        super(player, 45, header);
        this.plugin = pl;
        this.regions = land;
        this.lm = plugin.getLangManager();
        this.toggleMobs = new HashSet<>(pl.getConfig().getStringList("Manage.mob-spawning.toggleableMobs"));
        this.mats = pl.getMaterialsManager();
    }

    AManage(ILandLord pl, Player player, MultiPagedGUI landGui, String header, List<IOwnedLand> land) {
        super(player, 54, header, landGui);
        this.regions = land;
        this.plugin = pl;
        this.lm = plugin.getLangManager();
        this.toggleMobs = new HashSet<>(pl.getConfig().getStringList("Manage.mob-spawning.toggleableMobs"));
        this.mats = pl.getMaterialsManager();
    }

    @Override
    protected void create() {
        createFrame();
        createWGFlags();
        createGeneralOptions();
    }

    /**
     * Inserts the frame information, and column information for wg flags.
     * Also fills up the entire gui with gray stained glass.
     */
    private void createFrame() {
        for (int i = 0; i < this.getSize(); i++) {
            Icon placehodler = new Icon(mats.getGreyStainedGlass());
            placehodler.setName(" ");
            this.setIcon(i, placehodler);
        }

        List<String> strings = formatList(lm.getStringList("Commands.Manage.info.description"),
                "%land%", regions.get(0).getName());
        Icon info = new Icon(new ItemStack(Material.ITEM_FRAME));
        info.setName(lm.getRawString("Commands.Manage.info.title"));
        info.setLore(strings);
        this.setIcon(0, info);

        Icon friends = new Icon(mats.getPlayerHead(player.getUniqueId()));
        friends.setName(lm.getRawString("Commands.Manage.friends.title"));
        friends.setLore(lm.getStringList("Commands.Manage.friends.description"));
        this.setIcon(9, friends);

        Icon everyone = new Icon(mats.getWitherSkull());
        everyone.setName(lm.getRawString("Commands.Manage.everyone.title"));
        everyone.setLore(lm.getStringList("Commands.Manage.everyone.description"));
        this.setIcon(18, everyone);
    }

    /**
     * Inserts all wg flags and their toggles for friends+all into the gui.
     */
    private void createWGFlags() {
        IOwnedLand land = regions.get(0);

        List<Icon[]> flags = new ArrayList<>();
        for (ILLFlag flag : land.getFlags()) {
            String flagName = flag.getName();
            if (plugin.getConfig().getBoolean("Manage." + flagName + ".enable") &&
                    player.hasPermission("landlord.player.manage." + flagName)) {
                flags.add(getIcons(flag));
            }
        }

        if (flagPage * 8 > flags.size()) {
            throw new RuntimeException("Invalid page!!");
        }

        for (int i = flagPage * 8; i < flagPage * 8 + 8; i++) {
            if (flags.size() <= i) break;
            Icon[] flagArray = flags.get(i);
            setIcon(i - flagPage * 8 + 1, flagArray[0]);
            setIcon(i - flagPage * 8 + 10, flagArray[1]);
            setIcon(i - flagPage * 8 + 19, flagArray[2]);
        }

        if (flags.size() > 8) {
            // add navigation items
            Icon prev = new Icon(new ItemStack(Material.ARROW));
            prev.setName(lm.getRawString("Commands.Manage.prevArrow"));
            prev.addClickAction((p) -> {
                if (flagPage > 0) {
                    flagPage--;
                    refresh();
                }
            });
            setIcon(34, prev);
            Icon next = new Icon(new ItemStack(Material.ARROW));
            next.setName(lm.getRawString("Commands.Manage.nextArrow"));
            next.addClickAction((p) -> {
                if ((flagPage + 1) * 8 < flags.size()) {
                    flagPage++;
                    refresh();
                }
            });
            setIcon(35, next);
        }

    }

    /**
     * Get all the icons related to a single wg flag.
     * The flag item itself and the friends+all green/red wool toggle items
     * icon[0] = the wg flag item
     * icon[1] = the friend toggle wool
     * icon[2] = the all toggle wool
     *
     * @param flag the flag to get the items for
     * @return an array containing the items.
     */
    private Icon[] getIcons(ILLFlag flag) {
        Icon[] icons = new Icon[3];
        String flagname = flag.getName();
        String title =
                lm.getRawString("Commands.Manage.Allow" + flagname.substring(0, 1).toUpperCase() + flagname.substring(1) + ".title");
        List<String> description =
                lm.getStringList("Commands.Manage.Allow" + flagname.substring(0, 1).toUpperCase() + flagname.substring(1) + ".description");

        Icon item = new Icon(new ItemStack(flag.getMaterial()));
        item.setLore(description);
        item.setName(title);
        icons[0] = item;

        boolean isFriend = flag.getFriendStatus();
        boolean isAll = flag.getAllStatus();

        Icon friend = new Icon(isFriend ? mats.getLimeWool() : mats.getRedWool());
        friend.addClickAction((p) -> {
            if (flag.toggleFriends()) {
                refresh();
            }
        });
        friend.setName(isFriend ? lm.getRawString("Commands.Manage.allow") : lm.getRawString("Commands.Manage.deny"));
        icons[1] = friend;

        Icon all = new Icon(isAll ? mats.getLimeWool() : mats.getRedWool());
        all.addClickAction((p) -> {
            if (flag.toggleAll()) {
                refresh();
            }
        });
        all.setName(isAll ? lm.getRawString("Commands.Manage.allow") : lm.getRawString("Commands.Manage.deny"));
        icons[2] = all;
        return icons;
    }

    /**
     * Creates the general options.
     * Currently: regenerate, setgreet, setfarewell, friends, unclaim, mobspawn
     */
    private void createGeneralOptions() {
        int position = 36;
        IOwnedLand land = regions.get(0);


        // Reminder: Regenerate is not implemented in Manageall, cos it might cos some trouble. Calculating costs
        // might be a bit tedious
        if (plugin.getConfig().getBoolean("Manage.regenerate.enable") && regions.size() == 1 &&
                player.hasPermission("landlord.player.manage.regenerate")) {
            List<String> regenerateDesc = lm.getStringList("Commands.Manage.Regenerate.description");
            double cost = plugin.getConfig().getDouble("ResetCost");
            String costString = (Options.isVaultEnabled() ? plugin.getVaultManager().format(cost) : "-1");

            Icon icon = new Icon(new ItemStack(Material.BARRIER));
            icon.setLore(formatList(regenerateDesc, "%var", costString));
            icon.setName(lm.getRawString("Commands.Manage.Regenerate.title"));
            icon.addClickAction((p) -> {
                if (!land.isOwner(player.getUniqueId())) {
                    return;
                }
                ConfirmationGUI confi = new ConfirmationGUI(p, lm.getRawString("Commands.Manage.Regenerate" +
                        ".confirmation")
                        .replace("%cost%", costString),
                        (p1) -> {
                            boolean flag = true;
                            if (Options.isVaultEnabled()) {
                                if (plugin.getVaultManager().hasBalance(player.getUniqueId(), cost)) {
                                    plugin.getVaultManager().take(player.getUniqueId(), cost);
                                } else {
                                    lm.sendMessage(player, lm.getString("Commands.Manage.Regenerate.notEnoughMoney")
                                            .replace("%cost%", costString)
                                            .replace("%name%", land.getName()));
                                    flag = false;
                                }
                            }
                            if (flag) {
                                LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                        null, "REGENERATE", "REGENERATE");
                                Bukkit.getPluginManager().callEvent(landManageEvent);
                                player.getWorld().regenerateChunk(land.getChunk().getX(), land.getChunk().getZ());
                                /*
                                 * From Spigot JavaDocs :
                                 * Deprecated. regenerating a single chunk is not likely to produce the same chunk as before as terrain decoration may be spread across chunks.
                                 * Use of this method should be avoided as it is known to produce buggy results.Regenerates the Chunk at the specified coordinates
                                 *
                                 * "Impossible" to fix that with SpigotApi, PaperApi perhaps, but it requires sacrifices :/
                                 * I propose to use a Worldedit selection and apply "//regen".
                                 *
                                */
                                lm.sendMessage(player, lm.getString("Commands.Manage.Regenerate.success")
                                        .replace("%land%", land.getName()));
                                display();
                            }

                        }, (p2) -> {
                    lm.sendMessage(player, lm.getString("Commands.Manage.Regenerate.abort")
                            .replace("%land%", land.getName()));
                    display();
                }, this);

                confi.setConfirm(lm.getRawString("Confirmation.accept"));
                confi.setDecline(lm.getRawString("Confirmation.decline"));

                confi.display();

            });
            this.setIcon(position++, icon);
        } else {
            System.out.println("no perms");
        }

        // Set greet icon
        if (plugin.getConfig().getBoolean("Manage.setgreet.enable") &&
                player.hasPermission("landlord.player.manage.setgreet")) {
            String currentGreet = land.getGreetMessage();
            List<String> greetDesc = lm.getStringList("Commands.Manage.SetGreet.description");

            Icon icon = new Icon(new ItemStack(Material.valueOf(plugin.getConfig().getString("Manage.setgreet.item"))));
            icon.setName(lm.getRawString("Commands.Manage.SetGreet.title"));
            icon.setLore(formatList(greetDesc, "%var%", currentGreet));
            icon.addClickAction(((p) -> {
                p.closeInventory();
                ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Manage.SetGreet.clickMsg"));
                if (regions.size() > 1)
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setgreetall "));
                else
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setgreet "));

                plugin.getUtilsManager().sendBasecomponent(p, builder.create());
            }));
            this.setIcon(position++, icon);
        }

        // set farewell icon
        if (plugin.getConfig().getBoolean("Manage.setfarewell.enable") &&
                player.hasPermission("landlord.player.manage.setfarewell")) {
            List<String> farewellDesc = lm.getStringList("Commands.Manage.SetFarewell.description");
            String currentFarewell = land.getFarewellMessage();

            Icon icon = new Icon(new ItemStack(Material.valueOf(plugin.getConfig().getString("Manage.setfarewell" +
                    ".item"))));
            icon.setName(lm.getRawString("Commands.Manage.SetFarewell.title"));
            icon.setLore(formatList(farewellDesc, "%var%", currentFarewell));
            icon.addClickAction(((p) -> {
                p.closeInventory();
                ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Manage.SetFarewell.clickMsg"));
                if (regions.size() > 1) {
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setfarewellall "));
                } else {
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setfarewell "));
                }
                plugin.getUtilsManager().sendBasecomponent(p, builder.create());
            }));
            this.setIcon(position++, icon);
        }

        // set friends icon
        if (plugin.getConfig().getBoolean("Manage.friends.enable") &&
                player.hasPermission("landlord.player.manage.friends")) {
            Icon icon = new Icon(mats.getPlayerHead(player.getUniqueId()));
            icon.setName(lm.getRawString("Commands.Manage.ManageFriends.title"));
            icon.setLore(lm.getStringList("Commands.Manage.ManageFriends.description"));

            Set<UUID> friends = land.getFriends();
            MultiPagedGUI friendsGui = new MultiPagedGUI(player, (int) Math.ceil((double) friends.size() / 9.0),
                    lm.getRawString("Commands.Manage.ManageFriends.title"), new ArrayList<>(), this) {
            };

            //TODO test this
            Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
                friends.forEach(id -> {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(id);
                    Icon friend = new Icon(mats.getPlayerHead(id));
                    friend.setName(op.getName());
                    friend.setLore(formatFriendsSegment(id));
                    friend.addClickAction((player) -> {
                        ConfirmationGUI confirmationGUI = new ConfirmationGUI(player, lm.getRawString("Commands" +
                                ".Manage" +
                                ".ManageFriends.unfriend")
                                .replace("%player%", op.getName()),
                                (p) -> {
                                    friendsGui.removeIcon(friendsGui.filter(op.getName()).get(0));
                                    for (IOwnedLand region : regions) {
                                        Bukkit.dispatchCommand(player,
                                                "land unfriend " + region.getName() + " " + op.getName());
                                    }
                                    player.closeInventory();
                                    friendsGui.display();
                                },
                                (p) -> {
                                    player.closeInventory();
                                    friendsGui.display();
                                }, friendsGui);
                        confirmationGUI.setConfirm(lm.getRawString("Confirmation.accept"));
                        confirmationGUI.setDecline(lm.getRawString("Confirmation.decline"));
                        confirmationGUI.display();
                        friendsGui.addIcon(friend);
                    });
                });
            });

            icon.addClickAction((p) -> friendsGui.display());

            this.setIcon(position++, icon);
        }


        // unclaim
        if (plugin.getConfig().getBoolean("Manage.unclaim.enable") &&
                player.hasPermission("landlord.player.manage.unclaim")) {
            Icon icon = new Icon(new ItemStack(Material.valueOf(plugin.getConfig().getString("Manage.unclaim.item"))));
            icon.setName(lm.getRawString("Commands.Manage.Unclaim.title"));
            icon.setLore(lm.getStringList("Commands.Manage.Unclaim.description"));
            icon.addClickAction(((p) -> {
                ConfirmationGUI gui = new ConfirmationGUI(p, lm.getRawString("Commands.Manage.Unclaim" +
                        ".confirmationTitle").replace("%land%", land.getName()),
                        (p1) -> {
                            if (regions.size() > 1) {
                                Bukkit.dispatchCommand(p, "ll unclaimall");
                            } else {
                                Bukkit.dispatchCommand(p, "ll unclaim " + land.getName());
                            }
                            p.closeInventory();
                        },
                        (p1) -> {
                            p.closeInventory();
                            display();
                        }, this);
                gui.setConfirm(lm.getRawString("Confirmation.accept"));
                gui.setDecline(lm.getRawString("Confirmation.decline"));
                gui.display();
            }));
            this.setIcon(position++, icon);
        }

        // spawn management
        if (plugin.getConfig().getBoolean("Manage.mob-spawning.enable") &&
                player.hasPermission("landlord.player.manage.mobspawn")) {
            String title = lm.getRawString("Commands.Manage.AllowMob-spawning.title");
            Icon icon = new Icon(new ItemStack(Material.valueOf(plugin.getConfig().getString("Manage.mob-spawning" +
                    ".item"))));
            icon.setName(title);
            icon.setLore(lm.getStringList("Commands.Manage.AllowMob-spawning.description"));

            icon.addClickAction((p) -> {
                List<Icon> icons = new ArrayList<>();
                List<String> lore = lm.getStringList("Commands.Manage.AllowMob-spawning.toggleItem.description");

                MultiPagedGUI gui = new MultiPagedGUI(p, 5, title, icons, this) {
                };
                String titleMob = lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.title");
                for (IMob m : plugin.getMobManager().values()) {
                    // Skip mob if its not in the list, because that means this mob should not be manageable
                    if (!toggleMobs.contains(m.getName())) {
                        continue;
                    }

                    Icon mob = new Icon(m.getEgg());
                    mob.setName(titleMob.replace("%mob%", m.getNiceName()));
                    mob.setLore(formatList(formatList(lore, "%value%", formatMobState(land.isMobDenied(m))),
                            "%mob%", m.getNiceName()));
                    gui.addIcon(mob);

                    mob.addClickAction((p1) -> {
                        regions.forEach(l -> l.toggleMob(m));
                        mob.setLore(formatList(formatList(lore, "%value%", formatMobState(land.isMobDenied(m))),
                                "%mob%", m.getNiceName()));
                        gui.refresh();
                    });
                }
                gui.display();
            });

            this.setIcon(position++, icon);
        }
    }

    private String formatMobState(boolean bool) {
        if (bool) {
            return lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.deny");
        } else {
            return lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.allow");
        }
    }

    private List<String> formatList(List<String> list, String toReplace, String newValue) {
        List<String> newList = new ArrayList<>();
        list.forEach(s -> newList.add(s.replace(toReplace, newValue)));
        return newList;
    }

    private List<String> formatFriendsSegment(UUID id) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(id);
        List<String> toReturn = new ArrayList<>();

        IPlayer offline = plugin.getPlayerManager().getOfflineSync(id);
        List<String> stringList = lm.getStringList("Commands.Manage.ManageFriends.friendSegment");
        String lastseen;

        if (op.isOnline()) {
            lastseen = lm.getRawString("Commands.Info.online");
        } else {
            if (offline != null) {
                lastseen = offline.getLastSeen().toString();
            } else {
                lastseen = "NaN";
            }
        }
        stringList.forEach(s -> {
            String ss = s.replace("%seen%", lastseen);
            toReturn.add(ss);
        });

        return toReturn;
    }


}

