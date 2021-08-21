package com.zero.support.xx.util;

import com.excean.support.work.exception.StorageOverFlowException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class SeekOutputStream extends OutputStream {
    boolean useDefault;
    private RandomAccessFile randomAccessFile;

    public SeekOutputStream(String path, long offset) throws IOException {
        this(new File(path), offset);
    }

    public SeekOutputStream(File file, long offset) throws IOException {
        file.getParentFile().mkdirs();
        randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.seek(offset);
        useDefault = true;
    }

    public SeekOutputStream(RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    public void seek(long position) throws IOException {
        randomAccessFile.seek(position);
    }

    @Override
    public void write(int b) throws IOException {
        try {
            randomAccessFile.write(b);
        } catch (Exception e) {
            throw new StorageOverFlowException();
        }

    }

    @Override
    public void write(byte[] b) throws IOException {
        try {
            randomAccessFile.write(b, 0, b.length);
        } catch (Exception e) {
            throw new StorageOverFlowException();
        }

    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            randomAccessFile.write(b, off, len);
        } catch (Exception e) {
            throw new StorageOverFlowException();
        }
    }

    @Override
    public void close() throws IOException {
        if (useDefault) {
            randomAccessFile.close();
        }
    }
}
