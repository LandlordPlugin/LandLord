package biz.princeps.landlord.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
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
        subcommands.put("info", new Info());
        subcommands.put("unclaim", new Unclaim());
        subcommands.put("addfriend", new Addfriend());
        subcommands.put("unfriend", new Unfriend());
        subcommands.put("addfriendall", new AddfriendAll());
        subcommands.put("unfriendall", new UnfriendAll());
        subcommands.put("listlands", new ListLands());
        subcommands.put("landmap", new LandMap());
        subcommands.put("clearworld", new Clear());
    }

    //TODO
    @Default
    @UnknownHandler
    @Subcommand("help")
    @CommandPermission("landlord.use")
    public void onDefault(CommandSender sender) {
        sender.sendMessage("help command");
    }

    @Subcommand("claim|buy|cl")
    @CommandAlias("claim")
    @Syntax("land claim - Claims the land you are currently standing on")
    @CommandPermission("landlord.player.own")
    public void onClaim(Player player) {
        ((Claim) subcommands.get("claim")).onClaim(player);
    }

    @Subcommand("info|i")
    @CommandAlias("landi|landinfo")
    @CommandPermission("landlord.player.info")
    @Syntax("land info - Shows information about the land you are standing on")
    public void onInfo(Player player) {
        ((Info) subcommands.get("info")).onInfo(player);
    }

    @Subcommand("unclaim|sell")
    @Syntax("land sell - Unclaim the chunk you are standing on")
    @CommandPermission("landlord.player.own")
    public void onUnClaim(Player player) {
        ((Unclaim) subcommands.get("unclaim")).onUnclaim(player);
    }

    @Subcommand("addfriend|friendadd")
    @Syntax("land addfriend - Adds friends to your land")
    @CommandPermission("landlord.player.own")
    public void onAddFriend(Player player, String[] names) {
        ((Addfriend) subcommands.get("addfriend")).onAddfriend(player, names);
    }

    @Subcommand("unfriend|friendremove|frienddelete")
    @Syntax("land unfriend - removes a friend from your land")
    @CommandPermission("landlord.player.own")
    public void onUnFriend(Player player, String[] names) {
        ((Unfriend) subcommands.get("unfriend")).onUnfriend(player, names);
    }

    @Subcommand("addfriendall|friendall")
    @Syntax("land addfriend - Adds friends to all your land")
    @CommandPermission("landlord.player.own")
    public void onAddfriendAll(Player player, String[] names) {
        ((AddfriendAll) subcommands.get("addfriendall")).onAddfriend(player, names);
    }

    @Subcommand("unfriendall|removeallfriends")
    @Syntax("land unfriendall - anfriend someone on all your lands")
    @CommandPermission("landlord.player.own")
    public void onUnfriendAll(Player player, String[] names) {
        ((UnfriendAll) subcommands.get("unfriendall")).onUnfriendall(player, names);
    }

    @Subcommand("list")
    @CommandAlias("listlands|landlist")
    @Syntax("land list - lists all your lands")
    @CommandPermission("landlord.player.own")
    public void onLandList(Player player) {
        int i = -1;
        for (String s : getOrigArgs()) {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
            }
        }
        ((ListLands) subcommands.get("listlands")).onListLands(player, new String[]{String.valueOf(i == -1 ? 0 : i)});
    }

    @Subcommand("map")
    @CommandAlias("landmap")
    @Syntax("land map - toggles the landmap")
    @CommandPermission("landlord.player.map")
    public void onToggleLandMap(Player player) {
        ((LandMap) subcommands.get("landmap")).onToggleLandMap(player);
    }


    @Subcommand("clear|clearworld")
    @CommandAlias("clearworld")
    @Syntax("land clear - toggles the landmap")
    @CommandPermission("landlord.admin.clearworld")
    public void onClearWorld(Player player, @Default("null") String target) {
        ((Clear) subcommands.get("clearworld")).onClearWorld(player, target);
    }

}
