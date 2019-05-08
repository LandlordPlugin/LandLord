package biz.princeps.landlord.util;

public class MapConstants {
    public static String b1, b2, mi, ar;

    public static final String[][] s = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, ar, b2, b1, b1},
            {b1, b2, b1, ar, b1, b2, b1},
            {b2, b1, b1, ar, b1, b1, b2}
    };

    public static final String[][] ssw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, ar, b2, b2, b1, b1},
            {b1, b2, ar, b1, b1, b2, b1},
            {b2, ar, b1, b1, b1, b1, b2}
    };
    public static final String[][] sw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, ar, b2, b2, b1, b1},
            {b1, ar, b1, b1, b1, b2, b1},
            {ar, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] wsw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, ar, ar, b2, b2, b1, b1},
            {ar, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] w = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {ar, ar, ar, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] wnw = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {ar, b1, b2, b1, b2, b1, b2},
            {b2, ar, ar, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] nw = new String[][]{
            {ar, b2, b2, b2, b2, b2, b1},
            {b2, ar, b2, b1, b2, b1, b2},
            {b2, b2, ar, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] nnw = new String[][]{
            {b1, ar, b2, b2, b2, b2, b1},
            {b2, b1, ar, b1, b2, b1, b2},
            {b2, b2, ar, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] n = new String[][]{
            {b1, b2, b2, ar, b2, b2, b1},
            {b2, b1, b2, ar, b2, b1, b2},
            {b2, b2, b1, ar, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] nne = new String[][]{
            {b1, b2, b2, b2, b2, ar, b1},
            {b2, b1, b2, b1, ar, b1, b2},
            {b2, b2, b1, b2, ar, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] ne = new String[][]{
            {b1, b2, b2, b2, b2, b2, ar},
            {b2, b1, b2, b1, b2, ar, b2},
            {b2, b2, b1, b2, ar, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] ene = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, ar},
            {b2, b2, b1, b2, ar, ar, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] e = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, ar, ar, ar},
            {b1, b1, b2, b2, b2, b1, b1},
            {b1, b2, b1, b1, b1, b2, b1},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] ese = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, ar, ar, b1},
            {b1, b2, b1, b1, b1, b2, ar},
            {b2, b1, b1, b1, b1, b1, b2}
    };
    public static final String[][] se = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, ar, b1, b1},
            {b1, b2, b1, b1, b1, ar, b1},
            {b2, b1, b1, b1, b1, b1, ar}
    };
    public static String[][] sse = new String[][]{
            {b1, b2, b2, b2, b2, b2, b1},
            {b2, b1, b2, b1, b2, b1, b2},
            {b2, b2, b1, b2, b1, b2, b2},
            {b1, b2, b1, mi, b2, b1, b2},
            {b1, b1, b2, b2, ar, b1, b1},
            {b1, b2, b1, b1, ar, b2, b1},
            {b2, b1, b1, b1, b1, ar, b2}
    };

}
