package com.zero.support.simple.apps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zero.support.core.AppGlobal;
import com.zero.support.recycler.Cell;
import com.zero.support.compat.widget.SlideBar;
import com.zero.support.simple.CellViewModel;
import com.zero.support.simple.vo.AppPackageCell;
import com.zero.support.simple.vo.LetterCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackagesViewModel extends CellViewModel<Integer> {

    @Override
    protected List<Cell> performExecute(Integer integer) {
        PackageManager pm = AppGlobal.currentApplication().getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        Map<String, List<AppPackageCell>> map = new HashMap<>(list.size());
        Set<String> set = new HashSet<>();
        int size = 0;
        for (PackageInfo info : list) {
            AppPackageCell packageCell = onCreateAppPackageCell(pm, info);
            if (packageCell == null) {
                continue;
            }
            List<AppPackageCell> mirrorPackages = map.get(packageCell.getLetter());

            if (mirrorPackages == null) {
                mirrorPackages = new ArrayList<>();
                map.put(packageCell.getLetter(), mirrorPackages);
                size++;
            }
            mirrorPackages.add(packageCell);
        }
        final List<Cell> cells = new ArrayList<>(size);
        for (String letter : SlideBar.LETTERS) {
            List<AppPackageCell> packages = map.get(letter);
            if (packages == null) {
                continue;
            }
            cells.add(new LetterCell(letter));
            cells.addAll(packages);
        }
        return cells;
    }

    AppPackageCell onCreateAppPackageCell(PackageManager pm, PackageInfo info) {
        if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            return null;
        }
        if ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return null;
        }
        return new AppPackageCell(pm, info);
    }

    public int indexOf(List<Cell> cells, String letter) {
        for (int i = 0; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            if (cell instanceof LetterCell) {
                if (((LetterCell) cell).getLetter().equalsIgnoreCase(letter)) {
                    return i;
                }
            }
        }
        return 0;
    }

}
