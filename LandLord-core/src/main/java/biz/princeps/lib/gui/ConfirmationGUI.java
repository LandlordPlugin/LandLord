package biz.princeps.lib.gui;

import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.crossversion.MaterialProxy;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Action;
import biz.princeps.lib.gui.simple.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by spatium on 21.07.17.
 */
public class ConfirmationGUI extends AbstractGUI {

    private final Action onAccept;
    private final Action onDecline;
    private String confirm = PrincepsLib.getTranslateableStrings().get("Confirmation.accept");
    private String decline = PrincepsLib.getTranslateableStrings().get("Confirmation.decline");

    public ConfirmationGUI(Player player, String msg, Action onAccept, Action onDecline, AbstractGUI mainMenu) {
        super(player, 9, msg, mainMenu);
        this.onAccept = onAccept;
        this.onDecline = onDecline;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public void setDecline(String decline) {
        this.decline = decline;
    }

    @Override
    protected void create() {
        ItemStack item = MaterialProxy.GREEN_WOOL.crossVersion();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(confirm);
        item.setItemMeta(meta);
        this.setIcon(0, new Icon(item).addClickAction(onAccept));

        ItemStack item2 = MaterialProxy.RED_WOOL.crossVersion();
        ItemMeta meta2 = item2.getItemMeta();
        meta2.setDisplayName(decline);
        item2.setItemMeta(meta2);
        this.setIcon(8, new Icon(item2).addClickAction(onDecline));
    }
}
