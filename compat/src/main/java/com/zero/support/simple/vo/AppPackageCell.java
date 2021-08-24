package com.zero.support.simple.vo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zero.support.vo.BaseObject;
import com.zero.support.simple.util.PinyinUtil;

public class AppPackageCell extends BaseObject {
    private final String name;
    private final PackageInfo packageInfo;
    private String indexKey;
    private String letter;

    public AppPackageCell(PackageManager pm, PackageInfo info) {
        this.name = String.valueOf(info.applicationInfo.loadLabel(pm));
        this.packageInfo = info;
        this.indexKey = PinyinUtil.chineseToSpell(name);
        this.letter = PinyinUtil.formatAlpha(indexKey);
    }

    public String getName() {
        return name;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getLetter() {
        return letter;
    }
}
