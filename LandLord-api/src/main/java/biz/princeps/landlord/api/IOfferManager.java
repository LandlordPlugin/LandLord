package biz.princeps.landlord.api;

public interface IOfferManager {

    IOffer getOffer(String landname);

    void addOffer(IOffer offer);

    void removeOffer(String landname);
}
