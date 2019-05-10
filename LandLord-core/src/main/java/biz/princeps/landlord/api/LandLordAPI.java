package biz.princeps.landlord.api;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: Unknown
 */
public interface LandLordAPI {

    IPlayerManager getPlayerManager();

    IWorldGuardProxy getWgproxy();

    IOfferManager getOfferManager();
}


