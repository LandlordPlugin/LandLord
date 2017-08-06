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
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private LandMessageDisplay type;

    /**
     * such a mess, but I cant think of a less intrusive way
     */
    public LandAlerter() {
        playerInLand = new HashMap<>();
        type = LandMessageDisplay.valueOf(pl.getConfig().getString("LandMessage"));

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(pl, PacketType.Play.Server.CHAT) {
            private JSONParser parser = new JSONParser();

            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                packet.getChatTypes().getValues().forEach(System.out::println);
                //   System.out.println(packet.getChatTypes().getValues().size());
                if (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].equals("v1_11_R1")) {
                    if (packet.getBytes().getValues().get(0) != 1)
                        return;
                } else if (!packet.getChatTypes().getValues().contains(EnumWrappers.ChatType.SYSTEM))
                    return;

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
                if (json.get("extra") instanceof JSONArray) {
                    JSONArray array = ((JSONArray) json.get("extra"));
                    if (array != null) {

                        StringBuilder sb = new StringBuilder();
                        for (Object anArray : array) {
                            if (anArray instanceof JSONObject) {
                                sb.append(((JSONObject) anArray).get("text"));
                            }
                        }

                        String msg = sb.toString().trim();

                       // System.out.println(msg);
                        boolean goingOn = false;

                        if (regionInsideNow != null) {
                            String greet = ChatColor.stripColor(regionInsideNow.getLand().getFlag(DefaultFlag.GREET_MESSAGE)).replaceAll("&([a-f]|[0-7])", "").trim();
                            String farewell = ChatColor.stripColor(regionInsideNow.getLand().getFlag(DefaultFlag.FAREWELL_MESSAGE)).replaceAll("&([a-f]|[0-7])", "").trim();
                            //     System.out.println(msg + ":" + greet + ":" + farewell);

                            if (msg.equals(greet) || msg.equals(farewell)) {
                                goingOn = true;
                            }
                        }

                        if (before != null) {
                            String greet = ChatColor.stripColor(before.getLand().getFlag(DefaultFlag.GREET_MESSAGE)).replaceAll("&([a-f]|[0-7])", "").trim();
                            String farewell = ChatColor.stripColor(before.getLand().getFlag(DefaultFlag.FAREWELL_MESSAGE)).replaceAll("&([a-f]|[0-7])", "").trim();
                            //          System.out.println(msg + ":" + greet + ":" + farewell);

                            if (msg.equals(greet) || msg.equals(farewell)) {
                                goingOn = true;
                            }
                        }
                        //        System.out.println(goingOn);


                        //on leave: da wo man her kam
                        // on enter null
                        //  if (regionInsideNow == null)
                        //       System.out.println("1. null");
                        //   else
                        //      System.out.println(regionInsideNow.getLandName());

                        //on leave null
                        // on enter da wo man nun ist
                        if (goingOn) {

                            if (type == LandMessageDisplay.Disabled) {
                                event.setCancelled(true);
                                return;
                            }
                            // PacketContainer chat = event.getPacket();
                            // chat.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
                            // chat.getChatComponents().write(0, WrappedChatComponent.fromJson(json.toJSONString()));

                            if (type == LandMessageDisplay.Chat) {
                                if (before != null && regionInsideNow != null) {
                                    if (before.isOwner(p.getUniqueId()) && regionInsideNow.isOwner(p.getUniqueId())) {
                                        event.setCancelled(true);
                                    }
                                }
                            }

                            if (before == null) {
                                //          System.out.println("2. null");
                                if (type == LandMessageDisplay.ActionBar) {
                                    PrincepsLib.crossVersion().sendActionBar(p, craftColoredMessage(array));
                                    event.setCancelled(true);
                                }
                            } else {
                                //          System.out.println(before.getLandName());
                                if (regionInsideNow == null) {
                                    if (type == LandMessageDisplay.ActionBar) {
                                        PrincepsLib.crossVersion().sendActionBar(p, craftColoredMessage(array));
                                        event.setCancelled(true);
                                    }
                                } else {
                                    if (type == LandMessageDisplay.ActionBar) {
                                        boolean flag = true;
                                        for (UUID uuid : regionInsideNow.getLand().getOwners().getUniqueIds()) {
                                            if (!before.isOwner(uuid))
                                                flag = false;
                                        }
                                        if (!flag) {
                                            PrincepsLib.crossVersion().sendActionBar(p, craftColoredMessage(array));

                                        }
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }

            }
        });
    }

    private String craftColoredMessage(JSONArray array) {
        StringBuilder sb = new StringBuilder();
        for (Object anArray : array) {
            if (anArray instanceof JSONObject) {
                JSONObject obj = (JSONObject) anArray;
                if (obj.get("color") != null)
                    sb.append(ChatColor.valueOf(String.valueOf(obj.get("color")).toUpperCase()));
                sb.append(obj.get("text"));
            }
        }
        return sb.toString();
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

    enum LandMessageDisplay {
        ActionBar,
        Chat,
        Disabled;
    }

}
