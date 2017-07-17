package biz.princeps.landlord.manager;

import biz.princeps.landlord.persistent.Data;
import biz.princeps.landlord.persistent.Land;
import biz.princeps.lib.manager.CachedManager;
import com.google.common.cache.CacheLoader;

/**
 * Created by spatium on 17.07.17.
 */
public class LandManager extends CachedManager<Data, Land> {

    public LandManager() {
        //TODO get maxSize from config
        super(1000, new CacheLoader<Data, Land>() {
            @Override
            public Land load(Data data) throws Exception {
                return null;
            }
        });
    }

    @Override
    public void saveAll() {

    }

    @Override
    public void loadAll() {

    }
}
