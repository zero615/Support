/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Apache Harmony HEADER because the code in this class comes mostly from ZipFile, ZipEntry2 and
 * ZipConstants from android libcore.
 */

package com.zero.support.compat.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.zip.ZipException;

public class ZipUtil {
    public static final String TAG = "zip";
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    /* redefine those constant here because of bug 13721174 preventing to compile using the
     * constants defined in ZipFile */
    private static final int ENDHDR = 22;
    private static final int ENDSIG = 0x6054b50;

    /**
     * Size of reading buffers.
     */
    private static final int BUFFER_SIZE = 0x4000;

    /**
     * Compute crc32 of the central directory of an apk. The central directory contains
     * the crc32 of each entries in the zip so the computed result is considered valid for the whole
     * zip file. Does not support zip64 nor multidisk but it should be OK for now since ZipFile does
     * not either.
     */
    public static String getZipSign(File apk) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(apk, "r");
        try {
            CentralDirectory dir = findCentralDirectory(raf);
            return bytesToHex(computeSHA1OfCentralDir(raf, dir));
        } catch (Exception e) {
            return "";
        } finally {
            raf.close();
        }
    }

    /* Package visible for testing */
    static CentralDirectory findCentralDirectory(RandomAccessFile raf) throws IOException,
            ZipException {
        long scanOffset = raf.length() - ENDHDR;
        if (scanOffset < 0) {
            throw new ZipException("File too short to be a zip file: " + raf.length());
        }

        long stopOffset = scanOffset - 0x10000 /* ".ZIP file comment"'s max length */;
        if (stopOffset < 0) {
            stopOffset = 0;
        }

        int endSig = Integer.reverseBytes(ENDSIG);
        while (true) {
            raf.seek(scanOffset);
            if (raf.readInt() == endSig) {
                break;
            }

            scanOffset--;
            if (scanOffset < stopOffset) {
                throw new ZipException("End Of Central Directory signature not found");
            }
        }
        // Read the End Of Central Directory. ENDHDR includes the signature
        // bytes,
        // which we've already read.

        // Pull out the information we need.
        raf.skipBytes(2); // diskNumber
        raf.skipBytes(2); // diskWithCentralDir
        raf.skipBytes(2); // numEntries
        raf.skipBytes(2); // totalNumEntries
        CentralDirectory dir = new CentralDirectory();
        dir.size = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        dir.offset = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        return dir;
    }

    /* Package visible for testing */
    static byte[] computeSHA1OfCentralDir(RandomAccessFile raf, CentralDirectory dir)
            throws IOException {

        MessageDigest messagedigest;
        try {
            messagedigest = MessageDigest.getInstance("SHA-1");
        } catch (Exception ignored) {
            return null;
        }

        long stillToRead = dir.size;
        raf.seek(dir.offset);
        int length = (int) Math.min(BUFFER_SIZE, stillToRead);
        byte[] buffer = new byte[BUFFER_SIZE];
        length = raf.read(buffer, 0, length);
        while (length != -1) {
            messagedigest.update(buffer, 0, length);
            stillToRead -= length;
            if (stillToRead == 0) {
                break;
            }
            length = (int) Math.min(BUFFER_SIZE, stillToRead);
            length = raf.read(buffer, 0, length);
        }
        return messagedigest.digest();
    }

    public static String bytesToHex(byte[] bytes) {

        if (bytes == null) return "";

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // TODO: 2016/11/19 consider use native implementation if new ZipFile is time consuming
    public static long getCentralPos(String filePath, RandomAccessFile raFile) throws Exception {
        long centralPos = 0;
        if (raFile == null)
            return centralPos;

        byte[] comment = extractZipComment(filePath);
        int commentLen = 0;
        if (comment != null)
            commentLen = comment.length;

        byte[] buffer = new byte[22 + commentLen];
        long fileLen = raFile.length();
        raFile.seek(fileLen - buffer.length);
        if (raFile.read(buffer) > 0)//read central offset
            centralPos = (buffer[16] & 0xffL) + ((buffer[17] & 0xffL) << 8) + ((buffer[18] & 0xffL) << 16) + ((buffer[19] & 0xffL) << 24);

        return centralPos;
    }

    public static byte[] extractZipComment(String filename) {
        byte[] byteComment = null;
        try {
            File file = new File(filename);
            int fileLen = (int) file.length();
            FileInputStream in = new FileInputStream(file); /* The whole ZIP comment (including the magic byte sequence) * MUST fit in the buffer * otherwise, the comment will not be recognized correctly * * You can safely increase the buffer size if you like */
            byte[] buffer = new byte[Math.min(fileLen, 8192)];//0x2000,not the max value of the comment length
            int len;
            in.skip(fileLen - buffer.length);
            if ((len = in.read(buffer)) > 0) {
                byteComment = getZipCommentFromBuffer(buffer, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteComment;
    }

    private static byte[] getZipCommentFromBuffer(byte[] buffer, int len) throws Exception {
        byte[] byteComment = null;
        byte[] magicDirEnd = {0x50, 0x4B, 0x05, 0x06};
        int buffLen = Math.min(buffer.length, len);

        for (int i = buffLen - 22; i >= 0; i--) { //Check the buffer from the end
            boolean isMagicStart = true;
            for (int k = 0; k < magicDirEnd.length; k++) {
                if (buffer[i + k] != magicDirEnd[k]) {
                    isMagicStart = false;
                    break;
                }
            }
            if (isMagicStart) {
                int commentLen = (int) (buffer[i + 20] & 0xff) + (int) ((buffer[i + 21] << 8) & 0xffff);
                int realLen = buffLen - i - 22;
                Log.d(TAG, "ZIP comment found at buffer position " + (i + 22) + " with len=" + commentLen);
                if (commentLen != realLen) {
                    Log.d(TAG, "ZIP comment size mismatch: directory says len is " + commentLen + ", but file ends after " + realLen);
                }
                byteComment = new byte[commentLen];
                for (int m = buffLen - commentLen, n = 0; m < buffLen && n < commentLen; m++, n++) {
                    byteComment[n] = buffer[m];
                }
                return byteComment;
            }
        }

        return byteComment;
    }

    public static boolean writeCommentToZip(byte[] byteComment, String zipFilePath) {
        boolean flag = true;

        File file = new File(zipFilePath);

        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.seek(file.length() - 2);

            ByteBuffer bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            bb.putShort((short) byteComment.length);
            accessFile.write(bb.array());
            accessFile.write(byteComment);
        } catch (Exception e) {
            flag = false;
            Log.d(TAG, "write comment failed");
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }

    static class CentralDirectory {
        long offset;
        long size;
    }

}
