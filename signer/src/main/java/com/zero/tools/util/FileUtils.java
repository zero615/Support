package com.zero.tools.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int count;
        while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
    }

    public static void moveFile(File src, File out) throws IOException {
        if (!src.renameTo(out)) {
            FileInputStream is = new FileInputStream(src);
            FileOutputStream os = new FileOutputStream(out);
            copy(is, os);
            is.close();
            os.close();
        }
    }

    public static void extractAsset(Context context, String name, File target) throws IOException {
        InputStream stream = context.getAssets().open(name);
        OutputStream outputStream = new FileOutputStream(target);
        FileUtils.copy(stream, outputStream);
        stream.close();
        outputStream.close();
    }

    public static void extractFile(File file, String dir, File output) throws IOException {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith(dir)) {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    File target = new File(output, entry.getName());
                    InputStream inputStream = zipFile.getInputStream(entry);
                    target.getParentFile().mkdirs();
                    OutputStream outputStream = new FileOutputStream(target);
                    FileUtils.copy(inputStream, outputStream);
                    inputStream.close();
                    outputStream.close();
                }
            }
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static boolean deleteQuietly(File scratch) {
        try {
            if (!scratch.isFile()) {
                File[] files = scratch.listFiles();
                for (File file : files) {
                    deleteQuietly(file);
                }
            }
            return scratch.delete();
        } catch (Exception e) {

        }
        return false;

    }
}
