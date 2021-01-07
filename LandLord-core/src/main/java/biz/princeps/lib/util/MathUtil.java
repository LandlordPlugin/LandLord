package biz.princeps.lib.util;

import org.bukkit.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spatium on 29.07.17.
 */
public class MathUtil {

    public static List<Location> helix(Location center, float radius, int amount, double offset) {
        List<Location> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double a = (8 * Math.PI / amount * i) + offset;
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            list.add(center.clone().add(x, 0.11 * i, z));
        }
        return list;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
