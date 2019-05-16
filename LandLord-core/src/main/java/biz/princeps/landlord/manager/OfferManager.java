package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOffer;
import biz.princeps.landlord.api.IOfferManager;
import biz.princeps.landlord.persistent.Database;
import org.bukkit.Bukkit;

import java.util.Map;

public class OfferManager implements IOfferManager {

    private Database db;
    private ILandLord plugin;

    private Map<String, IOffer> offers;

    public OfferManager(ILandLord plugin, Database db) {
        this.db = db;
        this.plugin = plugin;
        this.offers = db.fetchOffers();
    }

    @Override
    public IOffer getOffer(String landname) {
        return offers.get(landname);
    }

    @Override
    public void addOffer(IOffer offer) {
        offers.put(offer.getLandname(), offer);
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin.getPlugin(), () -> db.save(offer));
    }

    @Override
    public void removeOffer(String landname) {
        if (getOffer(landname) != null) {
            offers.remove(landname);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin.getPlugin(), () -> db.remove(landname));
        }
    }

}