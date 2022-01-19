package org.geotoolkit.internal.image.jai;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.Dimension;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;

/**
 * Drastically simplified copy of JAI RawImageInputStream. Done temporarily to remove dependency to jai_imageio artifact.
 */
public class RawImageInputStream implements ImageInputStream {
    private ImageInputStream source;
    private ImageTypeSpecifier type;
    private long[] imageOffsets;
    private Dimension[] imageDimensions;

    public RawImageInputStream(ImageInputStream source, ImageTypeSpecifier type, long[] imageOffsets, Dimension[] imageDimensions) {
        if (imageOffsets != null && imageDimensions != null && imageOffsets.length == imageDimensions.length) {
            this.source = source;
            this.type = type;
            this.imageOffsets = imageOffsets;
            this.imageDimensions = imageDimensions;
        } else {
            throw new IllegalArgumentException("Invalid arguments for raw image input stream");
        }
    }

    public RawImageInputStream(ImageInputStream source, SampleModel sampleModel, long[] imageOffsets, Dimension[] imageDimensions) {
        if (imageOffsets != null && imageDimensions != null && imageOffsets.length == imageDimensions.length) {
            this.source = source;
            // TODO: check if we can avoid that
            ColorModel colorModel = ImageUtil.createColorModel(sampleModel);
            if (colorModel == null) {
                throw new IllegalArgumentException("Cannot create color model");
            } else {
                this.type = new ImageTypeSpecifier(colorModel, sampleModel);
                this.imageOffsets = imageOffsets;
                this.imageDimensions = imageDimensions;
            }
        } else {
            throw new IllegalArgumentException("Raw image input stream arguments are invalid");
        }
    }

    public ImageTypeSpecifier getImageType() {
        return this.type;
    }

    public long getImageOffset(int imageIndex) {
        if (imageIndex >= 0 && imageIndex < this.imageOffsets.length) {
            return this.imageOffsets[imageIndex];
        } else {
            throw new IllegalArgumentException("Given image index is invalid");
        }
    }

    public Dimension getImageDimension(int imageIndex) {
        if (imageIndex >= 0 && imageIndex < this.imageOffsets.length) {
            return this.imageDimensions[imageIndex];
        } else {
            throw new IllegalArgumentException("Given image index is invalid");
        }
    }

    public int getNumImages() {
        return this.imageOffsets.length;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.source.setByteOrder(byteOrder);
    }

    public ByteOrder getByteOrder() {
        return this.source.getByteOrder();
    }

    public int read() throws IOException {
        return this.source.read();
    }

    public int read(byte[] b) throws IOException {
        return this.source.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return this.source.read(b, off, len);
    }

    public void readBytes(IIOByteBuffer buf, int len) throws IOException {
        this.source.readBytes(buf, len);
    }

    public boolean readBoolean() throws IOException {
        return this.source.readBoolean();
    }

    public byte readByte() throws IOException {
        return this.source.readByte();
    }

    public int readUnsignedByte() throws IOException {
        return this.source.readUnsignedByte();
    }

    public short readShort() throws IOException {
        return this.source.readShort();
    }

    public int readUnsignedShort() throws IOException {
        return this.source.readUnsignedShort();
    }

    public char readChar() throws IOException {
        return this.source.readChar();
    }

    public int readInt() throws IOException {
        return this.source.readInt();
    }

    public long readUnsignedInt() throws IOException {
        return this.source.readUnsignedInt();
    }

    public long readLong() throws IOException {
        return this.source.readLong();
    }

    public float readFloat() throws IOException {
        return this.source.readFloat();
    }

    public double readDouble() throws IOException {
        return this.source.readDouble();
    }

    public String readLine() throws IOException {
        return this.source.readLine();
    }

    public String readUTF() throws IOException {
        return this.source.readUTF();
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        this.source.readFully(b, off, len);
    }

    public void readFully(byte[] b) throws IOException {
        this.source.readFully(b);
    }

    public void readFully(short[] s, int off, int len) throws IOException {
        this.source.readFully(s, off, len);
    }

    public void readFully(char[] c, int off, int len) throws IOException {
        this.source.readFully(c, off, len);
    }

    public void readFully(int[] i, int off, int len) throws IOException {
        this.source.readFully(i, off, len);
    }

    public void readFully(long[] l, int off, int len) throws IOException {
        this.source.readFully(l, off, len);
    }

    public void readFully(float[] f, int off, int len) throws IOException {
        this.source.readFully(f, off, len);
    }

    public void readFully(double[] d, int off, int len) throws IOException {
        this.source.readFully(d, off, len);
    }

    public long getStreamPosition() throws IOException {
        return this.source.getStreamPosition();
    }

    public int getBitOffset() throws IOException {
        return this.source.getBitOffset();
    }

    public void setBitOffset(int bitOffset) throws IOException {
        this.source.setBitOffset(bitOffset);
    }

    public int readBit() throws IOException {
        return this.source.readBit();
    }

    public long readBits(int numBits) throws IOException {
        return this.source.readBits(numBits);
    }

    public long length() throws IOException {
        return this.source.length();
    }

    public int skipBytes(int n) throws IOException {
        return this.source.skipBytes(n);
    }

    public long skipBytes(long n) throws IOException {
        return this.source.skipBytes(n);
    }

    public void seek(long pos) throws IOException {
        this.source.seek(pos);
    }

    public void mark() {
        this.source.mark();
    }

    public void reset() throws IOException {
        this.source.reset();
    }

    public void flushBefore(long pos) throws IOException {
        this.source.flushBefore(pos);
    }

    public void flush() throws IOException {
        this.source.flush();
    }

    public long getFlushedPosition() {
        return this.source.getFlushedPosition();
    }

    public boolean isCached() {
        return this.source.isCached();
    }

    public boolean isCachedMemory() {
        return this.source.isCachedMemory();
    }

    public boolean isCachedFile() {
        return this.source.isCachedFile();
    }

    public void close() throws IOException {
        this.source.close();
    }
}

