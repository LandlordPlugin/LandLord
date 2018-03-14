package biz.princeps.landlord.commands;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.commands.admin.AdminTeleport;
import biz.princeps.landlord.commands.admin.Clear;
import biz.princeps.landlord.commands.admin.GiveClaims;
import biz.princeps.landlord.commands.admin.Update;
import biz.princeps.landlord.commands.claiming.*;
import biz.princeps.landlord.commands.claiming.adv.Advertise;
import biz.princeps.landlord.commands.friends.Addfriend;
import biz.princeps.landlord.commands.friends.AddfriendAll;
import biz.princeps.landlord.commands.friends.Unfriend;
import biz.princeps.landlord.commands.friends.UnfriendAll;
import biz.princeps.landlord.commands.homes.Home;
import biz.princeps.landlord.commands.homes.SetHome;
import biz.princeps.landlord.commands.management.*;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.util.UUIDFetcher;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.MainCommand;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.command.SubCommand;
import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import biz.princeps.lib.storage.AbstractDatabase;
import biz.princeps.lib.storage.MySQL;
import biz.princeps.lib.storage.SQLite;
import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by spatium on 16.07.17.
 */
public class Landlordbase extends MainCommand {

    private Map<String, LandlordCommand> subcommands;
    private static Landlord pl = Landlord.getInstance();

    public Landlordbase() {
        super(pl.getConfig().getString("CommandSettings.Main.name"),
                pl.getConfig().getString("CommandSettings.Main.description"),
                pl.getConfig().getString("CommandSettings.Main.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Main.permissions")),
                pl.getConfig().getStringList("CommandSettings.Main.aliases").toArray(new String[]{}));


        subcommands = new HashMap<>();
        subcommands.put("claim", new Claim());
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
        subcommands.put("borders", new Borders());
        subcommands.put("admintp", new AdminTeleport());
        subcommands.put("item", new LLItem());
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
                .setCommand("ll help", argsN).build();
        properties.getPlayer().spigot().sendMessage(msg.create());
    }

    class ClaimCMD extends SubCommand {

        public ClaimCMD() {
            super(pl.getConfig().getString("CommandSettings.Claim.name"),
                    pl.getConfig().getString("CommandSettings.Claim.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claim.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Claim.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Claim) subcommands.get("claim")).onClaim(properties.getPlayer());
            }
        }
    }

    class InfoCMD extends SubCommand {

        public InfoCMD() {
            super(pl.getConfig().getString("CommandSettings.Info.name"),
                    pl.getConfig().getString("CommandSettings.Info.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Info.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Unclaim.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.UnclaimAll.permissions")),
                    pl.getConfig().getStringList("CommandSettings.UnclaimAll.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((UnclaimAll) subcommands.get("unclaimall")).onUnclaim(properties.getPlayer());
            }
        }
    }

    class AddfriendCMD extends SubCommand {

        public AddfriendCMD() {
            super(pl.getConfig().getString("CommandSettings.Addfriend.name"),
                    pl.getConfig().getString("CommandSettings.Addfriend.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Addfriend.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemoveFriend.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.AddfriendAll.permissions")),
                    pl.getConfig().getStringList("CommandSettings.AddfriendAll.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((AddfriendAll) subcommands.get("addfriendall")).onAddfriend(properties.getPlayer(), arguments.get());
            }
        }
    }

    class RemoveFriendAllCMD extends SubCommand {

        public RemoveFriendAllCMD() {
            super(pl.getConfig().getString("CommandSettings.RemovefriendAll.name"),
                    pl.getConfig().getString("CommandSettings.RemovefriendAll.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.RemovefriendAll.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ListLands.permissions")),
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
                    ((ListLands) subcommands.get("listlands")).onListLands(properties.getPlayer(), properties.getPlayer(), page);
                } else if (properties.getPlayer().hasPermission("landlord.admin.list")) {
                    // Admin, Other lands, need to lookup their names
                    int finalPage = page;
                    String finalTarget = target;
                    UUIDFetcher.getUUID(target, uuid -> {

                        if (uuid == null) {
                            // Failure
                            properties.getPlayer().sendMessage(Landlord.getInstance().getLangManager().getString("Commands.ListLands.noPlayer").replace("%player%", finalTarget));
                        } else {
                            // Success
                            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                            if (op != null)
                                ((ListLands) subcommands.get("listlands")).onListLands(properties.getPlayer(), op, finalPage);
                            else {
                                properties.getPlayer().sendMessage(Landlord.getInstance().getLangManager().getString("Commands.ListLands.noPlayer").replace("%player%", finalTarget));
                            }
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Map.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Clear.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Clear.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                try {
                    ((Clear) subcommands.get("clearworld")).onClearWorld(properties.getPlayer(), arguments.get(0));
                } catch (ArgumentsOutOfBoundsException e) {
                    ((Clear) subcommands.get("clearworld")).onClearWorld(properties.getPlayer(), null);
                }
            }
        }
    }

    class ManageCMD extends SubCommand {

        public ManageCMD() {
            super(pl.getConfig().getString("CommandSettings.Manage.name"),
                    pl.getConfig().getString("CommandSettings.Manage.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Manage.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.ManageAll.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Update.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Shop.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Reload.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Claims.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Sethome.permissions")),
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
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Home.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Home.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                String target = "own";
                //TODO implement other homes
                ((Home) subcommands.get("home")).onHome(properties.getPlayer(), target);
            }
        }
    }

    class GiveClaimsCMD extends SubCommand {

        public GiveClaimsCMD() {
            super(pl.getConfig().getString("CommandSettings.GiveClaims.name"),
                    pl.getConfig().getString("CommandSettings.GiveClaims.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.GiveClaims.permissions")),
                    pl.getConfig().getStringList("CommandSettings.GiveClaims.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            try {
                String target = arguments.get(0);
                double price = arguments.getDouble(1);
                int amount = arguments.getInt(2);
                ((GiveClaims) subcommands.get("giveclaims")).onGiveClaims(properties.getCommandSender(), target, price, amount);
            } catch (ArgumentsOutOfBoundsException | NumberFormatException e) {
                properties.sendUsage();
            }
        }
    }

    class AdvertiseCMD extends SubCommand {

        public AdvertiseCMD() {
            super(pl.getConfig().getString("CommandSettings.Advertise.name"),
                    pl.getConfig().getString("CommandSettings.Advertise.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Advertise.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Advertise.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                if (Landlord.getInstance().isVaultEnabled()) {
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

    class BordersCMD extends SubCommand {

        public BordersCMD() {
            super(pl.getConfig().getString("CommandSettings.Borders.name"),
                    pl.getConfig().getString("CommandSettings.Borders.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Borders.permissions")),
                    pl.getConfig().getStringList("CommandSettings.Borders.aliases").toArray(new String[]{}));
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            if (properties.isPlayer()) {
                ((Borders) subcommands.get("borders")).onToggleBorder(properties.getPlayer());
            }
        }
    }

    class AdminTPCMD extends SubCommand {

        public AdminTPCMD() {
            super(pl.getConfig().getString("CommandSettings.AdminTP.name"),
                    pl.getConfig().getString("CommandSettings.AdminTP.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.AdminTP.permissions")),
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
            super(pl.getConfig().getString("CommandSettings.AdminTP.name"),
                    pl.getConfig().getString("CommandSettings.AdminTP.usage"),
                    Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.AdminTP.permissions")),
                    pl.getConfig().getStringList("CommandSettings.AdminTP.aliases").toArray(new String[]{}));
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

    class MigrateCMD extends SubCommand {

        public MigrateCMD() {
            super("migrate", "/ll migrate <v1|v2> (v1 the original landlord, v2 Princeps upgraded version)",
                    Sets.newHashSet("landlord.admin.migrate"));
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
}
