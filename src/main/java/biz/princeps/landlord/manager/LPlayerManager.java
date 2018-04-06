package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.landlord.persistent.Offers;
import biz.princeps.lib.manager.MappedManager;
import biz.princeps.lib.storage.DatabaseAPI;
import biz.princeps.lib.storage.requests.Conditions;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
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

    public LPlayer getOfflineLPlayer(UUID id) {
        // TODO add proper asyn operation, this might cause lag
        List<Object> list = Landlord.getInstance().getDatabaseAPI().retrieveObjects(LPlayer.class, new Conditions.Builder().addCondition("uuid", id.toString()).create());
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
}
