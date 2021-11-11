package biz.princeps.landlord.guis;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.IMaterialsManager;
import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.api.ManageMode;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.api.events.LandManageEvent;
import biz.princeps.landlord.commands.Landlordbase;
import biz.princeps.landlord.commands.friends.Unfriend;
import biz.princeps.landlord.commands.management.Manage;
import biz.princeps.landlord.multi.MultiMode;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.crossversion.MaterialProxy;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AManage extends AbstractGUI {

    private final List<IOwnedLand> regions;
    private final ILangManager lm;
    private final ILandLord plugin;
    private final Set<String> toggleMobs;
    private final IMaterialsManager mats;
    private final ManageMode manageMode;
    private final MultiMode multiMode;
    private final int radius;
    private int flagPage = 0;

    AManage(ILandLord plugin, Player player, String header, List<IOwnedLand> land, ManageMode manageMode, MultiMode multiMode, int radius) {
        super(plugin, player, 45, header);
        this.plugin = plugin;
        this.regions = land;
        this.lm = plugin.getLangManager();
        this.toggleMobs = new HashSet<>(plugin.getConfig().getStringList("Manage.mob-spawning.toggleableMobs"));
        this.mats = plugin.getMaterialsManager();
        this.manageMode = manageMode;
        this.multiMode = multiMode;
        this.radius = radius;
    }

    AManage(ILandLord plugin, Player player, MultiPagedGUI landGui, String header, List<IOwnedLand> land, ManageMode manageMode, MultiMode multiMode, int radius) {
        super(plugin, player, 54, header, landGui);
        this.plugin = plugin;
        this.regions = land;
        this.lm = plugin.getLangManager();
        this.toggleMobs = new HashSet<>(plugin.getConfig().getStringList("Manage.mob-spawning.toggleableMobs"));
        this.mats = plugin.getMaterialsManager();
        this.manageMode = manageMode;
        this.multiMode = multiMode;
        this.radius = radius;
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
            throw new RuntimeException("Invalid page!");
        }

        for (int i = flagPage * 8; i < flagPage * 8 + 8; i++) {
            if (flags.size() <= i)
                break;
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
                //Avoid lag with lots of lands
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (IOwnedLand land : regions.subList(1, regions.size())) {
                            for (ILLFlag landFlag : land.getFlags()) {
                                if (!flag.getName().equals(landFlag.getName())
                                    || flag.getFriendStatus() == landFlag.getFriendStatus())
                                    continue;

                                landFlag.toggleFriends();

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                                landFlag.getName(), !landFlag.getFriendStatus(), landFlag.getFriendStatus());
                                        plugin.getServer().getPluginManager().callEvent(landManageEvent);
                                    }
                                }.runTask(plugin);
                            }
                        }
                    }
                }.runTaskAsynchronously(plugin);
            }
        });
        friend.setName(isFriend ? lm.getRawString("Commands.Manage.allow") : lm.getRawString("Commands.Manage.deny"));
        icons[1] = friend;

        Icon all = new Icon(isAll ? mats.getLimeWool() : mats.getRedWool());
        all.addClickAction((p) -> {
            if (flag.toggleAll()) {
                refresh();
                //Avoid lag with lots of lands
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (IOwnedLand land : regions.subList(1, regions.size())) {
                            for (ILLFlag landFlag : land.getFlags()) {
                                if (!flag.getName().equals(landFlag.getName())
                                    || flag.getAllStatus() == landFlag.getAllStatus())
                                    continue;

                                landFlag.toggleAll();

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                                landFlag.getName(), !landFlag.getFriendStatus(), landFlag.getFriendStatus());
                                        plugin.getServer().getPluginManager().callEvent(landManageEvent);
                                    }
                                }.runTask(plugin);
                            }
                        }
                    }
                }.runTaskAsynchronously(plugin);
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

        String managecmd = PrincepsLib.getCommandManager().getCommand(Landlordbase.class)
                .getCommandString(Manage.class);
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
                ComponentBuilder builder = new ComponentBuilder(lm.getString(player, "Commands.Manage.SetGreet.clickMsg"));

                switch (manageMode) {
                    case ALL:
                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, managecmd + " setgreetall "));
                        break;
                    case MULTI:
                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, managecmd + " multisetgreet " + multiMode + " " + radius));
                        break;
                    case ONE:
                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, managecmd + " setgreet "));
                        break;
                }

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
                ComponentBuilder builder = new ComponentBuilder(lm.getString(player, "Commands.Manage.SetFarewell.clickMsg"));

                switch (manageMode) {
                    case ALL:
                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, managecmd + " setfarewellall "));
                        break;
                    case MULTI:
                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, managecmd + " multisetfarewell " + multiMode + " " + radius));
                        break;
                    case ONE:
                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, managecmd + " setfarewell "));
                        break;
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
            boolean canSpread = plugin.getConfig().getBoolean("Manage.spread-friends.enable") &&
                                player.hasPermission("landlord.player.manage.spreadfriends") && manageMode != ManageMode.ONE;
            MultiPagedGUI friendsGui = new MultiPagedGUI(plugin, player, (int) Math.ceil((double) friends.size() / 9.0),
                    lm.getRawString("Commands.Manage.ManageFriends.title"), new ArrayList<>(), this) {
                @Override
                protected void generateStaticIcons() {
                    if (canSpread) {
                        String spreadTitle = lm.getRawString("Commands.Manage.AllowSpread-friends.title");
                        Material material = Material.valueOf(plugin.getConfig().getString("Manage.spread-friends" + ".item"));
                        Icon spreadIcon = new Icon(new ItemStack(material));
                        spreadIcon.setName(spreadTitle);
                        spreadIcon.setLore(lm.getStringList("Commands.Manage.AllowSpread-friends.description"));

                        spreadIcon.addClickAction((p) ->
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Set<UUID> defaultFriends = land.getFriends();

                                        for (IOwnedLand region : regions.subList(1, regions.size())) {
                                            String oldfriends = region.getMembersString();

                                            for (UUID landFriend : region.getFriends()) {
                                                if (!land.isFriend(landFriend)) region.removeFriend(landFriend);
                                            }
                                            for (UUID defaultFriend : defaultFriends) {
                                                if (!region.isFriend(defaultFriend)) region.addFriend(defaultFriend);
                                            }

                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    LandManageEvent landManageEvent = new LandManageEvent(player, region,
                                                            "FRIENDS", oldfriends, region.getMembersString());
                                                    plugin.getServer().getPluginManager().callEvent(landManageEvent);
                                                }
                                            }.runTask(plugin);
                                        }
                                    }
                                }.runTaskAsynchronously(plugin));

                        this.setIcon((int) Math.ceil((double) friends.size() / 9.0) + 1, spreadIcon);
                    }
                }
            };

            String rawTitle = lm.getRawString("Commands.Manage.ManageFriends.unfriend");

            for (UUID id : friends) {
                OfflinePlayer op = plugin.getServer().getOfflinePlayer(id);
                Icon friend = new Icon(mats.getPlayerHead(id));
                String name = (op != null && op.getName() != null ? op.getName() : "OfflinePlayer");
                String confititle = rawTitle.replace("%player%", name);
                friend.setName(name);
                friend.setLore(formatFriendsSegment(id));
                friend.addClickAction((player) -> {
                    ConfirmationGUI confirmationGUI = new ConfirmationGUI(plugin, this.player,
                            confititle,
                            (p) -> {
                                friendsGui.removeIcon(friend);

                                for (IOwnedLand region : regions) {
                                    plugin.getServer().dispatchCommand(player, PrincepsLib.getCommandManager()
                                                                                       .getCommand(Landlordbase.class).getCommandString(Unfriend.class)
                                                                                       .substring(1) + " " + name + " " + region.getName());
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
                });
                friendsGui.addIcon(friend);
            }
            int friendPosition = position;
            icon.addClickAction((p) -> {
                friendsGui.generateAsync().display();
                this.setIcon(friendPosition, new Icon(MaterialProxy.CLOCK.crossVersion()).setName(
                        lm.getRawString("pleaseWait")
                ));
            });

            this.setIcon(position++, icon);
        }

        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("Manage.commands");
        Set<String> keys = cs.getKeys(false);
        for (String key : keys) {
            // plugin.getLogger().log(Level.INFO, plugin.getConfig().getBoolean("Manage.commands." + key + ".enable") + " " + player.hasPermission("landlord.player.manage." + key) + " " + regions.size());
            // Config: Manage.commands.x
            // Language manager: Commands.Manage.x
            if (plugin.getConfig().getBoolean("Manage.commands." + key + ".enable") &&
                player.hasPermission("landlord.player.manage." + key) &&
                manageMode == ManageMode.ONE) {
                List<String> descri = lm.getStringList("Commands.Manage." + key + ".description");
                double cost = plugin.getConfig().getDouble("ResetCost");
                String costString = (Options.isVaultEnabled() ? plugin.getVaultManager().format(cost) : "-1");

                Icon icon = new Icon(new ItemStack(Material.valueOf(plugin.getConfig().getString("Manage.commands." + key + ".item"))));
                icon.setLore(formatList(formatList(descri, "%regencost%", costString), "%land%", land.getName()));
                icon.setName(lm.getRawString("Commands.Manage." + key + ".title"));
                icon.addClickAction((p) ->
                        plugin.getServer()
                                .dispatchCommand(player, plugin.getConfig().getString("Manage.commands." + key + ".cmd")
                                        .replace("%land%", land.getName())));

                this.setIcon(position++, icon);
            }
        }

        // spawn management
        if (plugin.getConfig().getBoolean("Manage.mob-spawning.enable") &&
            player.hasPermission("landlord.player.manage.mob-spawning")) {
            String title = lm.getRawString("Commands.Manage.AllowMob-spawning.title");
            Icon icon = new Icon(new ItemStack(Material.valueOf(plugin.getConfig().getString("Manage.mob-spawning" +
                                                                                             ".item"))));
            icon.setName(title);
            icon.setLore(lm.getStringList("Commands.Manage.AllowMob-spawning.description"));

            icon.addClickAction((p) -> {
                List<Icon> icons = new ArrayList<>();
                List<String> lore = lm.getStringList("Commands.Manage.AllowMob-spawning.toggleItem.description");

                MultiPagedGUI gui = new MultiPagedGUI(plugin, p, 5, title, icons, this) {
                    @Override
                    protected void generateStaticIcons() {
                        if (plugin.getConfig().getBoolean("Manage.spread-mobs.enable") &&
                            player.hasPermission("landlord.player.manage.spreadmobs") && manageMode != ManageMode.ONE) {
                            String spreadTitle = lm.getRawString("Commands.Manage.AllowSpread-mobs.title");
                            Material material = Material.valueOf(plugin.getConfig().getString("Manage.spread-mobs" + ".item"));
                            Icon spreadIcon = new Icon(new ItemStack(material));
                            spreadIcon.setName(spreadTitle);
                            spreadIcon.setLore(lm.getStringList("Commands.Manage.AllowSpread-mobs.description"));

                            spreadIcon.addClickAction((p2) -> {
                                for (IMob m : plugin.getMobManager().values()) {
                                    // Skip mob if its not in the list, because that means this mob should not be manageable
                                    if (!toggleMobs.contains(m.getName()) || !player.hasPermission(m.getPermission())) {
                                        continue;
                                    }

                                    for (IOwnedLand region : regions.subList(1, regions.size())) {
                                        if (land.isMobDenied(m) != region.isMobDenied(m)) region.toggleMob(m);
                                    }
                                }
                            });

                            this.setIcon(46, spreadIcon);
                        }
                    }
                };

                String titleMob = lm.getRawString("Commands.Manage.AllowMob-spawning.toggleItem.title");
                for (IMob m : plugin.getMobManager().values()) {
                    // Skip mob if its not in the list, because that means this mob should not be manageable
                    if (!toggleMobs.contains(m.getName()) || !player.hasPermission(m.getPermission())) {
                        continue;
                    }

                    Icon mob = new Icon(m.getEgg());
                    mob.setName(titleMob.replace("%mob%", m.getNiceName()));
                    mob.setLore(formatList(formatList(lore, "%value%", formatMobState(land.isMobDenied(m))),
                            "%mob%", m.getNiceName()));
                    //Make allowed mobs icon more distinctive
                    setGlowing(mob.itemStack, !land.isMobDenied(m));
                    gui.addIcon(mob);

                    mob.addClickAction((p1) -> {
                        //Avoid lag with lots of lands
                        land.toggleMob(m);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (IOwnedLand region : regions.subList(1, regions.size())) {
                                    if (region.isMobDenied(m) == land.isMobDenied(m))
                                        continue;

                                    region.toggleMob(m);
                                }
                                mob.setLore(formatList(formatList(lore, "%value%", formatMobState(land.isMobDenied(m))),
                                        "%mob%", m.getNiceName()));
                                //Make allowed mobs icon more distinctive
                                setGlowing(mob.itemStack, !land.isMobDenied(m));
                                gui.refresh();
                            }
                        }.runTaskAsynchronously(plugin);
                    });
                }

                gui.display();
            });

            this.setIcon(position++, icon);
        }

        if (plugin.getConfig().getBoolean("Manage.spread-flags.enable") &&
            player.hasPermission("landlord.player.manage.spreadflags") && manageMode != ManageMode.ONE) {
            String title = lm.getRawString("Commands.Manage.AllowSpread-flags.title");
            Material material = Material.valueOf(plugin.getConfig().getString("Manage.spread-flags" + ".item"));
            Icon icon = new Icon(new ItemStack(material));
            icon.setName(title);
            icon.setLore(lm.getStringList("Commands.Manage.AllowSpread-flags.description"));

            icon.addClickAction((p) ->
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (int flagI = 0; flagI < land.getFlags().size(); flagI++) {
                                ILLFlag defaultFlag = land.getFlags().get(flagI);

                                for (IOwnedLand region : regions.subList(1, regions.size())) {
                                    ILLFlag landFlag = region.getFlags().get(flagI);

                                    if (defaultFlag.getFriendStatus() != landFlag.getFriendStatus()) {
                                        landFlag.toggleFriends();

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                                        landFlag.getName(), !landFlag.getFriendStatus(), landFlag.getFriendStatus());
                                                plugin.getServer().getPluginManager().callEvent(landManageEvent);
                                            }
                                        }.runTask(plugin);
                                    }
                                    if (defaultFlag.getAllStatus() != landFlag.getAllStatus()) {
                                        landFlag.toggleAll();

                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                LandManageEvent landManageEvent = new LandManageEvent(player, land,
                                                        landFlag.getName(), !landFlag.getAllStatus(), landFlag.getAllStatus());
                                                plugin.getServer().getPluginManager().callEvent(landManageEvent);
                                            }
                                        }.runTask(plugin);
                                    }
                                }
                            }

                            for (IOwnedLand region : regions) {
                                region.setFarewellMessage(land.getFarewellMessage());
                                region.setGreetMessage(land.getGreetMessage());
                            }
                        }
                    }.runTaskAsynchronously(plugin));

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

    private void setGlowing(ItemStack stack, boolean glowing) {
        ItemMeta itemMeta = stack.getItemMeta();
        if (glowing) {
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            itemMeta.removeEnchant(Enchantment.DAMAGE_ALL);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        stack.setItemMeta(itemMeta);
    }

    private List<String> formatList(List<String> list, String toReplace, String newValue) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(s.replace(toReplace, newValue));
        }
        return newList;
    }

    private List<String> formatFriendsSegment(UUID id) {
        OfflinePlayer op = plugin.getServer().getOfflinePlayer(id);
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
        for (String s : stringList) {
            String ss = s.replace("%seen%", lastseen);
            toReturn.add(ss);
        }

        return toReturn;
    }

}
