package com.zero.tools.resource.util;

import com.excean.virutal.api.virtual.FileUtils;
import com.zero.tools.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DataUtil {
    private static final int PAD_BOUNDARY = 4;

    public static void skipCheckChunkTypeInt(ByteBuffer buffer, int expected, int possible) throws IOException {
        int got = buffer.getInt();

        if (got == possible || got < expected) {
            skipCheckChunkTypeInt(buffer, expected, -1);
        } else if (got != expected) {
            throw new IOException(String.format("Expected: 0x%08x, got: 0x%08x", expected, got));
        }
    }

    public static int[] readIntArray(ByteBuffer buffer, int length) throws IOException {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = buffer.getInt();
        }
        return array;
    }

    public static String readNullEndedString(ByteBuffer buffer, int length, boolean fixed)
            throws IOException {
        StringBuilder string = new StringBuilder(16);
        while (length-- != 0) {
            short ch = buffer.getShort();
            if (ch == 0) {
                break;
            }
            string.append((char) ch);
        }
        if (fixed) {
            buffer.position(buffer.position() + length * 2);
        }

        return string.toString();
    }


    public static void skipCheckShort(ByteBuffer buffer, short expected) throws IOException {
        short got = buffer.getShort();
        if (got != expected) {
            throw new IOException(String.format("CheckShort Expected: 0x%08x, got: 0x%08x", expected, got));
        }
    }

    public static void skipCheckByte(ByteBuffer buffer, byte expected) throws IOException {
        byte got = buffer.get();
        if (got != expected) {
            throw new IOException(String.format(
                    "Expected: 0x%08x, got: 0x%08x", expected, got));
        }
    }

    public static int skipBytes(ByteBuffer buffer, int n) throws IOException {
        buffer.position(buffer.position() + n);
        return n;
    }

    public static byte[] toBytes(InputStream stream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            FileUtils.copy(stream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public static void skipCheckInt(ByteBuffer buffer, int expected1, int expected2) throws IOException {
        int got = buffer.getInt();
        if (got != expected1 && got != expected2) {
            throw new IOException(String.format(
                    "Expected: 0x%08x or 0x%08x, got: 0x%08x", expected1, expected2, got));
        }
    }

    public static void skipCheckInt(ByteBuffer buffer, int expected) throws IOException {
        int got = buffer.getInt();
        if (got != expected) {
            throw new IOException(String.format("CheckInt Expected: 0x%08x, got: 0x%08x", expected, got));
        }
    }


    public static byte[] encodeString(String str, Charset type) throws IOException {
        byte[] bytes = str.getBytes(type);
        // The extra 5 bytes is for metadata (character count + byte count) and the NULL terminator.
        ByteArrayOutputStream output = new ByteArrayOutputStream(bytes.length + 5);
        encodeLength(output, str.length(), type);
        if (type == StandardCharsets.UTF_8) {  // Only UTF-8 strings have the encoding length.
            encodeLength(output, bytes.length, type);
        }
        output.write(bytes);
        // NULL-terminate the string
        if (type == StandardCharsets.UTF_8) {
            output.write(0);
        } else {
            output.write(0);
            output.write(0);
        }
        return output.toByteArray();
    }

    private static void encodeLength(OutputStream output, int length, Charset type) throws IOException {
        if (length < 0) {
            output.write(0);
            return;
        }
        if (type == StandardCharsets.UTF_8) {
            if (length > 0x7F) {
                output.write(((length & 0x7F00) >> 8) | 0x80);
            }
            output.write(length & 0xFF);
        } else {  // UTF-16
            // TODO(acornwall): Replace output with a little-endian output.
            if (length > 0x7FFF) {
                int highBytes = ((length & 0x7FFF0000) >> 16) | 0x8000;
                output.write(highBytes & 0xFF);
                output.write((highBytes & 0xFF00) >> 8);
            }
            int lowBytes = length & 0xFFFF;
            output.write(lowBytes & 0xFF);
            output.write((lowBytes & 0xFF00) >> 8);
        }
    }

    public static void writePad(ByteBuffer output, int padding) throws IOException {
        while (padding-- != 0) {
            output.put((byte) 0);
        }
    }
}
