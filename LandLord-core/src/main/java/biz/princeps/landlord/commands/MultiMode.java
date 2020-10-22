package biz.princeps.landlord.commands;

import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.util.JavaUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public enum MultiMode {

    // Based on WorldEdit cylinder region.
    CIRCULAR {
        @Override
        public Set<Location> getLandsLocations(int radius, Location center) {
            final Set<Location> landsLocations = new HashSet<>();
            final World world = center.getWorld();
            int xCenter = center.getBlockX() >> 4;
            int zCenter = center.getBlockZ() >> 4;

            final double invRadiusX = 1 / (double) radius;
            final double invRadiusZ = 1 / (double) radius;

            double nextXn = 0;
            forX:
            for (int x = 0; x <= radius; ++x) {
                final double xn = nextXn;
                nextXn = (x + 1) * invRadiusX;
                double nextZn = 0;

                for (int z = 0; z <= radius; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = (xn * xn) + (zn * zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            break forX;
                        }
                        break;
                    }

                    landsLocations.add(new Location(world, (xCenter + x) << 4, 0, (zCenter + z) << 4));
                    landsLocations.add(new Location(world, (xCenter - x) << 4, 0, (zCenter - z) << 4));
                    landsLocations.add(new Location(world, (xCenter + x) << 4, 0, (zCenter - z) << 4));
                    landsLocations.add(new Location(world, (xCenter - x) << 4, 0, (zCenter + z) << 4));
                }
            }

            return landsLocations;
        }
    },
    RECTANGULAR {
        @Override
        public Set<Location> getLandsLocations(int radius, Location center) {
            final Set<Location> landsLocations = new HashSet<>();
            final World world = center.getWorld();
            int xCenter = center.getBlockX() >> 4;
            int zCenter = center.getBlockZ() >> 4;

            for (int x = xCenter - radius; x <= xCenter + radius; x++) {
                for (int z = zCenter - radius; z <= zCenter + radius; z++) {
                    landsLocations.add(new Location(world, x << 4, 0, z << 4));
                }
            }

            return landsLocations;
        }
    },
    LINEAR {
        @Override
        public Set<Location> getLandsLocations(int radius, Location center) {
            final Set<Location> landsLocations = new HashSet<>();
            final World world = center.getWorld();
            int xCenter = center.getBlockX() >> 4;
            int zCenter = center.getBlockZ() >> 4;

            final BlockFace blockFace = JavaUtils.getBlockFace(center.getYaw());

            switch (blockFace) {
                case NORTH:
                    for (int z = zCenter; z >= zCenter - radius; z--) {
                        landsLocations.add(new Location(world, xCenter << 4, 0, z << 4));
                    }
                    break;
                case EAST:
                    for (int x = xCenter; x <= xCenter + radius; x++) {
                        landsLocations.add(new Location(world, x << 4, 0, zCenter << 4));
                    }
                    break;
                case WEST:
                    for (int x = xCenter; x >= xCenter - radius; x--) {
                        landsLocations.add(new Location(world, x << 4, 0, zCenter << 4));
                    }
                    break;
                case SOUTH:
                    for (int z = zCenter; z <= zCenter + radius; z++) {
                        landsLocations.add(new Location(world, xCenter << 4, 0, z << 4));
                    }
                    break;
            }

            return landsLocations;
        }
    };

    public abstract Set<Location> getLandsLocations(int radius, Location center);

    public Set<Chunk> getFreeLands(int radius, Location center, IWorldGuardManager worldGuardManager) {
        final Set<Chunk> chunks = new HashSet<>();

        for (Location landLocation : getLandsLocations(radius, center)) {
            final IOwnedLand land = worldGuardManager.getRegion(landLocation);

            if (land == null)
                chunks.add(landLocation.getChunk());
        }

        return chunks;
    }

    public Set<IOwnedLand> getLandsOf(int radius, Location center, UUID uuid, IWorldGuardManager worldGuardManager) {
        final Set<IOwnedLand> lands = new HashSet<>();

        for (Location landLocation : getLandsLocations(radius, center)) {
            final IOwnedLand land = worldGuardManager.getRegion(landLocation);

            if (land != null && land.isOwner(uuid))
                lands.add(land);
        }

        return lands;
    }

}
