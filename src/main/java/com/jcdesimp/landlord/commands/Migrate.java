package com.jcdesimp.landlord.commands;

import com.jcdesimp.landlord.Landlord;
import org.bukkit.command.CommandSender;

/**
 * Created by spatium on 12.06.17.
 */
public class Migrate implements LandlordCommand {


    public Migrate(Landlord plugin) {
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, String label) {
        return false;
    }

    @Override
    public String getHelpText(CommandSender sender) {
        return null;
    }

    @Override
    public String[] getTriggers() {
        return new String[0];
    }
}
