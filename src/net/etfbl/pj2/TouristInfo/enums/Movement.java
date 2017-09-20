package net.etfbl.pj2.TouristInfo.enums;

import java.util.Random;

public enum Movement {
    ONE_ROW, DIAGONAL, WHOLE_MATRIX;

    public static Movement randomVal() {
        return values()[new Random().nextInt(values().length)];
    }
}
