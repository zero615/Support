package com.zero.tools.resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

public class XMLDecoder {
    private static final int AXML_CHUNK_TYPE = 0x00080003;
    private ByteBuffer mReader;
    private int size;
    private int type;
    private StringBlock stringBlock;
    private int stringBlockOffset;
    private int stringBlockLength;

    public XMLDecoder(ByteBuffer buffer) {
        this.mReader = buffer;
    }

    public StringBlock getStringBlock() {
        return stringBlock;
    }

    public ByteBuffer write(Map<String, String> map) throws IOException {
        mReader.position(0);
        StringBlock block = stringBlock.newStringBlock(map);
        int offset = block.getChunkSize() - stringBlock.getChunkSize();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[size + offset]).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(type);
        buffer.putInt(size + offset);
        block.write(buffer);
        writeExceptStringBlock(buffer);
        return buffer;
    }

    private void writeExceptStringBlock(ByteBuffer buffer) {
        int offset = stringBlockOffset + stringBlockLength;
        mReader.position(offset);
        buffer.put(mReader);
    }

    public void readStringBlock() throws IOException {
        mReader.position(0);
        this.type = mReader.getInt();
        checkChunk(type);
        size = mReader.getInt();
        stringBlockOffset = mReader.position();
        stringBlock = StringBlock.read(mReader);
        stringBlockLength = mReader.position() - stringBlockOffset;
    }

    private void checkChunk(int type) throws IOException {
        if (type != XMLDecoder.AXML_CHUNK_TYPE)
            throw new IOException(String.format("Invalid chunk type: expected=0x%08x, got=0x%08x",
                    XMLDecoder.AXML_CHUNK_TYPE, type));
    }

}
