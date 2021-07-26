package com.zero.support.compat.util;

public class DataUtils {

    public static int swapEndian(int i) {
        return ((i & 0xff) << 24) + ((i & 0xff00) << 8) + ((i & 0xff0000) >>> 8)
                + ((i >>> 24) & 0xff);
    }

    public static int swapEndian(short i) {
        return ((i & 0x00FF) << 8 | (i & 0xFF00) >>> 8);
    }
}
