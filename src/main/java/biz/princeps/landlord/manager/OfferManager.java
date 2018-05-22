package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.persistent.Database;
import biz.princeps.landlord.persistent.Offer;
import org.bukkit.Bukkit;

import java.util.Map;

public class OfferManager {

    private Database db;
    private Landlord plugin;

    private Map<String, Offer> offers;

    public OfferManager(Database db) {
        this.db = db;
        this.plugin = Landlord.getInstance();
        this.offers = db.fetchOffers();
    }

    public Offer getOffer(String landname) {
        return offers.get(landname);
    }

    public void addOffer(Offer offer) {
        offers.put(offer.getLandname(), offer);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> db.save(offer));
    }

    public void removeOffer(String landname) {
        offers.remove(landname);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> db.remove(landname));
    }

}