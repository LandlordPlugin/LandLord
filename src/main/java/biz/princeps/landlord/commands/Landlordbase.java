package biz.princeps.landlord.commands;

import biz.princeps.landlord.Landlord;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.storage.AbstractDatabase;
import biz.princeps.lib.storage.MySQL;
import biz.princeps.lib.storage.SQLite;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by spatium on 16.07.17.
 */
@CommandAlias("ll|land|landlord")
public class Landlordbase extends BaseCommand {

    private Map<String, LandlordCommand> subcommands;

    public Landlordbase() {
        subcommands = new HashMap<>();
        subcommands.put("claim", new Claim());
        subcommands.put("info", new Info());
        subcommands.put("unclaim", new Unclaim());
        subcommands.put("addfriend", new Addfriend());
        subcommands.put("unfriend", new Unfriend());
        subcommands.put("addfriendall", new AddfriendAll());
        subcommands.put("unfriendall", new UnfriendAll());
        subcommands.put("listlands", new ListLands());
        subcommands.put("landmap", new LandMap());
        subcommands.put("clearworld", new Clear());
        subcommands.put("manage", new Manage());
    }

    //TODO
    @Default
    @UnknownHandler
    @Subcommand("help")
    @CommandPermission("landlord.use")
    public void onDefault(CommandSender sender) {
        sender.sendMessage("help command");
    }

    @Subcommand("claim|buy|cl")
    @CommandAlias("claim")
    @Syntax("land claim - Claims the land you are currently standing on")
    @CommandPermission("landlord.player.own")
    public void onClaim(Player player) {
        ((Claim) subcommands.get("claim")).onClaim(player);
    }

    @Subcommand("info|i")
    @CommandAlias("landi|landinfo")
    @CommandPermission("landlord.player.info")
    @Syntax("land info - Shows information about the land you are standing on")
    public void onInfo(Player player) {
        ((Info) subcommands.get("info")).onInfo(player);
    }

    @Subcommand("unclaim|sell")
    @Syntax("land sell - Unclaim the chunk you are standing on")
    @CommandPermission("landlord.player.own")
    public void onUnClaim(Player player) {
        ((Unclaim) subcommands.get("unclaim")).onUnclaim(player);
    }

    @Subcommand("addfriend|friendadd")
    @Syntax("land addfriend - Adds friends to your land")
    @CommandPermission("landlord.player.own")
    public void onAddFriend(Player player, String[] names) {
        ((Addfriend) subcommands.get("addfriend")).onAddfriend(player, names);
    }

    @Subcommand("unfriend|friendremove|frienddelete")
    @Syntax("land unfriend - removes a friend from your land")
    @CommandPermission("landlord.player.own")
    public void onUnFriend(Player player, String[] names) {
        ((Unfriend) subcommands.get("unfriend")).onUnfriend(player, names);
    }

    @Subcommand("addfriendall|friendall")
    @Syntax("land addfriend - Adds friends to all your land")
    @CommandPermission("landlord.player.own")
    public void onAddfriendAll(Player player, String[] names) {
        ((AddfriendAll) subcommands.get("addfriendall")).onAddfriend(player, names);
    }

    @Subcommand("unfriendall|removeallfriends")
    @Syntax("land unfriendall - anfriend someone on all your lands")
    @CommandPermission("landlord.player.own")
    public void onUnfriendAll(Player player, String[] names) {
        ((UnfriendAll) subcommands.get("unfriendall")).onUnfriendall(player, names);
    }

    @Subcommand("list")
    @CommandAlias("listlands|landlist")
    @Syntax("land list - lists all your lands")
    @CommandPermission("landlord.player.own")
    public void onLandList(Player player) {
        int i = -1;
        for (String s : getOrigArgs()) {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
            }
        }
        ((ListLands) subcommands.get("listlands")).onListLands(player, new String[]{String.valueOf(i == -1 ? 0 : i)});
    }

    @Subcommand("map")
    @CommandAlias("landmap")
    @Syntax("land map - toggles the landmap")
    @CommandPermission("landlord.player.map")
    public void onToggleLandMap(Player player) {
        ((LandMap) subcommands.get("landmap")).onToggleLandMap(player);
    }


    @Subcommand("clear|clearworld")
    @CommandAlias("clearworld")
    @Syntax("land clear - toggles the landmap")
    @CommandPermission("landlord.admin.clearworld")
    public void onClearWorld(Player player, @Default("null") String target) {
        ((Clear) subcommands.get("clearworld")).onClearWorld(player, target);
    }


    @Subcommand("manage|mgn")
    @Syntax("land manage - manages the land you are standing on")
    @CommandPermission("landlord.player.own")
    public void onLandManage(Player player, @Default("null") String[] args) {
        ((Manage) subcommands.get("manage")).onManage(player, args);
    }


    @Subcommand("migrate")
    public void onMigrate(String[] args) {
        if (getCurrentCommandIssuer().hasPermission("landlord.admin.manage")) {
            Logger logger = Landlord.getInstance().getLogger();

            if (args.length > 0) {

                if (args[0].equals("v1")) {
                    // SQLite based migration

                    SQLite sqLite = new SQLite(logger, Landlord.getInstance().getDataFolder() + "/Landlord.db") {
                    };

                    logger.info("Starting to migrate from v1 Ebean Database...");
                    migrate(sqLite, "ll_land", "owner_name", "world_name", "x", "z");
                }
            } else if (args[0].equals("v2")) {
                if (args.length == 2) {
                    if (args[1].equals("sqlite")) {
                        // SQLite based migration
                        SQLite sqLite = new SQLite(logger, Landlord.getInstance().getDataFolder() + "/landlord.db") {
                        };

                        logger.info("Starting to migrate from v2-SQLite Database...");
                        migrate(sqLite, "ll_land", "owneruuid", "world", "x", "z");

                    } else if (args[1].equals("mysql")) {
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

    class DataObject {
        UUID owner;
        String world;
        int x, z;

        public DataObject(UUID owner, String world, int x, int z) {
            this.owner = owner;
            this.world = world;
            this.x = x;
            this.z = z;
        }
    }


    public void migrate(AbstractDatabase db, String tablename, String ownerColumn, String worldColumn, String xColumn, String zColumn) {
        List<DataObject> objs = new ArrayList<>();

        db.handleResultSet("SELECT * FROM ?", res -> {

            try {
                while (res.next()) {
                    UUID owner = UUID.fromString(res.getString(ownerColumn));
                    String world = res.getString(worldColumn);
                    int x = res.getInt(xColumn);
                    int z = res.getInt(zColumn);

                    objs.add(new DataObject(owner, world, x, z));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, tablename);
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
