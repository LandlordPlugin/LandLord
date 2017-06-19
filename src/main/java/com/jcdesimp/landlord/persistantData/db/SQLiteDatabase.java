package com.jcdesimp.landlord.persistantData.db;

import biz.princeps.lib.storage.SQLite;
import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.LandManager;
import com.jcdesimp.landlord.persistantData.Data;
import com.jcdesimp.landlord.persistantData.Friend;
import com.jcdesimp.landlord.persistantData.LandFlag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by spatium on 12.06.17.
 */
public class SQLiteDatabase extends SQLite {

    public SQLiteDatabase(String dbpath) {
        super(dbpath);
        pool = Executors.newFixedThreadPool(1);
    }

    @Override
    public void setupDatabase() {
        String query1 = "CREATE TABLE IF NOT EXISTS ll_flagperm (landid INTEGER, identifier VARCHAR(20), canEveryone BOOLEAN, canFriends BOOLEAN, id INTEGER, PRIMARY KEY (id))";
        this.execute(query1);
        String query3 = "CREATE TABLE IF NOT EXISTS ll_friend (landid INTEGER, frienduuid VARCHAR(36), id INTEGER, PRIMARY KEY (landid))";
        this.execute(query3);
        String query5 = "CREATE TABLE IF NOT EXISTS ll_land (landid INTEGER, owneruuid VARCHAR(36), x INTEGER, z INTEGER, world VARCHAR(16), PRIMARY KEY(landid))";
        this.execute(query5);
        Landlord.getInstance().getLogger().info("Connected to SQLite and setted up tables!");
    }

    public OwnedLand getLand(Data data) {
        try {
            return pool.submit(() -> {
                OwnedLand land = null;
                String query = "SELECT * FROM ll_land WHERE world = ? AND x = ? AND z = ?";
                try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {
                    st.setString(1, data.getWorld());
                    st.setInt(2, data.getX());
                    st.setInt(3, data.getZ());

                    ResultSet res = st.executeQuery();
                    while (res.next()) {
                        land = new OwnedLand(data);
                        land.setOwner(UUID.fromString(res.getString("owneruuid")));
                        land.setLandId(res.getInt("landid"));
                        land.setFriends(getFriends(land.getLandId()));
                        land.setFlags(stringToFlags(res.getString("flags")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return land;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    private List<LandFlag> stringToFlags(String flags) {
        String[] splitted = flags.split("_");
        List<LandFlag> list = new ArrayList<>();
        for (String flag : splitted) {
            String[] params = flag.split(":");
            LandFlag flagy = new LandFlag(Integer.parseInt(params[0]), params[1], Boolean.parseBoolean(params[2]), Boolean.parseBoolean(params[3]));
            list.add(flagy);
        }
        return list;
    }

    private String flagsToString(List<LandFlag> list) {
        StringBuilder sb = new StringBuilder(list.get(0).toString());
        for (int i = 1; i < list.size(); i++) {
            sb.append("_");
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    protected List<Friend> getFriends(int id) {
        ArrayList<Friend> list = new ArrayList<>();
        String query = "SELECT * FROM ll_friend WHERE landid = ?";
        try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);

            ResultSet res = st.executeQuery();
            while (res.next()) {
                Friend f = new Friend(UUID.fromString(res.getString("frienduuid")));
                f.setId(res.getInt("id"));
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void removeFriend(UUID f) {
        pool.submit(() -> {
            String query = "DELETE FROM ll_friend WHERE frienduuid = ?";
            try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {
                st.setString(1, f.toString());
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeLand(int landid) {
        pool.submit(() -> {
            String query = "DELETE FROM ll_land WHERE landid = ?";
            String query3 = "DELETE FROM ll_friend WHERE landid = ?";

            try (Connection con = getSQLConnection();
                 PreparedStatement st = con.prepareStatement(query);
                 PreparedStatement st3 = con.prepareStatement(query3)) {
                st.setInt(1, landid);
                st.executeUpdate();
                st3.setInt(1, landid);
                st3.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void save(OwnedLand land) {
        String query2 = "INSERT OR REPLACE INTO ll_friend (landid, frienduuid, id) VALUES (?,?,?)";
        String query3 = "INSERT OR REPLACE INTO ll_land (landid, owneruuid, x, z, world, flags) VALUES (?,?,?,?,?,?)";
        pool.submit(() -> {
            try (Connection con = getSQLConnection();
                 PreparedStatement st2 = con.prepareStatement(query2);
                 PreparedStatement st3 = con.prepareStatement(query3)) {

                for (Friend f : land.getFriends()) {
                    st2.setInt(1, land.getLandId());
                    st2.setString(2, f.getUuid().toString());
                    st2.setInt(3, f.getId());
                    synchronized (this) {
                        st2.execute();
                    }
                }

                st3.setInt(1, land.getLandId());
                st3.setString(2, land.getOwner().toString());
                st3.setInt(3, land.getData().getX());
                st3.setInt(4, land.getData().getZ());
                st3.setString(5, land.getData().getWorld());
                st3.setString(6, flagsToString(land.getFlags()));
                synchronized (this) {
                    st3.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public List<OwnedLand> getLands(UUID owner) {
        try {
            return pool.submit(() -> {
                ArrayList<OwnedLand> list = new ArrayList<>();
                String query = "SELECT * FROM ll_land WHERE owneruuid = ?";
                try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {
                    st.setString(1, owner.toString());

                    ResultSet res = st.executeQuery();
                    while (res.next()) {
                        Data data = new Data(res.getString("world"), res.getInt("x"), res.getInt("z"));
                        OwnedLand ownedLand = new OwnedLand(data);
                        ownedLand.setOwner(owner);
                        ownedLand.setLandId(res.getInt("landid"));
                        ownedLand.setFriends(getFriends(ownedLand.getLandId()));
                        ownedLand.setFlags(stringToFlags(res.getString("flags")));
                        list.add(ownedLand);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return list;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public List<OwnedLand> getLands(UUID owner, String world) {
        try {
            return pool.submit(() -> {
                ArrayList<OwnedLand> list = new ArrayList<>();
                String query = "SELECT * FROM ll_land WHERE owneruuid = ? AND world = ?";
                try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {
                    st.setString(1, owner.toString());
                    st.setString(2, world);
                    ResultSet res = st.executeQuery();
                    while (res.next()) {
                        Data data = new Data(world, res.getInt("x"), res.getInt("z"));
                        OwnedLand ownedLand = new OwnedLand(data);
                        ownedLand.setOwner(owner);
                        ownedLand.setLandId(res.getInt("landid"));
                        ownedLand.setFriends(getFriends(ownedLand.getLandId()));
                        ownedLand.setFlags(stringToFlags(res.getString("flags")));
                        list.add(ownedLand);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return list;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public List<OwnedLand> getLands(String world) {
        try {
            return pool.submit(() -> {
                ArrayList<OwnedLand> list = new ArrayList<>();
                String query = "SELECT * FROM ll_land WHERE world = ?";
                try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {
                    st.setString(1, world);
                    ResultSet res = st.executeQuery();
                    while (res.next()) {
                        Data data = new Data(world, res.getInt("x"), res.getInt("z"));
                        OwnedLand ownedLand = new OwnedLand(data);
                        ownedLand.setOwner(UUID.fromString(res.getString("owneruuid")));
                        ownedLand.setLandId(res.getInt("landid"));
                        ownedLand.setFriends(getFriends(ownedLand.getLandId()));
                        ownedLand.setFlags(stringToFlags(res.getString("flags")));
                        list.add(ownedLand);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return list;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public List<OwnedLand> getNearbyLands(Location location, int offsetX, int offsetZ) {
        try {
            return pool.submit(() -> {
                ArrayList<OwnedLand> list = new ArrayList<>();
                String query = "SELECT * FROM ll_land WHERE world = ?";
                try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {

                    st.setString(1, location.getWorld().getName());
                    ResultSet res = st.executeQuery();
                    while (res.next()) {
                        Data data = new Data(location.getWorld().getName(), res.getInt("x"), res.getInt("z"));

                        if (data.getX() <= location.getChunk().getX() + offsetX &&
                                data.getX() >= location.getChunk().getX() - offsetX &&
                                data.getZ() <= location.getChunk().getZ() + offsetZ &&
                                data.getZ() >= location.getChunk().getZ() - offsetZ) {

                            OwnedLand ownedLand = new OwnedLand(data);
                            ownedLand.setOwner(UUID.fromString(res.getString("owneruuid")));
                            ownedLand.setLandId(res.getInt("landid"));
                            ownedLand.setFriends(getFriends(ownedLand.getLandId()));
                            ownedLand.setFlags(stringToFlags(res.getString("flags")));
                            list.add(ownedLand);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return list;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public int getFirstFreeLandID() {
        try {
            return pool.submit(() -> {
                int var = 1;
                String query = "SELECT * FROM ll_land";
                try (Connection con = getSQLConnection(); PreparedStatement st = con.prepareStatement(query)) {
                    ResultSet res = st.executeQuery();
                    while (res.next()) {
                        if (res.getInt("landid") == var) {
                            var++;
                        } else {
                            return var;
                        }
                    }
                }
                return var;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            return -1;
        }
    }

    public static void migrate() {
        SQLiteDatabase oldDB = new SQLiteDatabase(Landlord.getInstance().getDataFolder() + "/Landlord.db");

        String query = "SELECT * FROM ll_land";
        new BukkitRunnable(){

            @Override
            public void run() {
                Landlord.getInstance().getLogger().info("Starting database conversion!");

                try (PreparedStatement st = oldDB.getSQLConnection().prepareStatement(query);
                     ResultSet res = st.executeQuery()) {

                    while (res.next()) {
                        OwnedLand land = new OwnedLand(new Data(res.getString("world_name"), res.getInt("x"), res.getInt("z")));
                        land.setOwner(UUID.fromString(res.getString("owner_name")));
                        land.setLandId(res.getInt("id"));
                        land.setFlags(LandManager.getDefaultFlags(land.getLandId()));
                        land.setFriends(new ArrayList<>());
                        Landlord.getInstance().getDatabase().save(land);
                        Thread.sleep(50L);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    Landlord.getInstance().getLogger().info("Conversion of database completed!");
                }
            }
        }.runTaskAsynchronously(Landlord.getInstance());

    }

}
