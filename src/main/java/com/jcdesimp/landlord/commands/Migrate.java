package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.db.SQLiteDatabase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

/**
 * Created by spatium on 12.06.17.
 */
public class Migrate implements LandlordCommand {


    private Landlord pl;

    public Migrate(Landlord plugin) {
        this.pl = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        if (sender.isOp()) {
            if (args.length == 1) {
                ComponentBuilder builder = new ComponentBuilder("")
                        .color(ChatColor.GREEN).append("You are about to convert your existing LandLord Database to the 1.12 SQlite/MySQL database.\n")
                        .color(ChatColor.RED).append("This process is very experimental and might take a while! \n")
                        .color(ChatColor.RED).append("Please note: The conversion is not going to convert existing Landflags or existing friends!\n")
                        .color(ChatColor.YELLOW).append("If you want to start the conversion, type '/ll migrate confirm' or simply click this line")
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ll migrate confirm"));
                sender.spigot().sendMessage(builder.create());
                return true;
            }
            if (args.length == 2) {
                if (args[1].equals("confirm")) {
                    SQLiteDatabase.migrate();
                    sender.sendMessage(ChatColor.GREEN + "Migration was successfull... hopefully! \nIf you have a large database, give the server some time to import everything! \nCheck the log for any errors, and make sure everything is working correctly.");
                }
            }
        }
        return true;
    }

    @Override
    public String getHelpText(CommandSender sender) {
        return "This command will convert your existing landlord database to the new 1.12 format!";
    }

    @Override
    public String[] getTriggers() {
        return new String[]{"migrate", "conversion"};
    }
}
