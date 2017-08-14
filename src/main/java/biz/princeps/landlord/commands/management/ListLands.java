package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.guis.ManageGUI;
import biz.princeps.landlord.guis.ManageGUIAll;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spatium on 18.07.17.
 */
public class ListLands extends LandlordCommand {

    private String header = plugin.getLangManager().getRawString("Commands.ListLands.header");

    public void onListLands(Player player) {

        List<ProtectedRegion> lands = new ArrayList<>();

        for (ProtectedRegion protectedRegion : plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegions().values()) {
            if (protectedRegion.isOwner(plugin.getWgHandler().getWG().wrapPlayer(player))) {
                lands.add(protectedRegion);
            }
        }
        if (lands.size() > 0) {

            MultiPagedGUI landGui = new MultiPagedGUI(player, 5, header);

            lands.forEach(land -> landGui.addIcon(new Icon(new ItemStack(Material.GRASS))
                    .setName(land.getId())
                    .addClickAction((p) -> {
                                ManageGUI manageGui = new ManageGUI(player, land, landGui);
                                manageGui.setTitle(manageGui.getRawTitle().replace("%info%", land.getId()));
                                manageGui.display();
                            }
                    )
            ));

            landGui.setIcon(52, new Icon(new ItemStack(Material.BEACON))
                    .setName(lm.getRawString("Commands.ListLands.manageAll"))
                    .addClickAction((p) -> {
                        ManageGUIAll manageGUIAll = new ManageGUIAll(player, landGui);
                        manageGUIAll.display();
                    }));

            landGui.display();

        } else {
            player.sendMessage(plugin.getLangManager().getString("Commands.ListLands.noLands"));
        }
    }


}
