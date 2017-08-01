package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
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

import java.util.*;

/**
 * Created by spatium on 24.07.17.
 */
public class ManageGUIAll extends AbstractGUI {

    private LangManager lm;
    private Landlord plugin;

    public ManageGUIAll(Player player) {
        super(player, 9, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.all.header"));
        plugin = Landlord.getInstance();
        lm = plugin.getLangManager();
    }

    public ManageGUIAll(Player player, MultiPagedGUI landGui) {
        super(player, 18, Landlord.getInstance().getLangManager().getRawString("Commands.Manage.all.header"), landGui);
        plugin = Landlord.getInstance();
        lm = plugin.getLangManager();
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
        List<String> allowUseDesc = lm.getStringList("Commands.Manage.AllowUse.description");
        List<String> regenerateDesc = lm.getStringList("Commands.Manage.Regenerate.description");
        List<String> greedDesc = lm.getStringList("Commands.Manage.SetGreet.description");
        List<String> farewellDesc = lm.getStringList("Commands.Manage.SetFarewell.description");

        Collection<ProtectedRegion> lands = plugin.getWgHandler().getWG().getRegionManager(player.getWorld()).getRegions().values();
        ProtectedRegion land = lands.iterator().next();

        if (!land.getFlags().keySet().contains(DefaultFlag.USE)) {
            land.setFlag(DefaultFlag.USE, StateFlag.State.DENY);
            land.setFlag(DefaultFlag.USE.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
        }

        int position = 0;

        // Allow building icon
        if (plugin.getConfig().getBoolean("Manage.building")) {
            this.setIcon(position, new Icon(createItem(Material.GRASS, 1,
                    lm.getRawString("Commands.Manage.AllowBuild.title"), formatList(allowDesc, land.getFlag(DefaultFlag.BUILD).name())))
                    .addClickAction((p) -> {
                        StateFlag.State state = StateFlag.State.ALLOW;

                        if (land.getFlag(DefaultFlag.BUILD) == StateFlag.State.ALLOW)
                            state = StateFlag.State.DENY;

                        for (ProtectedRegion landi : lands) {
                            landi.setFlag(DefaultFlag.BUILD, state);
                        }

                        updateLore(0, formatList(allowDesc, land.getFlag(DefaultFlag.BUILD).name()));
                    })
            );
            position++;
        }

        // Allow use icon
        if (plugin.getConfig().getBoolean("Manage.use")) {
            this.setIcon(position, new Icon(createItem(Material.REDSTONE_TORCH_ON, 1,
                    lm.getRawString("Commands.Manage.AllowUse.title"), formatList(allowUseDesc, land.getFlag(DefaultFlag.USE).name())))
                    .addClickAction((p) -> {
                        StateFlag.State state = StateFlag.State.ALLOW;

                        if (land.getFlag(DefaultFlag.USE) == StateFlag.State.ALLOW)
                            state = StateFlag.State.DENY;

                        for (ProtectedRegion landi : lands) {
                            landi.setFlag(DefaultFlag.USE, state);
                        }

                        updateLore(1, formatList(allowUseDesc, land.getFlag(DefaultFlag.USE).name()));
                    })
            );
            position++;
        }

        // Regenerate icon
        if (plugin.getConfig().getBoolean("Manage.regenerate")) {
            double cost = plugin.getConfig().getDouble("ResetCost") * lands.size();
            this.setIcon(position, new Icon(createItem(Material.BARRIER, 1,
                    lm.getRawString("Commands.Manage.Regenerate.title"), formatList(regenerateDesc, plugin.getVaultHandler().format(cost))))
                    .addClickAction((p) -> {
                        ConfirmationGUI confi = new ConfirmationGUI(p, lm.getRawString("Commands.Manage.Regenerate.confirmation")
                                .replace("%cost%", plugin.getVaultHandler().format(cost)),
                                (p1) -> {
                                    boolean flag = false;
                                    if (plugin.isVaultEnabled()) {
                                        if (plugin.getVaultHandler().hasBalance(player.getUniqueId(), cost)) {
                                            plugin.getVaultHandler().take(player.getUniqueId(), cost);
                                            flag = true;
                                        } else
                                            player.sendMessage(lm.getString("Commands.Manage.Regenerate.notEnoughMoney")
                                                    .replace("%cost%", plugin.getVaultHandler().format(cost))
                                                    .replace("%name%", land.getId()));
                                    }
                                    if (flag) {
                                        for (ProtectedRegion protectedRegion : lands) {
                                            //         System.out.println(protectedRegion.getMinimumPoint().getBlockX() / 16 + ":" + protectedRegion.getMinimumPoint().getBlockZ() / 16);
                                            player.getWorld().regenerateChunk(protectedRegion.getMinimumPoint().getBlockX() / 16, protectedRegion.getMinimumPoint().getBlockZ() / 16);
                                        }

                                        player.sendMessage(lm.getString("Commands.Manage.Regenerate.success")
                                                .replace("%land%", land.getId()));
                                        display();
                                    }
                                }, (p2) -> {
                            player.sendMessage(lm.getString("Commands.Manage.Regenerate.abort")
                                    .replace("%land%", land.getId()));
                            display();
                        }, this);
                        confi.display();
                    })
            );
            position++;
        }


        // Set greet icon
        if (plugin.getConfig().getBoolean("Manage.setgreet")) {
            String currentGreet = land.getFlag(DefaultFlag.GREET_MESSAGE);
            this.setIcon(position, new Icon(createItem(Material.BAKED_POTATO, 1,
                    lm.getRawString("Commands.Manage.SetGreet.title"), formatList(greedDesc, currentGreet))).addClickAction((p -> {
                        p.closeInventory();
                        ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Manage.SetGreet.clickMsg"));

                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setgreetall "));

                        p.spigot().sendMessage(builder.create());
                    }))
            );
            position++;
        }

        // set farewell icon
        if (plugin.getConfig().getBoolean("Manage.setfarewell")) {
            String currentFarewell = land.getFlag(DefaultFlag.FAREWELL_MESSAGE);
            this.setIcon(position, new Icon(createItem(Material.CARROT_ITEM, 1, lm.getRawString("Commands.Manage.SetFarewell.title"), formatList(farewellDesc, currentFarewell))).addClickAction((p -> {
                        p.closeInventory();
                        ComponentBuilder builder = new ComponentBuilder(lm.getString("Commands.Manage.SetFarewell.clickMsg"));
                        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setfarewellall "));
                        p.spigot().sendMessage(builder.create());
                    }))
            );
            position++;
        }

        // set friends icon
        if (plugin.getConfig().getBoolean("Manage.friends")) {
            ItemStack skull = createSkull(player.getName(), lm.getRawString("Commands.Manage.ManageFriends.title"), lm.getStringList("Commands.Manage.ManageFriends.description"));
            Set<UUID> friends = land.getMembers().getUniqueIds();
            MultiPagedGUI friendsGui = new MultiPagedGUI(player, (int) Math.ceil((double) friends.size() / 9.0), lm.getRawString("Commands.Manage.ManageFriends.title"), new ArrayList<>(), this) {

            };
            friends.forEach(id -> friendsGui.addIcon(new Icon(createSkull(Bukkit.getOfflinePlayer(id).getName(),
                    Bukkit.getOfflinePlayer(id).getName(), lm.getStringList("Commands.Manage.ManageFriends.friendSegment"))).addClickAction(player -> {
                ConfirmationGUI confirmationGUI = new ConfirmationGUI(player, lm.getRawString("Commands.Manage.ManageFriends.unfriend")
                        .replace("%player%", Bukkit.getOfflinePlayer(id).getName()),
                        p -> {
                            friendsGui.removeIcon(friendsGui.filter(Bukkit.getOfflinePlayer(id).getName()).get(0));
                            Bukkit.dispatchCommand(player, "land unfriendall " + Bukkit.getOfflinePlayer(id).getName());
                            player.closeInventory();
                            friendsGui.display();
                        },
                        p -> {
                            player.closeInventory();
                            friendsGui.display();
                        }, friendsGui);
                confirmationGUI.display();
            })));

            this.setIcon(position, new Icon(skull).setName(lm.getRawString("Commands.Manage.ManageFriends.title")).addClickAction(p -> friendsGui.display()));
            position++;
        }
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