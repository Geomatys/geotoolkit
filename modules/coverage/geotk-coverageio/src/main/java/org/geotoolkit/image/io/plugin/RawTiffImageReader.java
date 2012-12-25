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
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.image.SampleModels;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.lang.SystemOverride;
import org.geotoolkit.resources.Errors;


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
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
public class RawTiffImageReader extends SpatialImageReader {
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
    private static final short
            TYPE_BYTE  =  6, TYPE_UBYTE  =  1,
            TYPE_SHORT =  8, TYPE_USHORT =  3,
            TYPE_INT   =  9, TYPE_UINT   =  4, TYPE_IFD  = 13, // IFD is like UINT.
            TYPE_LONG  = 17, TYPE_ULONG  = 16, TYPE_IFD8 = 18, // IFD is like ULONG.
            TYPE_FLOAT = 11, TYPE_DOUBLE = 12;

    /**
     * The size of each type in bytes, or 0 if unknown.
     */
    private static final int[] TYPE_SIZE = new int[19];
    static {
        final int[] size = TYPE_SIZE;
        size[TYPE_BYTE]  = size[TYPE_UBYTE]                   =    Byte.SIZE / Byte.SIZE;
        size[TYPE_SHORT] = size[TYPE_USHORT]                  =   Short.SIZE / Byte.SIZE;
        size[TYPE_INT]   = size[TYPE_UINT]  = size[TYPE_IFD]  = Integer.SIZE / Byte.SIZE;
        size[TYPE_LONG]  = size[TYPE_ULONG] = size[TYPE_IFD8] =    Long.SIZE / Byte.SIZE;
        size[TYPE_FLOAT]                                      =   Float.SIZE / Byte.SIZE;
        size[TYPE_DOUBLE]                                     =  Double.SIZE / Byte.SIZE;
    }

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
     * Creates a new reader.
     *
     * @param provider The provider, or {@code null} if none.
     */
    public RawTiffImageReader(final Spi provider) {
        super((provider != null) ? provider : new Spi());
        buffer = ByteBuffer.allocateDirect(8196);
        positionIFD = new long[4];
        currentImage = -1;
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
            if (input instanceof String) {
                in = new FileInputStream((String) input);
            } else {
                in = new FileInputStream((File) input);
            }
            channel = in.getChannel();
            // Closing the channel will close the input stream.
            buffer.clear();
            readFully(16, 1024); // Header size of Big TIFF (the standard header size is 8 bytes).
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
                channel.position(position);
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
            min -= n;
        }
        positionBuffer = (filePosition = channel.position()) - buffer.position();
        buffer.rewind();
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
        final long position = readInt();
        if (position != 0) {
            if (countIFD == positionIFD.length) {
                positionIFD = Arrays.copyOf(positionIFD, Math.max(4, countIFD*2));
            }
            positionIFD[countIFD++] = position;
            return true;
        } else {
            positionIFD = XArrays.resize(positionIFD, countIFD);
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
        open(); // Does nothing if already open.
        if (countIFD >= 0) { // Should never be 0 actually.
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
            open(); // Does nothing if already open.
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
                if (countIFD >= 0) { // Should never be 0 actually.
                    int imageAhead = imageIndex - countIFD;
                    while (imageAhead >= 0) {
                        long position = positionIFD[countIFD - 1];
                        ensureBufferContains(position, shortSize, IFD_SIZE);
                        final long n = readShort();
                        position += shortSize;
                        ensureBufferContains(position + n * entrySize, intSize, IFD_SIZE);
                        if (!nextImageFileDirectory()) {
                            throw new IndexOutOfBoundsException(error(
                                    Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, imageIndex));
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

                    final Collection<long[]> deferred = new ArrayList<>(4);
                    long position = positionIFD[imageIndex];
                    ensureBufferContains(position, shortSize + intSize, IFD_SIZE);
                    final long n = readShort();
                    position += shortSize;
                    for (int i=0; i<n; i++) {
                        ensureBufferContains(position, entrySize + intSize, IFD_SIZE);
                        parseDirectoryEntries(deferred);
                        position += entrySize;
                    }
                    /*
                     * Complete the arrays that needs further processing.
                     */
                    readDeferredArrays(deferred.toArray(new long[deferred.size()][]));
                    /*
                     * Declare the image as valid only if the mandatory information are present.
                     */
                    ensureDefined(imageWidth,      "imageWidth");
                    ensureDefined(imageHeight,     "imageHeight");
                    ensureDefined(samplesPerPixel, "samplesPerPixel");
                    if (true) {
                        ensureDefined(tileWidth,   "tileWidth");
                        ensureDefined(tileHeight,  "tileHeight");
                        ensureDefined(tileOffsets, "tileOffsets");
                    }
                    currentImage = imageIndex;
                    return;
                }
            }
            throw new IndexOutOfBoundsException(error(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, imageIndex));
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
            throw new IIOException(error(Errors.Keys.NO_SUCH_ELEMENT_NAME_$1, name));
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
            throw new IIOException(error(Errors.Keys.NO_SUCH_ELEMENT_NAME_$1, name));
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
        final short id = buffer.getShort();
        switch (id) {
            case 0x0100: imageWidth      = entryValue ("imageWidth");              break;
            case 0x0101: imageHeight     = entryValue ("imageHeight");             break;
            case 0x0102: bitsPerSample   = entryValues("bitsPerSample", deferred); break;
            case 0x0115: samplesPerPixel = entryValue ("samplesPerPixel");         break;
            case 0x0142: tileWidth       = entryValue ("tileWidth");               break;
            case 0x0143: tileHeight      = entryValue ("tileHeight");              break;
            case 0x0144: tileOffsets     = entryValues("tileOffsets", deferred);   break;
            case 0x011C: { // PlanarConfiguration.
                final int planarConfiguration = entryValue("PlanarConfiguration");
                if (planarConfiguration != 1) { // '1' stands for "chunky", 2 for "planar".
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2,
                            "planarConfiguration", planarConfiguration));
                }
                break;
            }
            case 0x0106: { // PhotometricInterpretation.
                final int photometricInterpretation = entryValue("photometricInterpretation");
                if (photometricInterpretation != 2) { // '2' stands for RGB.
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2,
                            "photometricInterpretation", photometricInterpretation));
                }
                break;
            }
            case 0x0103: { // Compression.
                final int compression = entryValue("compression");
                if (compression != 1) { // '1' stands for "uncompressed".
                    final Object name;
                    switch (compression) {
                        case 6:  name = "JPEG";      break;
                        case 7:  name = "LZW";       break;
                        default: name = compression; break;
                    }
                    throw new UnsupportedImageFormatException(error(Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2,
                            "compression", name));
                }
                break;
            }
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
            case TYPE_UBYTE:  return buffer.get() & 0xFFL;
            case TYPE_SHORT:  return buffer.getShort();
            case TYPE_USHORT: return buffer.getShort() & 0xFFFFL;
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
     * Reads a value from the directory entry. This method can be invoked right after the entry ID.
     * The {@linkplain #buffer} is assumed to have all the required bytes for one entry. The buffer
     * position is undetermined after this method call.
     *
     * @param  name The name of the entry being parsed.
     * @return The entry value as an integer.
     * @throws IIOException If the entry can not be read as an integer.
     */
    private int entryValue(final String name) throws IIOException {
        final short type = buffer.getShort();
        if (readInt() != 1) {
            throw new IIOException(error(Errors.Keys.DUPLICATED_VALUE_$1, name));
        }
        switch (type) {
            case TYPE_BYTE:   return buffer.get();
            case TYPE_UBYTE:  return buffer.get() & 0xFF;
            case TYPE_SHORT:  return buffer.getShort();
            case TYPE_USHORT: return buffer.getShort() & 0xFFFF;
            case TYPE_INT:
            case TYPE_UINT:   return buffer.getInt();
            default: throw new IIOException(error(Errors.Keys.ILLEGAL_PARAMETER_TYPE_$2, name, type));
        }
    }

    /**
     * Reads many values from the director entry. This method can be invoked right after the entry
     * ID. The {@linkplain #buffer} is assumed to have all the required bytes for one entry. The
     * buffer position is undetermined after this method call.
     * <p>
     * Note that the returned array may need further processing after this method call, in which
     * case it is added to the {@code deferred} collection.
     *
     * @param  name The name of the entry being parsed.
     * @param  deferred A collection where to add arrays for which the reading has been deferred.
     * @return The array of values (need further processing if added in the {@code deferred}) collection).
     * @throws IIOException If the entry can not be read as an integer.
     */
    private long[] entryValues(final String name, final Collection<long[]> deferred) throws IIOException {
        final short type  = buffer.getShort();
        final long  count = readInt();
        if (count > Integer.MAX_VALUE) {
            throw new IIOException(error(Errors.Keys.FILE_HAS_TOO_MANY_DATA));
        }
        final int dataSize, intSize = isBigTIFF ? SIZE_BIG_INT : SIZE_INT;
        if (type < 0 || type == TYPE_FLOAT || type == TYPE_DOUBLE || (dataSize = TYPE_SIZE[type]) == 0) {
            throw new IIOException(error(Errors.Keys.ILLEGAL_PARAMETER_TYPE_$2, name, type));
        }
        final long[] values = new long[(int) count];
        if (values.length * dataSize <= intSize) {
            for (int i=0; i<values.length; i++) {
                values[i] = read(type);
            }
        } else {
            values[0] = readInt();
            values[1] = type;
            deferred.add(values);
        }
        return values;
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
        Arrays.sort(deferred, OFFSET_COMPARATOR);
        for (final long[] array : deferred) {
            final short type = (short) array[1];
            assert type == array[1]; // Ensure that the value is in the range of the short type.
            final int dataSize = TYPE_SIZE[type];
            long position = array[0];
            for (int i=0; i<array.length;) {
                int n = array.length - i;
                final int length = n * dataSize;
                ensureBufferContains(position, Math.min(length, buffer.capacity()), Math.max(length, 1024));
                n = Math.min(n, buffer.remaining() / dataSize);
                position += n * dataSize;
                while (--n >= 0) {
                    array[i++] = read(type);
                }
            }
        }
    }

    /**
     * Comparator used for sorting the array to be processed by {@link #readDeferredArrays(long[][])},
     * in order to read the data sequentially from the disk.
     */
    private static final Comparator<long[]> OFFSET_COMPARATOR = new Comparator<long[]>() {
        @Override public int compare(final long[] o1, final long[] o2) {
            return MathFunctions.sgn(o1[0] - o2[0]);
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
    private static final class Tile extends Rectangle implements Comparable<Tile> {
        /** Tile position in file.  */ final long position;

        /** Creates a new instance for the given position in file and target region. */
        Tile(final int x, final int y, final int width, final int height, final long position) {
            super(x, y, width, height);
            this.position = position;
            assert !isEmpty() && x >= 0 && y >= 0: this;
        }

        /** Compares this tile with the specified tile for order of file position. */
        @Override public int compareTo(final Tile other) {
            return MathFunctions.sgn(position - other.position);
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
    private Tile[] getTiles(final Rectangle r, final int xSubsampling, final int ySubsampling,
            final int pixelStride, final int scanlineStride)
    {
        final int minTileX  =  r.x / tileWidth;                               // Inclusive
        final int minTileY  =  r.y / tileHeight;                              // Inclusive
        final int maxTileX  = (r.x + r.width  + tileWidth  - 2) / tileWidth;  // Exclusive
        final int maxTileY  = (r.y + r.height + tileHeight - 2) / tileHeight; // Exclusive
        final int rowLength = (imageWidth + tileWidth - 1) / tileWidth;
        final Tile[] tiles  = new Tile[(maxTileX - minTileX) * (maxTileY - minTileY)];
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
                tiles[count++]      = new Tile(x, y, width, height, tileOffsets[tileIndex] + offset);
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
            final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            final int type;
            final int[] bits;
            final long[] bitsPerSample = this.bitsPerSample;
            if (bitsPerSample != null) {
                int size = 0;
                bits = new int[bitsPerSample.length];
                for (int i=0; i<bits.length; i++) {
                    final long b = bitsPerSample[i];
                    if ((bits[i] = (int) b) != b) {
                        // Verify that 'bitPerSample' values are inside 'int' range (paranoiac check).
                        throw new UnsupportedImageFormatException(error(
                                Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2, "bitsPerSample", b));
                    }
                    if (i != 0 && b != size) {
                        // Current implementation requires all sample values to be of the same size.
                        throw new UnsupportedImageFormatException(error(Errors.Keys.INCONSISTENT_VALUE));
                    }
                    size = (int) b;
                }
                /*
                 * We require exact value, because the reading process read all sample values
                 * in one contiguous read operation.
                 */
                switch (size) {
                    case Byte   .SIZE: type = DataBuffer.TYPE_BYTE;   break;
                    case Short  .SIZE: type = DataBuffer.TYPE_USHORT; break;
                    case Integer.SIZE: type = DataBuffer.TYPE_INT;    break;
                    default: {
                        throw new UnsupportedImageFormatException(error(
                                Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2, "bitsPerSample", size));
                    }
                }
            } else {
                /*
                 * If the bitsPerSample field were not specified, assume bytes.
                 */
                type = DataBuffer.TYPE_BYTE;
                bits = new int[(samplesPerPixel != 0) ? samplesPerPixel : cs.getNumComponents()];
                Arrays.fill(bits, 8);
            }
            final boolean hasAlpha = bits.length > cs.getNumComponents();
            final ColorModel cm = new ComponentColorModel(cs, bits, hasAlpha, false,
                    hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE, type);
            rawImageType = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(imageWidth, imageHeight));
        }
        return rawImageType;
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
        final BufferedImage image = getDestination(param, getImageTypes(imageIndex), imageWidth, imageHeight);
        final Rectangle srcRegion = new Rectangle();
        final Rectangle dstRegion = new Rectangle();
        computeRegions(param, imageWidth, imageHeight, image, srcRegion, dstRegion);
        read(image.getRaster(), param, srcRegion, dstRegion);
        return image;
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
        final int targetPixelStride    = sampleSize * numBands;
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
        for (int bank=0; bank<bankOffsets.length; bank++) {
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
            final int targetImageStart = bankOffsets[bank] + targetScanlineStride * dstRegion.y + targetPixelStride * dstRegion.x;
            for (final Tile tile : getTiles(srcRegion, sourceXSubsampling, sourceYSubsampling, sourcePixelStride, sourceScanlineStride)) {
                /*
                 * Constants used for the iterations.
                 */
                final int targetTileStart = targetImageStart + targetScanlineStride * tile.y + targetPixelStride * tile.x;
                final int numSourceBytesToRead =
                        (tile.height-1) * sourceScanlineStride * sourceYSubsampling +
                        (tile.width -1) * sourcePixelStride    * sourceXSubsampling + sourcePixelStride;
                int numTargetPixelsPerRow = tile.width;
                /*
                 * If the data to read and the data to write are contiguous, read and write the
                 * pixels in one single pass. We will do that by pretending that all the data to
                 * read is like a single line.
                 */
                if (tile.width * targetPixelStride == targetScanlineStride &&
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
                        switch (dataType) {
                            case DataBuffer.TYPE_BYTE:   ((ByteBuffer)   sourceBuffer).get((byte[])   targetArray, targetPosition, targetStep); break;
                            case DataBuffer.TYPE_USHORT:
                            case DataBuffer.TYPE_SHORT:  ((ShortBuffer)  sourceBuffer).get((short[])  targetArray, targetPosition, targetStep); break;
                            case DataBuffer.TYPE_INT:    ((IntBuffer)    sourceBuffer).get((int[])    targetArray, targetPosition, targetStep); break;
                            case DataBuffer.TYPE_FLOAT:  ((FloatBuffer)  sourceBuffer).get((float[])  targetArray, targetPosition, targetStep); break;
                            case DataBuffer.TYPE_DOUBLE: ((DoubleBuffer) sourceBuffer).get((double[]) targetArray, targetPosition, targetStep); break;
                            default: throw new AssertionError(dataType);
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
    private String error(final int key) {
        return Errors.getResources(getLocale()).getString(key);
    }

    /**
     * Formats an error message with one argument.
     */
    private String error(final int key, final Object arg0) {
        return Errors.getResources(getLocale()).getString(key, arg0);
    }

    /**
     * Formats an error message with two argument.
     */
    private String error(final int key, final Object arg0, final Object arg1) {
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
        private static final String[] SUFFIXES = new String[] {"tiff", "tif"};

        /**
         * The mime types for the {@link RawTiffImageReader}.
         */
        private static final String[] MIME_TYPES = {"image/tiff"};

        /**
         * The list of valid input types.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {
            File.class, String.class
        };

        /**
         * Constructs a default {@code RawTiffImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = SUFFIXES;
            suffixes        = SUFFIXES;
            inputTypes      = INPUT_TYPES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.RawTiffImageReader";
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
            return false;
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
            return new RawTiffImageReader(this);
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
                    if (other != this && XArrays.contains(other.getFormatNames(), "tiff")) {
                        ImageReaderSpi last = this;
                        try {
                            if (Boolean.getBoolean(KEY_ALLOW_OVERRIDE)) {
                                last  = other;
                                other = this;
                            }
                        } catch (SecurityException e) {
                            Logging.recoverableException(Spi.class, "onRegistration", e);
                        }
                        registry.setOrdering(ImageReaderSpi.class, other, last);
                    }
                }
            }
        }
    }
}
