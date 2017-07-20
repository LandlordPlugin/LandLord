package biz.princeps.landlord.commands;

import chat.ChatAPI;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spatium on 18.07.17.
 */
public class ListLands extends LandlordCommand {


    String segment = plugin.getLangManager().getString("Commands.ListLands.landSegment");
    String header = plugin.getLangManager().getString("Commands.ListLands.header");

    public void onListLands(Player player, String[] args) {

        List<String> lands = new ArrayList<>();

        int i = 0;
        for (ProtectedRegion protectedRegion : plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegions().values()) {
            if (protectedRegion.isOwner(plugin.getWgHandler().getWG().wrapPlayer(player))) {
                lands.add(ChatColor.GOLD + String.valueOf(++i) + " " + ChatColor.WHITE + protectedRegion.getId());
            }
        }
        if (lands.size() > 0) {
            lands.forEach(s -> s = segment.replace("%info%", s));
            BaseComponent[] baseComponents = ChatAPI.createMultiPagedMessge()
                    .setPerSite(10)
                    .setHeaderString(header.replace("%count%", String.valueOf(lands.size())))
                    .setPreviousString("&a<<<< Previous >>>>          ")
                    .setNextString("&a<<<< Next >>>>")
                    .setCommand("landlist", args)
                    .setElements(lands).build().create();
            player.spigot().sendMessage(baseComponents);
        } else {
            player.sendMessage(plugin.getLangManager().getString("Commands.ListLands.noLands"));
        }
    }


}
