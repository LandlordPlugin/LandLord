package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import com.google.common.util.concurrent.*;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;

import java.util.concurrent.Executors;

/**
 * Created by spatium on 17.07.17.
 */
public class UUIDFetcher {

    private static UUIDFetcher instance;

    private ListeningExecutorService executor =
            MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public void namesToUUID(String[] names, FutureCallback<DefaultDomain> callback) {
        ProfileService profiles = Landlord.getInstance().getWgHandler().getWG().getProfileService();
        DomainInputResolver resolver = new DomainInputResolver(profiles, names);
        resolver.setLocatorPolicy(DomainInputResolver.UserLocatorPolicy.UUID_AND_NAME);
        ListenableFuture<DefaultDomain> future = executor.submit(resolver);

        Futures.addCallback(future, callback);
    }


    public static UUIDFetcher getInstance() {
        if (instance == null)
            instance = new UUIDFetcher();
        return instance;
    }
}
