package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOfferManager;
import biz.princeps.landlord.persistent.Database;
import biz.princeps.landlord.persistent.Offer;
import org.bukkit.Bukkit;

import java.util.Map;

public class OfferManager implements IOfferManager {

    private Database db;
    private ILandLord plugin;

    private Map<String, Offer> offers;

    public OfferManager(ILandLord plugin, Database db) {
        this.db = db;
        this.plugin = plugin;
        this.offers = db.fetchOffers();
    }

    public Offer getOffer(String landname) {
        return offers.get(landname);
    }

    public void addOffer(Offer offer) {
        offers.put(offer.getLandname(), offer);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin.getPlugin(), () -> db.save(offer));
    }

    public void removeOffer(String landname) {
        if (getOffer(landname) != null) {
            offers.remove(landname);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin.getPlugin(), () -> db.remove(landname));
        }
    }

}