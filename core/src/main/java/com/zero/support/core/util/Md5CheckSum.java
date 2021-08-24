package com.zero.support.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Checksum;

public class Md5CheckSum implements Checksum {
    MessageDigest md;

    {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int i) {
        md.update((byte) i);
    }

    @Override
    public void update(byte[] bytes, int i, int i1) {
        md.update(bytes, i, i1);
    }

    @Override
    public long getValue() {
        return 0;
    }

    public byte[] digest() {
        return md.digest();
    }

    @Override
    public void reset() {
        md.reset();
    }
}
