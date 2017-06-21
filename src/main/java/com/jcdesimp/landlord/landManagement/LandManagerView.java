package com.jcdesimp.landlord.landManagement;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.LandFlag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static java.lang.Math.ceil;

/**
 * File created by jcdesimp on 3/4/14.
 */
public class LandManagerView implements Listener {

    private Landlord plugin;

    private Player player;
    private Inventory ui;
    private OwnedLand mLand;
    private ArrayList<ItemStack[]> permCols = new ArrayList<>();
    private ArrayList<Integer> permSlots = new ArrayList<>();
    private int pageNum = 0;
    private boolean isOpen = true;
    private int numPages;

    public LandManagerView(Player player, OwnedLand mLand, Landlord plugin) {
        this.plugin = plugin;

        FileConfiguration messages = plugin.getMessageConfig();

        plugin.getServer().getPluginManager().registerEvents(this, Landlord.getInstance());

        this.player = player;
        this.mLand = mLand;
        this.ui = Bukkit.createInventory(null, 36, messages.getString("manager.title"));
        this.updateUIData();
        this.numPages = (int) ceil(((double) permCols.size()) / 8.0);
        if (numPages == 1) {
            this.ui = Bukkit.createInventory(null, 27, messages.getString("manager.title"));
        }

        this.setToggles();
        this.buildUI();


        //this.showUI();
    }

    private ItemStack makeButton(String displayName, String[] lore, Material material) {
        return makeButton(displayName, lore, new ItemStack(material));
    }

    private ItemStack makeButton(String displayName, String[] lore, ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }


    public void showUI() {
        player.openInventory(ui);
    }

    /*
    private void hideUI(){

    }
    */

    private void updateUIData() {
        FileConfiguration messages = plugin.getMessageConfig();
        //Generate dynamic items based on land perms
        permSlots.clear();
        permCols.clear();
        for (Map.Entry<String, Landflag> entry : Landlord.getInstance().getFlagManager().getRegisteredFlags().entrySet()) {
            Landflag l = entry.getValue();
            permSlots.add(l.getPermSlot());
            String[] loreData = l.getDescription().split("\\|");
            String[] desc = colorLore(loreData);
            ItemStack header = makeButton(ChatColor.YELLOW + l.getDisplayName(), desc, l.getHeaderItem());
            ItemStack allState;
            //System.out.println("VALUE: " + mLand.getFlag(l));
            if (mLand.getFlag(l).canEveryone()) {
                desc = colorLore((messages.getString("manager.players.others") + " " + l.getAllowedText() + "|" + ChatColor.YELLOW + messages.getString("manager.toggle")).split("\\|"));
                allState = makeButton(ChatColor.GREEN + l.getAllowedTitle(), desc, new ItemStack(Material.WOOL, 1, (short) 5));
            } else {
                desc = colorLore((messages.getString("manager.players.others") + " " + l.getDeniedText() + "|" + ChatColor.YELLOW + messages.getString("manager.toggle")).split("\\|"));
                allState = makeButton(ChatColor.RED + l.getDeniedTitle(), desc, new ItemStack(Material.WOOL, 1, (short) 14));
            }

            ItemStack friendState;
            if (mLand.getFlag(l).canFriends()) {
                desc = colorLore((messages.getString("manager.players.friends") + " " + l.getAllowedText() + "|" + ChatColor.YELLOW + messages.getString("manager.toggle")).split("\\|"));
                friendState = makeButton(ChatColor.GREEN + l.getAllowedTitle(), desc, new ItemStack(Material.WOOL, 1, (short) 5));
            } else {
                desc = colorLore((messages.getString("manager.players.friends") + " " + l.getDeniedText() + "|" + ChatColor.YELLOW + messages.getString("manager.toggle")).split("\\|"));
                friendState = makeButton(ChatColor.RED + l.getDeniedTitle(), desc, new ItemStack(Material.WOOL, 1, (short) 14));
            }

            permCols.add(new ItemStack[]{header, allState, friendState});
        }
    }

    private String[] colorLore(String[] loreData) {
        String[] desc = new String[loreData.length];
        for (int s = 0; s < loreData.length; s++) {
            desc[s] = ChatColor.RESET + loreData[s];
        }
        return desc;
    }

    private void buildUI() {
        // Static Items Help and row/column markers
        FileConfiguration messages = plugin.getMessageConfig();
        final List<String> helpText = messages.getStringList("manager.help.text");
        ui.setItem(0, makeButton(ChatColor.GOLD + messages.getString("manager.help.button"),
                helpText.toArray(new String[helpText.size()]),
                Material.ENCHANTED_BOOK));

        /*
        ui.setItem(1,makeButton(ChatColor.YELLOW+ "Build", new String[]{ChatColor.RESET+"Gives permission to place",ChatColor.RESET+"and break blocks."}, Material.COBBLESTONE));
        ui.setItem(2,makeButton(ChatColor.YELLOW+"Harm Animals", new String[]{ChatColor.RESET+"Gives permission to hurt or kill",
                ChatColor.RESET+"pigs, sheep, cows, mooshrooms,",ChatColor.RESET+"chickens, horses, dogs and cats."}, Material.LEATHER));
        ui.setItem(3, makeButton(ChatColor.YELLOW+"Open Containers", new String[]{ChatColor.RESET+"Gives permission to use trap chests,",
                ChatColor.RESET+"chests, furnaces, anvils, hoppers,", ChatColor.RESET+"droppers, and dispensers."}, Material.CHEST));
        */

        final List<String> friendsText = messages.getStringList("manager.table.friends.description");
        final List<String> othersText = messages.getStringList("manager.table.others.description");

        final List<String> nextPageText = messages.getStringList("manager.pagination.next.description");
        final List<String> prevPageText = messages.getStringList("manager.pagination.previous.description");

        ui.setItem(9, makeButton(ChatColor.YELLOW + messages.getString("manager.table.others.title"),
                othersText.toArray(new String[othersText.size()]),
                new ItemStack(Material.SKULL_ITEM, 1, (short) 2)));

        ui.setItem(18, makeButton(ChatColor.YELLOW + messages.getString("manager.table.friends.title"),
                friendsText.toArray(new String[friendsText.size()]),
                new ItemStack(Material.SKULL_ITEM, 1, (short) 3)));

        if (pageNum < numPages - 1) {
            //35
            ui.setItem(35, makeButton(ChatColor.YELLOW + messages.getString("manager.pagination.next.title"),
                    nextPageText.toArray(new String[nextPageText.size()]),
                    new ItemStack(Material.PAPER)));
        }
        if (pageNum > 0) {
            //27
            ui.setItem(27, makeButton(ChatColor.YELLOW + messages.getString("manager.pagination.previous.title"),
                    prevPageText.toArray(new String[prevPageText.size()]),
                    new ItemStack(Material.PAPER)));
        }

    }

    private void setToggles() {

        int startIndex = pageNum * 8;
        int endIndex;
        ui.clear();
        if (pageNum == numPages - 1) {
            endIndex = permCols.size();
        } else {
            endIndex = startIndex + 8;
        }
        int slot = 1;
        for (int i = startIndex; i < endIndex; i++) {
            ui.setItem(slot, permCols.get(i)[0]);
            ui.setItem(slot + 9, permCols.get(i)[1]);
            ui.setItem(slot + 18, permCols.get(i)[2]);
            slot++;
        }


    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity p = event.getPlayer();
        FileConfiguration messages = plugin.getMessageConfig();
        //player.sendMessage(ChatColor.GREEN + "Closing: " + event.getInventory().getTitle() + " of type "+ event.getInventory().getType());
        //player.sendMessage(ChatColor.GREEN + "Viewer:" +
        //" " + event.getViewers().toString());
        //todo customizing the name of the land management UI creates possible a risk, player check addresses this
        if (event.getInventory().getTitle().contains(messages.getString("manager.title")) && p.getName().equalsIgnoreCase(player.getName()) && isOpen) {
            mLand.save();
            player.sendMessage(ChatColor.GREEN + messages.getString("manager.saved"));
            if (Landlord.getInstance().getConfig().getBoolean("options.soundEffects", true)) {
                player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 10, 10);
            }
            mLand.highlightLand(player, Effect.LAVADRIP);
            //InventoryCloseEvent.getHandlerList().unregister(this);
            Landlord.getInstance().getManageViewManager().NoCloseDeactivateView(player);
            HandlerList.unregisterAll(this);
        }
        //isOpen = true;

    }

    @EventHandler
    public void clickButton(InventoryClickEvent event) {
        FileConfiguration messages = plugin.getMessageConfig();
        if (event.getInventory().getTitle().contains(messages.getString("manager.title")) && event.getWhoClicked().getName().equalsIgnoreCase(player.getName())) {
            //player.sendMessage(ChatColor.GREEN+"CLICK!");
            //System.out.println(event.getSlot() +"");
            event.setCancelled(true);
            //System.out.println("ClickedSlot: "+event.getRawSlot());
            int slot = event.getRawSlot();

            HashMap<String, Landflag> pSlots = Landlord.getInstance().getFlagManager().getRegisteredFlags();

            //RowCount
            int row = slot / 9;
            //System.out.println("ROW: "+row);

            //ColCount
            int col = slot % 9;
            //System.out.println("COL: "+col);

            //System.out.println("PAGES: "+pageNum);
            int startIndex = pageNum * 8;
            int endIndex;
            if (pageNum == numPages - 1) {
                endIndex = permCols.size();
            } else {
                endIndex = startIndex + 8;
            }
            //System.out.println("EndIndex: "+endIndex);
            //System.out.println("StartIndex: "+startIndex);
            if ((col <= (endIndex - startIndex) && col > 0)) {

                if (row == 1) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1L, 1L);
                    System.out.println(event.getCurrentItem().getItemMeta().getDisplayName());
                    Landflag flag = plugin.getFlagManager().getFlag(event.getCurrentItem().getItemMeta().getDisplayName());
                    LandFlag flagy = mLand.getFlag(flag);
                    flagy.setCanEveryone(!flagy.canEveryone());
                    mLand.save();
                } else if (row == 2) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1L, 1L);
                    System.out.println(event.getCurrentItem().getItemMeta().getDisplayName());
                    Landflag flag = plugin.getFlagManager().getFlag(event.getCurrentItem().getItemMeta().getDisplayName());
                    LandFlag flagy = mLand.getFlag(flag);
                    flagy.setCanFriends(!flagy.canFriends());
                    mLand.save();
                }
                updateUIData();
                setToggles();
                buildUI();
            }

            if (pageNum < numPages - 1 && event.getRawSlot() == 35) {
                //35
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1L, 1L);
                pageNum++;
                updateUIData();
                setToggles();
                buildUI();
            }
            if (pageNum > 0 && event.getRawSlot() == 27) {
                //27
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1L, 1L);
                pageNum--;
                updateUIData();
                setToggles();
                buildUI();
            }

        }

    }

    public void closeView() {
        player.closeInventory();
    }


}
