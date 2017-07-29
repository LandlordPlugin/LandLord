package biz.princeps.landlord.listener;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.lib.PrincepsLib;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by spatium on 24.07.17.
 */
public class LandAlerter extends BasicListener {

    private Landlord pl = Landlord.getInstance();
    private HashMap<UUID, OwnedLand> playerInLand;

    /**
     * such a mess, but I cant think of a less intrusive way
     */
    public LandAlerter() {
        playerInLand = new HashMap<>();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(pl, PacketType.Play.Server.CHAT) {
            private JSONParser parser = new JSONParser();

            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                StructureModifier<WrappedChatComponent> componets = packet.getChatComponents();
                Player p = event.getPlayer();

                OwnedLand regionInsideNow = pl.getWgHandler().getRegion(p.getLocation());
                OwnedLand before = playerInLand.get(p.getUniqueId());

                JSONObject json = null;
                try {
                    if (componets.read(0) != null)
                        if (parser.parse(componets.read(0).getJson()) instanceof JSONObject)
                            json = (JSONObject) parser.parse(componets.read(0).getJson());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (json != null && json.get("extra") instanceof JSONArray) {
                    JSONArray array = ((JSONArray) json.get("extra"));
                    if (array != null && array.get(0) instanceof JSONObject) {
                        JSONObject obj = (JSONObject) array.get(0);
                        if (obj != null && obj.get("text") instanceof String) {
                            String msg = (String) obj.get("text");

                            boolean goingOn = false;
                            if (regionInsideNow != null) {
                                if (msg.equals(ChatColor.stripColor(regionInsideNow.getLand().getFlag(DefaultFlag.GREET_MESSAGE))) ||
                                        msg.equals(ChatColor.stripColor(regionInsideNow.getLand().getFlag(DefaultFlag.FAREWELL_MESSAGE)))) {
                                    //         System.out.println(msg + " |" + ChatColor.stripColor(regionInsideNow.getLand().getFlag(DefaultFlag.GREET_MESSAGE)) + "|" + ChatColor.stripColor(regionInsideNow.getLand().getFlag(DefaultFlag.FAREWELL_MESSAGE)));
                                    goingOn = true;
                                }
                            }

                            if (before != null) {
                                if (msg.equals(ChatColor.stripColor(before.getLand().getFlag(DefaultFlag.GREET_MESSAGE))) ||
                                        msg.equals(ChatColor.stripColor(before.getLand().getFlag(DefaultFlag.FAREWELL_MESSAGE)))) {
                                    //         System.out.println(msg + " |" + ChatColor.stripColor(before.getLand().getFlag(DefaultFlag.GREET_MESSAGE)) + "|" + ChatColor.stripColor(before.getLand().getFlag(DefaultFlag.FAREWELL_MESSAGE)));
                                    goingOn = true;
                                }
                            }


                            //on leave: da wo man her kam
                            // on enter null
                            //  if (regionInsideNow == null)
                            //       System.out.println("1. null");
                            //   else
                            //      System.out.println(regionInsideNow.getLandName());

                            //on leave null
                            // on enter da wo man nun ist
                            if (goingOn)
                                if (before == null) {
                                    //          System.out.println("2. null");
                                    PrincepsLib.crossVersion().sendActionBar(p, msg);
                                    event.setCancelled(true);
                                } else {
                                    //          System.out.println(before.getLandName());
                                    if (regionInsideNow == null) {
                                        PrincepsLib.crossVersion().sendActionBar(p, msg);
                                        event.setCancelled(true);
                                    } else {
                                        boolean flag = true;
                                        for (UUID uuid : regionInsideNow.getLand().getOwners().getUniqueIds()) {
                                            if (!before.isOwner(uuid))
                                                flag = false;
                                        }
                                        if (!flag) PrincepsLib.crossVersion().sendActionBar(p, msg);
                                        event.setCancelled(true);
                                    }
                                }
                        }
                    }
                }
            }
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();
        Location comingFrom = e.getFrom();
        Location headingTowards = e.getTo();

        OwnedLand landFrom = pl.getWgHandler().getRegion(comingFrom);
        OwnedLand landTowards = pl.getWgHandler().getRegion(headingTowards);


        if (landTowards == null) {
            // System.out.println(playerInLand.get(p.getUniqueId()) + " removed");
            playerInLand.remove(p.getUniqueId());
        }

        if (landTowards != null)
            if (!landTowards.equals(landFrom)) {
                playerInLand.put(p.getUniqueId(), landTowards);
                //     System.out.println(landTowards.getLandName() + " added");
            }

    }

}
