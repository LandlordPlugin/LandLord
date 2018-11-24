package biz.princeps.landlord.commands;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.admin.AdminTeleport;
import biz.princeps.landlord.commands.admin.Clear;
import biz.princeps.landlord.commands.admin.GiveClaims;
import biz.princeps.landlord.commands.admin.Update;
import biz.princeps.landlord.commands.claiming.*;
import biz.princeps.landlord.commands.claiming.adv.Advertise;
import biz.princeps.landlord.commands.claiming.adv.RemoveAdvertise;
import biz.princeps.landlord.commands.friends.*;
import biz.princeps.landlord.commands.homes.Home;
import biz.princeps.landlord.commands.homes.SetHome;
import biz.princeps.landlord.commands.management.*;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.MainCommand;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.command.SubCommand;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import biz.princeps.lib.storage_old.AbstractDatabase;
import biz.princeps.lib.storage_old.MySQL;
import biz.princeps.lib.storage_old.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/07/17
 */
public class Landlordbase extends MainCommand {

    private static Landlord pl = Landlord.getInstance();
    private Map<String, LandlordCommand> subcommands;

    public Landlordbase() {
        super(pl.getConfig().getString("CommandSettings.Main.name"),
                pl.getConfig().getString("CommandSettings.Main.description"),
                pl.getConfig().getString("CommandSettings.Main.usage"),
                new HashSet<>(pl.getConfig().getStringList("CommandSettings.Main.permissions")),
                pl.getConfig().getStringList("CommandSettings.Main.aliases").toArray(new String[]{}));

        subcommands = new HashMap<>();
        subcommands.put("claim", new Claim(false));
        subcommands.put("info", new Info());
        subcommands.put("unclaim", new Unclaim());
        subcommands.put("unclaimall", new UnclaimAll());
        subcommands.put("addfriend", new Addfriend());
        subcommands.put("unfriend", new Unfriend());
        subcommands.put("addfriendall", new AddfriendAll());
        subcommands.put("unfriendall", new UnfriendAll());
        subcommands.put("listlands", new ListLands());
        subcommands.put("landmap", new LandMap());
        subcommands.put("clearworld", new Clear());
        subcommands.put("manage", new Manage());
        subcommands.put("manageall", new ManageAll());
        subcommands.put("shop", new Shop());
        subcommands.put("claims", new Claims());
        subcommands.put("sethome", new SetHome());
        subcommands.put("home", new Home());
        subcommands.put("giveclaims", new GiveClaims());
        subcommands.put("update", new Update());
        subcommands.put("advertise", new Advertise());
        subcommands.put("remadvertise", new RemoveAdvertise());
        subcommands.put("borders", new Borders());
        subcommands.put("admintp", new AdminTeleport());
        subcommands.put("item", new LLItem());
        subcommands.put("listfriends", new ListFriends());
        subcommands.put("multiclaim", new MultiClaim());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tabReturn = new ArrayList<>();

        // Length == 1 means there is just the first thing like /ll typed
        if (args.length == 1) {
            for (SubCommand subCommand : this.subCommandMap.values()) {
                if (subCommand.hasPermission(sender)) {

                    if (subCommand instanceof BordersCMD) {
                        if (Options.enabled_borders()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof MapCMD) {
                        if (Options.enabled_map()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof ShopCMD || subCommand instanceof ClaimsCMD) {
                        if (Options.enabled_shop()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof HomeCMD || subCommand instanceof SetHomeCMD) {
                        if (Options.enabled_homes()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else {
                        tabReturn.add(subCommand.getName());
                    }
                }
            }

            if (!args[0].isEmpty())
                tabReturn.removeIf(next -> !next.startsWith(args[0]));

        } else if (args.length == 2) {
            for (SubCommand subcmd : subCommandMap.values()) {
                if (subcmd.matches(args[0])) {
                    if (subcmd instanceof AddfriendCMD || subcmd instanceof AddFriendAllCMD ||
                            subcmd instanceof RemoveFriendCMD || subcmd instanceof RemoveFriendAllCMD) {

                        if (args[1].isEmpty()) {
                            Bukkit.getOnlinePlayers().forEach(p -> tabReturn.add(p.getName()));
                        } else {
                            Bukkit.getOnlinePlayers().stream()
                                    .filter(p -> p.getName().startsWith(args[1])).forEach(p -> tabReturn.add(p.getName()));
                        }
                        return tabReturn;
                    } else if (subcmd instanceof MultiClaimCMD) {
                        for (MultiClaim.MultiClaimMode value : MultiClaim.MultiClaimMode.values()) {
                            tabReturn.add(value.name());
                        }
                        return tabReturn;
                    }
                }
            }
        }

        return tabReturn;
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (properties.isConsole()) return;

        LangManager lm = Landlord.getInstance().getLangManager();
        List<String> playersList = lm.getStringList("Commands.Help.players");
        List<String> adminList = lm.getStringList("Commands.Help.admins");

        int perSite = Landlord.getInstance().getConfig().getInt("HelpCommandPerSite");

        String[] argsN = new String[1];
        if (arguments.get().length == 1) {
            argsN[0] = (arguments.get()[0] == null ? "0" : arguments.get()[0]);
        }

        List<String> toDisplay = new ArrayList<>();
        if (properties.getPlayer().hasPermission("landlord.admin.help"))
            toDisplay.addAll(adminList);
        toDisplay.addAll(playersList);

        // System.out.println(toDisplay.size());

        MultiPagedMessage msg = new MultiPagedMessage.Builder()
                .setElements(toDisplay)
                .setPerSite(perSite)
                .setHeaderString(lm.getRawString("Commands.Help.header"))
                .setNextString(lm.getRawString("Commands.Help.next"))
                .setPreviousString(lm.getRawString("Commands.Help.previous"))
                .setCommand(pl.getConfig().getString("CommandSettings.Main.name"), argsN).build();
        properties.getPlayer().spigot().sendMessage(msg.create());
    }

    void migrate(AbstractDatabase db, String tablename, String ownerColumn, String worldColumn, String xColumn, String zColumn) {
        List<DataObject> objs = new ArrayList<>();

        db.executeQuery("SELECT * FROM " + tablename, res -> {

            try {
                while (res.next()) {
                    UUID owner = UUID.fromString(res.getString(ownerColumn));
                    String world = res.getString(worldColumn);
                    int x = res.getInt(xColumn);
                    int z = res.getInt(zColumn);

                    objs.add(new DataObject(owner, world, x, z));
                }
            } catch (SQLException e) {
                Landlord.getInstance().getLogger().warning("There was an error while trying to fetching original data: " + e);
            }
        });
        db.getLogger().info("Finished fetching data from old database. Size: " + objs.size() + " lands");
        db.getLogger().info("The next step will take around " + objs.size() / 20 / 60 + " minutes");

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter >= objs.size() - 1) {
                    db.getLogger().info("Finished migrating database. Migrated " + objs.size() + " lands!");
                    cancel();
                }

                DataObject next = objs.get(counter);
                World world1 = Bukkit.getWorld(next.world);
                if (world1 != null) {
                    Chunk chunk = world1.getChunkAt(next.x, next.z);
                    Landlord.getInstance().getWgHandler().claim(chunk, next.owner);
                }
                counter++;

                if (counter % 600 == 0)
                    db.getLogger().info("Processed " + counter + " lands already. " + (objs.size() - counter) / 20 / 60 + " minutes remaining!");
            }
        }.runTaskTimer(Landlord.getInstance(), 0, 1);
    }

    class ClaimCMD extends SubCommand {

        public ClaimCMD() {
            super(pl.getConfig().getString("CommandSettings.Claim.name"),
                    pl.getConfig().getString("CommandSettings.Claim.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Claim.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Claim.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                Chunk chunk = properties.getPlayer().getWorld().getChunkAt(properties.getPlayer().getLocation());
                ((Claim) subcommands.get("claim")).onClaim(properties.getPlayer(), chunk);
            }
        }
    }

    class InfoCMD extends SubCommand {

        public InfoCMD() {
            super(pl.getConfig().getString("CommandSettings.Info.name"),
                    pl.getConfig().getString("CommandSettings.Info.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Info.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Info.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Info) subcommands.get("info")).onInfo(properties.getPlayer());
            }
        }
    }

    class UnclaimCMD extends SubCommand {

        public UnclaimCMD() {
            super(pl.getConfig().getString("CommandSettings.Unclaim.name"),
                    pl.getConfig().getString("CommandSettings.Unclaim.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Unclaim.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Unclaim.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                String landname;
                try {
                    landname = arguments.get(0);
                } catch (ArgumentsOutOfBoundsException e) {
                    landname = "null";
                }
                ((Unclaim) subcommands.get("unclaim")).onUnclaim(properties.getPlayer(), landname);
            }
        }
    }

    class UnclaimAllCMD extends SubCommand {

        public UnclaimAllCMD() {
            super(pl.getConfig().getString("CommandSettings.UnclaimAll.name"),
                    pl.getConfig().getString("CommandSettings.UnclaimAll.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.UnclaimAll.permissions")),
                    pl.getConfig().getStringList("CommandSettings.UnclaimAll.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((UnclaimAll) subcommands.get("unclaimall")).onUnclaim(properties.getPlayer());
            }
        }
    }

    class ListfriendsCMD extends SubCommand {

        public ListfriendsCMD() {
            super(pl.getConfig().getString("CommandSettings.Listfriends.name"),
                    pl.getConfig().getString("CommandSettings.Listfriends.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Listfriends.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Listfriends.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {

                String landname;
                try {
                    landname = arguments.get(0);
                } catch (ArgumentsOutOfBoundsException e) {
                    if (pl.getWgHandler().getRegion(properties.getPlayer().getLocation()) != null) {
                        landname = OwnedLand.getName(properties.getPlayer().getLocation().getChunk());
                    } else {
                        landname = null;
                    }
                }

                ((ListFriends) subcommands.get("listfriends")).onListFriends(properties.getPlayer(), landname);
            }
        }
    }

    class AddfriendCMD extends SubCommand {

        public AddfriendCMD() {
            super(pl.getConfig().getString("CommandSettings.Addfriend.name"),
                    pl.getConfig().getString("CommandSettings.Addfriend.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Addfriend.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Addfriend.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Addfriend) subcommands.get("addfriend")).onAddfriend(properties.getPlayer(), arguments.get());
            }
        }
    }

    class RemoveFriendCMD extends SubCommand {

        public RemoveFriendCMD() {
            super(pl.getConfig().getString("CommandSettings.RemoveFriend.name"),
                    pl.getConfig().getString("CommandSettings.RemoveFriend.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.RemoveFriend.permissions")),
                    pl.getConfig().getStringList("CommandSettings.RemoveFriend.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Unfriend) subcommands.get("unfriend")).onUnfriend(properties.getPlayer(), arguments.get());
            }
        }
    }

    class AddFriendAllCMD extends SubCommand {

        public AddFriendAllCMD() {
            super(pl.getConfig().getString("CommandSettings.AddfriendAll.name"),
                    pl.getConfig().getString("CommandSettings.AddfriendAll.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.AddfriendAll.permissions")),
                    pl.getConfig().getStringList("CommandSettings.AddfriendAll.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                try {
                    ((AddfriendAll) subcommands.get("addfriendall")).onAddfriend(properties.getPlayer(), arguments.get(0));
                } catch (ArgumentsOutOfBoundsException e) {
                    properties.sendMessage(pl.getLangManager().getString("Commands.AddfriendAll.noPlayer")
                            .replace("%player%", "[]"));
                }
            }
        }
    }

    class RemoveFriendAllCMD extends SubCommand {

        public RemoveFriendAllCMD() {
            super(pl.getConfig().getString("CommandSettings.RemovefriendAll.name"),
                    pl.getConfig().getString("CommandSettings.RemovefriendAll.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.RemovefriendAll.permissions")),
                    pl.getConfig().getStringList("CommandSettings.RemovefriendAll.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                try {
                    ((UnfriendAll) subcommands.get("unfriendall")).onUnfriendall(properties.getPlayer(), arguments.get(0));
                } catch (ArgumentsOutOfBoundsException e) {
                    properties.sendUsage();
                }
            }
        }
    }

    class ListLandsCMD extends SubCommand {

        public ListLandsCMD() {
            super(pl.getConfig().getString("CommandSettings.ListLands.name"),
                    pl.getConfig().getString("CommandSettings.ListLands.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.ListLands.permissions")),
                    pl.getConfig().getStringList("CommandSettings.ListLands.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                String target = null;
                int page = 0;
                try {
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
                    ((ListLands) subcommands.get("listlands")).onListLands(properties.getPlayer(), pl.getPlayerManager().get(properties.getPlayer().getUniqueId()), page);
                } else if (properties.getPlayer().hasPermission("landlord.admin.list")) {
                    // Admin, Other lands, need to lookup their names
                    int finalPage = page;
                    String finalTarget = target;
                    pl.getPlayerManager().getOfflinePlayerAsync(target, lPlayer -> {
                        if (lPlayer == null) {
                            // Failure
                            properties.getPlayer().sendMessage(Landlord.getInstance().getLangManager()
                                    .getString("Commands.ListLands.noPlayer").replace("%player%", finalTarget));
                        } else {
                            // Success
                            ((ListLands) subcommands.get("listlands")).onListLands(properties.getPlayer(), lPlayer, finalPage);
                        }
                    });
                }
            }
        }
    }

    class MapCMD extends SubCommand {
        public MapCMD() {
            super(pl.getConfig().getString("CommandSettings.Map.name"),
                    pl.getConfig().getString("CommandSettings.Map.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Map.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Map.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((LandMap) subcommands.get("landmap")).onToggleLandMap(properties.getPlayer());
            }
        }
    }

    class ClearWorldCMD extends SubCommand {

        public ClearWorldCMD() {
            super(pl.getConfig().getString("CommandSettings.Clear.name"),
                    pl.getConfig().getString("CommandSettings.Clear.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Clear.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Clear.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Clear) subcommands.get("clearworld")).onClearWorld(properties.getPlayer());
            }
        }
    }

    class ManageCMD extends SubCommand {

        public ManageCMD() {
            super(pl.getConfig().getString("CommandSettings.Manage.name"),
                    pl.getConfig().getString("CommandSettings.Manage.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Manage.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Manage.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Manage) subcommands.get("manage")).onManage(properties.getPlayer(), arguments.get());
            }
        }
    }

    class ManageAllCMD extends SubCommand {

        public ManageAllCMD() {
            super(pl.getConfig().getString("CommandSettings.ManageAll.name"),
                    pl.getConfig().getString("CommandSettings.ManageAll.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.ManageAll.permissions")),
                    pl.getConfig().getStringList("CommandSettings.ManageAll.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((ManageAll) subcommands.get("manageall")).onManageAll(properties.getPlayer());
            }
        }
    }

    class UpdateCMD extends SubCommand {

        public UpdateCMD() {
            super(pl.getConfig().getString("CommandSettings.Update.name"),
                    pl.getConfig().getString("CommandSettings.Update.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Update.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Update.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            try {
                if (arguments.get(0).equals("-r")) {
                    ((Update) subcommands.get("update")).onResetLands(properties.getCommandSender());
                }
            } catch (ArgumentsOutOfBoundsException e) {
                ((Update) subcommands.get("update")).onUpdateLands(properties.getCommandSender());
            }
        }
    }

    class ShopCMD extends SubCommand {

        public ShopCMD() {
            super(pl.getConfig().getString("CommandSettings.Shop.name"),
                    pl.getConfig().getString("CommandSettings.Shop.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Shop.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Shop.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Shop) subcommands.get("shop")).onShop(properties.getPlayer());
            }
        }
    }

    class ReloadCMD extends SubCommand {

        public ReloadCMD() {
            super(pl.getConfig().getString("CommandSettings.Reload.name"),
                    pl.getConfig().getString("CommandSettings.Reload.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Reload.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Reload.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            String msg = Landlord.getInstance().getLangManager().getString("Commands.Reload.success");
            CommandSender issuer = properties.getCommandSender();

            issuer.sendMessage(ChatColor.RED + "Reloading is not recommended! Before reporting any bugs, please restart your server.");

            Landlord.getInstance().getPluginLoader().disablePlugin(Landlord.getInstance());
            Landlord.getInstance().getPluginLoader().enablePlugin(Landlord.getInstance());
            issuer.sendMessage(msg);
        }
    }

    class ClaimsCMD extends SubCommand {

        public ClaimsCMD() {
            super(pl.getConfig().getString("CommandSettings.Claims.name"),
                    pl.getConfig().getString("CommandSettings.Claims.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Claims.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Claims.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Claims) subcommands.get("claims")).onClaims(properties.getPlayer());
            }
        }
    }

    class SetHomeCMD extends SubCommand {

        public SetHomeCMD() {
            super(pl.getConfig().getString("CommandSettings.Sethome.name"),
                    pl.getConfig().getString("CommandSettings.Sethome.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Sethome.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Sethome.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((SetHome) subcommands.get("sethome")).onSetHome(properties.getPlayer());
            }
        }
    }

    class HomeCMD extends SubCommand {

        public HomeCMD() {
            super(pl.getConfig().getString("CommandSettings.Home.name"),
                    pl.getConfig().getString("CommandSettings.Home.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Home.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Home.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                String target;
                try {
                    target = arguments.get(0);
                } catch (ArgumentsOutOfBoundsException e) {
                    target = "own";
                }
                ((Home) subcommands.get("home")).onHome(properties, target);
            }
        }
    }

    class GiveClaimsCMD extends SubCommand {

        public GiveClaimsCMD() {
            super(pl.getConfig().getString("CommandSettings.GiveClaims.name"),
                    pl.getConfig().getString("CommandSettings.GiveClaims.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.GiveClaims.permissions")),
                    pl.getConfig().getStringList("CommandSettings.GiveClaims.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            ((GiveClaims) subcommands.get("giveclaims")).onGiveClaims(properties, arguments);
        }
    }

    class AdvertiseCMD extends SubCommand {

        public AdvertiseCMD() {
            super(pl.getConfig().getString("CommandSettings.Advertise.name"),
                    pl.getConfig().getString("CommandSettings.Advertise.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Advertise.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Advertise.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                if (Options.isVaultEnabled()) {
                    try {
                        String landname = "this";
                        double price;
                        if (arguments.size() > 1) {
                            landname = arguments.get(0);
                            price = arguments.getDouble(1);
                        } else {
                            price = arguments.getDouble(0);
                        }

                        ((Advertise) subcommands.get("advertise")).onAdvertise(properties.getPlayer(), landname, price);
                    } catch (ArgumentsOutOfBoundsException e) {
                        properties.sendUsage();
                    }
                }
            }
        }
    }


    class RemoveAdvertiseCMD extends SubCommand {

        public RemoveAdvertiseCMD() {
            super(pl.getConfig().getString("CommandSettings.RemoveAdvertise.name"),
                    pl.getConfig().getString("CommandSettings.RemoveAdvertise.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.RemoveAdvertise.permissions")),
                    pl.getConfig().getStringList("CommandSettings.RemoveAdvertise.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                if (Options.isVaultEnabled()) {
                    try {
                        String landname = "this";
                        if (arguments.size() == 1) {
                            landname = arguments.get(0);
                        }
                        ((RemoveAdvertise) subcommands.get("remadvertise")).onRemoveAdvertise(properties.getPlayer(), landname);
                    } catch (ArgumentsOutOfBoundsException e) {
                        properties.sendUsage();
                    }
                }
            }
        }
    }

    class BordersCMD extends SubCommand {

        public BordersCMD() {
            super(pl.getConfig().getString("CommandSettings.Borders.name"),
                    pl.getConfig().getString("CommandSettings.Borders.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.Borders.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Borders.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Borders) subcommands.get("borders")).onToggleBorder(properties.getPlayer());
            }
        }
    }

    class MultiClaimCMD extends SubCommand {

        public MultiClaimCMD() {
            super(pl.getConfig().getString("CommandSettings.MultiClaim.name"),
                    pl.getConfig().getString("CommandSettings.MultiClaim.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.MultiClaim.permissions")),
                    pl.getConfig().getStringList("CommandSettings.MultiClaim.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            ((MultiClaim) subcommands.get("multiclaim")).onMultiClaim(properties, arguments);
        }
    }

    class AdminTPCMD extends SubCommand {

        public AdminTPCMD() {
            super(pl.getConfig().getString("CommandSettings.AdminTP.name"),
                    pl.getConfig().getString("CommandSettings.AdminTP.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.AdminTP.permissions")),
                    pl.getConfig().getStringList("CommandSettings.AdminTP.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                String target;
                try {
                    target = arguments.get(0);
                } catch (ArgumentsOutOfBoundsException e) {
                    properties.sendUsage();
                    return;
                }
                ((AdminTeleport) subcommands.get("admintp")).onAdminTeleport(properties.getPlayer(), target);
            }
        }
    }

    class MAItemCMD extends SubCommand {

        public MAItemCMD() {
            super(pl.getConfig().getString("CommandSettings.MAItem.name"),
                    pl.getConfig().getString("CommandSettings.MAItem.usage"),
                    new HashSet<>(pl.getConfig().getStringList("CommandSettings.MAItem.permissions")),
                    pl.getConfig().getStringList("CommandSettings.MAItem.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            String target = null;

            if (arguments.size() > 0) {
                target = arguments.get()[0];
            }

            ((LLItem) subcommands.get("item")).onItem(properties.getCommandSender(), target);
        }
    }

    class ConfirmCMD extends SubCommand {

        public ConfirmCMD() {
            super("confirm", "/lldm help", new HashSet<>(Collections.singleton("landlord.use")));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            // just a placeholder for the confirmationmanager
        }
    }

    class MigrateCMD extends SubCommand {

        public MigrateCMD() {
            super("migrate", "/ll migrate <v1|v2> (v1 the original landlord, v2 Princeps upgraded version)",
                    new HashSet<>(Collections.singletonList("landlord.admin.migrate")));
        }

        @Override
        public void onCommand(Properties properties, Arguments args) {
            if (properties.getCommandSender().hasPermission("landlord.admin.manage")) {
                Logger logger = Landlord.getInstance().getLogger();

                if (args.size() > 0) {

                    if (args.get()[0].equals("v1")) {
                        // SQLite based migration

                        SQLite sqLite = new SQLite(logger, Landlord.getInstance().getDataFolder() + "/Landlord.db") {
                        };

                        logger.info("Starting to migrate from v1 Ebean Database...");
                        migrate(sqLite, "ll_land", "owner_name", "world_name", "x", "z");
                    }
                    if (args.get()[0].equals("v2")) {
                        if (args.size() == 2) {
                            if (args.get()[1].equals("sqlite")) {
                                // SQLite based migration
                                SQLite sqLite = new SQLite(logger, Landlord.getInstance().getDataFolder() + "/database.db") {
                                };

                                logger.info("Starting to migrate from v2-SQLite Database...");
                                migrate(sqLite, "ll_land", "owneruuid", "world", "x", "z");

                            } else if (args.get()[1].equals("mysql")) {
                                // mysql based migration

                                logger.info("In your plugin folder a file called MySQL.yml has been generated. You need to enter the credentials of your former landlord database.");
                                FileConfiguration mysqlConfig = PrincepsLib.prepareDatabaseFile();
                                MySQL mySQL = new MySQL(Landlord.getInstance().getLogger(), mysqlConfig.getString("MySQL.Hostname"),
                                        mysqlConfig.getInt("MySQL.Port"),
                                        mysqlConfig.getString("MySQL.Database"),
                                        mysqlConfig.getString("MySQL.User"),
                                        mysqlConfig.getString("MySQL.Password")) {
                                };
                                logger.info("Starting to migrate from v2-MySQL Database...");
                                migrate(mySQL, "ll_land", "owneruuid", "world", "x", "z");
                            }
                        }
                    }
                }
            }
        }
    }

    class DataObject {
        UUID owner;
        String world;
        int x, z;

        DataObject(UUID owner, String world, int x, int z) {
            this.owner = owner;
            this.world = world;
            this.x = x;
            this.z = z;
        }
    }
}
