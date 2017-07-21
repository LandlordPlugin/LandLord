package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.ClickAction;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by spatium on 21.07.17.
 */
public class ManageGUI extends AbstractGUI {

    private ProtectedRegion land;
    private LangManager lm;

    public ManageGUI(Player player, ProtectedRegion land) {
        super(player, 9, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.header").replace("%info%", land.getId()));
        this.land = land;
        lm = Landlord.getInstance().getLangManager();
    }

    public ManageGUI(Player player, ProtectedRegion land, MultiPagedGUI landGui) {
        super(player, 18, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.header").replace("%info%", land.getId()), landGui);
        this.land = land;
        lm = Landlord.getInstance().getLangManager();
    }


    @Override
    public Inventory display() {
        create();
        this.player.openInventory(this.getInventory());
        return this.getInventory();
    }

    @Override
    protected void create() {
        List<String> allowDesc = lm.getStringList("Commands.Manage.AllowBuild.description");
        List<String> regenerateDesc = lm.getStringList("Commands.Manage.Regenerate.description");
        List<String> greedDesc = lm.getStringList("Commands.Manage.SetGreet.description");
        List<String> farewellDesc = lm.getStringList("Commands.Manage.SetFarewell.description");


        // Allow building icon
        this.setIcon(0, new Icon(createItem(Material.GRASS, 1,
                lm.getRawString("Commands.Manage.AllowBuild.title"), formatList(allowDesc, land.getFlag(DefaultFlag.BUILD).name())))
                .addClickAction((p) -> {
                    StateFlag.State state = StateFlag.State.ALLOW;

                    if (land.getFlag(DefaultFlag.BUILD) == StateFlag.State.ALLOW)
                        state = StateFlag.State.DENY;

                    land.setFlag(DefaultFlag.BUILD, state);
                    updateLore(0, formatList(allowDesc, land.getFlag(DefaultFlag.BUILD).name()));
                })
        );

        // Regenerate icon
        double cost = Landlord.getInstance().getConfig().getDouble("ResetCost");
        this.setIcon(1, new Icon(createItem(Material.BARRIER, 1,
                lm.getRawString("Commands.Manage.Regenerate.title"), formatList(regenerateDesc, Landlord.getInstance().getVaultHandler().format(cost))))
                .addClickAction((p) -> {
                    ConfirmationGUI confi = new ConfirmationGUI(p, lm.getRawString("Commands.Manage.Regenerate.confirmation")
                            .replace("%cost%", Landlord.getInstance().getVaultHandler().format(cost)),
                            (p1) -> {
                                if (Landlord.getInstance().getVaultHandler().hasBalance(player.getUniqueId(), cost)) {
                                    Landlord.getInstance().getVaultHandler().take(player.getUniqueId(), cost);
                                    player.getWorld().regenerateChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
                                    player.sendMessage(lm.getString("Commands.Manage.Regenerate.success")
                                            .replace("%land%", land.getId()));
                                    display();
                                } else
                                    player.sendMessage(lm.getString("Commands.Manage.Regenerate.notEnoughMoney")
                                            .replace("%cost%", Landlord.getInstance().getVaultHandler().format(cost))
                                            .replace("%name%", land.getId()));
                            }, (p2) -> {
                        player.sendMessage(lm.getString("Commands.Manage.Regenerate.abort")
                                .replace("%land%", land.getId()));
                        display();
                    }, this);
                    confi.display();
                })
        );

        // Set greet icon
        String currentGreet = land.getFlag(DefaultFlag.GREET_MESSAGE);
        this.setIcon(2, new Icon(createItem(Material.BAKED_POTATO, 1,
                lm.getRawString("Commands.Manage.SetGreet.title"), formatList(greedDesc, currentGreet)))
                .addClickAction((p -> {
                    p.closeInventory();
                    ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Manage.SetGreet.clickMsg"));
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage " + land.getId() + " setgreet "));
                    p.spigot().sendMessage(builder.create());
                }))
        );

        // set farewell icon
        String currentFarewell = land.getFlag(DefaultFlag.FAREWELL_MESSAGE);
        this.setIcon(3, new Icon(createItem(Material.BEETROOT, 1,
                lm.getRawString("Commands.Manage.SetFarewell.title"), formatList(farewellDesc, currentFarewell)))
                .addClickAction((p -> {
                    p.closeInventory();
                    ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Manage.SetFarewell.clickMsg"));
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage " + land.getId() + " setfarewell "));
                    p.spigot().sendMessage(builder.create());
                }))
        );

        // set friends icon
        ItemStack skull = createSkull(player.getName(), lm.getRawString("Commands.Manage.ManageFriends.title"), lm.getStringList("Commands.Manage.ManageFriends.description"));
        Set<UUID> friends = land.getMembers().getUniqueIds();
        MultiPagedGUI friendsGui = new MultiPagedGUI(player, (int) Math.ceil((double) friends.size() / 9.0), lm.getRawString("Commands.Manage.ManageFriends.title"), new ArrayList<>(), this) {

        };
        friends.forEach(id -> friendsGui.addIcon(new Icon(createSkull(Bukkit.getOfflinePlayer(id).getName(),
                Bukkit.getOfflinePlayer(id).getName(), lm.getStringList("Commands.Manage.ManageFriends.friendSegment")))
                .addClickAction(player -> {
                    ConfirmationGUI confirmationGUI = new ConfirmationGUI(player, lm.getRawString("Commands.Manage.ManageFriends.unfriend")
                            .replace("%player%", Bukkit.getOfflinePlayer(id).getName()),
                            p -> {
                                friendsGui.removeIcon(friendsGui.filter(Bukkit.getOfflinePlayer(id).getName()).get(0));
                                Bukkit.dispatchCommand(player, "land unfriend " + Bukkit.getOfflinePlayer(id).getName());
                                player.closeInventory();
                                friendsGui.display();
                            },
                            p -> {
                                player.closeInventory();
                                friendsGui.display();
                            }, friendsGui);
                    confirmationGUI.display();
                })));

        this.setIcon(4, new Icon(skull)
                .setName(lm.getRawString("Commands.Manage.ManageFriends.title"))
                .addClickAction(p -> friendsGui.display())
        );
    }

    private void updateLore(int index, List<String> lore) {
        ItemStack item = this.getIcon(index).itemStack;
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        refresh();
    }

    private List<String> formatList(List<String> allowDesc, String flag) {
        List<String> newList = new ArrayList<>();
        allowDesc.forEach(s -> newList.add(s.replace("%var%", flag)));
        return newList;
    }

    private ItemStack createItem(Material mat, int amount, String title, List<String> desc) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(title);
        itemMeta.setLore(desc);
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack createSkull(String owner, String displayname, List<String> lore) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(owner);
        skullMeta.setDisplayName(displayname);
        skullMeta.setLore(lore);
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
