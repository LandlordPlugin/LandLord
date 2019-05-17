package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import biz.princeps.lib.chat.MultiPagedMessage;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.MainCommand;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.command.SubCommand;
import com.google.common.collect.Sets;
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
 * This command protection may look a bit unfamiliar. It is based on shitty system I programmed a long time ago
 * (PrincepsLib)
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
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tabReturn = new ArrayList<>();

        // Length == 1 means there is just the first thing like /ll typed
        if (args.length == 1) {
            for (SubCommand subCommand : this.subCommandMap.values()) {
                if (subCommand.hasPermission(sender)) {
                    tabReturn.add(subCommand.getName());
                }
            }

            if (!args[0].isEmpty())
                tabReturn.removeIf(next -> !next.startsWith(args[0]));

        } else if (args.length == 2) {
            for (SubCommand subcmd : subCommandMap.values()) {
                if (subcmd.matches(args[0])) {

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
        pl.getUtilsManager().sendBasecomponent(properties.getPlayer(), msg.create());
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
