package biz.princeps.landlord.util;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * MapConstants provides constants for the Map. I felt like it would be pointless to build all these two dimensional
 * String arrays on demand. So this class builds them once and then provides them via getter methods.
 */
public class MapConstants {
    private String b1, b2, mi, ar;
    private String[][] s, ssw, sw, wsw,
            w, wnw, nw, nnw,
            n, nne, ne, ene,
            e, ese, se, sse;

    public MapConstants(FileConfiguration config) {
        b1 = config.getString("CommandSettings.Map.symbols.background1");
        b2 = config.getString("CommandSettings.Map.symbols.background2");
        ar = config.getString("CommandSettings.Map.symbols.arrow");
        mi = config.getString("CommandSettings.Map.symbols.middle");

        s = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, ar, b2, b1, b1},
                {b1, b2, b1, ar, b1, b2, b1},
                {b2, b1, b1, ar, b1, b1, b2}
        };

        ssw = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, ar, b2, b2, b1, b1},
                {b1, b2, ar, b1, b1, b2, b1},
                {b2, ar, b1, b1, b1, b1, b2}
        };
        sw = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, ar, b2, b2, b1, b1},
                {b1, ar, b1, b1, b1, b2, b1},
                {ar, b1, b1, b1, b1, b1, b2}
        };
        wsw = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, ar, ar, b2, b2, b1, b1},
                {ar, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        w = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {ar, ar, ar, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        wnw = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {ar, b1, b2, b1, b2, b1, b2},
                {b2, ar, ar, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        nw = new String[][]{
                {ar, b2, b2, b2, b2, b2, b1},
                {b2, ar, b2, b1, b2, b1, b2},
                {b2, b2, ar, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        nnw = new String[][]{
                {b1, ar, b2, b2, b2, b2, b1},
                {b2, b1, ar, b1, b2, b1, b2},
                {b2, b2, ar, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        n = new String[][]{
                {b1, b2, b2, ar, b2, b2, b1},
                {b2, b1, b2, ar, b2, b1, b2},
                {b2, b2, b1, ar, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        nne = new String[][]{
                {b1, b2, b2, b2, b2, ar, b1},
                {b2, b1, b2, b1, ar, b1, b2},
                {b2, b2, b1, b2, ar, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        ne = new String[][]{
                {b1, b2, b2, b2, b2, b2, ar},
                {b2, b1, b2, b1, b2, ar, b2},
                {b2, b2, b1, b2, ar, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        ene = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, ar},
                {b2, b2, b1, b2, ar, ar, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        e = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, ar, ar, ar},
                {b1, b1, b2, b2, b2, b1, b1},
                {b1, b2, b1, b1, b1, b2, b1},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        ese = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, ar, ar, b1},
                {b1, b2, b1, b1, b1, b2, ar},
                {b2, b1, b1, b1, b1, b1, b2}
        };
        se = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, ar, b1, b1},
                {b1, b2, b1, b1, b1, ar, b1},
                {b2, b1, b1, b1, b1, b1, ar}
        };
        sse = new String[][]{
                {b1, b2, b2, b2, b2, b2, b1},
                {b2, b1, b2, b1, b2, b1, b2},
                {b2, b2, b1, b2, b1, b2, b2},
                {b1, b2, b1, mi, b2, b1, b2},
                {b1, b1, b2, b2, ar, b1, b1},
                {b1, b2, b1, b1, ar, b2, b1},
                {b2, b1, b1, b1, b1, ar, b2}
        };
    }

    public String getB1() {
        return b1;
    }

    public String getB2() {
        return b2;
    }

    public String getMi() {
        return mi;
    }

    public String getAr() {
        return ar;
    }

    public String[][] getS() {
        return s;
    }

    public String[][] getSsw() {
        return ssw;
    }

    public String[][] getSw() {
        return sw;
    }

    public String[][] getWsw() {
        return wsw;
    }

    public String[][] getW() {
        return w;
    }

    public String[][] getWnw() {
        return wnw;
    }

    public String[][] getNw() {
        return nw;
    }

    public String[][] getNnw() {
        return nnw;
    }

    public String[][] getN() {
        return n;
    }

    public String[][] getNne() {
        return nne;
    }

    public String[][] getNe() {
        return ne;
    }

    public String[][] getEne() {
        return ene;
    }

    public String[][] getE() {
        return e;
    }

    public String[][] getEse() {
        return ese;
    }

    public String[][] getSe() {
        return se;
    }

    public String[][] getSse() {
        return sse;
    }
}
