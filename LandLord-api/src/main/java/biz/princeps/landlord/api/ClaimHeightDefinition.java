package biz.princeps.landlord.api;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public enum ClaimHeightDefinition {
    /**
     * Calculate the average of all points.
     */
    AVERAGE(list -> {
        int sum = 0;
        for (Integer val : list) {
            sum += val;
        }
        return sum / list.size();
    }),
    /**
     * Returns always zero.
     * <p>
     * Defines a claim with a fixed x and y value.
     */
    FIXED(list -> 0),
    /**
     * Returns always zero.
     * <p>
     * Defines a full chunk
     */
    FULL(list -> 0),
    /**
     * Calculate the highest point.
     */
    HIGHEST(list -> {
        int highest = 0;
        for (int integer : list) {
            highest = Math.max(highest, integer);
        }
        return highest;
    }),
    /**
     * Calculate the lowest point
     */
    LOWEST(list -> {
        int lowest = Integer.MAX_VALUE;
        for (int integer : list) {
            lowest = Math.min(lowest, integer);
        }
        return lowest;
    }),
    /**
     * Calculate the median out of all points.
     */
    MEDIAN(list -> {
        Collections.sort(list);
        return (list.get(list.size() / 2) + list.get(list.size() / 2 + 1)) / 2;
    });

    private final Function<List<Integer>, Integer> calc;

    ClaimHeightDefinition(Function<List<Integer>, Integer> calc) {
        this.calc = calc;
    }

    public int getCenter(List<Integer> points) {
        return calc.apply(points);
    }

    public static ClaimHeightDefinition parse(String value) {
        for (ClaimHeightDefinition def : values()) {
            if (def.name().equalsIgnoreCase(value)) {
                return def;
            }
        }
        return null;
    }
}
