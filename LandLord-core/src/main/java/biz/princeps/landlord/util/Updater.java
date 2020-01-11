package biz.princeps.landlord.util;

import biz.princeps.landlord.api.ILandLord;
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
    private ILandLord pl;
    private SpigetUpdate updater;

    public Updater(ILandLord pl) {
        this.pl = pl;
        updater = new SpigetUpdate(pl.getPlugin(), 44398);

        if (pl.getConfig().getBoolean("checkUpdatesPeriodically", true)) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(pl.getPlugin(), this::checkForUpdate, 0, 20 * 60 * 60 * 24);
        } else {
            if (pl.getConfig().getBoolean("checkUpdateOnStart", true)) {
                checkForUpdate();
            }
        }
    }

    private void checkForUpdate() {
        updater.setVersionComparator(VersionComparator.EQUAL);

        updater.checkForUpdate(new UpdateCallback() {
            @Override
            public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
                try {
                    String localVersion = pl.getPlugin().getDescription().getVersion();

                    // check for normal update
                    if (newVersion.charAt(0) == localVersion.charAt(0)) {
                        if (Integer.parseInt(newVersion.substring(2)) > Integer.parseInt(localVersion.substring(2))) {
                            pl.getLogger().info("LandLord was updated! Download the latest version (" + newVersion + ") from " + downloadUrl);
                        }
                    } else if (newVersion.charAt(0) > localVersion.charAt(0)) {
                        // in case there was a major update e.g. 3.xxx -> 4.0
                        pl.getLogger().info("LandLord was updated! Download the latest version (" + newVersion + ") from " + downloadUrl);
                    }

                    //System.out.println(newVersion.charAt(0));
                    //System.out.println(localVersion.charAt(0));

                    //System.out.println(Integer.parseInt(newVersion.substring(2)));
                    //System.out.println(Integer.parseInt(Landlord.getInstance().getDescription().getVersion().substring(2)));

                } catch (NumberFormatException ignored) {
                }
            }

            @Override
            public void upToDate() {
                // Plugin is up-to-date
            }
        });
    }
}