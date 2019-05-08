package biz.princeps.landlord.manager.map;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardProxy;
import biz.princeps.landlord.util.SimpleScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

import static biz.princeps.landlord.util.MapConstants.*;

/**
 * File created by jcdesimp on 3/1/14. updated on 23/09/17 by SpatiumPrinceps
 */
public class LandMap {

    private IWorldGuardProxy wg;
    private long refreshRate;
    private String friendsSymbol, ownSymbol, foreignSymbol, header, yours, friends, others;

    private Player mapViewer;
    private SimpleScoreboard scoreboard;
    private Chunk currChunk;
    private String currDir;
    private boolean update;

    public LandMap(Player p, ILandLord plugin) {
        this.wg = plugin.getWGProxy();
        this.ownSymbol = plugin.getConfig().getString("CommandSettings.Map.symbols.yours");
        this.friendsSymbol = plugin.getConfig().getString("CommandSettings.Map.symbols.friends");
        this.foreignSymbol = plugin.getConfig().getString("CommandSettings.Map.symbols.others");
        this.refreshRate = plugin.getConfig().getLong("Map.refreshRate", 10);
        this.header = plugin.getLangManager().getRawString("Commands.LandMap.header");
        this.yours = plugin.getLangManager().getRawString("Commands.LandMap.yours");
        this.friends = plugin.getLangManager().getRawString("Commands.LandMap.friends");
        this.others = plugin.getLangManager().getRawString("Commands.LandMap.others");


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

    void removeMap() {
        scoreboard.deactivate();
    }

    /**
     * core method for actually displaying the Map
     *
     * @param p the player, who asked for a map
     * @return a reference to the scoreboard
     */
    private SimpleScoreboard displayMap(Player p) {
        scoreboard = new SimpleScoreboard(header, p);

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
        }, 0, refreshRate);

        return scoreboard;
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

        Map<Chunk, IOwnedLand> nearby = wg.getNearbyLands(p.getLocation(), radius, radius);

        for (int z = 0; z < mapBoard.length; z++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < mapBoard[z].length; x++) {

                int xx = x - radius;
                int zz = z - radius;

                IOwnedLand land = nearby.get(p.getWorld().getChunkAt(xx + p.getLocation().getChunk().getX(), zz + p.getLocation().getChunk().getZ()));

                String currSpot = mapBoard[z][x];

                if (land != null) {
                    if (land.getOwner().equals(p.getUniqueId())) {
                        currSpot = ChatColor.GREEN + currSpot;
                    } else if (land.isFriend(p.getUniqueId())) {
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
            mapRows[mapRows.length - 3] = ChatColor.GREEN + ownSymbol + "- " + yours;
        } else {
            mapRows[mapRows.length - 3] = ChatColor.GREEN + ownSymbol + "- " + yours.substring(0, 25);
        }

        if (friends.length() <= 25) {
            mapRows[mapRows.length - 2] = ChatColor.YELLOW + friendsSymbol + "- " + friends;
        } else {
            mapRows[mapRows.length - 2] = ChatColor.YELLOW + friendsSymbol + "- " + friends.substring(0, 25);
        }

        if (others.length() <= 25) {
            mapRows[mapRows.length - 1] = ChatColor.RED + foreignSymbol + "- " + others;
        } else {
            mapRows[mapRows.length - 1] = ChatColor.RED + foreignSymbol + "- " + others.substring(0, 25);
        }

        return mapRows;
    }

}
