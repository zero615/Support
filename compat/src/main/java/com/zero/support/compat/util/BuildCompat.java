/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.zero.support.compat.util;

import android.os.Build;

import java.lang.reflect.Method;


public class BuildCompat {

    public static final String ARM = "arm";

    public static final String ARM64 = "arm64";

    public static final String[] SUPPORTED_ABIS;

    public static final String[] SUPPORTED_32_BIT_ABIS;

    public static final String[] SUPPORTED_64_BIT_ABIS;
    private static final byte[] GET_LOCKER = new byte[0];
    private static volatile Boolean sIs64Bit;

    static {
        //init SUPPORTED_ABIS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.SUPPORTED_ABIS != null) {
                SUPPORTED_ABIS = new String[Build.SUPPORTED_ABIS.length];
                System.arraycopy(Build.SUPPORTED_ABIS, 0, SUPPORTED_ABIS, 0, SUPPORTED_ABIS.length);
            } else {
                SUPPORTED_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
        } else {
            SUPPORTED_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }

        //init SUPPORTED_32_BIT_ABIS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.SUPPORTED_32_BIT_ABIS != null) {
                SUPPORTED_32_BIT_ABIS = new String[Build.SUPPORTED_32_BIT_ABIS.length];
                System.arraycopy(Build.SUPPORTED_32_BIT_ABIS, 0, SUPPORTED_32_BIT_ABIS, 0, SUPPORTED_32_BIT_ABIS.length);
            } else {
                SUPPORTED_32_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
        } else {
            SUPPORTED_32_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }

        //init SUPPORTED_64_BIT_ABIS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.SUPPORTED_64_BIT_ABIS != null) {
                SUPPORTED_64_BIT_ABIS = new String[Build.SUPPORTED_64_BIT_ABIS.length];
                System.arraycopy(Build.SUPPORTED_64_BIT_ABIS, 0, SUPPORTED_64_BIT_ABIS, 0, SUPPORTED_64_BIT_ABIS.length);
            } else {
                SUPPORTED_64_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
        } else {
            SUPPORTED_64_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
    }

    /**
     * 精确判断是否为64位
     */
    public static boolean is64Bit() {
        // 最终使用下列方法：
        // VMRuntime.getRuntime().is64Bit();
        if (sIs64Bit != null) {
            return sIs64Bit;
        }
        synchronized (GET_LOCKER) {
            if (sIs64Bit != null) {
                return sIs64Bit;
            }

            // 确保只获取一次。但不排除个别手机一上来获取会有问题（没遇到）
            sIs64Bit = is64BitImpl();
            return sIs64Bit;
        }
    }

    private static boolean is64BitImpl() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // Android API 21之前不支持64位CPU
                return false;
            }

            Class<?> clzVMRuntime = Class.forName("dalvik.system.VMRuntime");
            Method mthVMRuntimeGet = clzVMRuntime.getDeclaredMethod("getRuntime");
            Object objVMRuntime = mthVMRuntimeGet.invoke(null);
            if (objVMRuntime == null) {
                return false;
            }
            Method sVMRuntimeIs64BitMethod = clzVMRuntime.getDeclaredMethod("is64Bit");
            Object objIs64Bit = sVMRuntimeIs64BitMethod.invoke(objVMRuntime);
            if (objIs64Bit instanceof Boolean) {
                return (boolean) objIs64Bit;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Art虚拟机，引入AOT编译后，读取oat目录下当前正在使用的目录
     * TODO 目前仅支持arm
     *
     * @return
     */
    public static String getArtOatCpuType() {
        return is64Bit() ? BuildCompat.ARM64 : BuildCompat.ARM;
    }

    public static boolean isX86() {
        return BuildCompat.SUPPORTED_ABIS[0].startsWith("x86");
    }

}
