package com.zero.support.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class SeekInputStream extends InputStream {
    boolean useDefault;
    private RandomAccessFile randomAccessFile;

    public SeekInputStream(String path, long offset) throws IOException {
        this(new File(path), offset);
    }

    public SeekInputStream(File file, long offset) throws IOException {
        randomAccessFile = new RandomAccessFile(file, "r");
        randomAccessFile.seek(offset);
        useDefault = true;
    }

    public SeekInputStream(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return randomAccessFile.read(b, off, len);
    }

    @Override
    public int read() throws IOException {
        return randomAccessFile.read();
    }

    @Override
    public void close() throws IOException {
        if (useDefault) {
            randomAccessFile.close();
        }
    }
}
