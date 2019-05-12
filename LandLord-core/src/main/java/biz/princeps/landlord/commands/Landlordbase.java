package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.commands.admin.*;
import biz.princeps.landlord.commands.claiming.*;
import biz.princeps.landlord.commands.claiming.adv.Advertise;
import biz.princeps.landlord.commands.claiming.adv.RemoveAdvertise;
import biz.princeps.landlord.commands.friends.*;
import biz.princeps.landlord.commands.homes.Home;
import biz.princeps.landlord.commands.homes.SetHome;
import biz.princeps.landlord.commands.management.*;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.MainCommand;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.command.SubCommand;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        this.addSubcommand(new Migrate(pl));
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



}
