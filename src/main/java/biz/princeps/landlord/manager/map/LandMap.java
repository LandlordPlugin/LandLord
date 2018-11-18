package biz.princeps.landlord.manager.map;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.manager.LangManager;
import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.landlord.util.SimpleScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * File created by jcdesimp on 3/1/14. updated on 23/09/17 by SpatiumPrinceps
 */
public class LandMap {

    private static final String b1 = Landlord.getInstance().getConfig().getString("CommandSettings.Map.symbols.background1");
    private static final String b2 = Landlord.getInstance().getConfig().getString("CommandSettings.Map.symbols.background2");
    private static final String ar = Landlord.getInstance().getConfig().getString("CommandSettings.Map.symbols.arrow");
    private static final String mi = Landlord.getInstance().getConfig().getString("CommandSettings.Map.symbols.middle");
    private static final String yours = Landlord.getInstance().getLangManager().getRawString("Commands.LandMap.yours");
    private static final String friends = Landlord.getInstance().getLangManager().getRawString("Commands.LandMap.friends");
    private static final String others = Landlord.getInstance().getLangManager().getRawString("Commands.LandMap.others");

    private static final String[][] s = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, ar, b2, b1, b1},
            {b1, b2, b1, ar, b1, b2, b1},
            {b2, b1, b1, ar, b1, b1, b2}
    };

    private static final String[][] ssw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, ar, b2, b2, b1, b1},
            {b1, b2, ar, b1, b1, b2, b1},
            {b2, ar, b1, b1, b1, b1, b2}
    };
    private static final String[][] sw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, ar, b2, b2, b1, b1},
            {b1, ar, b1, b1, b1, b2, b1},
            {ar, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] wsw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, ar, ar, b2, b2, b1, b1},
            {ar, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] w = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {ar, ar, ar, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] wnw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {ar, b1, b2, b1, b2, b1, b2},
            {b2, ar, ar, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] nw = new String[][]{
            {ar, b2, b2, b2, b2, b2, b1},
            {b2, ar, b2, b1, b2, b1, b2},
            {b2, b2, ar, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] nnw = new String[][]{
            {b1, ar, b2, b2, b2, b2, b1},
            {b2, b1, ar, b1, b2, b1, b2},
            {b2, b2, ar, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] n = new String[][]{
            {b1, b2, b2, ar, b2, b2, b1},
            {b2, b1, b2, ar, b2, b1, b2},
            {b2, b2, b1, ar, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] nne = new String[][]{
            {b1, b2, b2, b2, b2, ar, b1},
            {b2, b1, b2, b1, ar, b1, b2},
            {b2, b2, b1, b2, ar, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] ne = new String[][]{
            {b1, b2, b2, b2, b2, b2, ar},
            {b2, b1, b2, b1, b2, ar, b2},
            {b2, b2, b1, b2, ar, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] ene = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, ar},
            {b2, b2, b1, b2, ar, ar, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] e = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, ar, ar, ar},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] ese = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, ar, ar, b1},
            {b1, b2, b1, b1, b1, b2, ar},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    private static final String[][] se = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, ar, b1, b1},
            {b1, b2, b1, b1, b1, ar, b1},
            {b2, b1, b1, b1, b1, b1, ar}
    };
    private static final String[][] sse = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, ar, b1, b1},
            {b1, b2, b1, b1, ar, b2, b1},
            {b2, b1, b1, b1, b1, ar, b2}
    };

    private Player mapViewer;
    private SimpleScoreboard scoreboard;
    private Chunk currChunk;
    private String currDir;
    //private String currDir;
    private Landlord plugin;
    private boolean update;

    public LandMap(Player p, Landlord plugin) {
        this.plugin = plugin;
        this.mapViewer = p;
        this.currChunk = p.getLocation().getChunk();
        this.currDir = getPlayerDirection(p);
        this.displayMap(this.mapViewer);
    }

    private static String getPlayerDirection(Player playerSelf) {
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

    private static String[][] getMapDir(Player p) {
        float y = p.getLocation().getYaw();
        if (y < 0) {
            y += 360;
        }
        y %= 360;
        int i = (int) ((y + 8) / 22.5);
        if (i == 0) {
            return s;
        } else if (i == 1) {
            return ssw;
        } else if (i == 2) {
            return sw;
        } else if (i == 3) {
            return wsw;
        } else if (i == 4) {
            return w;
        } else if (i == 5) {
            return wnw;
        } else if (i == 6) {
            return nw;
        } else if (i == 7) {
            return nnw;
        } else if (i == 8) {
            return n;
        } else if (i == 9) {
            return nne;
        } else if (i == 10) {
            return ne;
        } else if (i == 11) {
            return ene;
        } else if (i == 12) {
            return e;
        } else if (i == 13) {
            return ese;
        } else if (i == 14) {
            return se;
        } else if (i == 15) {
            return sse;
        } else {
            return s;
        }
    }

    public Player getMapViewer() {
        return mapViewer;
    }

    public void removeMap() {
        scoreboard.deactivate();
    }

    /**
     * core method for actually displaying the Map
     *
     * @param p the player, who asked for a map
     * @return a reference to the scoreboard
     */
    private SimpleScoreboard displayMap(Player p) {
        LangManager messages = plugin.getLangManager();

        scoreboard = new SimpleScoreboard(messages.getRawString("Commands.LandMap.header"), p);

        scoreboard.scheduleUpdate(new Runnable() {
            List<String> prev;

            @Override
            public void run() {
                if (!update) {
                    if (prev != null && currChunk.equals(mapViewer.getLocation().getChunk()) && currDir.equals(getPlayerDirection(mapViewer))) {
                        return;
                    }
                }
                update = false;
                LandMap.this.updateMap();
                scoreboard.reset();
                String[] mapData = LandMap.this.buildMap(p);
                for (String aMapData : mapData) {
                    scoreboard.add(aMapData);
                }
                scoreboard.send();
            }
        }, 0, plugin.getConfig().getLong("Map.refreshRate", 10));

        return scoreboard;

        /*
        Scoreboard board = ScoreboardLib.createScoreboard(p).setHandler(new ScoreboardHandler() {
            LangManager messages = plugin.getLangManager();
            List<Entry> prev;

            @Override
            public String getTitle(Player p) {
                return messages.getRawString("Commands.LandMap.header");
            }

            @Override
            public List<Entry> getEntries(Player player) {
                if (!update) {
                    if (prev != null && currChunk.equals(mapViewer.getLocation().getChunk()) && currDir.equals(getPlayerDirection(mapViewer))) {
                        return prev;
                    }
                }
                update = false;
                updateMap();
                String[] mapData = buildMap(p);
                EntryBuilder eb = new EntryBuilder();
                for (String aMapData : mapData) {
                    // Not sure what this part does. It works without lol
                    /*if (mapData[i].length() < 21) {
                        for (int f = 0; f < (21 - mapData[i].length()); f++) {
                            mapData[i] += ChatColor.RESET;
                        }
                    }
                    eb.next(aMapData);
                }
                prev = eb.build();
                return prev;
            }
        }).setUpdateInterval(plugin.getConfig().getLong("Map.refreshRate", 10));

        SimpleScoreboard simpleScoreboard = (SimpleScoreboard) board;
        simpleScoreboard.activate();
        this.scoreboard = simpleScoreboard;
        return simpleScoreboard;
        */
    }

    private void updateMap() {
        this.currChunk = mapViewer.getLocation().getChunk();
        this.currDir = getPlayerDirection(mapViewer);
    }

    void forceUpdate() {
        this.update = true;
    }

    private String[] buildMap(Player p) {
        final int radius = 3;

        String[][] mapBoard = getMapDir(p);
        String[] mapRows = new String[mapBoard.length + 3];

        Map<Chunk, OwnedLand> nearby = plugin.getWgHandler().getNearbyLands(p.getLocation(), radius, radius);

        for (int z = 0; z < mapBoard.length; z++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < mapBoard[z].length; x++) {

                int xx = x - radius;
                int zz = z - radius;

                OwnedLand land = nearby.get(p.getWorld().getChunkAt(xx + p.getLocation().getChunk().getX(), zz + p.getLocation().getChunk().getZ()));

                String currSpot = mapBoard[z][x];

                if (land != null) {
                    if (land.getOwner().equals(p.getUniqueId())) {
                        currSpot = ChatColor.GREEN + currSpot;
                    } else if (land.getMembers().contains(p.getUniqueId())) {
                        currSpot = ChatColor.YELLOW + currSpot;
                    } else {
                        currSpot = ChatColor.RED + currSpot;
                    }
                } else {
                    if (currSpot.equals(ar) || currSpot.equals(mi)) {
                        currSpot = ChatColor.RESET + currSpot;
                    } else {
                        currSpot = ChatColor.GRAY + currSpot;
                    }
                }
                row.append(currSpot);

            }
            mapRows[z] = row.toString();
        }

        if (yours.length() <= 25) {
            mapRows[mapRows.length - 3] = ChatColor.GREEN + "" + plugin.getConfig().get("CommandSettings.Map.symbols.yours") + "- " + yours;
        } else {
            mapRows[mapRows.length - 3] = ChatColor.GREEN + "" + plugin.getConfig().get("CommandSettings.Map.symbols.yours") + "- " + yours.substring(0, 25);
        }

        if (friends.length() <= 25) {
            mapRows[mapRows.length - 2] = ChatColor.YELLOW + "" + plugin.getConfig().get("CommandSettings.Map.symbols.friends") + "- " + friends;
        } else {
            mapRows[mapRows.length - 2] = ChatColor.YELLOW + "" + plugin.getConfig().get("CommandSettings.Map.symbols.friends") + "- " + friends.substring(0, 25);
        }

        if (others.length() <= 25) {
            mapRows[mapRows.length - 1] = ChatColor.RED + "" + plugin.getConfig().get("CommandSettings.Map.symbols.others") + "- " + others;
        } else {
            mapRows[mapRows.length - 1] = ChatColor.RED + "" + plugin.getConfig().get("CommandSettings.Map.symbols.others") + "- " + others.substring(0, 25);
        }

        return mapRows;
    }

}
