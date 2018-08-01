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
import com.sk89q.worldguard.protection.flags.Flags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 24/7/17
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

                //   packet.getChatTypes().getValues().forEach(System.out::println);
                if (Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[1]) < 12) {
                    if (packet.getBytes().getValues().get(0) != 1)
                        return;
                } else if (!packet.getChatTypes().getValues().contains(EnumWrappers.ChatType.SYSTEM))
                    return;

                StructureModifier<WrappedChatComponent> components = packet.getChatComponents();
                Player p = event.getPlayer();

                OwnedLand regionInsideNow = pl.getWgHandler().getRegion(p.getLocation());
                OwnedLand before = playerInLand.get(p.getUniqueId());

                JSONObject json = null;
                try {
                    if (components.read(0) != null)
                        if (components.read(0).getJson() != null &&
                                parser.parse(components.read(0).getJson()) instanceof JSONObject)
                            json = (JSONObject) parser.parse(components.read(0).getJson());
                } catch (Exception ignored) {

                }

                if (json != null && json.get("extra") instanceof JSONArray) {
                    JSONArray array = ((JSONArray) json.get("extra"));
                    if (array != null) {
                        System.out.println(array);
                        StringBuilder sb = new StringBuilder();
                        for (Object anArray : array) {
                            if (anArray instanceof JSONObject) {
                                sb.append(((JSONObject) anArray).get("text"));
                            } else if (anArray instanceof String) {
                                sb.append(anArray);
                            }
                        }

                        String msg = sb.toString().trim();
                        // System.out.println("Trimmed message: " + msg);

                        boolean goingOn = false;

                        if (regionInsideNow != null) {
                            String greet = stripColors(regionInsideNow.getWGLand().getFlag(Flags.GREET_MESSAGE));
                            String farewell = stripColors(regionInsideNow.getWGLand().getFlag(Flags.FAREWELL_MESSAGE));
                            // System.out.println(msg + ":" + greet + ":" + farewell);

                            if (msg.equals(greet) || msg.equals(farewell)) {
                                goingOn = true;
                            }
                        }

                        if (before != null) {
                            String greet = stripColors(before.getWGLand().getFlag(Flags.GREET_MESSAGE));
                            String farewell = stripColors(before.getWGLand().getFlag(Flags.FAREWELL_MESSAGE));
                            // System.out.println(msg + ":" + greet + ":" + farewell);

                            if (msg.equals(greet) || msg.equals(farewell)) {
                                goingOn = true;
                            }
                        }
                        // System.out.println(goingOn);


                        // on leave: da wo man her kam
                        // on enter null
                        //  if (regionInsideNow == null)
                        //       System.out.println("1. null");
                        //   else
                        //      System.out.println(regionInsideNow.getName());

                        // on leave null
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
                                if (send(craftColoredMessage(array), p)) {
                                    event.setCancelled(true);
                                }
                            } else {
                                //          System.out.println(before.getName());
                                if (regionInsideNow == null) {
                                    if (send(craftColoredMessage(array), p)) {
                                        event.setCancelled(true);
                                    }
                                } else {
                                    boolean flag = true;
                                    for (UUID uuid : regionInsideNow.getWGLand().getOwners().getUniqueIds()) {
                                        if (!before.isOwner(uuid))
                                            flag = false;
                                    }
                                    if (!flag) {
                                        send(craftColoredMessage(array), p);
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

    private boolean send(String msg, Player p) {
        if (type == LandMessageDisplay.ActionBar) {
            PrincepsLib.getStuffManager().sendActionBar(p, msg);
            return true;
        } else if (type == LandMessageDisplay.Title) {
            p.sendTitle(msg, null, 10, 70, 10);
            return true;
        }
        return false;
    }

    private String stripColors(String input) {
        return ChatColor.stripColor(input).replaceAll("&([a-f]|[0-7]|[k-o]|[r])", "").trim();
    }

    private String craftColoredMessage(JSONArray array) {
        StringBuilder sb = new StringBuilder();
        for (Object anArray : array) {
            if (anArray instanceof JSONObject) {
                JSONObject obj = (JSONObject) anArray;
                if (obj.get("color") != null) {
                    sb.append(ChatColor.valueOf(String.valueOf(obj.get("color")).toUpperCase()));
                }
                sb.append(obj.get("text"));
            } else if (anArray instanceof String) {
                sb.append(anArray);
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
        } else if (!landTowards.equals(landFrom)) {
            playerInLand.put(p.getUniqueId(), landTowards);
            //     System.out.println(landTowards.getName() + " added");
        }
    }


    enum LandMessageDisplay {
        ActionBar,
        Chat,
        Title,
        Disabled
    }

}
