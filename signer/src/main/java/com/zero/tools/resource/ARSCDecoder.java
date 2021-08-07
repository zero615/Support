/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.zero.tools.resource;



import com.zero.tools.resource.data.ResConfigFlags;
import com.zero.tools.resource.data.ResID;
import com.zero.tools.resource.data.ResPackage;
import com.zero.tools.resource.data.ResResSpec;
import com.zero.tools.resource.data.ResResource;
import com.zero.tools.resource.data.ResTable;
import com.zero.tools.resource.data.ResType;
import com.zero.tools.resource.data.ResTypeSpec;
import com.zero.tools.resource.data.value.ResBagValue;
import com.zero.tools.resource.data.value.ResFileValue;
import com.zero.tools.resource.data.value.ResIntBasedValue;
import com.zero.tools.resource.data.value.ResReferenceValue;
import com.zero.tools.resource.data.value.ResScalarValue;
import com.zero.tools.resource.data.value.ResStringValue;
import com.zero.tools.resource.data.value.ResValue;
import com.zero.tools.resource.data.value.ResValueFactory;
import com.zero.tools.resource.data.value.TypedValue;
import com.zero.tools.resource.exception.ResourceException;
import com.zero.tools.resource.util.DataUtil;
import com.zero.tools.resource.util.Duo;
import com.zero.tools.resource.data.ResConfigFlags;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class ARSCDecoder {
    private int size;
    private int stringBlockOffset;
    private int stringBlockLength;
    private int packageCount;


    public ARSCDecoder(ByteBuffer buffer, ResTable resTable, boolean storeFlagsOffsets, boolean keepBroken) {
        if (storeFlagsOffsets) {
            mFlagsOffsets = new ArrayList<FlagsOffset>();
        } else {
            mFlagsOffsets = null;
        }
        // We need to explicitly cast to DataInput as otherwise the constructor is ambiguous.
        // We choose DataInput instead of InputStream as ExtDataInput wraps an InputStream in
        // a DataInputStream which is big-endian and ignores the little-endian behavior.
        mIn = buffer;
        mResTable = resTable;
        mKeepBroken = keepBroken;
        size = buffer.limit();
    }

    public ByteBuffer write(Map<String, String> map) throws IOException {
        mIn.position(0);
        StringBlock block = mTableStrings.newStringBlock(map);
        int offset = block.getChunkSize()-mTableStrings.getChunkSize();
        Header header = mHeader.newHeader(mHeader.chunkSize+offset);
        System.out.println("---------"+mHeader.chunkSize+offset);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[header.chunkSize]).order(ByteOrder.LITTLE_ENDIAN);
        header.write(buffer);
        buffer.putInt(packageCount);
        block.write(buffer);
        writeExceptStringBlock(buffer);
        return buffer;
    }
    private void writeExceptStringBlock(ByteBuffer buffer){
        int offset = stringBlockOffset+stringBlockLength;
        mIn.position(offset);
        buffer.put(mIn);
    }

    public void readStringBlock() throws IOException {
        mIn.position(0);
        nextChunkCheckType(Header.TYPE_TABLE);
        packageCount = mIn.getInt();
        stringBlockOffset = mIn.position();
        mTableStrings = StringBlock.read(mIn);
        stringBlockLength = mIn.position() - stringBlockOffset;
    }

    public StringBlock getTableStrings() {
        return mTableStrings;
    }

    public ResPackage[] readTableHeader() throws IOException, ResourceException {
        nextChunkCheckType(Header.TYPE_TABLE);
        int packageCount = mIn.getInt();

        mTableStrings = StringBlock.read(mIn);
        ResPackage[] packages = new ResPackage[packageCount];

        nextChunk();
        for (int i = 0; i < packageCount; i++) {
            mTypeIdOffset = 0;
            packages[i] = readTablePackage();
        }
        return packages;
    }

    private ResPackage readTablePackage() throws IOException, ResourceException {
        checkChunkType(Header.TYPE_PACKAGE);
        int id = mIn.getInt();

        if (id == 0) {
            // This means we are dealing with a Library Package, we should just temporarily
            // set the packageId to the next available id . This will be set at runtime regardless, but
            // for Apktool's use we need a non-zero packageId.
            // AOSP indicates 0x02 is next, as 0x01 is system and 0x7F is private.
            id = 2;
            if (mResTable.getPackageOriginal() == null && mResTable.getPackageRenamed() == null) {
                mResTable.setSharedLibrary(true);
            }
        }

        String name = DataUtil.readNullEndedString(mIn, 128, true);
        /* typeStrings */
        mIn.getInt();
        /* lastPublicType */
        mIn.getInt();
        /* keyStrings */
        mIn.getInt();
        /* lastPublicKey */
        mIn.getInt();

        // TypeIdOffset was added platform_frameworks_base/@f90f2f8dc36e7243b85e0b6a7fd5a590893c827e
        // which is only in split/new applications.
        int splitHeaderSize = (2 + 2 + 4 + 4 + (2 * 128) + (4 * 5)); // short, short, int, int, char[128], int * 4
        if (mHeader.headerSize == splitHeaderSize) {
            mTypeIdOffset = mIn.getInt();
        }

        if (mTypeIdOffset > 0) {
            LOGGER.warning("Please report this application to Apktool for a fix: https://github.com/iBotPeaches/Apktool/issues/1728");
        }

        mTypeNames = StringBlock.read(mIn);
        mSpecNames = StringBlock.read(mIn);

        mResId = id << 24;
        mPkg = new ResPackage(mResTable, id, name);

        nextChunk();
        boolean flag = true;
        while (flag) {
            switch (mHeader.type) {
                case Header.TYPE_LIBRARY:
                    readLibraryType();
                    break;
                case Header.TYPE_SPEC_TYPE:
                    readTableTypeSpec();
                    break;
                default:
                    flag = false;
                    break;
            }
        }

        return mPkg;
    }

    private void readLibraryType() throws ResourceException, IOException {
        checkChunkType(Header.TYPE_LIBRARY);
        int libraryCount = mIn.getInt();

        int packageId;
        String packageName;

        for (int i = 0; i < libraryCount; i++) {
            packageId = mIn.getInt();
            packageName = DataUtil.readNullEndedString(mIn, 128, true);
            LOGGER.info(String.format("Decoding Shared Library (%s), pkgId: %d", packageName, packageId));
        }

        while (nextChunk().type == Header.TYPE_TYPE) {
            readTableTypeSpec();
        }
    }

    private void readTableTypeSpec() throws ResourceException, IOException {
        mTypeSpec = readSingleTableTypeSpec();
        addTypeSpec(mTypeSpec);

        int type = nextChunk().type;
        ResTypeSpec resTypeSpec;

        while (type == Header.TYPE_SPEC_TYPE) {
            resTypeSpec = readSingleTableTypeSpec();
            addTypeSpec(resTypeSpec);
            type = nextChunk().type;

            // We've detected sparse resources, lets record this so we can rebuild in that same format (sparse/not)
            // with aapt2. aapt1 will ignore this.
            if (!mResTable.getSparseResources()) {
                mResTable.setSparseResources(true);
            }
        }

        while (type == Header.TYPE_TYPE) {
            readTableType();

            // skip "TYPE 8 chunks" and/or padding data at the end of this chunk
            if (mIn.position() < mHeader.endPosition) {
                LOGGER.warning("Unknown data detected. Skipping: " + (mHeader.endPosition - mIn.position()) + " byte(s)");
                DataUtil.skipBytes(mIn, mHeader.endPosition - mIn.position());
            }

            type = nextChunk().type;

            addMissingResSpecs();
        }
    }

    private ResTypeSpec readSingleTableTypeSpec() throws ResourceException, IOException {
        checkChunkType(Header.TYPE_SPEC_TYPE);
        int id = ((0xFF) & mIn.get());
        DataUtil.skipBytes(mIn, 3);
        int entryCount = mIn.getInt();

        if (mFlagsOffsets != null) {
            mFlagsOffsets.add(new FlagsOffset(mIn.position(), entryCount));
        }

        /* flags */
        DataUtil.skipBytes(mIn, entryCount * 4);
        mTypeSpec = new ResTypeSpec(mTypeNames.getString(id - 1), mResTable, mPkg, id, entryCount);
        mPkg.addType(mTypeSpec);
        return mTypeSpec;
    }

    private ResType readTableType() throws IOException, ResourceException {
        checkChunkType(Header.TYPE_TYPE);
        int typeId = ((0xFF) & mIn.get()) - mTypeIdOffset;
        if (mResTypeSpecs.containsKey(typeId)) {
            mResId = (0xff000000 & mResId) | mResTypeSpecs.get(typeId).getId() << 16;
            mTypeSpec = mResTypeSpecs.get(typeId);
        }

        int typeFlags = mIn.get();
        /* reserved */
        mIn.getShort();
        int entryCount = mIn.getInt();
        int entriesStart = mIn.getInt();
        mMissingResSpecs = new boolean[entryCount];
        Arrays.fill(mMissingResSpecs, true);

        ResConfigFlags flags = readConfigFlags();
        int position = (mHeader.startPosition + entriesStart) - (entryCount * 4);

        // For some APKs there is a disconnect between the reported size of Configs
        // If we find a mismatch skip those bytes.
        if (position != mIn.position()) {
            LOGGER.warning("Invalid data detected. Skipping: " + (position - mIn.position()) + " byte(s)");
            mIn.position(position);
        }

        if (typeFlags == 1) {
            LOGGER.info("Sparse type flags detected: " + mTypeSpec.getName());
        }
        int[] entryOffsets = DataUtil.readIntArray(mIn, entryCount);

        if (flags.isInvalid) {
            String resName = mTypeSpec.getName() + flags.getQualifiers();
            if (mKeepBroken) {
                LOGGER.warning("Invalid config flags detected: " + resName);
            } else {
                LOGGER.warning("Invalid config flags detected. Dropping resources: " + resName);
            }
        }

        mType = flags.isInvalid && !mKeepBroken ? null : mPkg.getOrCreateConfig(flags);
        HashMap<Integer, EntryData> offsetsToEntryData = new HashMap<Integer, EntryData>();

        for (int offset : entryOffsets) {
            if (offset == -1 || offsetsToEntryData.containsKey(offset)) {
                continue;
            }

            offsetsToEntryData.put(offset, readEntryData());
        }

        for (int i = 0; i < entryOffsets.length; i++) {
            if (entryOffsets[i] != -1) {
                mMissingResSpecs[i] = false;
                mResId = (mResId & 0xffff0000) | i;
                EntryData entryData = offsetsToEntryData.get(entryOffsets[i]);
                readEntry(entryData);
            }
        }

        return mType;
    }


    private EntryData readEntryData() throws IOException, ResourceException {
        short size = mIn.getShort();
        if (size < 0) {
            throw new ResourceException("Entry size is under 0 bytes.");
        }

        short flags = mIn.getShort();
        int specNamesId = mIn.getInt();
        ResValue value = (flags & ENTRY_FLAG_COMPLEX) == 0 ? readValue() : readComplexEntry();
        EntryData entryData = new EntryData();
        entryData.mFlags = flags;
        entryData.mSpecNamesId = specNamesId;
        entryData.mValue = value;
        return entryData;
    }

    private void readEntry(EntryData entryData) throws ResourceException {
        int specNamesId = entryData.mSpecNamesId;
        ResValue value = entryData.mValue;

        if (mTypeSpec.isString() && value instanceof ResFileValue) {
            value = new ResStringValue(value.toString(), ((ResFileValue) value).getRawIntValue());
        }
        if (mType == null) {
            return;
        }

        ResID resId = new ResID(mResId);
        ResResSpec spec;
        if (mPkg.hasResSpec(resId)) {
            spec = mPkg.getResSpec(resId);

            if (spec.isDummyResSpec()) {
                removeResSpec(spec);

                spec = new ResResSpec(resId, mSpecNames.getString(specNamesId), mPkg, mTypeSpec);
                mPkg.addResSpec(spec);
                mTypeSpec.addResSpec(spec);
            }
        } else {
            spec = new ResResSpec(resId, mSpecNames.getString(specNamesId), mPkg, mTypeSpec);
            mPkg.addResSpec(spec);
            mTypeSpec.addResSpec(spec);
        }
        ResResource res = new ResResource(mType, spec, value);

        try {
            mType.addResource(res);
            spec.addResource(res);
        } catch (ResourceException ex) {
            if (mKeepBroken) {
                mType.addResource(res, true);
                spec.addResource(res, true);
                LOGGER.warning(String.format("Duplicate Resource Detected. Ignoring duplicate: %s", res.toString()));
            } else {
                throw ex;
            }
        }
    }

    private ResBagValue readComplexEntry() throws IOException, ResourceException {
        int parent = mIn.getInt();
        int count = mIn.getInt();

        ResValueFactory factory = mPkg.getValueFactory();
        Duo<Integer, ResScalarValue>[] items = new Duo[count];
        ResIntBasedValue resValue;
        int resId;

        for (int i = 0; i < count; i++) {
            resId = mIn.getInt();
            resValue = readValue();

            if (resValue instanceof ResScalarValue) {
                items[i] = new Duo<Integer, ResScalarValue>(resId, (ResScalarValue) resValue);
            } else {
                resValue = new ResStringValue(resValue.toString(), resValue.getRawIntValue());
                items[i] = new Duo<Integer, ResScalarValue>(resId, (ResScalarValue) resValue);
            }
        }

        return factory.bagFactory(parent, items, mTypeSpec);
    }

    private ResIntBasedValue readValue() throws IOException, ResourceException {
        /* size */
        DataUtil.skipCheckShort(mIn, (short) 8);
        /* zero */
        DataUtil.skipCheckByte(mIn, (byte) 0);
        byte type = mIn.get();
        int data = mIn.getInt();

        return type == TypedValue.TYPE_STRING
                ? mPkg.getValueFactory().factory(mTableStrings.getHTML(data), data)
                : mPkg.getValueFactory().factory(type, data, null);
    }

    private ResConfigFlags readConfigFlags() throws IOException, ResourceException {
        int size = mIn.getInt();
        int read = 28;

        if (size < 28) {
            throw new ResourceException("Config size < 28");
        }

        boolean isInvalid = false;

        short mcc = mIn.getShort();
        short mnc = mIn.getShort();

        char[] language = this.unpackLanguageOrRegion(mIn.get(), mIn.get(), 'a');
        char[] country = this.unpackLanguageOrRegion(mIn.get(), mIn.get(), '0');

        byte orientation = mIn.get();
        byte touchscreen = mIn.get();

        int density = 0xFFFF & mIn.getShort();

        byte keyboard = mIn.get();
        byte navigation = mIn.get();
        byte inputFlags = mIn.get();
        /* inputPad0 */
        mIn.get();

        short screenWidth = mIn.getShort();
        short screenHeight = mIn.getShort();

        short sdkVersion = mIn.getShort();
        /* minorVersion, now must always be 0 */
        mIn.getShort();

        byte screenLayout = 0;
        byte uiMode = 0;
        short smallestScreenWidthDp = 0;
        if (size >= 32) {
            screenLayout = mIn.get();
            uiMode = mIn.get();
            smallestScreenWidthDp = mIn.getShort();
            read = 32;
        }

        short screenWidthDp = 0;
        short screenHeightDp = 0;
        if (size >= 36) {
            screenWidthDp = mIn.getShort();
            screenHeightDp = mIn.getShort();
            read = 36;
        }

        char[] localeScript = null;
        char[] localeVariant = null;
        if (size >= 48) {
            localeScript = readScriptOrVariantChar(4).toCharArray();
            localeVariant = readScriptOrVariantChar(8).toCharArray();
            read = 48;
        }

        byte screenLayout2 = 0;
        byte colorMode = 0;
        if (size >= 52) {
            screenLayout2 = mIn.get();
            colorMode = mIn.get();
            mIn.getShort(); // reserved padding
            read = 52;
        }

        if (size >= 56) {
            mIn.getInt();
            read = 56;
        }

        int exceedingSize = size - KNOWN_CONFIG_BYTES;
        if (exceedingSize > 0) {
            byte[] buf = new byte[exceedingSize];
            read += exceedingSize;
            mIn.get(buf);
            BigInteger exceedingBI = new BigInteger(1, buf);

            if (exceedingBI.equals(BigInteger.ZERO)) {
                LOGGER.fine(String
                        .format("Config flags size > %d, but exceeding bytes are all zero, so it should be ok.",
                                KNOWN_CONFIG_BYTES));
            } else {
                LOGGER.warning(String.format("Config flags size > %d. Size = %d. Exceeding bytes: 0x%X.",
                        KNOWN_CONFIG_BYTES, size, exceedingBI));
                isInvalid = true;
            }
        }

        int remainingSize = size - read;
        if (remainingSize > 0) {
            mIn.position(mIn.position() + remainingSize);
        }

        return new ResConfigFlags(mcc, mnc, language, country,
                orientation, touchscreen, density, keyboard, navigation,
                inputFlags, screenWidth, screenHeight, sdkVersion,
                screenLayout, uiMode, smallestScreenWidthDp, screenWidthDp,
                screenHeightDp, localeScript, localeVariant, screenLayout2,
                colorMode, isInvalid, size);
    }

    private char[] unpackLanguageOrRegion(byte in0, byte in1, char base) {
        // check high bit, if so we have a packed 3 letter code
        if (((in0 >> 7) & 1) == 1) {
            int first = in1 & 0x1F;
            int second = ((in1 & 0xE0) >> 5) + ((in0 & 0x03) << 3);
            int third = (in0 & 0x7C) >> 2;

            // since this function handles languages & regions, we add the value(s) to the base char
            // which is usually 'a' or '0' depending on language or region.
            return new char[]{(char) (first + base), (char) (second + base), (char) (third + base)};
        }
        return new char[]{(char) in0, (char) in1};
    }

    private String readScriptOrVariantChar(int length) throws IOException {
        StringBuilder string = new StringBuilder(16);

        while (length-- != 0) {
            short ch = mIn.get();
            if (ch == 0) {
                break;
            }
            string.append((char) ch);
        }
        mIn.position(mIn.position() + length);

        return string.toString();
    }

    private void addTypeSpec(ResTypeSpec resTypeSpec) {
        mResTypeSpecs.put(resTypeSpec.getId(), resTypeSpec);
    }

    private void addMissingResSpecs() throws ResourceException {
        int resId = mResId & 0xffff0000;

        for (int i = 0; i < mMissingResSpecs.length; i++) {
            if (!mMissingResSpecs[i]) {
                continue;
            }

            ResResSpec spec = new ResResSpec(new ResID(resId | i), "APKTOOL_DUMMY_" + Integer.toHexString(i), mPkg, mTypeSpec);

            // If we already have this resID dont add it again.
            if (!mPkg.hasResSpec(new ResID(resId | i))) {
                mPkg.addResSpec(spec);
                mTypeSpec.addResSpec(spec);

                if (mType == null) {
                    mType = mPkg.getOrCreateConfig(new ResConfigFlags());
                }

                // We are going to make dummy attributes a null reference (@null) now instead of a boolean false.
                // This is because aapt2 is much more strict when it comes to what we can put in an application.
                ResValue value = new ResReferenceValue(mPkg, 0, "");

                ResResource res = new ResResource(mType, spec, value);
                mType.addResource(res);
                spec.addResource(res);
            }
        }
    }

    private void removeResSpec(ResResSpec spec) throws ResourceException {
        if (mPkg.hasResSpec(spec.getId())) {
            mPkg.removeResSpec(spec);
            mTypeSpec.removeResSpec(spec);
        }
    }

    private Header nextChunk() throws IOException {
        return mHeader = Header.read(mIn);
    }

    private void checkChunkType(int expectedType) throws ResourceException {
        if (mHeader.type != expectedType) {
            throw new ResourceException(String.format("Invalid chunk type: expected=0x%08x, got=0x%08x",
                    expectedType, mHeader.type));
        }
    }

    private void nextChunkCheckType(int expectedType) throws IOException, ResourceException {
        nextChunk();
        checkChunkType(expectedType);
    }

    private final ByteBuffer mIn;
    private final ResTable mResTable;
    private final List<FlagsOffset> mFlagsOffsets;
    private final boolean mKeepBroken;

    private Header mHeader;
    private StringBlock mTableStrings;
    private StringBlock mTypeNames;
    private StringBlock mSpecNames;
    private ResPackage mPkg;
    private ResTypeSpec mTypeSpec;
    private ResType mType;
    private int mResId;
    private int mTypeIdOffset = 0;
    private boolean[] mMissingResSpecs;
    private HashMap<Integer, ResTypeSpec> mResTypeSpecs = new HashMap<>();

    private final static short ENTRY_FLAG_COMPLEX = 0x0001;
    private final static short ENTRY_FLAG_PUBLIC = 0x0002;
    private final static short ENTRY_FLAG_WEAK = 0x0004;

    public static class Header {
        public final short type;
        public final int headerSize;
        public final int chunkSize;
        public final int startPosition;
        public final int endPosition;

        public Header(short type, int headerSize, int chunkSize, int headerStart) {
            this.type = type;
            this.headerSize = headerSize;
            this.chunkSize = chunkSize;
            this.startPosition = headerStart;
            this.endPosition = headerStart + chunkSize;
        }

        public void write(ByteBuffer buffer) {
            buffer.putShort(type);
            buffer.putShort((short) headerSize);
            buffer.putInt(chunkSize);
        }

        public Header newHeader(int size) {
            return new Header(type, headerSize, size, startPosition);
        }

        public static Header read(ByteBuffer in) throws IOException {
            short type;
            int start = in.position();
            try {
                type = in.getShort();
            } catch (BufferUnderflowException ex) {
                return new Header(TYPE_NONE, 0, 0, in.position());
            }
            return new Header(type, in.getShort(), in.getInt(), start);
        }

        public final static short TYPE_NONE = -1, TYPE_TABLE = 0x0002,
                TYPE_PACKAGE = 0x0200, TYPE_TYPE = 0x0201, TYPE_SPEC_TYPE = 0x0202, TYPE_LIBRARY = 0x0203;
    }

    public static class FlagsOffset {
        public final int offset;
        public final int count;

        public FlagsOffset(int offset, int count) {
            this.offset = offset;
            this.count = count;
        }
    }

    private class EntryData {
        public short mFlags;
        public int mSpecNamesId;
        public ResValue mValue;
    }

    private static final Logger LOGGER = Logger.getLogger(ARSCDecoder.class.getName());
    private static final int KNOWN_CONFIG_BYTES = 56;

    public static class ARSCData {

        public ARSCData(ResPackage[] packages, FlagsOffset[] flagsOffsets, ResTable resTable) {
            mPackages = packages;
            mFlagsOffsets = flagsOffsets;
            mResTable = resTable;
        }

        public FlagsOffset[] getFlagsOffsets() {
            return mFlagsOffsets;
        }

        public ResPackage[] getPackages() {
            return mPackages;
        }

        public ResPackage getOnePackage() throws ResourceException {
            if (mPackages.length <= 0) {
                throw new ResourceException("Arsc file contains zero packages");
            } else if (mPackages.length != 1) {
                int id = findPackageWithMostResSpecs();
                LOGGER.info("Arsc file contains multiple packages. Using package "
                        + mPackages[id].getName() + " as default.");

                return mPackages[id];
            }
            return mPackages[0];
        }

        public int findPackageWithMostResSpecs() {
            int count = mPackages[0].getResSpecCount();
            int id = 0;

            for (int i = 0; i < mPackages.length; i++) {
                if (mPackages[i].getResSpecCount() >= count) {
                    count = mPackages[i].getResSpecCount();
                    id = i;
                }
            }
            return id;
        }

        public ResTable getResTable() {
            return mResTable;
        }

        private final ResPackage[] mPackages;
        private final FlagsOffset[] mFlagsOffsets;
        private final ResTable mResTable;
    }

//    public void write(OutputStream os) throws IOException {
//        // 二进制文件输出流
//        LEDataOutputStream lmOut = new LEDataOutputStream(os);
//        // 先将字符串数据写入到一个临时的流中
//        ByteArrayOutputStream mStrings = mTableStrings.writeString(mTableStrings.getList());
//        /////////////////////////////////////////////////////////////////////////////////////////
//        // 这里才正式开始写arsc文件
//        // 写入一个short型数据，标识着该文件的种类
//        lmOut.writeShort(stringsHeader.type);
//        // 写入两个未知字节
//        lmOut.writeByte(stringsHeader.byte1);
//        lmOut.writeByte(stringsHeader.byte2);
//        // 写入chunkSize
//        lmOut.writeInt(stringsHeader.chunkSize + (mStrings.size() - mTableStrings.m_strings.length));
//        // 写入包的数量
//        lmOut.writeInt(packageCount);
//        // 写入字符串
//        mTableStrings.writeFully(lmOut, mStrings);
//        // 二进制输入流跳过size1-size2 个字节，目的是我们只需修改前面的包含有字符串的数据，而后面的数据，则从文件中直接复制
//        mIn.reset();
//        mIn.skipBytes(size1 - size2);
//        byte[] buffer = new byte[1024];
//        int count;
//        // 将剩余内容写入文件
//        while ((count = mIn.read(buffer, 0, buffer.length)) != -1) {
//            lmOut.writeFully(buffer, 0, count);
//        }
//        lmOut.close();
//    }

}
