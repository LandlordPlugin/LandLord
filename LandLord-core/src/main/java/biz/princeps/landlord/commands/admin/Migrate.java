package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IPlayer;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.persistent.Database;
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.storage_old.AbstractDatabase;
import biz.princeps.lib.storage_old.DatabaseType;
import biz.princeps.lib.storage_old.MySQL;
import biz.princeps.lib.storage_old.SQLite;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class Migrate extends LandlordCommand {

    public Migrate(ILandLord pl) {
        super(pl, "migrate",
                "/ll migrate <v1|v2|v3> <see manual> (v1 the original landlord, v2 Princeps upgraded version, v3 " +
                        "landlord 4.0)",
                Sets.newHashSet(Collections.singletonList("landlord.admin.migrate")),
                Sets.newHashSet());
    }

    @Override
    public void onCommand(Properties properties, Arguments args) {
        if (properties.getCommandSender().hasPermission("landlord.admin.manage")) {
            Logger logger = this.plugin.getLogger();

            if (args.size() == 0) {
                properties.sendUsage();
                return;
            }

            switch (args.get(0)) {
                case "v1":
                    // SQLite based migration

                    SQLite sqLite = new SQLite(logger, plugin.getPlugin().getDataFolder() + "/Landlord.db") {
                    };

                    logger.info("Starting to migrate from v1 Ebean Database...");
                    migrate(sqLite, "ll_land", "owner_name", "world_name", "x", "z");
                    break;

                case "v2":
                    if (args.size() != 2) {
                        properties.sendMessage("Usage: /ll migrate v2 <sqlite/mysql>");
                        return;
                    }

                    switch (args.get(1)) {
                        case "sqlite":
                            // SQLite based migration
                            sqLite = new SQLite(logger, plugin.getPlugin().getDataFolder() + "/database.db") {
                            };

                            logger.info("Starting to migrate from v2-SQLite Database...");
                            migrate(sqLite, "ll_land", "owneruuid", "world", "x", "z");
                            break;

                        case "mysql":
                            // mysql based migration

                            logger.info("In your plugin folder a file called MySQL.yml has been generated. You need " +
                                    "to enter the credentials of your former landlord database.");
                            FileConfiguration mysqlConfig = PrincepsLib.prepareDatabaseFile();
                            MySQL mySQL = new MySQL(logger, mysqlConfig.getString("MySQL.Hostname"),
                                    mysqlConfig.getInt("MySQL.Port"),
                                    mysqlConfig.getString("MySQL.Database"),
                                    mysqlConfig.getString("MySQL.User"),
                                    mysqlConfig.getString("MySQL.Password")) {
                            };
                            logger.info("Starting to migrate from v2-MySQL Database...");
                            migrate(mySQL, "ll_land", "owneruuid", "world", "x", "z");
                            break;
                        default:
                            properties.sendMessage("Usage: /ll migrate v2 <sqlite/mysql>");
                    }
                    break;

                case "v3":
                    // ll migrate v3 <fromdb>
                    // fromdb also requires hostname port database username password
                    // destination db is provided via config
                    if (properties.isConsole()) {
                        properties.sendMessage("Player command only!");
                        return;
                    }
                    if ((args.size() != 7 && args.get(1).equals("MySQL")) ||
                            args.size() != 3 && args.get(1).equals("H2")) {
                        properties.sendMessage("Usage: /ll migrate v3 <H2 dbfilename | MySQL hostname port database " +
                                "username password>");
                        return;
                    }
                    PrincepsLib.getConfirmationManager().drawChat(properties.getPlayer(),
                            ChatColor.translateAlternateColorCodes('&', "&cHave you entered the correct data in the " +
                                    "config.yml for the destination database? Are you alone on the server? \nClick " +
                                    "here to proceed!"),
                            (p) -> {
                                // on accept
                                migratev3(properties, args);
                            }, (p) -> {
                                // on abort
                                properties.sendMessage("Aborted!");

                            }, "/ll confirm", 10);

                    break;

                default:
                    properties.sendUsage();
            }
        }
    }

    /**
     * Copies everything from a in the parameters defined database into a new database
     *
     * @param p    the properties (sender)
     * @param args the arguments
     */
    private void migratev3(Properties p, Arguments args) {
        if (!new File(plugin.getPlugin().getDataFolder() + "/" + args.get(2) + ".mv.db").exists()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis database does not exist! Please enter " +
                    "the correct name of the database without .mv.db"));
            return;
        }

        if (Bukkit.getOnlinePlayers().size() != 1) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be alone on the server!"));
            return;
        }

        Database sourcedb;
        try {
            DatabaseType type = DatabaseType.valueOf(args.get(1));
            String hostname = type == DatabaseType.H2 ? "localhost" : args.get(2);
            int port = type == DatabaseType.H2 ? 3306 : args.getInt(3);
            String databasename = type == DatabaseType.H2 ? plugin.getPlugin().getDataFolder() + "/" + args.get(2) :
                    args.get(4);
            String user = type == DatabaseType.H2 ? "root" : args.get(5);
            String password = type == DatabaseType.H2 ? "passy" : args.get(6);
            sourcedb = new Database(plugin.getLogger(), type, hostname, String.valueOf(port), user, password,
                    databasename);
        } catch (Exception ex) {
            p.sendMessage("Usage: /ll migrate v3 <H2 dbfilename | MySQL hostname port database username password>");
            return;
        }
        plugin.getPlugin().getServer().getScheduler().scheduleAsyncDelayedTask(plugin.getPlugin(), () -> {
            Collection<IPlayer> players = sourcedb.getPlayers();
            plugin.getPlayerManager().remove(p.getPlayer().getUniqueId());
            players.forEach(player -> plugin.getPlayerManager().add(player));

            p.sendMessage("Migration done. Please restart (no reload) the server now.");
        });


    }

    /**
     * Hui this is some really old legacy shit. I would not dare to touch any of this ever.
     * There used to be a different database scheme a very long time ago. Or maybe there wasnt. I don't really remember.
     * Anyways, this function can be used to migrate this old database to the new one. Maybe.
     */
    private void migrate(AbstractDatabase db, String tablename, String ownerColumn, String worldColumn,
                         String xColumn, String zColumn) {
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
                plugin.getLogger().warning("There was an error while trying to fetching original data: " + e);
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
                    plugin.getWGManager().claim(chunk, next.owner);
                }
                counter++;

                if (counter % 600 == 0)
                    db.getLogger().info("Processed " + counter + " lands already. " + (objs.size() - counter) / 20 / 60 + " minutes remaining!");
            }
        }.runTaskTimer(plugin.getPlugin(), 0, 1);
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
