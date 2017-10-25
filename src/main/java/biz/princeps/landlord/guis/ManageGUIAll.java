package biz.princeps.landlord.guis;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.gui.ConfirmationGUI;
import biz.princeps.lib.gui.MultiPagedGUI;
import biz.princeps.lib.gui.simple.AbstractGUI;
import biz.princeps.lib.gui.simple.Icon;
import biz.princeps.lib.storage.requests.Conditions;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by spatium on 24.07.17.
 */
public class ManageGUIAll extends ManageGUI {

    protected ManageGUIAll(Player player, String header) {
        super(player, header);
    }
}