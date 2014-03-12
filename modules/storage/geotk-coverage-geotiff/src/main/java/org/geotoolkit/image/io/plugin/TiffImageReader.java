/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.image.io.plugin;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;

import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferDouble;
import java.awt.image.IndexColorModel;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.internal.storage.ChannelImageInputStream;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.SampleModels;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.lang.SystemOverride;
import org.geotoolkit.metadata.geotiff.GeoTiffMetaDataReader;
import org.geotoolkit.resources.Errors;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;


/**
 * An image reader for uncompressed TIFF files or RGB images. This image reader duplicates the works
 * performed by the reader provided in <cite>Image I/O extension for Java Advanced Imaging</cite>,
 * but is specialized to the specific case of uncompressed files. For such case, this
 * {@code RawTiffImageReader} is faster.
 * <p>
 * {@code RawTiffImageReader} has the following restrictions:
 * <p>
 * <ul>
 *   <li>Can read only from files as a {@link File} or {@link String} input objects
 *       (the standard {@link javax.imageio.stream.ImageInputStream} is not supported).</li>
 *   <li>Can read only uncompressed (RAW) images
 *       (the JPEG and LZW compressions are not supported).</li>
 *   <li>Can read only tiled images (this restriction may be removed in a future version).</li>
 *   <li>Color model must be RGB (this restriction may be removed in a future version).</li>
 *   <li>Components are stored in "chunky" format, not planar (this restriction may be removed
 *       in a future version).</li>
 *   <li>The source and target bands can not be modified (this restriction may be removed
 *       in a future version).</li>
 *   <li>Metadata are ignored (this restriction may be removed in a future version).</li>
 * </ul>
 * <p>
 * Because of the above-cited restrictions, this reader registers itself only after the JAI
 * readers (unless otherwise specified). Users wanting this reader should request for it
 * explicitly, for example as below:
 *
 * {@preformat java
 *     ImageReaderSpi spi = IIORegistry.getDefaultInstance().getServiceProviderByClass(RawTiffImageReader.class);
 *     ImageReader reader = spi.createReaderInstance();
 * }
 *
 * This image reader can also process <cite>Big TIFF</cite> images.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Remi Marechal       (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
public class TiffImageReader extends SpatialImageReader {
    /**
     * Typical size of a <cite>Image File Directory</cite> (IFD). This is only a hint;
     * FIDs can safely be larger than that. The only purpose of this value is to reduce
     * the amount of unneeded data to read from the disk.
     */
    private static final int IFD_SIZE = 1024;

    /**
     * Size of data structures in standard TIFF files ({@code SIZE_*}) and in big TIFF files
     * ({@code SIZE_BIG_*}). In standard TIFF, the size of structures for counting the number
     * of records or the file offsets vary, while in big TIFF everything is 64 bits.
     */
    private static final int
            SIZE_ENTRY = 12,                     SIZE_BIG_ENTRY = 20,
            SIZE_SHORT =   Short.SIZE/Byte.SIZE, SIZE_BIG_SHORT = Long.SIZE/Byte.SIZE,
            SIZE_INT   = Integer.SIZE/Byte.SIZE, SIZE_BIG_INT   = Long.SIZE/Byte.SIZE;

    /**
     * Types supported by this reader. The type is the short at offset 2 in the directory entry.
     */
    private static final short // 1, 2, 3, 4, 5 unsigned 0 -> 2^nbit  6, 7, 8, 9, 10 signed -2^(nbit-1) -> 2^(nbit-1) 11, 12 signed.
            TYPE_UBYTE     = 1,
            TYPE_ASCII     = 2,
            TYPE_USHORT    = 3,
            TYPE_UINT      = 4,
            TYPE_URATIONAL = 5, // unsigned Integer / unsigned Integer
            TYPE_BYTE      = 6,
            // type 7 is undefined
            TYPE_SHORT     = 8,
            TYPE_INT       = 9,
            TYPE_RATIONAL  = 10, // signed Integer / signed Integer
            TYPE_FLOAT     = 11,
            TYPE_DOUBLE    = 12,
            TYPE_IFD       = 13, // IFD is like UINT.
            TYPE_ULONG     = 16,
            TYPE_LONG      = 17,
            TYPE_IFD8      = 18; // IFD is like ULONG.

    /**
     * The size of each type in bytes, or 0 if unknown.
     */
    private static final int[] TYPE_SIZE = new int[19];

    static {
        int[] size = TYPE_SIZE;
        size[TYPE_BYTE]  = size[TYPE_UBYTE] = size[TYPE_ASCII] =    Byte.SIZE        / Byte.SIZE;
        size[TYPE_SHORT] = size[TYPE_USHORT]                   =   Short.SIZE        / Byte.SIZE;
        size[TYPE_INT]   = size[TYPE_UINT]  = size[TYPE_IFD]   = Integer.SIZE        / Byte.SIZE;
        size[TYPE_LONG]  = size[TYPE_ULONG] = size[TYPE_IFD8]  =    Long.SIZE        / Byte.SIZE;
        size[TYPE_FLOAT]                                       =   Float.SIZE        / Byte.SIZE;
        size[TYPE_DOUBLE]                                      =  Double.SIZE        / Byte.SIZE;
        size[TYPE_URATIONAL] = size[TYPE_RATIONAL]             = (Integer.SIZE << 1) / Byte.SIZE; //rational = Integer / Integer. 2 Integer values red.
    }

    private final static short LZW_CLEAR_CODE = 256;
    private final static short LZW_EOI_CODE   = 257;
//    private ImageInputStream inputLZW;
    
//    private int currentLZWCodeLength;
//    private Map<Short, byte[]> lzwTab;
//    private byte[][] lzwTab;

    /**
     * The channel to the TIFF file. Will be created from the {@linkplain #input} when first needed.
     */

    private FileChannel channel;

    /**
     * The buffer for reading blocks of data.
     */
    private final ByteBuffer buffer;

    /**
     * Position in the {@linkplain #channel} of the first byte in the {@linkplain #buffer}.
     */
    private long positionBuffer;

    /**
     * Current position of the file channel. Stored for avoiding multiple calls to
     * {@link FileChannel#position(long)} while reading consecutive block of data.
     */
    private long filePosition;

    /**
     * Positions of each <cite>Image File Directory</cite> (IFD) in this file. The positions are
     * fetched when first needed. The number of valid positions is either {@link #countIFD}, or
     * the length of the {@code positionIFD} array if {@code countIFD} is negative.
     */
    private long[] positionIFD;

    /**
     * Number of valid elements in {@link #positionIFD}, or -1 if the {@code positionIFD}
     * array is now completed. In the later case, the length of {@code positionIFD} is the
     * number of images.
     */
    private int countIFD;

    /**
     * {@code true} if the file uses the BigTIFF format, or (@code false} for standard TIFF.
     */
    private boolean isBigTIFF;

    /**
     * Index of the current image, or -1 if none.
     */
    private int currentImage;

    /**
     * Information from the <cite>Directory Entries</cite> for the
     * {@linkplain #currentImage current image}.
     */
    private int imageWidth, imageHeight, tileWidth, tileHeight, samplesPerPixel;

    /**
     * Information from the <cite>Directory Entries</cite> for the
     * {@linkplain #currentImage current image}.
     */
    private long[] stripOffsets, stripByteCounts;

    /**
     * How image rows are cover by strip.
     */
    private int rowsPerStrip;

    /**
     * Number of bits per samples. This is (8,8,8) for RGB images.
     */
    private long[] bitsPerSample;

    /**
     * The offsets of each tiles in the current image.
     */
    private long[] tileOffsets;

    /**
     * The cached return value of {@link #getRawDataType(int)} for the current image.
     */
    private ImageTypeSpecifier rawImageType;

    /**
     * Metadata root Node for each image.
     */
    private IIOMetadataNode[] roots;

    /**
     * Compression type of current selected image.
     */
    private int compression;

    /**
     * Map table which contain all tiff properties from all images.
     */
    private Map<Integer, Map>[] metaHeads;

    /**
     * Map which contain all tiff properties of the current selected image.
     */
    private Map<Integer, Map> headProperties;

    /**
     * Define size in {@code Byte} of offset in each Tiff tag.<br/>
     * In tiff specification : {@link Integer#SIZE} / {@link Byte#SIZE}.<br/>
     * In big tiff specification : {@link Long#SIZE} / {@link Byte#SIZE}.
     */
    private int offsetSize;
    
    FileInputStream fIStImageReader;
    
    Rectangle srcRegion;
    Rectangle dstRegion;
    
    private boolean reverseByte;
    

    /**
     * Creates a new reader.
     *
     * @param provider The provider, or {@code null} if none.
     */
    public TiffImageReader(final TiffImageReader.Spi provider) {
        super((provider != null) ? provider : new RawTiffImageReader.Spi());
        buffer       = ByteBuffer.allocate(8196);
        positionIFD  = new long[4];
        metaHeads    = new Map[4];
        roots        = new IIOMetadataNode[4];
        currentImage = -1;
        reverseByte  = false;
    }

    /**
     * Returns {@code true} since this image format places no inherent impediment on random access
     * to pixels. Actually, having easy random access is the whole point of uncompressed TIFF files
     * in many GIS infrastructures.
     *
     * @param  imageIndex The image index (ignored by this implementation).
     * @return Always {@code true} in this implementation.
     * @throws IOException If an I/O access was necessary and failed.
     */
    @Override
    public boolean isRandomAccessEasy(final int imageIndex) throws IOException {
        return true;
    }

    /**
     * {@inheritDoc }.
     * May return {@code null} if there are no metadatas.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if(imageIndex < 0){
            //stream metadata
            return super.createMetadata(imageIndex);
        }
        if (metaHeads[imageIndex] == null) {
            selectImage(imageIndex);
        }

        //-- if there are not geotiff tag return null
        boolean isGeotiff = false;
        for(int key : headProperties.keySet()) {
            if (key == 34735) { //-- if geotiff tag exist
                isGeotiff = true;
                break;
            }
        }

        if (!isGeotiff) return null;

        fillRootMetadataNode(imageIndex);
        final IIOMetadata metadata = new IIOTiffMetadata(roots[imageIndex]);
            final GeoTiffMetaDataReader metareader = new GeoTiffMetaDataReader(metadata);
        try {
            return metareader.readSpatialMetaData();
        } catch (NoSuchAuthorityCodeException ex) {
            throw new IOException(ex);
        } catch (FactoryException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Ensures that the channel is open. If the channel is already open, then this method
     * does nothing.
     *
     * @throws IllegalStateException if the input is not set.
     * @throws IOException If an error occurred while opening the channel.
     */
    private void open() throws IllegalStateException, IOException {

        if (channel == null) {
            if (input == null) {
                throw new IllegalStateException(error(Errors.Keys.NO_IMAGE_INPUT));
            }
            final FileInputStream in;
//            if (input instanceof String) {
//                in = new FileInputStream((String) input);
//            } else {
//                in = new FileInputStream((File) input);
//            }
//            channel = in.getChannel();
            if (input instanceof String) {
                in = new FileInputStream((String) input);
                fIStImageReader = new FileInputStream((String) input);
            } else {
                in = new FileInputStream((File) input);
                fIStImageReader = new FileInputStream((File) input);
            }
            channel = in.getChannel();
            //-- Closing the channel will close the input stream.
            buffer.clear();
            readFully(16, 1024); //-- Header size of Big TIFF (the standard header size is 8 bytes).
            final byte c = buffer.get();
            if (c != buffer.get()) {
                throw invalidFile("ByteOrder");
            }
            final ByteOrder order;
            if (c == 'M') {
                order = ByteOrder.BIG_ENDIAN;
            } else if (c == 'I') {
                order = ByteOrder.LITTLE_ENDIAN;
            } else {
                throw invalidFile("ByteOrder");
            }
            final short version = buffer.order(order).getShort(); 
            if (isBigTIFF = (version == 0x002B)) {
                if (buffer.getShort() != 8 || buffer.getShort() != 0) {
                    throw invalidFile("OffsetSize");
                }
            } else if (version != 0x002A) {
                throw invalidFile("MagicNumber");
            }
            offsetSize = ((isBigTIFF) ? Long.SIZE : Integer.SIZE) / Byte.SIZE;
            countIFD = 0;
            nextImageFileDirectory();
            currentImage = -1;
        }
    }

    /**
     * Sets the new buffer position and ensures that the buffer contains at least the given number
     * of bytes. Bytes will be read from the channel if needed. If there is not enough remaining
     * bytes in the channel, a {@link EOFException} is thrown.
     *
     * @param  position The channel position of the first byte to be read (if needed).
     * @param  min Minimal amount of bytes that the buffer shall contain.
     * @param  max Suggested maximal number of bytes. This is only a hint for reducing unneeded read.
     * @throws IOException If the given amount of bytes can not be read.
     */
    private void ensureBufferContains(long position, final int min, final int max) throws IOException {
        final long offset = position - positionBuffer;
        if (offset >= 0 && offset < buffer.limit()) {
            if (buffer.position((int) offset).remaining() < min) {
                final int valid = buffer.compact().position();
                if ((position += valid) != filePosition) {
                    channel.position(position);
                }
                readFully(min - valid, Math.max(min, max) - valid);
            }
        } else {
            buffer.clear();
            if (position != filePosition) {
//                try {
                    channel.position(position);
//                } catch (Exception ex) {
//                    System.out.println("pos de merde : "+position);
//                }
                
            }
            readFully(min, Math.max(min, max));
        }
    }

    /**
     * Reads at least the given minimal number of bytes (more bytes may be read), but no more
     * bytes than the given maximum. The {@linkplain #buffer} position is set to zero and its
     * limit is set to the number valid bytes.
     *
     * @param  min Minimal amount of bytes that the buffer shall contain.
     * @param  max Suggested maximal number of bytes. This is only a hint for reducing unneeded read.
     * @throws IOException If the given amount of bytes can not be read.
     */
    private void readFully(int min, int max) throws IOException {
        max += buffer.position();
        if (max < buffer.limit()) {
            buffer.limit(max);
        }
        while (min > 0) {
            assert buffer.hasRemaining();
            final int n = channel.read(buffer);
            if (n < 0) {
                throw new EOFException();
            }
            //-- reverse byte from buffer if fillOrder tag == 2 (buffer.position() - n) a buffer.position --// 
            if (reverseByte) reverseBytes(buffer, buffer.position() - n, buffer.position());
            min -= n;
        }
        positionBuffer = (filePosition = channel.position()) - buffer.position();
        buffer.rewind();
    }

    /**
     * Reverse <code>bits</code> sens of all byte from buffer between inclusive positionMin and exclusive positionMax.
     * 
     * @param buffer buffer where byte will be reverse.
     * @param positionMin first reverse byte position in buffer.
     * @param positionMax last exclusive reverse byte position in buffer.
     */
    private void reverseBytes(final ByteBuffer buffer, final int positionMin, final int positionMax) {
        buffer.position(positionMin);
        for (int bpos = positionMin; bpos < positionMax; bpos++) {
            final byte b = buffer.get();
            buffer.put(bpos, (byte) reverseByte(b));
        }
        assert buffer.position() == positionMax : "expected position after reverse byte = "+positionMax+" found : "+buffer.position();
    }
    
    /**
     * Reverse <code>bits</code> sens from 8 first Integer <code>bits</code>.
     * 
     * @param b <code>byte</code> which will be invert.
     * @return inverted <code>byte</code>.
     */
    private static int reverseByte(final int b) {
        return ((Integer.reverse(b) >>> 24));
    }
    
    /**
     * Reads the next bytes in the {@linkplain #buffer}, which must be the 32 or 64 bits
     * offset to the next <cite>Image File Directory</cite> (IFD). The offset is then stored
     * in the next free slot of {@link #positionIFD}.
     *
     * @return {@code true} if we found a new IFD, or {@code false} if there is no more IFD.
     */
    private boolean nextImageFileDirectory() {
        assert countIFD >= 0;
        final long position = readInt();//-- long if it's big tiff
        if (position != 0) {
            if (countIFD == positionIFD.length) {
                final int countIFD2 = countIFD << 1;
                positionIFD = Arrays.copyOf(positionIFD, Math.max(4, countIFD2));//-- head table
                metaHeads   = Arrays.copyOf(metaHeads,   Math.max(4, countIFD2));
                roots       = Arrays.copyOf(roots,       Math.max(4, countIFD2));
            }
            positionIFD[countIFD++] = position;
            return true;
        } else {
            positionIFD = ArraysExt.resize(positionIFD, countIFD);
            countIFD = -1;
            return false;
        }
    }

    /**
     * Reads the {@code int} or {@code long} value (depending if the file is
     * standard of big TIFF) at the current {@linkplain #buffer} position.
     *
     * @return The next integer.
     */
    private long readInt() {
        return isBigTIFF ? buffer.getLong() : buffer.getInt() & 0xFFFFFFFFL;
    }

    /**
     * Reads the {@code short} or {@code long} value (depending if the file is
     * standard of big TIFF) at the current {@linkplain #buffer} position.
     *
     * @return The next short.
     */
    private long readShort() {
        return isBigTIFF ? buffer.getLong() : buffer.getShort() & 0xFFFFL;
    }

    /**
     * Returns the number of images available from the current input file. This method
     * will scan the file the first time it is invoked with a {@code true} argument value.
     */
    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        open(); //-- Does nothing if already open.
        if (countIFD >= 0) { //-- Should never be 0 actually.
            if (!allowSearch) {
                return -1;
            }
            final int entrySize, shortSize, intSize;
            if (isBigTIFF) {
                entrySize = SIZE_BIG_ENTRY;
                shortSize = SIZE_BIG_SHORT;
                intSize   = SIZE_BIG_INT;
            } else {
                entrySize = SIZE_ENTRY;
                shortSize = SIZE_SHORT;
                intSize   = SIZE_INT;
            }
            do {
                long position = positionIFD[countIFD - 1];
                ensureBufferContains(position, shortSize, IFD_SIZE);
                final long n = readShort();
                position += shortSize;
                ensureBufferContains(position + n * entrySize, intSize, IFD_SIZE);
            } while (nextImageFileDirectory());
        }
        return positionIFD.length;
    }

    /**
     * Selects the image at the given index.
     *
     * @param  imageIndex  The index of the image to make the current one.
     * @throws IOException If an error occurred while reading the file.
     * @throws IndexOutOfBoundsException If the given image index is out of bounds.
     */
    private void selectImage(final int imageIndex) throws IOException, IndexOutOfBoundsException {
        if (imageIndex != currentImage) {
            open(); //-- Does nothing if already open.
            if (imageIndex >= minIndex) {
                final int entrySize, shortSize, intSize;
                if (isBigTIFF) {
                    entrySize = SIZE_BIG_ENTRY;
                    shortSize = SIZE_BIG_SHORT;
                    intSize   = SIZE_BIG_INT;
                } else {
                    entrySize = SIZE_ENTRY;
                    shortSize = SIZE_SHORT;
                    intSize   = SIZE_INT;
                }
                /*
                 * If the requested image if after the last image read,
                 * scan the file until we find the IFD location.
                 */
                if (countIFD >= 0) { //-- Should never be 0 actually.
                    int imageAhead = imageIndex - countIFD;
                    while (imageAhead >= 0) {
                        long position = positionIFD[countIFD - 1];
                        ensureBufferContains(position, shortSize, IFD_SIZE);
                        final long n = readShort();
                        position += shortSize;
                        ensureBufferContains(position + n * entrySize, intSize, IFD_SIZE);
                        if (!nextImageFileDirectory()) {
                            throw new IndexOutOfBoundsException(error(
                                    Errors.Keys.INDEX_OUT_OF_BOUNDS_1, imageIndex));
                        }
                        imageAhead--;
                    }
                }
                /*
                 * Read the Image File Directory (IFD) content.
                 */
                if (imageIndex < positionIFD.length) {
                    imageWidth      = -1;
                    imageHeight     = -1;
                    tileWidth       = -1;
                    tileHeight      = -1;
                    samplesPerPixel =  0;
                    bitsPerSample   = null;
                    tileOffsets     = null;
                    rawImageType    = null;

                    if (metaHeads[imageIndex] == null) {
                        metaHeads[imageIndex] = new HashMap<Integer, Map>();
                    }
                    headProperties = metaHeads[imageIndex];

                    final Collection<long[]> deferred = new ArrayList<>(4);
                    long position = positionIFD[imageIndex];
                    ensureBufferContains(position, shortSize + intSize, IFD_SIZE);
                    final long n = readShort();//-- n : tag number which define tiff image properties.
                    position += shortSize;
                    for (int i = 0; i < n; i++) {
                        ensureBufferContains(position, entrySize + intSize, IFD_SIZE);
                        parseDirectoryEntries(deferred);
                        position += entrySize;
                    }
                    /*
                     * Complete the arrays that needs further processing.
                     * Get the values that we lack.
                     */
                    readDeferredArrays(deferred.toArray(new long[deferred.size()][]));

                    final Map<String, Object> iwObj   = headProperties.get(ImageWidth);
                    final Map<String, Object> ihObj   = headProperties.get(ImageLength);
                    final Map<String, Object> isppObj = headProperties.get(SamplesPerPixel);
                    final Map<String, Object> ibpsObj = headProperties.get(BitsPerSample);

                    imageWidth      = (int) ((iwObj == null)   ? -1 : ((long[]) iwObj.get(ATT_VALUE))[0]);
                    imageHeight     = (int) ((ihObj == null)   ? -1 : ((long[]) ihObj.get(ATT_VALUE))[0]);
                    samplesPerPixel = (int) ((isppObj == null) ? -1 : ((long[]) isppObj.get(ATT_VALUE))[0]);
                    bitsPerSample   =  ((long[]) ibpsObj.get(ATT_VALUE));

                    final Map<String, Object> twObj   =  headProperties.get(TileWidth);
                    final Map<String, Object> thObj   =  headProperties.get(TileLength);
                    final Map<String, Object> toObj   =  headProperties.get(TileOffsets);

                    /*
                     * Declare the image as valid only if the mandatory information are present.
                     */
                    if (twObj == null || thObj == null || toObj == null) {
                        final Map<String, Object> rpsObj   =  headProperties.get(RowsPerStrip);
                        final Map<String, Object> sOffObj  =  headProperties.get(StripOffsets);
                        final Map<String, Object> sbcObj   =  headProperties.get(StripByteCounts);

                        rowsPerStrip = (int) ((rpsObj == null) ? -1 : ((long[]) rpsObj.get(ATT_VALUE))[0]);
                        if (sOffObj != null) stripOffsets    = (long[]) sOffObj.get(ATT_VALUE);
                        if (sbcObj  != null) stripByteCounts = (long[]) sbcObj.get(ATT_VALUE);
                        ensureDefined(rowsPerStrip,    "rowsPerStrip");
                        ensureDefined(stripOffsets,    "stripOffsets");
                        ensureDefined(stripByteCounts, "stripByteCounts");
                    } else {
                        tileWidth   = (int) ((twObj == null) ? -1 : ((long[]) twObj.get(ATT_VALUE))[0]);
                        tileHeight  = (int) ((thObj == null) ? -1 : ((long[]) thObj.get(ATT_VALUE))[0]);
                        if (toObj  != null)  tileOffsets = (long[]) toObj.get(ATT_VALUE);

                        ensureDefined(tileWidth,   "tileWidth");
                        ensureDefined(tileHeight,  "tileHeight");
                        ensureDefined(tileOffsets, "tileOffsets");
                    }
                    currentImage = imageIndex;
                    return;
                }
            }
            throw new IndexOutOfBoundsException(error(Errors.Keys.INDEX_OUT_OF_BOUNDS_1, imageIndex));
        }
    }

    /**
     * Fill {@link IIOMetadataNode} root in native metadata format to create {@link SpatialMetadata}.
     */
    private void fillRootMetadataNode(final int indexImage) throws UnsupportedEncodingException {
        if (roots[indexImage] != null) return;
        roots[indexImage] = new IIOMetadataNode(TAG_GEOTIFF_IFD);
        for (int tag : headProperties.keySet()) {
            final Map<String, Object> currenTagAttributs = headProperties.get(tag);

            final String name  = (String) currenTagAttributs.get(ATT_NAME);
            final short type   = (short) currenTagAttributs.get(ATT_TYPE);
            final int count    = (int)(long) currenTagAttributs.get(ATT_COUNT);
            final Object value = currenTagAttributs.get(ATT_VALUE);

            final IIOMetadataNode tagBody = new IIOMetadataNode(TAG_GEOTIFF_FIELD);
            tagBody.setAttribute(ATT_NAME, name);
            tagBody.setAttribute(ATT_NUMBER, Integer.toString(tag));

            switch (type) {
                case TYPE_ASCII : {
                    final IIOMetadataNode valuesNode = new IIOMetadataNode(TAG_GEOTIFF_ASCIIS);
                    final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_ASCII);
                    final byte[] allAscii = new byte[count];
                    for (int i = 0; i < count; i++) {
                        allAscii[i] = (byte) Array.getLong(value, i);
                    }
                    String str = new String(allAscii, "ISO-8859-1");
                    valN.setAttribute(ATT_VALUE, str);
                    valuesNode.appendChild(valN);
                    tagBody.appendChild(valuesNode);
                    break;
                }
                case TYPE_BYTE : {
                    final IIOMetadataNode valuesNode = new IIOMetadataNode(TAG_GEOTIFF_ASCIIS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_ASCII);
                        valN.setAttribute(ATT_VALUE, Byte.toString((byte)Array.getLong(value, i)));
                        valuesNode.appendChild(valN);
                    }
                    tagBody.appendChild(valuesNode);
                    break;
                }
                case TYPE_UBYTE :
                case TYPE_SHORT : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_SHORTS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_SHORT);
                        valN.setAttribute(ATT_VALUE, Short.toString((short)Array.getLong(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                case TYPE_USHORT : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_SHORTS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_SHORT);
                        valN.setAttribute(ATT_VALUE, Integer.toString((int)Array.getLong(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                case TYPE_INT :
                case TYPE_UINT :
                case TYPE_LONG :
                case TYPE_ULONG : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_LONGS);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_LONG);
                        valN.setAttribute(ATT_VALUE, Long.toString(Array.getLong(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                case TYPE_RATIONAL :
                case TYPE_URATIONAL :
                case TYPE_FLOAT :
                case TYPE_DOUBLE : {
                    final IIOMetadataNode valueNode = new IIOMetadataNode(TAG_GEOTIFF_DOUBLES);
                    for (int i = 0; i < count; i++) {
                        final IIOMetadataNode valN = new IIOMetadataNode(TAG_GEOTIFF_DOUBLE);
                        valN.setAttribute(ATT_VALUE, Double.toString(Array.getDouble(value, i)));
                        valueNode.appendChild(valN);
                    }
                    tagBody.appendChild(valueNode);
                    break;
                }
                default : throw new IllegalStateException("unknow type. type : "+type);
            }
            roots[indexImage].appendChild(tagBody);
        }
    }

    /**
     * Ensures that the given value is positive.
     *
     * @param  value  The value which must be positive.
     * @param  name   The name for the parameter value, to be used in case of error.
     * @param  locale The locale to use for formatting the error message.
     * @throws IIOException If the given value is considered undefined.
     */
    private void ensureDefined(final int value, final String name) throws IIOException {
        if (value < 0) {
            throw new IIOException(error(Errors.Keys.NO_SUCH_ELEMENT_NAME_1, name));
        }
    }

    /**
     * Ensures that the given array is non-null.
     *
     * @param  value  The value which must be non-null.
     * @param  name   The name for the parameter value, to be used in case of error.
     * @param  locale The locale to use for formatting the error message.
     * @throws IIOException If the given value is considered undefined.
     */
    private void ensureDefined(final long[] value, final String name) throws IIOException {
        if (value == null) {
            throw new IIOException(error(Errors.Keys.NO_SUCH_ELEMENT_NAME_1, name));
        }
    }

    /**
     * Parses the content of the directory entry at the current {@linkplain #buffer} position.
     * The {@linkplain #buffer} is assumed to have all the required bytes for one entry. The
     * buffer position is undetermined after this method call.
     *
     * @throws IIOException If an error occurred while parsing an entry.
     */
    private void parseDirectoryEntries(final Collection<long[]> deferred) throws IIOException {
        final int tag       = buffer.getShort() & 0xFFFF;
        final short type    = buffer.getShort();
        final long count    = readInt();
        final long datasize = count * TYPE_SIZE[type];
//        System.out.println("tag : "+tag+" : "+getName(tag));
        if (datasize <= offsetSize) {
            //-- offset is the real value(s).
            entryValue(tag, type, count);
        } else {                
            //-- offset from file begin to data set.
            deferred.add(new long[]{tag, type, count, readInt()});
        }
    }

    /**
     * Reads one value of the given type from the given buffer.
     * This method assumes that the type is valid.
     *
     * @param  type The data type.
     * @return The value.
     */
    private long read(final short type) throws IIOException {
        switch (type) {
            case TYPE_BYTE:   return buffer.get();
            case TYPE_ASCII : return buffer.get();
            case TYPE_UBYTE:  return buffer.get() & 0xFFL;
            case TYPE_SHORT:  {
                return buffer.getShort();
            }
            case TYPE_USHORT: {
                return buffer.getShort() & 0xFFFFL;
            }
            case TYPE_INT:    return buffer.getInt();
            case TYPE_IFD:
            case TYPE_UINT:   return buffer.getInt() & 0xFFFFFFFFL;
            case TYPE_LONG:   return buffer.getLong();
            case TYPE_IFD8:
            case TYPE_ULONG: {
                final long value = buffer.getLong();
                if (value < 0) {
                    throw new UnsupportedImageFormatException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE));
                }
                return value;
            }
            default: throw new AssertionError(type);
        }
    }

    /**
     * Reads one value of the given type from the given buffer.
     * This method assumes that the type is valid.
     *
     * @param  type The data type.
     * @return The value.
     */
    private double readAsDouble(final short type) throws IIOException {
        switch (type) {
            case TYPE_URATIONAL : {
                final long num = buffer.getInt() & 0xFFFFFFFFL;
                final long den = buffer.getInt() & 0xFFFFFFFFL;
                return num / (double) den;
            }
            case TYPE_RATIONAL : {
                final int num = buffer.getInt();
                final int den = buffer.getInt();
                return num / (double) den;
            }
            case TYPE_DOUBLE : return buffer.getDouble();
            case TYPE_FLOAT  : return buffer.getFloat();
            default: throw new AssertionError(type);
        }
    }

    /**
     * Reads a value from the directory entry and add them in {@linkplain #headProperties}. This method can be invoked right after the entry ID.
     * The {@linkplain #buffer} is assumed to have all the required bytes for one entry. The buffer
     * position is undetermined after this method call.
     *
     * @param tag Tiff tag integer.
     * @param type type of tag
     * @param count data number will be read
     * @param  name The name of the entry being parsed.
     * @throws IIOException If the entry can not be read as an integer.
     */
    private void entryValue(final int tag, final short type, final long count) throws IIOException {
        assert count != 0;
        if (count > 0xFFFFFFFFL) throw new IllegalStateException("count value too expensive. not supported yet.");
        final int offsetSize = (isBigTIFF) ? Long.SIZE : Integer.SIZE;
        final Map<String, Object> tagAttributs = new HashMap<String, Object>();
        tagAttributs.put(ATT_NAME, getName(tag));
        tagAttributs.put(ATT_TYPE, type);
        tagAttributs.put(ATT_COUNT, count);
        
        switch(tag) {
            case PlanarConfiguration: { //-- PlanarConfiguration.
                assert count == 1 : "with tiff PlanarConfiguration tag, count should be equal 1.";
                final short planarConfiguration = (short) buffer.getShort();
                if (planarConfiguration != 1) { // '1' stands for "chunky", 2 for "planar".
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2,
                            "planarConfiguration", planarConfiguration));
                }
                tagAttributs.put(ATT_VALUE, new short[]{planarConfiguration});
                headProperties.put(tag, tagAttributs);
                break;
            }
            case PhotometricInterpretation: { //-- PhotometricInterpretation.
                assert count == 1 : "with tiff PhotometricInterpretation tag, count should be equal 1.";
                final short photometricInterpretation = (short) buffer.getShort();
                tagAttributs.put(ATT_VALUE, new short[]{photometricInterpretation});
                headProperties.put(tag, tagAttributs);
                break;
            }
            case Compression: { //-- Compression.
                assert count == 1 : "with tiff compression tag, count should be equal 1.";
                compression = (int) (buffer.getShort() & 0xFFFF);
                if (compression != 1 && compression != 32773 && compression != 5) { // '1' stands for "uncompressed". // '32 773' stands for packbits compression
                    final Object nameCompress;
                    switch (compression) {
                        case 6:  nameCompress = "JPEG";      break;
                        case 7:  nameCompress = "LZW";       break;
                        default: nameCompress = compression; break;
                    }
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2,
                            "compression", nameCompress));
                }
                tagAttributs.put(ATT_VALUE, new int[]{compression});
                headProperties.put(tag, tagAttributs);
                break;
            }
            default : {
                /*
                 * Here the four byte at 8-11th positions are not offset but real value(s) 
                 * because data(s) size is lesser or equal with offset byte size.
                 * 4 bytes to normal tiff (Integer.size) and 8 bytes to big tiff (Long.size).
                 */
               switch (type) {
                    case TYPE_ASCII :
                    case TYPE_BYTE  : {
                        if (count > offsetSize / Byte.SIZE)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int) count]; 
                        for (int i = 0; i < count; i++) {
                            result[i] = buffer.get();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_UBYTE : {
                        if (count > offsetSize / Byte.SIZE)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count]; 
                        for (int i = 0; i < count; i++) {
                            result[i] = buffer.get() & 0xFF;
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_SHORT : {
                        if (count > (offsetSize / Short.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = (short) buffer.getShort();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_USHORT: {
                        if (count > (offsetSize / Short.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = (int) (buffer.getShort() & 0xFFFF);
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_INT   : {
                        if (count > (offsetSize / Integer.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = buffer.getInt();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_UINT :
                    case TYPE_IFD  : {
                        if (count > (offsetSize / Integer.SIZE))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = buffer.getInt() & 0xFFFFFFFFL;
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_FLOAT : {
                        if ((isBigTIFF && count > 2) || ((!isBigTIFF) && count > 1))
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        final float[] result = new float[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = buffer.getFloat();
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case TYPE_RATIONAL  : 
                    case TYPE_URATIONAL : 
                    case TYPE_DOUBLE    : {
                        if (count > 1)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        if (!isBigTIFF) {
                            throw new IllegalStateException("in standard tiff offset or value in tiff tag, should not be instance of double or rational.");
                        }
                        final double[] result = new double[1];
                        result[0] = readAsDouble(type);
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    /*
                     * Only use by bigTiff format.
                     */
                    case TYPE_LONG  :
                    case TYPE_ULONG :
                    case TYPE_IFD8  : {
                        if (count > 1)
                            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_1, getName(tag)));
                        if (!isBigTIFF) {
                            throw new IllegalStateException("in standard tiff offset or value in tiff tag, should not be instance of long.");
                        }
                        final long[] result = new long[(int)count];
                        for (int i = 0; i < count; i++) {
                            result[i] = buffer.getLong() & 0xFFFFFFFFL;
                        }
                        tagAttributs.put(ATT_VALUE, result);
                        headProperties.put(tag, tagAttributs);
                        break;
                    }
                    case 7 : {
                        break;
                    }
                    default : throw new IIOException(error(Errors.Keys.ILLEGAL_PARAMETER_TYPE_2, getName(tag), type));
                }
            }
        }
    }

    /**
     * To be invoked after {@link #entryValues(String, Collection)} in order to process all
     * the deferred arrays. This method tries to read the arrays in sequential order as much
     * as possible.
     *
     * @param  deferred The deferred arrays.
     * @throws IOException If an I/O operation was required and failed.
     */
    private void readDeferredArrays(final long[][] deferred) throws IOException {
        /*
         * Organize offsets in ascending order to avoid too much buffer deplacements.
         */
        Arrays.sort(deferred, OFFSET_COMPARATOR);

        for (long[] defTab : deferred) {
            final int tag      = (int) (defTab[0] & 0xFFFF);
            final short type   = (short) defTab[1];
            final int count    = (int) defTab[2];
            //-- in tiff spec offset or data are always unsigned
            final long offset  = defTab[3] & 0xFFFFFFFFL;
            final int dataSize = TYPE_SIZE[type];

            final int length = count * dataSize;

            final Map<String, Object> tagAttributs = new HashMap<String, Object>();
            tagAttributs.put(ATT_NAME, getName(tag));
            tagAttributs.put(ATT_TYPE, type);
            tagAttributs.put(ATT_COUNT, (long) count);

            Object result = (type == TYPE_DOUBLE || type == TYPE_FLOAT || type == TYPE_RATIONAL || type == TYPE_URATIONAL) ? new double[count] : new long[count];

            int currentPos = (int) offset;
            int maxBuffPos = (int) (offset + length);
            final int buffCapacity = (buffer.capacity() / dataSize) * dataSize;
            int resultId = 0;
            while (currentPos < maxBuffPos) {
                final int currentByteLength = Math.min(currentPos + buffCapacity, maxBuffPos) - currentPos;
                assert currentByteLength % dataSize == 0 : "current length = "+currentByteLength+" samplesize = "+dataSize;
//                try {
                    ensureBufferContains(currentPos, Math.min(currentByteLength, buffCapacity), Math.max(currentByteLength, 1024));
//                } catch (Exception ex) {
//                    System.out.println("");
//                }
                
//                    ensureBufferContains(currentPos, Math.min(currentByteLength, buffCapacity), Math.max(currentByteLength, 1024));
                    final int dataNumber = currentByteLength / dataSize;
                    assert buffer.remaining() >= currentByteLength;

                if (type == TYPE_DOUBLE || type == TYPE_FLOAT || type == TYPE_RATIONAL || type == TYPE_URATIONAL) {
                    for (int i = 0; i < dataNumber; i++) {
                        Array.setDouble(result, resultId++, readAsDouble(type));
                    }
                } else {
                    for (int i = 0; i < dataNumber; i++) {
                        Array.setLong(result, resultId++, read(type));
                    }
                }
                currentPos += currentByteLength;
            }
            tagAttributs.put(ATT_VALUE, result);
            headProperties.put(tag, tagAttributs);
        }
    }

    /**
     * Comparator used for sorting the array to be processed by {@link #readDeferredArrays(long[][])},
     * in order to read the data sequentially from the disk.
     */
    private static final Comparator<long[]> OFFSET_COMPARATOR = new Comparator<long[]>() {
        @Override public int compare(final long[] o1, final long[] o2) {
            return Long.signum(o1[0] - o2[0]);
        }
    };

    /**
     * Returns the number of bands available for the specified image.
     *
     * @param  imageIndex The image index.
     * @return The number of bands available for the specified image.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        selectImage(imageIndex);
        return samplesPerPixel;
    }

    /**
     * Returns the width of the image at the given index.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The width of the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        selectImage(imageIndex);
        return imageWidth;
    }

    /**
     * Returns the height of the image at the given index.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The height of the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getHeight(int imageIndex) throws IOException {
        selectImage(imageIndex);
        return imageHeight;
    }

    /**
     * Returns the width of the tiles in the given image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The width of the tile in the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getTileWidth(final int imageIndex) throws IOException {
        selectImage(imageIndex);
        return (tileWidth >= 0) ? tileWidth : imageWidth;
    }

    /**
     * Returns the height of the tiles in the given image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The height of the tile in the given image.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public int getTileHeight(int imageIndex) throws IOException {
        selectImage(imageIndex);
        return (tileHeight >= 0) ? tileHeight : imageHeight;
    }

    /**
     * Returns {@code true} if the image is organized into tiles.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return {@code true} if the image is organized into tiles.
     * @throws IOException If an error occurred while reading the file.
     */
    @Override
    public boolean isImageTiled(final int imageIndex) throws IOException {
        selectImage(imageIndex);
        return (tileWidth >= 0) && (tileHeight >= 0);
    }

    /**
     * Information about a tile. The inherited {@link Rectangle} contains the coordinate of
     * the region to write in the target image, <em>relative to the upper-left pixel to be
     * written (i.e. the upper-left pixel in the set of tiles returned by {@link #getTiles}
     * is located at (0,0) by definition).
     * <p>
     * Tiles can be sorted in ascending order of file positions, for sequential access.
     * Note: the {@code compareTo} method is inconsistent with the {@code equals} method.
     */
    @SuppressWarnings("serial")
    private static final class Tile extends Rectangle implements Comparable<TiffImageReader.Tile> {
        /** Tile position in file.  */ final long position;

        /** Creates a new instance for the given position in file and target region. */
        Tile(final int x, final int y, final int width, final int height, final long position) {
            super(x, y, width, height);
            this.position = position;
            assert !isEmpty() && x >= 0 && y >= 0: this;
        }

        /** Compares this tile with the specified tile for order of file position. */
        @Override public int compareTo(final TiffImageReader.Tile other) {
            return Long.signum(position - other.position);
        }
    }

    /**
     * Returns the tiles in the given source region. The tiles are sorted by increasing file
     * position, in order to perform sequential file access as much as possible.
     *
     * @param  r              The source region requested by the user.
     * @param  pixelStride    Number of bytes in each sample value in the source file.
     * @param  scanlineStride Number of bytes in each row of the tile in the source file.
     * @return The tiles that intersect the given region.
     */
    private TiffImageReader.Tile[] getTiles(final Rectangle r, final int xSubsampling, final int ySubsampling,
            final int pixelStride, final int scanlineStride)
    {
        final int minTileX  =  r.x / tileWidth;                               // Inclusive
        final int minTileY  =  r.y / tileHeight;                              // Inclusive
        final int maxTileX  = (r.x + r.width  + tileWidth  - 2) / tileWidth;  // Exclusive
        final int maxTileY  = (r.y + r.height + tileHeight - 2) / tileHeight; // Exclusive
        final int rowLength = (imageWidth + tileWidth - 1) / tileWidth;
        final TiffImageReader.Tile[] tiles  = new TiffImageReader.Tile[(maxTileX - minTileX) * (maxTileY - minTileY)];
        int count = 0;
        for (int tileY=minTileY; tileY<maxTileY; tileY++) {
            final int ySource = tileY * tileHeight - r.y;
            final int y       = (Math.max(0, ySource) + ySubsampling - 1) / ySubsampling;
            final int height  = (Math.min(r.height, ySource + tileHeight) - 1) / ySubsampling + 1 - y;
            final int rowBase = (y * ySubsampling - ySource) * scanlineStride;
            for (int tileX=minTileX; tileX<maxTileX; tileX++) {
                final int xSource   = tileX * tileWidth  - r.x;
                final int x         = (Math.max(0, xSource) + xSubsampling - 1) / xSubsampling;
                final int width     = (Math.min(r.width,  xSource + tileWidth)  - 1) / xSubsampling + 1 - x;
                final int offset    = (x * xSubsampling - xSource) * pixelStride + rowBase;
                final int tileIndex = tileY * rowLength + tileX;
                tiles[count++]      = new TiffImageReader.Tile(x, y, width, height, tileOffsets[tileIndex] + offset);
            }
        }
        assert count == tiles.length;
        Arrays.sort(tiles);
        return tiles;
    }

    /**
     * Returns {@code true} since TIFF images have color palette.
     */
    @Override
    public boolean hasColors(final int imageIndex) throws IOException {
        selectImage(imageIndex);
        return true;
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * The default implementation is as below:
     *
     * {@preformat java
     *     return getRawImageType(imageIndex).getSampleModel().getDataType();
     * }
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The data type (typically {@link DataBuffer#TYPE_BYTE}).
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    protected int getRawDataType(final int imageIndex) throws IOException {
        return getRawImageType(imageIndex).getSampleModel().getDataType();
    }

    /**
     * Returns the {@link SampleModel} and {@link ColorModel} which most closely represents the
     * internal format of the image.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The internal format of the image.
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    public ImageTypeSpecifier getRawImageType(final int imageIndex) throws IOException {
        selectImage(imageIndex);
        if (rawImageType == null) {
            // switch photo metrique interpretation
            final ColorSpace cs;
            final int photoInter = ((short[]) headProperties.get(PhotometricInterpretation).get(ATT_VALUE))[0];
            final Map<String, Object> bitsPerSamples = (headProperties.get(BitsPerSample));
            final Map<String, Object> sampleFormat = (headProperties.get(SampleFormat));
            
            //-- find appropriate databuffer image type from size of sample or sample format or both of them. --//
            int databufferType;
            
            //-- bits per sample study --//
            int[] bits = null;
            int sampleBitSize = 0;
            long[] bitsPerSample = null;
            if (bitsPerSamples != null) {
                bitsPerSample = ((long[]) bitsPerSamples.get(ATT_VALUE));
                assert bitsPerSample != null : "bitsPerSample array should not be null.";
                bits = new int[bitsPerSample.length];
                for (int i=0; i < bits.length; i++) {
                    final long b = bitsPerSample[i];
                    if ((bits[i] = (int) b) != b) {
                        //-- Verify that 'bitPerSample' values are inside 'int' range (paranoiac check).
                        throw new UnsupportedImageFormatException(error(
                                Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", b));
                    }
                    if (i != 0 && b != sampleBitSize) {
                        //-- Current implementation requires all sample values to be of the same size.
                        throw new UnsupportedImageFormatException(error(Errors.Keys.INCONSISTENT_VALUE));
                    }
                    sampleBitSize = (int) b;
                }
            }
            
            //-- sample format study --//
            if (sampleFormat == null) {
                if (bitsPerSamples == null) {
                    if (samplesPerPixel == 0) {
                        switch (photoInter) {
                            case 0  : 
                            case 1  : samplesPerPixel = 1; break; //-- gray color model --//
                            case 2  : samplesPerPixel = 3; break; //-- RGB color model --//
                            default : throw new UnsupportedImageFormatException(error(Errors.Keys.FORBIDDEN_ATTRIBUTE_2,
                                "bitsPerSamples", "sampleFormats"));
                        }
                    } 
                    /*
                    * If bitsPerSample and sample format fields were not specified, assume bytes.
                    */
                   databufferType = DataBuffer.TYPE_BYTE;
                   bits           = new int[samplesPerPixel];
                   Arrays.fill(bits, 8);
                } else {
                    /*
                     * We require exact value, because the reading process read all sample values
                     * in one contiguous read operation.
                     */
                    switch (sampleBitSize) {
                        case Byte   .SIZE : databufferType = DataBuffer.TYPE_BYTE;   break;
                        case Short  .SIZE : databufferType = DataBuffer.TYPE_USHORT; break;
                        case Integer.SIZE : databufferType = DataBuffer.TYPE_INT;    break;
                        case Double.SIZE  : databufferType = DataBuffer.TYPE_DOUBLE; break;
                        default: {
                            throw new UnsupportedImageFormatException(error(
                                    Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", sampleBitSize));
                        }
                    }
                }                    
            } else {
                final short samplFormat = (short) ((long[]) sampleFormat.get(ATT_VALUE)) [0];
                switch (samplFormat) {
                    case 1 :   
                    case 2 : {
                        if (bitsPerSample != null) assert sampleBitSize == Integer.SIZE : "With Integer sample format bits per sample must be equals to Integer.size = 32 bits";
                        databufferType = DataBuffer.TYPE_INT;
                        break;
                    } 
                    case 3 : {
                        databufferType = DataBuffer.TYPE_FLOAT;// adapted for debug
                        switch (sampleBitSize) {
                           case Float.SIZE  : databufferType = DataBuffer.TYPE_FLOAT; break;
                           case Double.SIZE : databufferType = DataBuffer.TYPE_DOUBLE; break;
                           default : {
                               throw new UnsupportedImageFormatException(error(
                                       Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", sampleBitSize));
                           }
                       }
                        break;
                    }    
                    default: {
                        //-- undefined sample format --//
                        if (bitsPerSample == null) 
                        throw new UnsupportedImageFormatException(error(
                                Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "sampleformat value", samplFormat));
                        
                       /*
                        * We require exact value, because the reading process read all sample values
                        * in one contiguous read operation.
                        */
                       switch (sampleBitSize) {
                           case Byte   .SIZE : databufferType = DataBuffer.TYPE_BYTE;   break;
                           case Short  .SIZE : databufferType = DataBuffer.TYPE_USHORT; break;
                           default : {
                               throw new UnsupportedImageFormatException(error(
                                       Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "bitsPerSample", sampleBitSize));
                           }
                       }
                    }
                }
            }
            
            switch (photoInter) {
                case 0 :   //--minIsWhite
                case 1 : { //-- minIsBlack
                    cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    break;
                }
                case 2 : { //-- RGB
                    cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                    break;
                }
                case 3 : {//-- palette
                    Map<String, Object> colorMod = headProperties.get(ColorMap);
                    if (colorMod == null)
                        throw new UnsupportedImageFormatException("image should own colorMap informations.");
                    final long[] index  = (long[]) colorMod.get(ATT_VALUE);
                    final int[] indexes = buildColorMapArray(index);
                    final ColorModel cm = new IndexColorModel(sampleBitSize, indexes.length, indexes, 0, true, -1, databufferType);
                    /*
                     * Create a SampleModel with size of 1x1 volontary just to know image properties.
                     * Image with correctively size will be create later with getDestination() in #read(int index, param) method.
                     */
                    rawImageType        = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
                    return rawImageType;
                }
                default : {
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2,
                            "photometricInterpretation", photoInter));
                }
            }
            final boolean hasAlpha = bits.length > cs.getNumComponents();
            final ColorModel cm = new ComponentColorModel(cs, bits, hasAlpha, false,
                    hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE, databufferType);
           /*
            * Create a SampleModel with size of 1x1 volontary just to know image properties.
            * Image with correctively size will be create later with getDestination() in #read(int index, param) method.
            */
            rawImageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
        }
        return rawImageType;
    }

    /**
     * Convert and return color map array from tiff file to an Integer array adapted to build {@link IndexColorModel} in java.
     *
     * @param colorMap array given by tiff reading.
     * @return an Integer array adapted to build {@link IndexColorModel} in java.
     */
    private int[] buildColorMapArray(final long[] colorMap) {
        final int indexLength = colorMap.length;
        assert (indexLength % 3 == 0) : "color map array length should be modulo 3";
        final int length_3 = indexLength / 3;
        final int[] result = new int[length_3];

        //-- color map in a tiff file : N Red values -> N Green values -> N Blue values
        int idR = 0;
        int idG = length_3;
        int idB = length_3 << 1;// = 2 * length_3

        /*
         * mask applied to avoid the low-order bits from the red color overlaps the bits of green color.
         * Moreover to avoid the low-order bits from the green color overlaps the bits of blue color.
         */
        final int mask = 0x0000FF00;

        /*
         * In indexed color model in java, values to defind palette for each color are between 0 -> 255.
         * To build integer value in palette, we need to shift red value by 16 bits, green value by 8 bits and no shift to blue.
         *
         * In our case we build a color model from color map (tiff palette) values define between 0 -> 65535.
         * Then build integer value in palette we will shift each color value by normaly shift minus 8, to bring back all values between 0 -> 256.
         */

        final int alpha = 0xFF000000;

        //-- pixel : 1111 1111 | R | G | B
        for (int i = 0; i < length_3; i++) {
            final int r = ((int) (colorMap[idR++] & mask) << 8);
            final int g = ((int) colorMap[idG++] & mask);
            final int b = ((int) colorMap[idB++] >> 8) ;
            result[i] = alpha | r | g | b;
        }
        return result;
    }

    /**
     * Returns a collection of {@link ImageTypeSpecifier} containing possible image types to which
     * the given image may be decoded. The default implementation returns a singleton containing
     * only the {@linkplain #getRawImageType(int) raw image type}.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return A set of suggested image types for decoding the current given image.
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
        return Collections.singleton(getRawImageType(imageIndex)).iterator();
    }

    /**
     * Reads the image at the given index.
     *
     * @param  imageIndex The index of the image to read.
     * @param  param Parameters used to control the reading process, or {@code null}.
     * @return The image.
     * @throws IOException If an error occurred while reading the image.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        selectImage(imageIndex);
        srcRegion = new Rectangle();
        dstRegion = new Rectangle();
        final BufferedImage image = getDestination(param, getImageTypes(imageIndex), imageWidth, imageHeight);
        /*
         * compute region : ajust les 2 rectangles src region et dest region en fonction des coeff subsampling present dans Imagereadparam.
         */
        computeRegions(param, imageWidth, imageHeight, image, srcRegion, dstRegion);// calculer une region de l'image sur le fichier que l'on doit lire
        if (compression == 32773) {
            assert stripOffsets != null : "with compression 32773 (packbits) : image should be writen in strip offset use case.";
            readFromStrip32773(image.getRaster(), param, srcRegion, dstRegion);
        } else if (compression == 5) {
            assert stripOffsets != null : "with compression LZW : image should be writen in strip offset use case.";
            readFromStripLZW(image.getRaster(), param, srcRegion, dstRegion);
        } else {
            //-- by strips
            if (stripOffsets != null) {
                readFromStrip(image.getRaster(), param, srcRegion, dstRegion);
            } else {
                //-- by tiles
                read(image.getRaster(), param, srcRegion, dstRegion);
            }
        }
        return image;
    }

    /**
     * Processes to the image reading, and stores the pixels in the given raster.<br/>
     * Process fill raster from informations stored in stripOffset made.
     *
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readFromStrip(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int sampleSize           = DataBuffer.getDataTypeSize(dataType) / Byte.SIZE;
        final int sourcePixelStride    = /*sampleSize */ samplesPerPixel;
        final int targetPixelStride    = /*sampleSize */ numBands;
        final int sourceScanlineStride = sourcePixelStride * imageWidth;
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());

        /*
         * Get a view of the ByteBuffer as a NIO Buffer of the appropriate type.
         * The buffer is cleared first because the 'sourceBuffer' capacity will
         * be set to this buffer limit.
         */
        final Buffer sourceBuffer;
        //-- For keep position and limit variables locale.
        final int position = buffer.position();
        final int limit    = buffer.limit();
        buffer.clear();
        switch (dataType) {
            case DataBuffer.TYPE_BYTE   : sourceBuffer = buffer.duplicate();      break;
            case DataBuffer.TYPE_USHORT :
            case DataBuffer.TYPE_SHORT  : sourceBuffer = buffer.asShortBuffer();  break;
            case DataBuffer.TYPE_INT    : sourceBuffer = buffer.asIntBuffer();    break;
            case DataBuffer.TYPE_FLOAT  : sourceBuffer = buffer.asFloatBuffer();  break;
            case DataBuffer.TYPE_DOUBLE : sourceBuffer = buffer.asDoubleBuffer(); break;
            default: throw new IIOException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE, dataBuffer.getClass()));
        }
        buffer.limit(limit).position(position);
        
        assert !reverseByte : "unexpected comportement reverseByte boolean should be false.";
        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        reverseByte = (fO == 2);

        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE   : targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT : targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT  : targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_INT    : targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT  : targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE : targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }

            int readLength, srcStepX, srcStepY;

            if (sourceXSubsampling == 1) {
                /*
                 * if we want to read all image which mean : srcRegion = dstRegion
                 * and all strip are organize inascending order in tiff file and
                 * moreover if datatype is Byte, we can fill a byteBuffer in one time, by all red samples values.
                 */
                if (sourceYSubsampling == 1 && dstRegion.equals(srcRegion)
                                            && checkStrips(stripOffsets, stripByteCounts)
                                            && sourceScanlineStride == dstRegion.width * targetPixelStride
                                            && dataType == DataBuffer.TYPE_BYTE) {

                    readLength = (int) ((stripOffsets[stripOffsets.length-1] + stripByteCounts[stripOffsets.length-1] - stripOffsets[0]) / sampleSize);
                    final ByteBuffer bbuff = ByteBuffer.wrap((byte[]) targetArray);
                    channel.read(bbuff, stripOffsets[0]);
                    continue;
                }
                //-- if sourceXSubsampling = 1 we read row by row.
                readLength = srcRegion.width * samplesPerPixel;
                srcStepX   = srcRegion.width;
            } else {
                //-- read pixel by pixel
                readLength = numBands;
                srcStepX   = sourceXSubsampling;
            }

            srcStepY              = sourceYSubsampling;
            
            //-- shift in X direction exprimate in Byte unit.
            final int buffStepX   = (sourceXSubsampling - 1) * sourcePixelStride * sampleSize;
            /*
             * Iterate over the strip to read, in sequential file access order (which is not
             * necessarily the same than row indices order).
             */
            int currentMaxRowPerStrip = -1;
            int currentStripOffset = -1;
            long srcBuffPos = -1;

            final int srcMaxx = srcRegion.x + srcRegion.width;
            final int srcMaxy = srcRegion.y + srcRegion.height;

            //-- target start
            int bankID = bankOffsets[bank] + targetScanlineStride * dstRegion.y;

            //-- step on x axis in source window (srcRegion) exprimate in (Byte).
            final int srcStepBeforeReadX = srcRegion.x * sourcePixelStride * sampleSize;
            final int srcStepAfterReadX  = (sourceScanlineStride - (srcRegion.x + srcRegion.width) * targetPixelStride) * sampleSize;

            //-- step on x axis in target window (dstRegion) exprimate in sample unit.
            final int dstStepBeforeReadX = dstRegion.x * numBands;
            final int dstStepAfterReadX  = (targetScanlineStride - (dstRegion.x + dstRegion.width) * targetPixelStride); // sampleSize;

            //-- byte number read for each buffer read action.
            final int srcBuffReadLength = readLength * sampleSize;

//            assert srcStepBeforeReadX + srcBuffReadLength + srcStepAfterReadX == (sourceScanlineStride * sampleSize) : "expected "+(sourceScanlineStride * sampleSize)+" found = "+(srcStepBeforeReadX + srcBuffReadLength + srcStepAfterReadX);
            
            //-- buffer.capacity
            final int buffCapacity = buffer.capacity();

            for (int y = srcRegion.y; y < srcMaxy; y += srcStepY) {
                if (y >= currentMaxRowPerStrip) {
                    currentStripOffset    = y / rowsPerStrip;
                    currentMaxRowPerStrip = currentStripOffset + rowsPerStrip;
                    //-- row begining exprimate in byte.
                    srcBuffPos = stripOffsets[currentStripOffset] + (y - currentStripOffset * rowsPerStrip) * sourceScanlineStride * sampleSize;
                }

                //-- move at correct table position from dstRegion.x begin position.
                bankID += dstStepBeforeReadX;

                //-- move at correct buffer position from srcRegion.x begin position.
                assert srcBuffPos != -1 : "bad source buffer position initialize value.";
                srcBuffPos += srcStepBeforeReadX;

                for (int x = srcRegion.x; x < srcMaxx; x += srcStepX) {
                    //-- adjust read length in function of buffer capacity.
                    long currentPos       = srcBuffPos;
                    final long maxBuffPos = srcBuffPos + srcBuffReadLength;
                    while (currentPos < maxBuffPos) {
                        final long currentByteLength = Math.min(currentPos + buffCapacity, maxBuffPos) - currentPos;
                        assert currentByteLength % sampleSize == 0 : "current length = "+currentByteLength+" samplesize = "+sampleSize;

                        //-- adjust buffer to read currentByteLength at srcBuff position
                        //-- position in Byte unit
                        ensureBufferContains(srcBuffPos, (int) Math.min(currentByteLength, buffCapacity), (int) Math.max(currentByteLength, 1024));
                        sourceBuffer.limit(buffer.limit() / sampleSize).position(buffer.position() / sampleSize);

                        assert currentByteLength % sampleSize == 0;

                        //-- sample number red
                        final int samplesLenght = (int) (currentByteLength / sampleSize);

                        switch (dataType) {
                            case DataBuffer.TYPE_BYTE   : ((ByteBuffer)   sourceBuffer).get((byte[])   targetArray, bankID, samplesLenght); break;
                            case DataBuffer.TYPE_USHORT :
                            case DataBuffer.TYPE_SHORT  : ((ShortBuffer)  sourceBuffer).get((short[])  targetArray, bankID, samplesLenght); break;
                            case DataBuffer.TYPE_INT    : ((IntBuffer)    sourceBuffer).get((int[])    targetArray, bankID, samplesLenght); break;
                            case DataBuffer.TYPE_FLOAT  : ((FloatBuffer)  sourceBuffer).get((float[])  targetArray, bankID, samplesLenght); break;
                            case DataBuffer.TYPE_DOUBLE : ((DoubleBuffer) sourceBuffer).get((double[]) targetArray, bankID, samplesLenght); break;
                            default: throw new AssertionError(dataType);
                        }
                        bankID     += samplesLenght;
                        currentPos += currentByteLength;
                    }

                    //-- move buffer position by read length (exprimate in Byte).
                    srcBuffPos += srcBuffReadLength;
                    /*
                     * jump pixel which are not read caused by subsampling X.
                     * For example with subsamplingX = 3 we read one pixelstride and we jump two of them for each read step.
                     */
                    srcBuffPos += buffStepX;
                }

                //-- advance bank index from end destination window (dstRegion.x + dstRegion.width) to end of row.
                bankID     += dstStepAfterReadX;

                /*
                 * move at correct buffer position from end source window (srcRegion.x + srcRegion.width) to end of row.
                 * Exprimate in Byte unit. 
                 */
                srcBuffPos += srcStepAfterReadX;
            }
        }
        reverseByte = false;
    }
    
    /**
     * Process to the image reading, and stores the pixels in the given raster.<br/>
     * Process fill raster from informations stored in stripOffset made.<br/>
     * Method adapted to read data from LZW(tag value 5) compression.
     * 
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readFromStripLZW(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int sourceScanlineStride = samplesPerPixel * imageWidth;
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());
        
        //-- predictor study ---//
        final Map<String, Object> predictor = (headProperties.get(Predictor));
        final short predic    = (predictor != null) ? (short) ((long[]) predictor.get(ATT_VALUE)) [0] : 1;
        //-- array which represent a pixel to permit horizontal differencing if exist --//
        final long[] prediPix = new long[samplesPerPixel];
        
        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        //-- adapt channel in function of fill order value --//
        final ReadableByteChannel fillOrderChannel = (fO == 2) ? new ReversedBitsChannel(fIStImageReader.getChannel()) : fIStImageReader.getChannel();
        final ImageInputStream inputLZW = new ChannelImageInputStream(null, fillOrderChannel, ByteBuffer.allocateDirect(8196), false);
        
        final long bitpersampl = bitsPerSample[0];
        
       /*
        * Iterate over the strip to read, in sequential file access order (which is not
        * necessarily the same than row indices order).
        */
       final int srcMaxy            = srcRegion.y + srcRegion.height;

       /*
        * Step on x axis in target window (dstRegion) when iterate pass to next row.
        * This step contain sum of shift at begin row and ending destination row shift.
        */
       final int dstStep            = targetScanlineStride - dstRegion.width * samplesPerPixel;

       //-- source sample step when iterar=tion pass to next row --//
       final int nextRowStep        = sourceScanlineStride * sourceYSubsampling;

       //-- stripoffset array index start --//
       final int currentStripOffset = srcRegion.y / rowsPerStrip;
       final int maxStripOffset     = (srcMaxy + rowsPerStrip - 1) / rowsPerStrip;
        
        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE   : targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT : targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT  : targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_INT    : targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT  : targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE : targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }
            
            /*
             * Long container use to build a sample,
             * because each sample is read byte per byte regardless their bit size.
             */
            long dataContainer = 0;
            int maskCount      = 0;

            //-- target start --//
            int bankID = bankOffsets[bank] + targetScanlineStride * dstRegion.y + dstRegion.x * samplesPerPixel;
            
            //-- current source y position --//
            int ypos = srcRegion.y;
            
            //-- read throught only necessarry stripOffsets to fill target image --//
            nextStrip : for (int cSO = currentStripOffset; cSO < maxStripOffset; cSO++) {
                
                //-- buffer start position from stripOffsets --//
                long currentBuffPos        = stripOffsets[cSO];
                inputLZW.seek(currentBuffPos);
                
                //-- initialize LZW attributs --//
                //-- length in bit of lzw data --//
                int currentLZWCodeLength = 9;
                //-- byte array map use to decompresse LZW datas --//
                byte[][] lzwTab          = new byte[LZW_CLEAR_CODE][];
                
                //-- current LZW array index --// 
                int idLZWTab         = 0;
                int maxIDLZWTab      = 511; //--> (1 << currentLZWCodeLength) - 1
                //-- precedently iteration LZW code --//
                byte[] oldCodeLZW    = null;
                int hdb              = 0;
                Arrays.fill(prediPix, 0);
                /*
                 * With LZW compression we must read all byte to build appropriate LZW map container.
                 * We define to positions "posRef" and "maxRowRefPos" where "posRef" represent
                 * index of current sample which will be written in target array and "maxRowRefPos" the last exclusive written sample.
                 */
                int posRef       = (ypos - cSO * rowsPerStrip) * sourceScanlineStride + srcRegion.x * samplesPerPixel;
                
                //-- in case where sourceYsubsampling greater than row per strip --//
                if (posRef >= rowsPerStrip * sourceScanlineStride) continue nextStrip;
                
                int nextPosRef         = posRef + nextRowStep;
                int maxRowRefPos       = posRef + dstRegion.width * samplesPerPixel * sourceXSubsampling;
                
                final int maxSamplePos = (Math.min((cSO + 1) * rowsPerStrip, srcMaxy) - cSO * rowsPerStrip) * sourceScanlineStride;
                int samplePos          = 0;
                
                //-- bytePos must read throught all file byte per byte --//
                int bytePos = 0;
                int b       = 0;
                short codeLZW;
                
                //-- work sample by sample --//
                while (LZW_EOI_CODE != (codeLZW = readLZWCode(inputLZW, currentLZWCodeLength))) {
                    
                    if (codeLZW == LZW_CLEAR_CODE) {
                        currentLZWCodeLength = 9;
                        lzwTab               = new byte[LZW_CLEAR_CODE][];
                        idLZWTab             = 0;
                        maxIDLZWTab          = 511;
                        oldCodeLZW           = null;
                        continue;
                    }
                    
                    assert (oldCodeLZW != null || (oldCodeLZW == null && codeLZW < LZW_CLEAR_CODE)) :"After a clear code, next code should be smaller than 256";
                    
                    byte[] entree;
                    if (codeLZW >= 258) {
                        if (lzwTab[codeLZW - 258] != null) {
                            entree = lzwTab[codeLZW - 258];
                        } else {
                            // w + w[0]
                            final int oldCLen = oldCodeLZW.length;
                            entree = Arrays.copyOf(oldCodeLZW, oldCLen + 1);
                            entree[oldCLen] = oldCodeLZW[0];
                        }
                    } else {
                        entree = new byte[] { (byte) codeLZW };
                    }

                    assert entree != null;

                    //-- write entree --//
                    for (int i = 0; i < entree.length; i++) {
                        //-- build sample in relation with bits per samples --//
                        final long val = entree[i] & 0x000000FFL;
                        dataContainer = dataContainer | (val << maskCount);
                        maskCount += Byte.SIZE;
                        
                        //-- if a sample is built --//
                        if (maskCount == bitpersampl) {
                            //-- add in precedently array before insertion --//
                            //-- if horizontal differencing add with precedently value --//
                            prediPix[hdb] = (predic == 2) ? (prediPix[hdb] + dataContainer) : dataContainer;
                            if (++hdb == samplesPerPixel) hdb = 0;

                            //-- re-initialize datacontainer --//
                            dataContainer = 0;
                            maskCount     = 0;

                            //-- write sample in target array if its necessary --//
                            if (samplePos == posRef) { 
                                switch (dataType) {
                                    case DataBuffer.TYPE_BYTE   : Array.setByte(targetArray, bankID++, (byte) (prediPix[b])); break;
                                    case DataBuffer.TYPE_SHORT  : 
                                    case DataBuffer.TYPE_USHORT : Array.setShort(targetArray, bankID++, (short) (prediPix[b])); break;
                                    case DataBuffer.TYPE_INT    : Array.setInt(targetArray, bankID++, (int) (prediPix[b])); break;
                                    case DataBuffer.TYPE_FLOAT  : Array.setFloat(targetArray, bankID++, Float.intBitsToFloat((int) (prediPix[b]))); break;
                                    case DataBuffer.TYPE_DOUBLE : Array.setDouble(targetArray, bankID++, Double.longBitsToDouble(prediPix[b])); break;
                                    default: throw new AssertionError(dataType);
                                }
                                if (++b == samplesPerPixel) {
                                    posRef += (sourceXSubsampling - 1) * samplesPerPixel;
                                    b = 0;
                                }
                                posRef++;

                                //-- this if means : pass to the next destination image row --//
                                if (posRef == maxRowRefPos) {
                                    assert hdb == 0 : "hdb should be zero. hdb = "+hdb;
                                    ypos += sourceYSubsampling;

                                    //-- begin source position writing --//
                                    posRef      = nextPosRef;
                                    nextPosRef += nextRowStep;
                                    
                                    //-- destination shifts --//
                                    bankID += dstStep;
                                    
                                    //-- if it is unnecessary to finish to read current strip --//
                                    if (posRef > maxSamplePos) continue nextStrip;
                                    
                                    //-- ending source position writing --//
                                    maxRowRefPos += nextRowStep;
                                }
                            }
                            //-- shift by one when a sample was built --//
                            samplePos++;
                        }
                        if (++bytePos == sourceScanlineStride) {
                            //-- initialize predictor array --//
                            Arrays.fill(prediPix, 0);
                            bytePos = 0;
                        }
                    }

                    if (oldCodeLZW == null) {
                        assert idLZWTab == 0 : "With old code null : lzw tab must be equals to zero.";
                        assert entree.length == 1;
                        oldCodeLZW = entree;
                        continue;
                    }

                    //-- add in LZW map array --//
                    final int oldLen      = oldCodeLZW.length;
                    final byte[] addedTab = Arrays.copyOf(oldCodeLZW, oldLen + 1);
                    addedTab[oldLen]      = entree[0];
                    lzwTab[idLZWTab++]    = addedTab;
                    
                    //-- if current map index reach the maximum value permit by bit number --//
                    if (((idLZWTab + 258) & 0xFFFF) == maxIDLZWTab) {
                        currentLZWCodeLength++;
                        final int nextLZWMapLength = 1 << currentLZWCodeLength;
                        maxIDLZWTab                = nextLZWMapLength - 1;
                        lzwTab                     = Arrays.copyOf(lzwTab, nextLZWMapLength);
                    }
                    oldCodeLZW = entree;
                    //---------------------------------------------------------//
                }
                assert samplePos == maxSamplePos : "pos = "+samplePos+" Expected pos = "+maxSamplePos;
            }
        }
    }
    
    /**
     * Read next data of {@code codeLZWLength} bits length from current {@code ImageInputStream input}.
     * 
     * @param input ImageInputStream where to read datas.
     * @param codeLZWLength current bits length of read data.
     * @return data value.
     * @throws IOException if problem during reading from input.
     * @see #readFromStripLZW(java.awt.image.WritableRaster, javax.imageio.ImageReadParam, java.awt.Rectangle, java.awt.Rectangle) 
     */
    private static short readLZWCode(final ImageInputStream input, final int codeLZWLength) throws IOException {
        return ((short) (input.readBits(codeLZWLength))); 
    }
    
    /**
     * Process to the image reading, and stores the pixels in the given raster.<br/>
     * Process fill raster from informations stored in stripOffset made.<br/>
     * Method adapted to read data from packbits(tag 32773) compression.
     *
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void readFromStrip32773(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int sampleSize           = DataBuffer.getDataTypeSize(dataType) / Byte.SIZE;
        final int sourcePixelStride    = sampleSize * samplesPerPixel;
        final int targetPixelStride    = sampleSize * numBands;
        final int sourceScanlineStride = sourcePixelStride * imageWidth;
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());

        assert dataType == DataBuffer.TYPE_BYTE : "with packBits compression input and output databuffer type should be instance of DataBuffer.TYPE_BYTE";
        /*
         * Get a view of the ByteBuffer as a NIO Buffer of the appropriate type.
         * The buffer is cleared first because the 'sourceBuffer' capacity will
         * be set to this buffer limit.
         */
        final ByteBuffer sourceBuffer;
        // For keep position and limit variables locale.
        final int position = buffer.position();
        final int limit    = buffer.limit();
        buffer.clear();
        sourceBuffer = buffer.duplicate();
        buffer.limit(limit).position(position);
        
        //-- fillOrder --//
        assert !reverseByte : "unexpected comportement reverseByte boolean should be false.";
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        reverseByte = (fO == 2);

        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final byte[] targetArray = ((DataBufferByte) dataBuffer).getData(bank);

            /*
             * Iterate over the strip to read, in sequential file access order (which is not
             * necessarily the same than row indices order).
             */
            final int srcMaxx = (srcRegion.x + srcRegion.width) * sourcePixelStride;
            final int srcMaxy = srcRegion.y  + srcRegion.height;

            //-- step on x axis in target window (dstRegion)
            final int dstStepBeforeReadX = dstRegion.x * numBands;
            final int dstStepAfterReadX  = (targetScanlineStride - (dstRegion.x + dstRegion.width) * targetPixelStride) / sampleSize;

            //-- target start
            int bankID = bankOffsets[bank] + targetScanlineStride * dstRegion.y;

            //-- buffer.capacity
            final int buffCapacity = buffer.capacity();

            //-- stripoffset array index start
            int currentStripOffset    = srcRegion.y / rowsPerStrip;

            //-- buffer start position
            int currentBuffPos        = (int) stripOffsets[currentStripOffset];

            //-- row index of next strip
            int currentMaxRowperStrip = (currentStripOffset + 1) * rowsPerStrip;

            //-- row index from stripoffset
            int ypos                  = currentStripOffset * rowsPerStrip;

            //-- row index to begin to write
            int yref                  = srcRegion.y;
            int xref, bankStepBefore, bankStepAfter;

            //-- channel size to do not exceed channel size during Buffer reading.
            final int channelSize = (int) channel.size();
            
            while (ypos < srcMaxy) {
                if (ypos >= currentMaxRowperStrip) {
                    currentStripOffset++;
                    currentBuffPos = (int) stripOffsets[currentStripOffset];
                    currentMaxRowperStrip += rowsPerStrip;
                }

                if (ypos == yref) {
                    //-- we write current row
                    xref     = srcRegion.x * sourcePixelStride;
                    yref    += sourceYSubsampling;
                    bankStepBefore = dstStepBeforeReadX;
                    bankStepAfter  = dstStepAfterReadX;
                } else {
                    //-- we travel row without writing action
                    xref = srcMaxx; // assertion (xpos == xref && xref < srcMaxx) will never be append
                    bankStepBefore = bankStepAfter = 0;
                }

                int xpos = 0;
                bankID += bankStepBefore;
                int b = 0;
                while (xpos < sourceScanlineStride) {
                    //-- adjust buffer to read currentByteLength at currentBuffPos position
                    final int minBuff = Math.min(channelSize-currentBuffPos, sourceScanlineStride - xpos + 1);
                    ensureBufferContains(currentBuffPos, Math.min(minBuff, buffCapacity), Math.max(sourceScanlineStride - xpos, 1024));
                    sourceBuffer.limit(buffer.limit()).position(buffer.position());                   
                    
                    int n = ((ByteBuffer)sourceBuffer).get();
                    if (n >= -127 && n <= - 1) {
                        n = - n + 1; //-- we write n times the following value.
                        final byte writeValue = ((ByteBuffer) sourceBuffer).get();
                        for (int i = 0; i < n; i++) {
                            if (xpos == xref && xref < srcMaxx) {
                                //-- setter le byte courant dans un long avec conteur et 
                                // faire avancer xref et xpos seulement quand on a attein assez de sample en rapport avec datasize
                                ((byte[]) targetArray) [bankID++] = writeValue;
                                if (++b == sourcePixelStride) {
                                    xref += (sourceXSubsampling - 1) * sourcePixelStride;
                                    b = 0;
                                }
                                xref++;
                            }
                            xpos++;
                        }
                        currentBuffPos += 2;//-- read n + value
                    } else if (n >= 0 && n < 128) {
                        if (sourceXSubsampling == 1) {
                            //-- copy data directly in target table
                            int debx = Math.max(xpos, xref);
                            final int endx = Math.min(xpos + n + 1, srcMaxx);
                            if (debx < endx & xref < srcMaxx) {
                                /*
                                 * In case where [xpos----[xref--------]xpos+n+1----]srcMaxx
                                 */
                                assert debx >= xpos;
                                currentBuffPos++;//-- n value red
                                if (debx != xpos) {
                                    currentBuffPos += (debx - xpos);
                                    final int remainCap = sourceScanlineStride - debx;
                                    //-- adjust buffer to read currentByteLength at currentBuffPos position
                                    ensureBufferContains(currentBuffPos, Math.min(remainCap, buffCapacity), Math.max(remainCap, 1024));
                                    sourceBuffer.limit(buffer.limit()).position(buffer.position());
                                }

                                final int length = (endx - debx);
                                sourceBuffer.get(targetArray, bankID, length);
                                bankID         += length;
                                xref           += length;
                                currentBuffPos += 1 + xpos + n - debx;
                            } else {
                                currentBuffPos += n + 2; // n + 1 + 1. we write n+1 byte and we shift buffer cursor by 1.
                            }
                            xpos += n + 1;
                        } else {
                            for (int i = 0; i < n + 1; i++) {// copy the next n + 1 bytes
                                final byte val = ((ByteBuffer)sourceBuffer).get();
                                if (xpos == xref && xref < srcMaxx) {
                                    ((byte[])targetArray)[bankID++] = val;
                                    if (++b == sourcePixelStride) {
                                        xref += (sourceXSubsampling - 1) * sourcePixelStride;
                                        b = 0;
                                    } 
                                    xref++;
                                }
                                xpos++;
                            }
                            assert n >= 0;
                            currentBuffPos += (n + 2); // n + 1 + 1. we write n + 1 bytes and we shift buffer cursor by 1.
                        }
                    } else {
                        currentBuffPos++;
                        continue;
                    }
                }
                ypos++;
                bankID += bankStepAfter;
            }
        }
        reverseByte = false;
    }

    /**
     * Verifies if all the strips are sequenced as a result in the file.
     *
     * @param stripOffsets strip offset table.
     * @param stripByteCounts strip length in Byte.
     * @return true if all the strips are sequenced as a result in the file else false.
     */
    private boolean checkStrips(long[] stripOffsets, long[] stripByteCounts) {
        for (int i = 0; i < stripOffsets.length-1; i++) {
            if (stripOffsets[i] + stripByteCounts[i] != stripOffsets[i + 1]) return false;
        }
        return true;
    }

    /**
     * Processes to the image reading, and stores the pixels in the given raster.
     *
     * @param  raster    The raster where to store the pixel values.
     * @param  param     Parameters used to control the reading process, or {@code null}.
     * @param  srcRegion The region to read in source image.
     * @param  dstRegion The region to write in the given raster.
     * @throws IOException If an error occurred while reading the image.
     */
    private void read(final WritableRaster raster, final ImageReadParam param,
            final Rectangle srcRegion, final Rectangle dstRegion) throws IOException
    {
        clearAbortRequest();
        final int numBands = raster.getNumBands();
        checkReadParamBandSettings(param, samplesPerPixel, numBands);
        final int[]      sourceBands;
        final int[] destinationBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            sourceBands        = param.getSourceBands();
            destinationBands   = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            sourceBands        = null;
            destinationBands   = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        if (sourceBands != null || destinationBands != null) {
            throw new IIOException("Source and target bands not yet supported.");
        }
        final DataBuffer dataBuffer    = raster.getDataBuffer();
        final int[] bankOffsets        = dataBuffer.getOffsets();
        final int dataType             = dataBuffer.getDataType();
        final int sampleSize           = DataBuffer.getDataTypeSize(dataType) / Byte.SIZE;
        final int sourcePixelStride    = sampleSize * samplesPerPixel;
        final int sourceScanlineStride = sourcePixelStride * tileWidth;
        final int targetScanlineStride = SampleModels.getScanlineStride(raster.getSampleModel());
        /*
         * Get a view of the ByteBuffer as a NIO Buffer of the appropriate type.
         * The buffer is cleared first because the 'sourceBuffer' capacity will
         * be set to this buffer limit.
         */
        final Buffer sourceBuffer;
        if (true) { // For keep position and limit variables locale.
            final int position = buffer.position();
            final int limit    = buffer.limit();
            buffer.clear();
            switch (dataType) {
                case DataBuffer.TYPE_BYTE:   sourceBuffer = buffer.duplicate();      break;
                case DataBuffer.TYPE_USHORT:
                case DataBuffer.TYPE_SHORT:  sourceBuffer = buffer.asShortBuffer();  break;
                case DataBuffer.TYPE_INT:    sourceBuffer = buffer.asIntBuffer();    break;
                case DataBuffer.TYPE_FLOAT:  sourceBuffer = buffer.asFloatBuffer();  break;
                case DataBuffer.TYPE_DOUBLE: sourceBuffer = buffer.asDoubleBuffer(); break;
                default: throw new IIOException(error(Errors.Keys.UNSUPPORTED_DATA_TYPE, dataBuffer.getClass()));
            }
            buffer.limit(limit).position(position);
        }
        /*
         * In current implementation, we support only one bank (in TIFF terminology: "chunky format").
         * However we loop over all banks as a matter of principle, in anticipation of a future version
         * that may support the "planar format".
         */
        final int sourceXSubsamplingStride = sourceXSubsampling * sourcePixelStride;
        
        assert !reverseByte : "unexpected comportement reverseByte boolean should be false.";
        //-- fillOrder --//
        final Map<String, Object> fillOrder = headProperties.get(FillOrder);
        short fO = 1;
        if (fillOrder != null) {
            fO = (short) ((long[]) fillOrder.get(ATT_VALUE)) [0];
        }
        reverseByte = (fO == 2);
        
        for (int bank = 0; bank < bankOffsets.length; bank++) {
            /*
             * Get the underlying array of the image DataBuffer in which to write the data.
             */
            final Object targetArray;
            switch (dataType) {
                case DataBuffer.TYPE_BYTE:   targetArray = ((DataBufferByte)   dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_USHORT: targetArray = ((DataBufferUShort) dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_SHORT:  targetArray = ((DataBufferShort)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_INT:    targetArray = ((DataBufferInt)    dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_FLOAT:  targetArray = ((DataBufferFloat)  dataBuffer).getData(bank); break;
                case DataBuffer.TYPE_DOUBLE: targetArray = ((DataBufferDouble) dataBuffer).getData(bank); break;
                default: throw new AssertionError(dataType);
            }
            /*
             * Iterate over the tiles to read, in sequential file access order (which is not
             * necessarily the same than tile indices order).  The rectangles inherited from
             * the tiles are the coordinates where to write in the target image.
             */
            final int targetImageStart = bankOffsets[bank] + targetScanlineStride * dstRegion.y + numBands * dstRegion.x;
            for (final TiffImageReader.Tile tile : getTiles(srcRegion, sourceXSubsampling, sourceYSubsampling, sourcePixelStride, sourceScanlineStride)) {
                /*
                 * Constants used for the iterations.
                 */
                final int targetTileStart = targetImageStart + targetScanlineStride * tile.y + numBands * tile.x;
                final int numSourceBytesToRead =
                        (tile.height-1) * sourceScanlineStride * sourceYSubsampling +
                        (tile.width -1) * sourcePixelStride    * sourceXSubsampling + sourcePixelStride;
                int numTargetPixelsPerRow = tile.width;
                /*
                 * If the data to read and the data to write are contiguous, read and write the
                 * pixels in one single pass. We will do that by pretending that all the data to
                 * read is like a single line.
                 */
                if (tile.width * numBands == targetScanlineStride &&
                    tile.width * sourcePixelStride == sourceScanlineStride &&
                    sourceYSubsampling == 1)
                {
                    numTargetPixelsPerRow *= tile.height;
                }
                /*
                 * Count the number of pixels that remain to be read for a particular row.
                 * This values will be decremented during the iteration until it reach 0,
                 * then reinitialized for a new row.
                 */
                int remainingRowPixels = numTargetPixelsPerRow;
                /*
                 * Initialize the position in the file from which to read data, and position in
                 * the 'targetArray' where to write the first sample value for the current tile.
                 */
                int row            = 0;
                int sourcePosition = 0;
                int targetPosition = targetTileStart;
                while (sourcePosition < numSourceBytesToRead) {
                    /*
                     * Following assertion fails if we did not computed correctly the 'n' value in
                     * this loop (see further below).  Should never fail since 'n' was computed as
                     * Math.min(remainingRowPixels, ...).
                     */
                    assert (remainingRowPixels >= 0) : remainingRowPixels;
                    if (remainingRowPixels == 0) {
                        /*
                         * If we finished reading all sample values for the current row, move
                         * the position to the begining of the next row and ensure that the
                         * source buffer contains at least one pixel.
                         */
                        remainingRowPixels = numTargetPixelsPerRow;
                        sourcePosition = ++row * sourceScanlineStride * sourceYSubsampling;
                        targetPosition =   row * targetScanlineStride + targetTileStart;
                        ensureBufferContains(tile.position + sourcePosition,
                                sourcePixelStride, numSourceBytesToRead - sourcePosition);
                    } else {
                        /*
                         * If the buffer is empty (see the comment at the end of this loop for an
                         * explanation why the buffer is empty), read sample values from the source
                         * file. We try to fill the buffer completly if possible for efficiency, but
                         * the algorithm is tolerant to partial filling.
                         */
                        final int remainingSourceBytesCount = numSourceBytesToRead - sourcePosition;
                        ensureBufferContains(tile.position + sourcePosition,
                                Math.min(remainingSourceBytesCount, buffer.capacity()),
                                remainingSourceBytesCount);
                    }
                    /*
                     * Compute the position in the view buffer. The byte buffer position
                     * must be aligned on a sample value boundary.
                     */
                    assert (buffer.position() % sampleSize) == 0 : buffer;
                    int bufferPosition = buffer.position() / sampleSize;
                    /*
                     * Compute the number of pixels that we can copy for the current row.
                     * The second argument of the Math.min(...) method call is a compact
                     * form (only one division) of the following steps:
                     *
                     *   numSourcePixels = (buffer.remaining() / sourcePixelStride) rounded toward 0.
                     *   numSubsampled = (numSourcePixels / sourceXSubsampling) rounded toward upper.
                     */
                    int n = Math.min(remainingRowPixels, (buffer.remaining() +
                            sourceXSubsamplingStride - sourcePixelStride) / sourceXSubsamplingStride);
                    /*
                     * Update the positions now, as if the copy operations were already completed.
                     */
                    remainingRowPixels -= n;
                    sourcePosition += (n * sourceXSubsamplingStride);
                    /*
                     * We will copy the pixel values in the target array using a fast bulk method
                     * if possible, or a slow loop if we need to apply a subsampling on the fly.
                     */
                    final int sourceStep, targetStep;
                    if (sourceXSubsampling == 1) {
                        sourceStep = targetStep = n * numBands;
                        n = 1;
                    } else {
                        sourceStep = numBands * sourceXSubsampling;
                        targetStep = numBands;
                    }
                    do {
                        sourceBuffer.position(bufferPosition);
                        try {
                            switch (dataType) {
                                case DataBuffer.TYPE_BYTE:   ((ByteBuffer)   sourceBuffer).get((byte[])   targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_USHORT:
                                case DataBuffer.TYPE_SHORT:  ((ShortBuffer)  sourceBuffer).get((short[])  targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_INT:    ((IntBuffer)    sourceBuffer).get((int[])    targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_FLOAT:  ((FloatBuffer)  sourceBuffer).get((float[])  targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_DOUBLE: ((DoubleBuffer) sourceBuffer).get((double[]) targetArray, targetPosition, targetStep); break;
                                default: throw new AssertionError(dataType);
                            }
                        } catch (IndexOutOfBoundsException ex) {
                            switch (dataType) {
                                case DataBuffer.TYPE_BYTE:   ((ByteBuffer)   sourceBuffer).get((byte[])   targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_USHORT:
                                case DataBuffer.TYPE_SHORT:  ((ShortBuffer)  sourceBuffer).get((short[])  targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_INT:    ((IntBuffer)    sourceBuffer).get((int[])    targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_FLOAT:  ((FloatBuffer)  sourceBuffer).get((float[])  targetArray, targetPosition, targetStep); break;
                                case DataBuffer.TYPE_DOUBLE: ((DoubleBuffer) sourceBuffer).get((double[]) targetArray, targetPosition, targetStep); break;
                                default: throw new AssertionError(dataType);
                            }
                        }
                        bufferPosition += sourceStep;
                        targetPosition += targetStep;
                    } while (--n != 0);
                    /*
                     * At this point, either we have read a full row (in which case the buffer position
                     * will be set by the first call to 'ensureBufferContains' in this loop), or either
                     * there is not enough remaining data, in which case the buffer position will be set
                     * by the second call to 'ensureBufferContains' in this loop. Consequently there is
                     * no need to set the buffer position explicitly here.
                     */
                }
            }
        }
        reverseByte = false;
    }

    /**
     * Formats an error message for an invalid TIFF file.
     *
     * @todo Localize.
     */
    private IIOException invalidFile(final String cause) {
        return new IIOException("Invalid value for record " + cause);
    }

    /**
     * Formats an error message with no argument.
     */
    private String error(final short key) {
        return Errors.getResources(getLocale()).getString(key);
    }

    /**
     * Formats an error message with one argument.
     */
    private String error(final short key, final Object arg0) {
        return Errors.getResources(getLocale()).getString(key, arg0);
    }

    /**
     * Formats an error message with two argument.
     */
    private String error(final short key, final Object arg0, final Object arg1) {
        return Errors.getResources(getLocale()).getString(key, arg0, arg1);
    }

    /**
     * Closes the file channel. If the channel is already closed, then this method does nothing.
     *
     * @throws IOException If an error occurred while closing the channel.
     */
    @Override
    protected void close() throws IOException {
        super.close();
        positionBuffer = 0;
        filePosition   = 0;
        countIFD       = 0;
        currentImage   = -1;
        bitsPerSample  = null;
        tileOffsets    = null;
        rawImageType   = null;
        if (channel != null) {
            channel.close();
            channel = null;
            // Keep the buffer, since we may reuse it for the next image.
        }
    }




    /**
     * Service provider interface (SPI) for {@code RawTiffImageReader}s. This SPI provides
     * necessary implementation for creating default {@link RawTiffImageReader} instances.
     * <p>
     * The default constructor initializes the fields to the values listed below.
     * Users wanting different values should create a subclass of {@code Spi} and
     * set the desired values in their constructor.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "tiff"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "image/tiff"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.RawTiffImageReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;Value of {@link org.geotoolkit.util.Version#GEOTOOLKIT}&nbsp;</td></tr>
     * </table>
     * <p>
     * By default, this provider register itself <em>after</em> the provider supplied by the
     * <cite>Image I/O extension for JAI</cite>, because the later supports a wider range of
     * formats. See {@link #onRegistration onRegistration} for more information.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.16
     *
     * @since 3.16
     * @module
     */
    public static class Spi extends SpatialImageReader.Spi implements SystemOverride {
        /**
         * Default list of file extensions.
         */
        private static final String[] SUFFIXES = new String[] {"tiff", "tif", "geotiff"};

        /**
         * The mime types for the {@link RawTiffImageReader}.
         */
        private static final String[] MIME_TYPES = {"image/tiff", "image/x-geotiff", "image/tiff;subtype=geotiff"};

        /**
         * The list of valid input types.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {
            File.class, String.class
        };

        /**
         * Constructs a default {@code TiffImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            super();
            names           = SUFFIXES;
            suffixes        = SUFFIXES;
            inputTypes      = INPUT_TYPES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.TiffImageReader";
            // Current implementation does not support metadata.
            nativeStreamMetadataFormatName = null;
            nativeImageMetadataFormatName  = null;
        }

        /**
         * Returns a brief, human-readable description of this service provider
         * and its associated implementation. The resulting string should be
         * localized for the supplied locale, if possible.
         *
         * @param  locale A Locale for which the return value should be localized.
         * @return A String containing a description of this service provider.
         */
        @Override
        public String getDescription(final Locale locale) {
            return "TIFF image reader";
        }

        /**
         * Current implementation returns {@code false} in every case. Future implementation
         * may perform a better check if the {@link RawTiffImageReader} become less restrictive.
         *
         * @param  source The input source to be decoded.
         * @return {@code true} if the given source can be used by {@link RawTiffImageReader}.
         * @throws IOException if an I/O error occurs while reading the stream.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
             final FileInputStream in;
//            if (input instanceof String) {
//                in = new FileInputStream((String) input);
//            } else {
//                in = new FileInputStream((File) input);
//            }
//            channel = in.getChannel();
            if (source instanceof String) {
                in = new FileInputStream((String) source);
            } else if (source instanceof  File) {
                in = new FileInputStream((File) source);
            } else {
                return false;
            }
            final FileChannel channel = in.getChannel();
            //-- Closing the channel will close the input stream.
            ByteBuffer buffer = ByteBuffer.allocateDirect(16);
            buffer.clear();
            channel.read(buffer);
//            readFully(16, 1024); //-- Header size of Big TIFF (the standard header size is 8 bytes).
            buffer.position(0);
            final byte c = buffer.get();
            if (c != buffer.get()) {
                return false;
            }
            final ByteOrder order;
            if (c == 'M') {
                order = ByteOrder.BIG_ENDIAN;
            } else if (c == 'I') {
                order = ByteOrder.LITTLE_ENDIAN;
            } else {
                return false;
            }
            final short version = buffer.order(order).getShort(); 
            if ((version == 0x002B)) {
                if (buffer.getShort() != 8 || buffer.getShort() != 0) {
                    return false; //-- invalide offset size
//                    throw invalidFile("OffsetSize");
                }
            } else if (version != 0x002A) {
                return false;//-- invalid magic number
//                throw invalidFile("MagicNumber");
            }
            return true;
        }

        /**
         * Returns an instance of the {@code ImageReader} implementation associated
         * with this service provider.
         *
         * @param  extension An optional extension object, which may be null.
         * @return An image reader instance.
         * @throws IOException if the attempt to instantiate the reader fails.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new TiffImageReader(this);
        }

        /**
         * Invoked when this Service Provider is registered. By default, this method
         * {@linkplain ServiceRegistry#setOrdering(Class, Object, Object) sets the ordering}
         * of this {@code RawTiffImageReader.Spi} after the one provided in <cite>Image I/O
         * extension for JAI</cite>. This behavior can be changed by setting the
         * <code>{@value org.geotoolkit.lang.SystemOverride#KEY_ALLOW_OVERRIDE}</code>
         * system property explicitly to {@code true}.
         * <p>
         * Note that the Geotk TIFF image reader will be selected only if the source given to the
         * {@link #canDecodeInput(Object)} method is compliant with the restrictions documented
         * in {@link RawTiffImageReader} javadoc, otherwise the standard TIFF image reader will
         * be selected instead.
         *
         * @param registry The registry where is service is registered.
         * @param category The category for which this service is registered.
         */
        @Override
        public void onRegistration(final ServiceRegistry registry, final Class<?> category) {
            super.onRegistration(registry, category);
            if (category.equals(ImageReaderSpi.class)) {
                for (Iterator<ImageReaderSpi> it = registry.getServiceProviders(ImageReaderSpi.class, false); it.hasNext();) {
                    ImageReaderSpi other = it.next();
                    if (other != this && ArraysExt.contains(other.getFormatNames(), "tiff")) {
                        ImageReaderSpi last = this;
                        try {
                            if (Boolean.getBoolean(KEY_ALLOW_OVERRIDE)) {
                                last  = other;
                                other = this;
                            }
                        } catch (SecurityException e) {
                            Logging.recoverableException(TiffImageReader.Spi.class, "onRegistration", e);
                        }
                        registry.setOrdering(ImageReaderSpi.class, other, last);
                    }
                }
            }
        }
    }
}