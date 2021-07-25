package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Reload extends LandlordCommand {
    public Reload(ILandLord pl) {
        super(pl, pl.getConfig().getString("CommandSettings.Reload.name"),
                pl.getConfig().getString("CommandSettings.Reload.usage"),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Reload.permissions")),
                Sets.newHashSet(pl.getConfig().getStringList("CommandSettings.Reload.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        CommandSender issuer = properties.getCommandSender();

        issuer.sendMessage(ChatColor.RED + "Reloading is not recommended! Before reporting any bugs, please restart your server.");

        plugin.getLangManager().reload();
        plugin.getPlugin().reloadConfig();
        plugin.setupPrincepsLib();
        plugin.postloadPrincepsLib();

        String msg = lm.getString("Commands.Reload.success");
        issuer.sendMessage(msg);
    }
}
