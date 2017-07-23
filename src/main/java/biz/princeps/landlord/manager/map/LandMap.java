package biz.princeps.landlord.manager.map;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.util.OwnedLand;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import me.tigerhix.lib.scoreboard.type.SimpleScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * File created by jcdesimp on 3/1/14. updated on 23/09/17 by SpatiumPrinceps
 */
public class LandMap {

    private Player mapViewer;
    private SimpleScoreboard scoreboard;
    private Chunk currChunk;
    private String currDir;
    private Landlord plugin;

    public LandMap(Player p, Landlord plugin) {
        this.plugin = plugin;
        this.mapViewer = p;
        this.currChunk = p.getLocation().getChunk();

        this.currDir = getPlayerDirection(mapViewer);
        this.displayMap(this.mapViewer);
    }

    public static String getPlayerDirection(Player playerSelf) {
        String dir;
        float y = playerSelf.getLocation().getYaw();
        if (y < 0) {
            y += 360;
        }
        y %= 360;
        int i = (int) ((y + 8) / 22.5);
        if (i == 0) {
            dir = "south";
        } else if (i == 1) {
            dir = "south southwest";
        } else if (i == 2) {
            dir = "southwest";
        } else if (i == 3) {
            dir = "west southwest";
        } else if (i == 4) {
            dir = "west";
        } else if (i == 5) {
            dir = "west northwest";
        } else if (i == 6) {
            dir = "northwest";
        } else if (i == 7) {
            dir = "north northwest";
        } else if (i == 8) {
            dir = "north";
        } else if (i == 9) {
            dir = "north northeast";
        } else if (i == 10) {
            dir = "northeast";
        } else if (i == 11) {
            dir = "east northeast";
        } else if (i == 12) {
            dir = "east";
        } else if (i == 13) {
            dir = "east southeast";
        } else if (i == 14) {
            dir = "southeast";
        } else if (i == 15) {
            dir = "south southeast";
        } else {
            dir = "south";
        }
        return dir;
    }

    public static String[][] getMapDir(String dir) {

        String[][] mapDir = new String[][]{
                {"▓", "▒", "▒", "∞", "▒", "▒", "▓"},
                {"▒", "▓", "▒", "∞", "▒", "▓", "▒"},
                {"▒", "▒", "▓", "∞", "▓", "▒", "▒"},
                {"▓", "▒", "▓", "█", "▒", "▓", "▒"},
                {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
        };

        switch (dir) {
            case "west":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"∞", "∞", "∞", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "west northwest":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"∞", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "∞", "∞", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "northwest":
                mapDir = new String[][]{
                        {"∞", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "∞", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "∞", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "north northwest":
                mapDir = new String[][]{
                        {"▓", "∞", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "∞", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "∞", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "north":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "∞", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "∞", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "∞", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "north northeast":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "∞", "▓"},
                        {"▒", "▓", "▒", "▓", "∞", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "∞", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "northeast":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "∞"},
                        {"▒", "▓", "▒", "▓", "▒", "∞", "▒"},
                        {"▒", "▒", "▓", "▒", "∞", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "east northeast":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "∞"},
                        {"▒", "▒", "▓", "▒", "∞", "∞", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "east":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "∞", "∞", "∞"},
                        {"▓", "▓", "▒", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "east southeast":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "∞", "∞", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "▒", "∞"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "southeast":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "∞", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "▓", "∞", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "∞"}
                };
                break;
            case "south southeast":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "▒", "∞", "▓", "▓"},
                        {"▓", "▒", "▓", "▓", "∞", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "∞", "▒"}
                };
                break;
            case "south":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "▒", "∞", "▒", "▓", "▓"},
                        {"▓", "▒", "▓", "∞", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "∞", "▓", "▓", "▒"}
                };
                break;
            case "south southwest":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "∞", "▒", "▒", "▓", "▓"},
                        {"▓", "▒", "∞", "▓", "▓", "▒", "▓"},
                        {"▒", "∞", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "southwest":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "▓", "∞", "▒", "▒", "▓", "▓"},
                        {"▓", "∞", "▓", "▓", "▓", "▒", "▓"},
                        {"∞", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
            case "west southwest":
                mapDir = new String[][]{
                        {"▓", "▒", "▒", "▒", "▒", "▒", "▓"},
                        {"▒", "▓", "▒", "▓", "▒", "▓", "▒"},
                        {"▒", "▒", "▓", "▒", "▓", "▒", "▒"},
                        {"▓", "▒", "▓", "\u2062", "▒", "▓", "▒"},
                        {"▓", "∞", "∞", "▒", "▒", "▓", "▓"},
                        {"∞", "▒", "▓", "▓", "▓", "▒", "▓"},
                        {"▒", "▓", "▓", "▓", "▓", "▓", "▒"}
                };
                break;
        }
        return mapDir;
    }

    public Player getMapViewer() {
        return mapViewer;
    }

    public void removeMap() {
        scoreboard.deactivate();
    }

    private SimpleScoreboard displayMap(Player p) {
        Scoreboard board = ScoreboardLib.createScoreboard(p).setHandler(new ScoreboardHandler() {
            LangManager messages = plugin.getLangManager();

            @Override
            public String getTitle(Player p) {
                return messages.getRawString("Commands.LandMap.header");
            }

            @Override
            public List<Entry> getEntries(Player player) {
                if (!currDir.equals(getPlayerDirection(mapViewer)) || !currChunk.equals(mapViewer.getLocation().getChunk())) {
                    currDir = getPlayerDirection(mapViewer);
                }
                EntryBuilder eb = new EntryBuilder();
                String[] mapData = buildMap(p);
                for (int i = 0; i < mapData.length; i++) {
                    // Not sure what this part does. It works without lol
                    /*if (mapData[i].length() < 21) {
                        for (int f = 0; f < (21 - mapData[i].length()); f++) {
                            mapData[i] += ChatColor.RESET;
                        }
                    }*/
                    eb.next(mapData[i]);
                }
                return eb.build();
            }
        }).setUpdateInterval(plugin.getConfig().getLong("Map.LandMapRefreshRate", 10));

        SimpleScoreboard simpleScoreboard = (SimpleScoreboard) board;
        simpleScoreboard.activate();
        this.scoreboard = simpleScoreboard;
        return simpleScoreboard;
    }

    public void updateMap() {
        currChunk = mapViewer.getLocation().getChunk();
        currDir = "";
    }

    private String[] buildMap(Player p) {
        final int radius = 3;

        String[][] mapBoard = getMapDir(getPlayerDirection(p));

        String[] mapRows = new String[mapBoard.length + 3];

        if (!currChunk.equals(mapViewer.getLocation().getChunk())) {
            updateMap();
        }

        Map<Chunk, OwnedLand> nearby = plugin.getWgHandler().getNearbyLands(p.getLocation(), radius, radius);

        for (int z = 0; z < mapBoard.length; z++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < mapBoard[z].length; x++) {

                int xx = x - radius;
                int zz = z - radius;

                OwnedLand land = nearby.get(p.getWorld().getChunkAt(xx + p.getLocation().getChunk().getX(), zz + p.getLocation().getChunk().getZ()));

                String currSpot = mapBoard[z][x];

                if (land != null) {
                    if (land.getLand().getOwners().getUniqueIds().contains(p.getUniqueId())) {
                        currSpot = ChatColor.GREEN + currSpot;
                    } else if (land.getLand().getMembers().getUniqueIds().contains(p.getUniqueId())) {
                        currSpot = ChatColor.YELLOW + currSpot;
                    } else {
                        currSpot = ChatColor.RED + currSpot;
                    }
                } else {
                    if (currSpot.equals("∞") || currSpot.equals("\u2062")) {
                        currSpot = ChatColor.RESET + currSpot;
                    } else {
                        currSpot = ChatColor.GRAY + currSpot;
                    }
                }
                row.append(currSpot);

            }
            mapRows[z] = row.toString();
        }

        LangManager messages = plugin.getLangManager();

        final String yours = messages.getRawString("Commands.LandMap.yours");
        final String friends = messages.getRawString("Commands.LandMap.friends");
        final String others = messages.getRawString("Commands.LandMap.others");

        if (yours.length() <= 25) {
            mapRows[mapRows.length - 3] = ChatColor.GREEN + "█- " + yours;
        } else {
            mapRows[mapRows.length - 3] = ChatColor.GREEN + "█- " + yours.substring(0, 25);
        }

        if (friends.length() <= 25) {
            mapRows[mapRows.length - 2] = ChatColor.YELLOW + "█- " + friends;
        } else {
            mapRows[mapRows.length - 2] = ChatColor.YELLOW + "█- " + friends.substring(0, 25);
        }

        if (others.length() <= 25) {
            mapRows[mapRows.length - 1] = ChatColor.RED + "█- " + others;
        } else {
            mapRows[mapRows.length - 1] = ChatColor.RED + "█- " + others.substring(0, 25);
        }

        return mapRows;
    }

}
