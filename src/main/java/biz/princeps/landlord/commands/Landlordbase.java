package biz.princeps.landlord.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.minecraft.util.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by spatium on 16.07.17.
 */
@CommandAlias("ll|land|landlord")
public class Landlordbase extends BaseCommand {

    private Map<String, LandlordCommand> subcommands;

    public Landlordbase() {
        subcommands = new HashMap<>();
        subcommands.put("claim", new Claim());
    }

    @Default
    @UnknownHandler
    @Subcommand("help")
    public void onDefault(CommandSender sender) {
        sender.sendMessage("help command");
    }

    @Subcommand("claim|buy|cl")
    @CommandAlias("claim")
    @Syntax("land claim - Claims the land you are currently standing on")
    public void onClaim(Player player) {
        ((Claim) subcommands.get("claim")).onClaim(player);
    }


}
