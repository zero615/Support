///*
// * Copyright (C) 2005-2017 Qihoo 360 Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed To in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//
//package com.zero.support.compat.util;
//
//import android.os.FileUtils;
//import android.text.TextUtils;
//import android.util.Log;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeSet;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//
//public class NativeLibsHelper {
//
//    private static final String TAG = "PluginNativeLibsHelper";
//    private static final boolean LOG = true;
//
//    /**
//     * 安装Native SO库 <p>
//     * 模拟系统安装流程，最终只释放一个最合身的SO库进入Libs目录中
//     *
//     * @param apkFile   APK文件路径
//     * @param nativeDir 要释放的Libs目录，通常从getLibDir中获取
//     * @return 安装是否成功
//     */
//    public static boolean install(File apkFile, File nativeDir) {
//        // 为防止加载旧SO，先清空目录
//        clear(nativeDir);
//        File tmpNativeDir = new File(nativeDir.getParent(), nativeDir.getName() + "-tmp");
//        clear(tmpNativeDir);
//        ZipFile zipFile = null;
//        try {
//            zipFile = new ZipFile(apkFile);
//            Map<String, ZipEntry> libZipEntries = new HashMap<>();
//            Map<String, Set<String>> soList = new HashMap<>();
//
//            // 找到所有的SO库，包括各种版本的，方便findSoPathForAbis中过滤
//            injectEntriesAndLibsMap(zipFile, libZipEntries, soList);
//
//            for (String soName : soList.keySet()) {
//                Set<String> soPaths = soList.get(soName);
//                String soPath = findSoPathForAbis(soPaths, soName);
//                if (LOG) {
//                    Logger.d(TAG, "attachBaseContext(): Ready to extract. so=" + soName + "; sop=" + soPath);
//                }
//                if (soPath == null) {
//                    continue;
//                }
//                File file = new File(tmpNativeDir, soName);
//                extractFile(zipFile, libZipEntries.get(soPath), file);
//            }
//            tmpNativeDir.renameTo(nativeDir);
//            return true;
//        } catch (Throwable e) {
//            if (LOG) {
//                e.printStackTrace();
//            }
//            // 清除所有释放的文件，防止释放了一半
//            clear(nativeDir);
//            return false;
//        } finally {
//            CloseableUtils.closeQuietly(zipFile);
//        }
//    }
//
//    public static void installX86(File apkFile, String soName, File targetFile) {
//        try {
//            ZipFile zipFile = new ZipFile(apkFile);
//            String name;
//            File tmpFile = new File(targetFile.getParent(), targetFile.getName() + ".tmp");
//            if (BuildCompat.is64Bit()) {
//                name = "libs/x86-64/" + soName;
//            } else {
//                name = "libs/x86/" + soName;
//            }
//            extractFile(zipFile, zipFile.getEntry(name), tmpFile);
//            tmpFile.renameTo(targetFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 删除插件的SO库，通常在插件SO释放失败后，或者已有新插件，需要清除老插件时才会生效
//     */
//    public static void clear(File nativeDir) {
//        if (!nativeDir.exists()) {
//            return;
//        }
//        try {
//            FileUtils.forceDelete(nativeDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void injectEntriesAndLibsMap(ZipFile zipFile, Map<String, ZipEntry> libZipEntries, Map<String, Set<String>> soList) {
//        Enumeration<? extends ZipEntry> entries = zipFile.entries();
//        while (entries.hasMoreElements()) {
//            ZipEntry entry = entries.nextElement();
//            String name = entry.getName();
//            if (name.contains("../")) {
//                // 过滤，防止被攻击
//                continue;
//            }
//            if (name.startsWith("lib/") && !entry.isDirectory()) {
//                libZipEntries.put(name, entry);
//                String soName = new File(name).getName();
//                Set<String> fs = soList.get(soName);
//                if (fs == null) {
//                    fs = new TreeSet<>();
//                    soList.put(soName, fs);
//                }
//                fs.add(name);
//            }
//        }
//    }
//
//    private static void extractFile(ZipFile zipFile, ZipEntry ze, File outFile) throws IOException {
//        InputStream in = null;
//        try {
//            in = zipFile.getInputStream(ze);
//            FileUtils.copyInputStreamToFile(in, outFile);
//            if (LOG) {
//                Log.i(TAG, "extractFile(): Success! fn=" + outFile.getName());
//            }
//        } finally {
//            try {
//                in.close();
//            } catch (Exception e) {
//
//            }
//
//        }
//    }
//
//    // 根据Abi来获取需要释放的SO在压缩包中的位置
//    private static String findSoPathForAbis(Set<String> soPaths, String soName) {
//        if (soPaths == null || soPaths.size() <= 0) {
//            return null;
//        }
//        // 若主程序用的是64位进程，则所属的SO必须只拷贝64位的，否则会出异常。32位也是如此
//        // 问：如果用户用的是64位处理器，宿主没有放任何SO，那么插件会如何？
//        // 答：宿主在被安装时，系统会标记此为64位App，则之后的SO加载则只认64位的
//        // 问：如何让插件支持32位？
//        // 答：宿主需被标记为32位才可以。可在宿主App中放入任意32位的SO（如放到libs/armeabi目录下）即可。
//
//        // 获取指令集列表
//        boolean is64 = BuildCompat.is64Bit();
//        String[] abis;
//        if (is64) {
//            abis = BuildCompat.SUPPORTED_64_BIT_ABIS;
//        } else {
//            abis = BuildCompat.SUPPORTED_32_BIT_ABIS;
//        }
//        String soPath;
//        soPath = findSoPathWithAbiList(soPaths, soName, abis);
//        return soPath;
//    }
//
//    private static String findSoPathWithAbiList(Set<String> soPaths, String soName, String[] supportAbis) {
//        Arrays.sort(supportAbis);
//        for (String soPath : soPaths) {
//            String abi = soPath.replaceFirst("lib/", "");
//            abi = abi.replace("/" + soName, "");
//            Logger.get().warning(Logger.TAG, Arrays.toString(supportAbis) + " " + soPath + " " + soName + " " + abi);
//            if (!TextUtils.isEmpty(abi)) {
//                for (String support : supportAbis) {
//                    if (support.equals(abi)) {
//                        return soPath;
//                    }
//                }
//            } else {
//                Logger.get().warning(Logger.TAG, "not found" + Arrays.toString(supportAbis) + " " + soPath + " " + soName + " " + abi);
//            }
//        }
//        return null;
//    }
//}
