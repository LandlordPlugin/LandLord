package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.lib.PrincepsLib;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 24/7/17
 */
public class LandAlerter extends BasicListener {

    private ILandLord pl;
    private IWorldGuardManager wg;
    private HashMap<UUID, ChunkCoords> currentLands;
    private HashMap<UUID, ChunkCoords> previousLands;
    // We need to update the player position separately bc spigot or worldguard sends the greeting message before actually
    // transferring the player to the teleported location
    private LandMessageDisplay type;

    /**
     * such a mess, but I cant think of a less intrusive way.
     * To be honest I never fully understand why and how this worked. If there ever is a problem with this piece of code
     * message me somehow so that I can figure it out. This is a very weird combination of async chat events and moveevent.
     * I feel like there are a hundred things that can go wrong here.
     */
    public LandAlerter(ILandLord pl) {
        super(pl);
        this.pl = pl;
        this.wg = pl.getWGManager();
        currentLands = new HashMap<>();
        previousLands = new HashMap<>();

        type = LandMessageDisplay.valueOf(pl.getConfig().getString("LandMessage"));

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(pl.getPlugin(), PacketType.Play.Server.CHAT) {
            private JSONParser parser = new JSONParser();

            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                if (Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[1]) < 12) {
                    if (packet.getBytes().getValues().get(0) != 1)
                        return;
                } else if (!packet.getChatTypes().getValues().contains(EnumWrappers.ChatType.SYSTEM))
                    return;

                StructureModifier<WrappedChatComponent> components = packet.getChatComponents();
                // System.out.println(packet);
                Player p = event.getPlayer();

                Location ploc = p.getLocation();
                JSONObject json = null;
                try {
                    if (components.read(0) != null)
                        if (components.read(0).getJson() != null &&
                                parser.parse(components.read(0).getJson()) instanceof JSONObject)
                            json = (JSONObject) parser.parse(components.read(0).getJson());
                } catch (Exception | Error ignored) {
                }

                if (json != null && json.get("extra") instanceof JSONArray) {
                    JSONArray array = ((JSONArray) json.get("extra"));
                    if (array != null) {
                        StringBuilder sb = new StringBuilder();
                        for (Object anArray : array) {
                            if (anArray instanceof JSONObject) {
                                sb.append(((JSONObject) anArray).get("text"));
                            } else if (anArray instanceof String) {
                                sb.append(anArray);
                            }
                        }

                        String msg = stripColors(sb.toString()).trim();
                        // System.out.println("Trimmed message: " + msg);

                        // Problem is, that the event somehow trigger's before the player actually crossed the border
                        // Thats why we get a nullland as regionInside now!
                        // So i decided to just check if the message is equals to one of the 4 surrounding lands, since
                        // vector calculations are not very accurate
                        IOwnedLand regionInsideNow = (ploc == null ? null : wg.getRegion(ploc));
                        // System.out.println("Position: " + ploc);
                        // System.out.println("Chunk: " + ploc.getChunk());
                        // System.out.println("RegionInsideNw: " + regionInsideNow);
                        IOwnedLand before = (previousLands.get(p.getUniqueId()) == null ? null :
                                wg.getRegion(previousLands.get(p.getUniqueId()).getLocation()));

                        IOwnedLand[] surroundings = wg.getSurroundings(ploc);

                        boolean goingOn = false;

                        // check surrounding lands for equal greet message
                        for (IOwnedLand surrounding : surroundings) {
                            // System.out.println("Surrounding: " + surrounding);
                            if (surrounding == null) {
                                continue;
                            }
                            String greet = stripColors(surrounding.getGreetMessage());
                            if (msg.equals(greet)) {
                                goingOn = true;
                                break;
                            }

                        }

                        if (regionInsideNow != null) {
                            String greet = stripColors(regionInsideNow.getGreetMessage());
                            String farewell = stripColors(regionInsideNow.getFarewellMessage());
                            // System.out.println("RegionInsideNow: " + msg + ":" + greet + ":" + farewell);

                            if (msg.equals(greet) || msg.equals(farewell)) {
                                goingOn = true;
                            }
                        }

                        if (before != null) {
                            String greet = stripColors(before.getGreetMessage());
                            String farewell = stripColors(before.getFarewellMessage());
                            // System.out.println("before:" + msg + ":" + greet + ":" + farewell);

                            if (msg.equals(greet) || msg.equals(farewell)) {
                                goingOn = true;
                            }
                        }
                        // System.out.println(goingOn);

                        if (goingOn) {
                            event.setCancelled(true);
                        }
                    }
                }

            }
        });
    }

    private boolean send(String msg, Player p) {
        switch (type) {
            case ActionBar:
                PrincepsLib.getStuffManager().sendActionBar(p, msg);
                return true;
            case Chat:
                pl.getLangManager().sendMessage(p, msg);
                return true;
            case Title:
                p.sendTitle(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', msg), null, 10, 70, 20);
                return true;
            case Disabled:
            default:
                return false;
        }
    }

    private String stripColors(String input) {
        if (input == null) return "";
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

        ChunkCoords landFrom = new ChunkCoords(comingFrom);
        ChunkCoords landTowards = new ChunkCoords(headingTowards);

        ChunkCoords currentLand = this.currentLands.get(p.getUniqueId());
        ChunkCoords prevLand = this.previousLands.get(p.getUniqueId());

        if (currentLand == null) {
            this.currentLands.put(p.getUniqueId(), landTowards);
            currentLand = landTowards;
        }
        if (prevLand == null) {
            this.previousLands.put(p.getUniqueId(), landFrom);
            prevLand = landFrom;
        }
        if (!landFrom.equals(landTowards)) {
            // Unequals, so they changed
            if (!currentLand.equals(landTowards)) {
                this.previousLands.replace(p.getUniqueId(), currentLand);
                this.currentLands.replace(p.getUniqueId(), landTowards);
                prevLand = currentLand;
                currentLand = landTowards;

                IOwnedLand prev = wg.getRegion(prevLand.getLocation());
                IOwnedLand curr = wg.getRegion(currentLand.getLocation());
                // System.out.println(prev + "  " + curr);

                if (prev == null && curr != null) {
                    send(curr.getGreetMessage(), p);
                }
                if (prev != null && curr == null) {
                    send(prev.getFarewellMessage(), p);
                }
                if (prev != null && curr != null) {
                    if (!prev.getOwner().equals(curr.getOwner())) {
                        send(curr.getGreetMessage(), p);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        this.previousLands.replace(p.getUniqueId(), new ChunkCoords(e.getFrom()));
        this.currentLands.replace(p.getUniqueId(), new ChunkCoords(e.getTo()));

        IOwnedLand toLand = wg.getRegion(e.getTo());
        if (toLand != null) {
            send(toLand.getGreetMessage(), p);
        }
    }

    enum LandMessageDisplay {
        ActionBar,
        Chat,
        Title,
        Disabled
    }

    static class ChunkCoords {
        int x;
        int z;
        World world;

        public ChunkCoords(World world, int x, int z) {
            this.x = x;
            this.z = z;
            this.world = world;
        }

        public ChunkCoords(Location loc) {
            this.x = loc.getChunk().getX();
            this.z = loc.getChunk().getZ();
            this.world = loc.getWorld();
        }

        public Location getLocation() {
            return new Location(world, x * 16, 0, z * 16);
        }

        @Override
        public String toString() {
            return "ChunkCoords{" +
                    "x=" + x +
                    ", z=" + z +
                    ", world=" + world.getName() +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChunkCoords that = (ChunkCoords) o;
            return x == that.x &&
                    z == that.z &&
                    Objects.equals(world, that.world);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z, world);
        }
    }

}
