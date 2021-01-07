package biz.princeps.lib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 2/5/18
 * <p>
 * Just a helper class
 */
public class Properties {

    private final CommandSender commandSender;
    private final Command command;

    public Properties(CommandSender commandSender, Command command) {
        this.commandSender = commandSender;
        this.command = command;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public boolean isPlayer() {
        return commandSender instanceof Player;
    }

    public boolean isConsole() {
        return commandSender instanceof ConsoleCommandSender;
    }

    public Player getPlayer() {
        if (!(commandSender instanceof Player)) {
            throw new RuntimeException("Invalid call on Properties#getPlayer! CommandSender is not a player!");
        }
        return (Player) commandSender;
    }

    public ConsoleCommandSender getConsole() {
        if (!(commandSender instanceof ConsoleCommandSender)) {
            throw new RuntimeException("Invalid call on Properties#getConsole! CommandSender is not a console!");
        }
        return (ConsoleCommandSender) commandSender;
    }

    public void sendMessage(String s) {
        commandSender.sendMessage(s);
    }

    public void sendUsage() {
        String[] split = command.getUsage().split("\\|");
        for (String s : split) {
            sendMessage(s);
        }
    }
}
