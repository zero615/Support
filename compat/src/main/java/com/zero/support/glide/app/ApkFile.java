package com.zero.support.glide.app;

import android.content.pm.PackageInfo;

import java.util.Objects;

public class ApkFile {
    private PackageInfo packageInfo;

    public ApkFile(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApkFile apkFile = (ApkFile) o;
        return Objects.equals(packageInfo.applicationInfo.publicSourceDir, apkFile.packageInfo.applicationInfo.publicSourceDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageInfo.applicationInfo.publicSourceDir);
    }
}
