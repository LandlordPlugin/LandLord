package biz.princeps.landlord.commands.admin;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Reload extends LandlordCommand {

    public Reload(ILandLord plugin) {
        super(plugin, plugin.getConfig().getString("CommandSettings.Reload.name"),
                plugin.getConfig().getString("CommandSettings.Reload.usage"),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Reload.permissions")),
                Sets.newHashSet(plugin.getConfig().getStringList("CommandSettings.Reload.aliases")));
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        CommandSender issuer = properties.getCommandSender();

        issuer.sendMessage(ChatColor.RED + "Reloading is not recommended! Before reporting any bugs, please restart your server.");

        plugin.getLangManager().reload();
        plugin.reloadConfig();
        plugin.setupPrincepsLib();
        plugin.postloadPrincepsLib();

        String msg = lm.getString("Commands.Reload.success");
        issuer.sendMessage(msg);
    }
}
