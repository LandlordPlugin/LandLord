package biz.princeps.landlord.items;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.lib.item.AbstractItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 11/22/17
 */
public class Maitem extends AbstractItem {
    // So called My-tem or Management-Item. Whatever sounds better to you <3
    // 19-05-02 I must have been on drugs when I wrote that comment lmao.

    public static final String NAME = "maitem";
    private final ItemStack STACK;
    private final ArrayList<ItemClickAction> clickActions = new ArrayList<>();

    private final ILandLord plugin;

    public Maitem(ILandLord pl) {
        super(NAME, new ItemStack(Material.valueOf(pl.getConfig().getString("MaItem.item"))), true, false);
        this.plugin = pl;
        this.STACK = getBukkitStack();
        initClickActions();
        setItemAppearance();
    }

    private void setItemAppearance() {
        List<String> lore = plugin.getLangManager().getStringList("MaItem.lore");
        String name = plugin.getLangManager().getRawString("MaItem.itemname");

        ItemMeta meta = STACK.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        STACK.setItemMeta(meta);
    }

    private void initClickActions() {
        List<String> stringList = plugin.getConfig().getStringList("MaItem.modes");

        for (String s : stringList) {
            String[] splitted = s.split(":");
            if (splitted.length != 3) {
                plugin.getLogger().warning("There was an error parsing your MaItem Config in line " + s);
                continue;
            }

            ClickedAtCondition clickedAtCondition = ClickedAtCondition.valueOf(splitted[0].toUpperCase());
            ClickCondition clickCondition = ClickCondition.valueOf(splitted[1].toUpperCase());
            ClickResult clickResult = ClickResult.valueOf(splitted[2].toUpperCase());
            clickActions.add(new ItemClickAction(clickedAtCondition, clickCondition, clickResult));
        }
    }

    @Override
    public void onClick(Action action, Player p, Location location) {
        if (location == null)
            location = p.getLocation();

        IOwnedLand landAtLoc = plugin.getWGManager().getRegion(location);
        switch (action) {
            case LEFT_CLICK_BLOCK:

                for (ItemClickAction clickAction : clickActions) {
                    if (clickAction.getActivation() == ClickedAtCondition.LEFTCLICKBLOCK && !p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    } else if (clickAction.getActivation() == ClickedAtCondition.SHIFTLEFTCLICKBLOCK && p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    }
                }
                break;

            case RIGHT_CLICK_BLOCK:
                for (ItemClickAction clickAction : clickActions) {
                    if (clickAction.getActivation() == ClickedAtCondition.RIGHTCLICKBLOCK && !p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    } else if (clickAction.getActivation() == ClickedAtCondition.SHIFTRIGHTCLICKBLOCK && p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    }
                }

                break;
            case LEFT_CLICK_AIR:

                for (ItemClickAction clickAction : clickActions) {
                    if (clickAction.getActivation() == ClickedAtCondition.LEFTCLICKAIR && !p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    } else if (clickAction.getActivation() == ClickedAtCondition.SHIFTLEFTCLICKAIR && p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    }
                }

                break;
            case RIGHT_CLICK_AIR:

                for (ItemClickAction clickAction : clickActions) {
                    if (clickAction.getActivation() == ClickedAtCondition.RIGHTCLICKAIR && !p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    } else if (clickAction.getActivation() == ClickedAtCondition.SHIFTRIGHTCLICKAIR && p.isSneaking()) {
                        checkForLandCondition(clickAction, p, landAtLoc, location);
                    }
                }

                break;
            case PHYSICAL:
                break;
        }


    }

    private void checkForLandCondition(ItemClickAction clickAction, Player p, IOwnedLand landAtLoc, Location loc) {
        switch (clickAction.getCondition()) {

            case OWNLAND:
                if (landAtLoc != null && landAtLoc.isOwner(p.getUniqueId()))
                    executeAction(clickAction, p, loc);
                break;

            case OTHERLAND:
                if (landAtLoc != null && !landAtLoc.isOwner(p.getUniqueId())) {
                    executeAction(clickAction, p, loc);
                }
                break;

            case ANYLAND:
                executeAction(clickAction, p, loc);
                break;
        }
    }

    private void executeAction(ItemClickAction clickAction, Player p, Location loc) {
        switch (clickAction.getResult()) {

            case INFO:
                Bukkit.dispatchCommand(p, "ll info");
                break;
            case BUY:
                Bukkit.dispatchCommand(p, "ll claim");
                break;
            case MANAGE:
                Bukkit.dispatchCommand(p, "ll manage");
                break;
            case MANAGEALL:
                Bukkit.dispatchCommand(p, "ll manageall");
                break;
            case TOGGLEMAP:
                Bukkit.dispatchCommand(p, "ll map");
                break;
            case TOGGLEBORDERS:
                Bukkit.dispatchCommand(p, "ll borders");
                break;
        }
    }


    enum ClickedAtCondition {
        LEFTCLICKBLOCK, RIGHTCLICKBLOCK, LEFTCLICKAIR, RIGHTCLICKAIR, SHIFTLEFTCLICKBLOCK, SHIFTRIGHTCLICKBLOCK, SHIFTLEFTCLICKAIR, SHIFTRIGHTCLICKAIR
    }

    enum ClickCondition {
        OWNLAND, OTHERLAND, ANYLAND
    }

    enum ClickResult {
        INFO, BUY, MANAGE, MANAGEALL, TOGGLEMAP, TOGGLEBORDERS
    }

    static class ItemClickAction {

        private final ClickedAtCondition activation;
        private final ClickCondition condition;
        private final ClickResult result;

        public ItemClickAction(ClickedAtCondition activation, ClickCondition condition, ClickResult result) {
            this.activation = activation;
            this.condition = condition;
            this.result = result;
        }

        public ClickedAtCondition getActivation() {
            return activation;
        }

        public ClickCondition getCondition() {
            return condition;
        }

        public ClickResult getResult() {
            return result;
        }
    }

}
