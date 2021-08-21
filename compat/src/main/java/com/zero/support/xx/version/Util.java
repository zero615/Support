package com.zero.support.xx.version;

public class Util {
    public static String getTextSize(long size) {
        if (size > 1024 * 1024) {
            return size / 1024 / 1024 + "MB";
        } else if (size > 1024) {
            return size / 1024 + "KB";
        } else {
            return size + "B";
        }
    }
}
