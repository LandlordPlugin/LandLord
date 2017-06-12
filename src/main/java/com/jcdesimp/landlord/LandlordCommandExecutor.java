package com.jcdesimp.landlord;

import com.jcdesimp.landlord.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

/**
 * Command Executor class for LandLord
 */
@SuppressWarnings("UnusedParameters")
public class LandlordCommandExecutor implements CommandExecutor {

    private HashMap<String, LandlordCommand> registeredCommands;

    private Help helpCommand;

    public LandlordCommandExecutor(Landlord plugin) {
        this.registeredCommands = new HashMap<>();

        this.helpCommand = new Help(plugin, this);

        // note order of registration will affect how they show up in the help menu
        this.register(helpCommand);             // register the help command (already instantiated)

        this.register(new Claim(plugin));       // register the claim command
        this.register(new Unclaim(plugin));     // register the unclaim command
        this.register(new AddFriend(plugin));   // register the addfriend command
        this.register(new Unfriend(plugin));    // register the unfriend command
        this.register(new Friends(plugin));     // register the friends command
        this.register(new FriendAll(plugin));   // register the friendall command
        this.register(new UnfriendAll(plugin)); // register the unfriendall command
        this.register(new ShowMap(plugin));     // register the map command
        this.register(new Manage(plugin));      // register the manage command
        this.register(new LandList(plugin));    // register the list command
        this.register(new Info(plugin));        // register the info command
        this.register(new ListPlayer(plugin));  // register the listplayer command
        this.register(new ClearWorld(plugin));  // register the clearworld command
        this.register(new Reload(plugin));      // register the reload command

    }


    /**
     * Main command handler
     *
     * @param sender who sent the command
     * @param label  exact command (or alias) run
     * @param args   given with command
     * @return boolean
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("landlord")) { // If the player typed /land then do the following...
            // Check if the attempted command is registered
            if (args.length == 0 || !registeredCommands.containsKey(args[0].toLowerCase())) {
                // if there is no command, or it's not registered, show the help text as the command given is unknown
                return helpCommand.execute(sender, new String[]{}, label);
            } else {
                // if it is, execute it with the given args
                return registeredCommands.get(args[0].toLowerCase()).execute(sender, args, label);
            }

        } //If this has happened the function should return true.
        // If this hasn't happened the value of false will be returned.
        return false;
    }


    /**
     * Register a new command
     *
     * @param cmd LandlordCommand to register
     * @return boolean of success - should fail if a requested label (name, alias) is not available
     */
    public boolean register(LandlordCommand cmd) {
        String[] commandTriggers = cmd.getTriggers();

        // if there are no aliases then fail, command would be impossible to trigger.
        if (commandTriggers.length <= 0) {

            return false;
        }

        // iterate the command aliases
        for (String trigger : commandTriggers) {

            // Check if the command is taken
            if (registeredCommands.containsKey(trigger.toLowerCase())) {
                System.out.println("Failed to register command with alias '" + trigger + "', already taken!");
                continue;   // Command name is taken already
            }

            // register an entry for this command trigger
            registeredCommands.put(trigger.toLowerCase(), cmd);
        }

        helpCommand.addCommand(cmd);    // add the command to the help list

        return true;
    }

}
