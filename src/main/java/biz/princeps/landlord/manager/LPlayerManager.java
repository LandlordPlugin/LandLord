package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.api.Options;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.lib.manager.MappedManager;
import biz.princeps.lib.storage.DatabaseAPI;
import biz.princeps.lib.storage.requests.Conditions;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 17/7/17
 */
public class LPlayerManager extends MappedManager<UUID, LPlayer> {

    private Map<String, Offers> offers = new HashMap<>();

    public LPlayerManager(DatabaseAPI api) {
        super(api);
    }

    public void save(UUID id) {
        LPlayer lp = get(id);
        Landlord.getInstance().getDatabaseAPI().saveObject(lp);
    }

    public void onStartup() {
        Landlord.getInstance().getDatabaseAPI().getDatabase().executeQuery("SELECT * FROM ll_advertise", res -> {
            while (res.next()) {
                offers.put(res.getString("landname"), new Offers(res.getString("landname"), res.getDouble("price"), UUID.fromString(res.getString("seller"))));
            }
        });
    }

    public void getOfflinePlayer(UUID uuid, Consumer<LPlayer> consumer) {
        ((Landlord) plugin).getExecutorService().execute(() -> consumer.accept(getLPlayer(uuid)));
    }

    private LPlayer getLPlayer(UUID uuid) {
        List<Object> list = Landlord.getInstance().getDatabaseAPI()
                .retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", uuid.toString()).create());
        if (list.size() > 0) {
            return (LPlayer) list.get(0);
        }
        return null;
    }

    public void getOfflinePlayer(String name, Consumer<LPlayer> consumer) {
        ((Landlord) plugin).getExecutorService().execute(() -> consumer.accept(getLPlayer(name)));
    }

    private LPlayer getLPlayer(String name) {
        if (this.contains(name)) return get(name);

        List<Object> list = Landlord.getInstance().getDatabaseAPI()
                .retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("name", name).create());
        if (list.size() > 0) {
            return (LPlayer) list.get(0);
        }
        return null;
    }

    public Offers getOffer(String landname) {
        return offers.get(landname);
    }

    public void addOffer(Offers offer) {
        offers.put(offer.getLandname(), offer);

        new BukkitRunnable() {

            @Override
            public void run() {
                Landlord.getInstance().getDatabaseAPI().saveObject(offer);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void removeOffer(String landname) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Landlord.getInstance().getDatabaseAPI().getDatabase().execute("DELETE FROM ll_advertise WHERE landname = '" + landname + "'");
                offers.remove(landname);
            }
        }.runTaskAsynchronously(plugin);
    }

    public boolean contains(String name) {
        for (LPlayer lPlayer : this.elements.values()) {
            if (lPlayer.getName() != null && lPlayer.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public LPlayer get(String name) {
        for (LPlayer lPlayer : this.elements.values()) {
            if (lPlayer.getName() != null && lPlayer.getName().equals(name)) {
                return lPlayer;
            }
        }
        return null;
    }

    /**
     * Measures if a player is inactive based on the date he was seen the last time.
     * If this date + the timegate is before right now, he is inactive
     *
     * @param lastSeenDate the date the player was last seen
     * @return if the player is inactive or not
     */
    public boolean isInactive(LocalDateTime lastSeenDate) {
        if (!Options.enabled_inactiveBuyUp()) return false;

        if (lastSeenDate == null) {
            return false;
        }

        int days = plugin.getConfig().getInt("BuyUpInactive.timegate");

        // yes, this guy is inactive
        return lastSeenDate.plusDays(days).isBefore(LocalDateTime.now());
    }

    public void isInactive(UUID id, Consumer<Boolean> consumer) {
        ((Landlord) plugin).getExecutorService().execute(() -> consumer.accept(isInactive(id)));
    }

    /**
     * Warning, this method might cause lag if done on the main thread!
     *
     * @param id the uuid which should be checked
     * @return if the given id is marked as inactive
     */
    public Boolean isInactive(UUID id) {
        LPlayer lPlayer = getLPlayer(id);
        if (lPlayer != null) {
            return isInactive(lPlayer.getLastSeen());
        }
        return false;
    }

    public void getInactiveRemainingDays(UUID owner, Consumer<Integer> consumer) {
        ((Landlord) plugin).getExecutorService().execute(() -> consumer.accept(getInactiveRemainingDays(owner)));
    }

    /**
     * Warning, this method might cause lag if done on the main thread!
     *
     * @param owner the uuid which should be checked
     * @return the amount of days, which are missing until the player become inactive
     */
    public int getInactiveRemainingDays(UUID owner) {

        long days = plugin.getConfig().getInt("BuyUpInactive.timegate");
        LPlayer lPlayer = getLPlayer(owner);
        if (lPlayer != null) {
            return (int) (days - (Duration.between(LocalDateTime.now(), lPlayer.getLastSeen()).toDays()));
        }
        return -1;
    }
}
