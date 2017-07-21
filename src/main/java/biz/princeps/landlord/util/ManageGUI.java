package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by spatium on 21.07.17.
 */
public class ManageGUI extends AbstractGUI {

    private ProtectedRegion land;

    public ManageGUI(Player player, ProtectedRegion land) {
        super(player, 9, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.header").replace("%info%", land.getId()));
        this.land = land;
    }

    public ManageGUI(Player player, ProtectedRegion land, MultiPagedGUI landGui) {
        super(player, 9, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.header").replace("%info%", land.getId()), landGui);
        this.land = land;
    }


    @Override
    public Inventory display() {
        create();
        this.player.openInventory(this.getInventory());
        return this.getInventory();
    }

    private void create(){

    }
}
