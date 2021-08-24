package com.zero.support.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BoundInputStream extends FilterInputStream {
    private long read;
    private long limit;
    private boolean dummy;

    public BoundInputStream(InputStream in, long limit) {
        this(in, limit, false);
    }

    /**
     * @param in
     * @param limit
     * @param dummy {@link java.util.zip.Inflater}
     */
    public BoundInputStream(InputStream in, long limit, boolean dummy) {
        super(in);
        this.limit = limit;
        this.dummy = dummy;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        long distance = limit - read;
        if (distance == 0) {
            if (dummy) {
                dummy = false;
                b[0] = 0;
                return 1;
            }
            return -1;
        }
        if (distance < len) {
            len = (int) distance;
        }
        int length = super.read(b, off, len);
        read += length;
        return length;
    }

    @Override
    public int read() throws IOException {
        if (read == limit) {
            if (dummy) {
                dummy = false;
                return 0;
            }
            return -1;
        }
        int length = super.read();
        read += 1;
        return length;
    }
}