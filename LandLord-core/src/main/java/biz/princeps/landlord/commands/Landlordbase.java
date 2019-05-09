package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
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
import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.MainCommand;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.command.SubCommand;
import biz.princeps.lib.storage_old.AbstractDatabase;
import biz.princeps.lib.storage_old.MySQL;
import biz.princeps.lib.storage_old.SQLite;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 16/07/17
 * <p>
 * This command handler may look a bit unfamiliar. It is based on shitty system I programmed a long time ago (PrincepsLib)
 * Basically a single command is created by extending MainCommand. For example you would do:
 * class HealCommand extends MainCommand {...} // introduces a heal command
 * Landlordbase describes the base command ./landlord
 * Subcommands are created by creating a class, that extends SubCommand. Add that one as subcmd.
 * Always call super(cmdname, description, usage, permissions, aliases) to initialize the (sub)command with everything
 * it needs to work.
 */
public class Landlordbase extends MainCommand {

    private ILandLord pl;

    public Landlordbase(ILandLord pl) {
        super(pl.getConfig().getString("CommandSettings.Main.name"),
                pl.getConfig().getString("CommandSettings.Main.description"),
                pl.getConfig().getString("CommandSettings.Main.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Main.permissions")),
                pl.getConfig().getStringList("CommandSettings.Main.aliases").toArray(new String[]{}));

        this.pl = pl;
        reloadCommands();
    }

    /**
     * Reloads all commands. Reinitialisation, to pick up changed config variables
     */
    private void reloadCommands() {
        this.clearSubcommands();
        this.addSubcommand(new Version());
        this.addSubcommand(new Migrate());
        this.addSubcommand(new Confirm());

        this.addSubcommand(new Info(pl));
        this.addSubcommand(new Claim(pl, false));
        this.addSubcommand(new Unclaim(pl));
        this.addSubcommand(new UnclaimAll(pl));
        this.addSubcommand(new Addfriend(pl));
        this.addSubcommand(new AddfriendAll(pl));
        this.addSubcommand(new Unfriend(pl));
        this.addSubcommand(new UnfriendAll(pl));
        this.addSubcommand(new Advertise(pl));
        this.addSubcommand(new RemoveAdvertise(pl));
        this.addSubcommand(new ListFriends(pl));
        this.addSubcommand(new ListLands(pl));
        this.addSubcommand(new Claims(pl));
        this.addSubcommand(new Shop(pl));
        this.addSubcommand(new GiveClaims(pl));
        this.addSubcommand(new Update(pl));
        this.addSubcommand(new AdminTeleport(pl));
        this.addSubcommand(new MultiClaim(pl));
        this.addSubcommand(new LLItem(pl));
        this.addSubcommand(new Borders(pl));
        this.addSubcommand(new Home(pl));
        this.addSubcommand(new SetHome(pl));
        this.addSubcommand(new Manage(pl));
        this.addSubcommand(new ManageAll(pl));
        this.addSubcommand(new Clear(pl));
        this.addSubcommand(new LandMap(pl));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tabReturn = new ArrayList<>();

        // Length == 1 means there is just the first thing like /ll typed
        if (args.length == 1) {
            for (SubCommand subCommand : this.subCommandMap.values()) {
                if (subCommand.hasPermission(sender)) {

                    if (subCommand instanceof Borders) {
                        if (Options.enabled_borders()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof LandMap) {
                        if (Options.enabled_map()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof Shop || subCommand instanceof Claims) {
                        if (Options.enabled_shop()) {
                            tabReturn.add(subCommand.getName());
                        }
                    } else if (subCommand instanceof Home || subCommand instanceof SetHome) {
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

                    if (subcmd instanceof LandMap) {
                        tabReturn.add("on");
                        tabReturn.add("off");
                    }

                    if (subcmd instanceof Addfriend || subcmd instanceof AddfriendAll ||
                            subcmd instanceof Unfriend || subcmd instanceof UnfriendAll) {

                        if (args[1].isEmpty()) {
                            Bukkit.getOnlinePlayers().forEach(p -> tabReturn.add(p.getName()));
                        } else {
                            Bukkit.getOnlinePlayers().stream()
                                    .filter(p -> p.getName().startsWith(args[1])).forEach(p -> tabReturn.add(p.getName()));
                        }
                        return tabReturn;
                    } else if (subcmd instanceof MultiClaim) {
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

    /**
     * Main onCommand function of ./landlord.
     * Display the help menu here.
     * This function is not called for subcommands like ./ll claim
     *
     * @param properties a cool properties object, that contains stuff like isPlayer, isConsole
     * @param arguments  the arguments passed here.
     */
    @Override
    public void onCommand(Properties properties, Arguments arguments) {

        if (properties.isConsole()) return;

        ILangManager lm = pl.getLangManager();
        List<String> playersList = lm.getStringList("Commands.Help.players");
        List<String> adminList = lm.getStringList("Commands.Help.admins");

        int perSite = pl.getConfig().getInt("HelpCommandPerSite");

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
        pl.getUtilsProxy().send_basecomponent(properties.getPlayer(), msg.create());
    }

    /**
     * Hui this is some really old legacy shit. I would not dare to touch any of this ever.
     * There used to be a different database scheme a very long time ago. Or maybe there wasnt. I don't really remember.
     * Anyways, this function can be used to migrate this old database to the new one. Maybe.
     */
    private void migrate(AbstractDatabase db, String tablename, String ownerColumn, String worldColumn, String xColumn, String zColumn) {
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
                pl.getLogger().warning("There was an error while trying to fetching original data: " + e);
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
                    pl.getWGProxy().claim(chunk, next.owner);
                }
                counter++;

                if (counter % 600 == 0)
                    db.getLogger().info("Processed " + counter + " lands already. " + (objs.size() - counter) / 20 / 60 + " minutes remaining!");
            }
        }.runTaskTimer(pl.getPlugin(), 0, 1);
    }

    public class Confirm extends SubCommand {

        public Confirm() {
            super("confirm",
                    "/lldm help",
                    Sets.newHashSet(Collections.singleton("landlord.use")),
                    Sets.newHashSet());
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            // just a placeholder for the confirmationmanager, this is on purpose! Check PrincepsLib for more info.
        }
    }

    public class Version extends SubCommand {

        public Version() {
            super("version",
                    "/ll version",
                    Sets.newHashSet(Collections.singleton("landlord.admin")),
                    Sets.newHashSet());
        }

        @Override
        public void onCommand(Properties properties, Arguments arguments) {
            String msg = pl.getLangManager().getTag() + " &aLandLord version: &7%version%"
                    .replace("%version%", pl.getPlugin().getDescription().getVersion());
            properties.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    /**
     * Do not touch! Black magic!
     */
    public class Migrate extends SubCommand {

        public Migrate() {
            super("migrate",
                    "/ll migrate <v1|v2> (v1 the original landlord, v2 Princeps upgraded version)",
                    Sets.newHashSet(Collections.singletonList("landlord.admin.migrate")),
                    Sets.newHashSet());
        }

        @Override
        public void onCommand(Properties properties, Arguments args) {
            if (properties.getCommandSender().hasPermission("landlord.admin.manage")) {
                Logger logger = pl.getLogger();

                if (args.size() > 0) {

                    if (args.get()[0].equals("v1")) {
                        // SQLite based migration

                        SQLite sqLite = new SQLite(logger, pl.getPlugin().getDataFolder() + "/Landlord.db") {
                        };

                        logger.info("Starting to migrate from v1 Ebean Database...");
                        migrate(sqLite, "ll_land", "owner_name", "world_name", "x", "z");
                    }
                    if (args.get()[0].equals("v2")) {
                        if (args.size() == 2) {
                            if (args.get()[1].equals("sqlite")) {
                                // SQLite based migration
                                SQLite sqLite = new SQLite(logger, pl.getPlugin().getDataFolder() + "/database.db") {
                                };

                                logger.info("Starting to migrate from v2-SQLite Database...");
                                migrate(sqLite, "ll_land", "owneruuid", "world", "x", "z");

                            } else if (args.get()[1].equals("mysql")) {
                                // mysql based migration

                                logger.info("In your plugin folder a file called MySQL.yml has been generated. You need to enter the credentials of your former landlord database.");
                                FileConfiguration mysqlConfig = PrincepsLib.prepareDatabaseFile();
                                MySQL mySQL = new MySQL(logger, mysqlConfig.getString("MySQL.Hostname"),
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
