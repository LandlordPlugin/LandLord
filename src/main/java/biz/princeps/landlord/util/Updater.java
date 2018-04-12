package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import org.bukkit.Bukkit;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 12/04/18
 */
public class Updater {
    SpigetUpdate updater = new SpigetUpdate(Landlord.getInstance(), 44398);

    public Updater() {
        if (Landlord.getInstance().getConfig().getBoolean("checkUpdatesPeriodically", true)) {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(Landlord.getInstance(), this::checkForUpdate, 0, 20 * 60 * 60 * 24);
        } else {
            checkForUpdate();
        }
    }

    private void checkForUpdate() {
        updater.setVersionComparator(VersionComparator.EQUAL);
        //   updater.setVersionComparator(VersionComparator.);

        updater.checkForUpdate(new UpdateCallback() {
            @Override
            public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
                if (Integer.parseInt(newVersion.substring(1)) >
                        Integer.parseInt(Landlord.getInstance().getDescription().getVersion().substring(2)))
                    Landlord.getInstance().getLogger().info("LandLord was updated! Download the latest version (" + newVersion + ") from " + downloadUrl);
            }

            @Override
            public void upToDate() {
                //// Plugin is up-to-date
            }
        });
    }
}