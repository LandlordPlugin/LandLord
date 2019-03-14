package biz.princeps.landlord.api;

import biz.princeps.landlord.persistent.Offer;

public interface IOfferManager {

    Offer getOffer(String landname);

    void addOffer(Offer offer);

    void removeOffer(String landname);
}
