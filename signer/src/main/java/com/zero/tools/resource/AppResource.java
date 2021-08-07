package com.zero.tools.resource;



import com.zero.tools.resource.data.ResPackage;
import com.zero.tools.resource.data.ResTable;
import com.zero.tools.resource.util.DataUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AppResource {
    private final ARSCDecoder decoder;
    private final ResTable resTable = new ResTable();
    private ByteBuffer buffer;

    public AppResource(InputStream stream) {
        buffer = ByteBuffer.wrap(DataUtil.toBytes(stream)).order(ByteOrder.LITTLE_ENDIAN);
        decoder = new ARSCDecoder(buffer, resTable, false, false);
    }

    public void loadMain() throws IOException {
        buffer.position(0);
        ResPackage[] pkgs = decoder.readTableHeader();
        ResPackage pkg = null;
        switch (pkgs.length) {
            case 1:
                pkg = pkgs[0];
                break;
            case 2:
                if (pkgs[0].getName().equals("android")) {
                    System.out.println("Skipping \"android\" package group");
                    pkg = pkgs[1];
                } else if (pkgs[0].getName().equals("com.htc")) {
                    System.out.println("Skipping \"htc\" package group");
                    pkg = pkgs[1];
                }
                break;
            default:
                pkg = selectPkgWithMostResSpecs(pkgs);
        }

        if (pkg == null) {
            throw new IOException("Arsc files with zero or multiple packages");
        }

        resTable.addPackage(pkg, true);
    }

    public void readStringBlock() throws IOException {
        decoder.readStringBlock();
    }

    public ResTable getResTable() {
        return resTable;
    }

    public ARSCDecoder getDecoder() {
        return decoder;
    }

    public ResPackage selectPkgWithMostResSpecs(ResPackage[] pkgs) throws IOException {
        int id = 0;
        int value = 0;

        for (ResPackage resPackage : pkgs) {
            if (resPackage.getResSpecCount() > value && !resPackage.getName().equalsIgnoreCase("android")) {
                value = resPackage.getResSpecCount();
                id = resPackage.getId();
            }
        }

        // if id is still 0, we only have one pkgId which is "android" -> 1
        return (id == 0) ? pkgs[0] : pkgs[1];
    }
}
