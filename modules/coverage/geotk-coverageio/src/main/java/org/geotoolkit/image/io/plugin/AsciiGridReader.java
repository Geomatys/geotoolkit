/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;
import com.sun.media.imageio.stream.RawImageInputStream;

import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.image.io.TextImageReader;
import org.geotoolkit.image.io.SampleConverter;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.stream.ChannelImageInputStream;
import org.geotoolkit.internal.image.io.DataTypes;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.util.Version;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Reader for the ASCII Grid format. As the "ASCII" name implies, the data files are read in
 * US-ASCII character encoding no matter what the {@link Spi#charset} value is. In addition,
 * the US locale is enforced no matter what the {@link Spi#locale} value is, with a tolerance
 * for the decimal separator character which can be either {@code '.'} or {@code ','}.
 * <p>
 * ASCII grid files contains a header before the actual data. The header contains (<var>key</var>
 * <var>value</var>) pairs, one pair per line and using the space as the separator between key and
 * value. The valid keys are listed in table below (see the
 * <a href="http://daac.ornl.gov/MODIS/ASCII_Grid_Format_Description.html">ASCII Grid Format
 * Description</a> for more details). Note that Geotk adds some extensions to the standard
 * ASCII grid format:
 * <p>
 * <ul>
 *   <li>{@linkplain #isComment(String) Comment lines} and empty lines are ignored.</li>
 *   <li>The {@code '='} and {@code ':'} characters can be used as a separator between
 *       the keys and the values.</li>
 *   <li>The {@code CELLSIZE} attribute can be substituted by the {@code DX} and {@code DY}
 *       attributes. {@code DX}/{@code DY} are not standard, but can be produced by the GDAL
 *       library. See <a href="http://www.gdal.org/frmt_various.html#AAIGrid">GDAL notes</a>
 *       for more information.</li>
 *   <li>The {@code "MIN_VALUE"} and {@code "MAX_VALUE"} attributes are Geotk extensions
 *       not defined in the ASCII Grid standard. While optional, they are quite convenient
 *       for setting the color space.</li>
 * </ul>
 * <p>
 * Subclasses can add their own (<var>key</var>, <var>value</var>) pairs, or modify the
 * ones defined below, by overriding the {@link #processHeader(Map)} method.
 * <p>
 * <table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue">
 *     <th>Keyword</th>
 *     <th>Value type</th>
 *     <th>Obligation</th>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code NCOLS}&nbsp;</td>
 *     <td>&nbsp;Integer&nbsp;</td>
 *     <td>&nbsp;Mandatory&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code NROWS}&nbsp;</td>
 *     <td>&nbsp;Integer&nbsp;</td>
 *     <td>&nbsp;Mandatory&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code XLLCORNER} or {@code XLLCENTER}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Mandatory&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code YLLCORNER} or {@code YLLCENTER}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Mandatory&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code CELLSIZE}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Mandatory, unless {@code DX} and {@code DY} are present&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code DX} and {@code DY}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Accepted but non-standard&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code NODATA_VALUE}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Optional&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code MIN_VALUE}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Optional - this is a Geotk extension&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code MAX_VALUE}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Optional - this is a Geotk extension&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code BINARY_TYPE}&nbsp;</td>
 *     <td>&nbsp;String&nbsp;</td>
 *     <td>&nbsp;Optional - this is a Geotk extension&nbsp;</td>
 *   </tr>
 * </table>
 * <p>
 * {@code BINARY_TYPE} is a Geotk extension provided for performance only. If this attribute
 * is provided and if the input is a {@link java.io.File}, {@link java.net.URL} or
 * {@link java.net.URI}, then {@code AsciiGridReader} will looks for a file of the same name
 * with the {@code ".raw"} extension. If this file is found, then the data in the ASCII file
 * will be ignored (they can be non-existent) and the RAW file will be read instead, which is
 * usually much faster. The value of the {@code BINARY_TYPE} attribute specify the data type:
 * {@code BYTE}, {@code SHORT}, {@code USHORT}, {@code INT}, {@code FLOAT} or {@code DOUBLE}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @see <a href="http://daac.ornl.gov/MODIS/ASCII_Grid_Format_Description.html">ASCII Grid Format Description</a>
 * @see <a href="http://en.wikipedia.org/wiki/ESRI_grid">ESRI Grid on Wikipedia</a>
 * @see AsciiGridWriter
 *
 * @since 3.08 (derived from 3.07)
 * @module
 *
 * @todo The current implementation ignores the <code>seekForwardOnly</code> parameter.
 *       It processes as if that parameter was always set to <code>true</code>.
 */
public class AsciiGridReader extends TextImageReader {
    /**
     * The size of the NIO direct buffer to create.
     */
    private static final int BUFFER_SIZE = 16 * 1024;

    /**
     * {@code true} if the header has been read.
     */
    private boolean headerValid;

    /**
     * The {@code NCOLS} and {@code NROWS} attributes read from the header.
     * Those values are valid only if {@link #headerValid} is {@code true}.
     */
    private int width, height;

    /**
     * The {@code XLLCORNER | XLLCENTER} and {@code YLLCORNER | YLLCENTER} attributes read
     * from the header. Those values are valid only if {@link #headerValid} is {@code true}.
     */
    private double xll, yll;

    /**
     * {@code true} if the {@link #xll} and {@link #yll} values are determined from the
     * {@code XLLCENTER} and {@code YLLCENTER} attributes, or {@code false} if they are
     * determined from the {@code XLLCORNER} and {@code YLLCORNER} attributes.
     */
    private boolean xCenter, yCenter;

    /**
     * The {@code CELLSIZE} attribute, or the {@code DX} and {@code DY} attributes.
     * This value is valid only if {@link #headerValid} is {@code true}.
     */
    private double scaleX, scaleY;

    /**
     * The optional {@code NODATA_VALUE} attribute, or {@code NaN} if none.
     * This value is valid only if {@link #headerValid} is {@code true}.
     */
    private double fillValue;

    /**
     * The minimum and maximum values, or infinities if they are not specified.
     */
    private double minValue, maxValue;

    /**
     * If a binary type has been specified, the corresponding {@link DataBuffer}
     * constant. Otherwise {@link DataBuffer#TYPE_UNDEFINED}. This information is
     * valid only if {@link headerValid} is {@code true}.
     */
    private int binaryType;

    /**
     * The image reader for reading binary images, or {@code null} if not needed.
     * This is used only if {@link #binaryType} is defined.
     */
    private transient ImageReader binaryReader;

    /**
     * The buffer used for data transfer. This is created only when first needed.
     * If more than one image is read with the same reader, this buffer will be
     * recycled for each image.
     */
    private transient ByteBuffer buffer;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     */
    protected AsciiGridReader(final Spi provider) {
        super(provider);
    }

    /**
     * Parses the given string as a double value. Before to parse, this method replaces the
     * {@code ','} character by {@code '.'} in order to take in account the ASCII-Grid format
     * created by localized version of ESRI softwares.
     *
     * @param  value The value to parse.
     * @return The value as a {@code double}.
     * @throws NumberFormatException If the given string can not be parsed as a {@code double}.
     */
    private static double parseDouble(final String value) throws NumberFormatException {
        return Double.parseDouble(value.replace(',', '.'));
    }

    /**
     * Reads the header, if it was not already read. If successful, then all the instance
     * variables declared in this class should be assigned to their final value.
     *
     * @throws IOException If an error occurred while reading the header.
     */
    private void ensureHeaderRead() throws IOException {
        if (!headerValid) {
            xCenter = true;
            yCenter = true;
            final Map<String,String> header = readHeader();
            processHeader(header);
            String key = null;
            try {
                width  = Integer.parseInt(ensureDefined(key = "NCOLS", header.remove(key)));
                height = Integer.parseInt(ensureDefined(key = "NROWS", header.remove(key)));
                String value = header.remove(key = "CELLSIZE");
                if (value != null) {
                    scaleX = scaleY = parseDouble(value);
                } else {
                    // If missing, declare that CELLSIZE is missing since DX and DY are not standard.
                    scaleX = parseDouble(ensureDefined("CELLSIZE", header.remove(key = "DX")));
                    scaleY = parseDouble(ensureDefined("CELLSIZE", header.remove(key = "DY")));
                }
                value = header.remove(key = "NODATA_VALUE");
                fillValue = (value != null) ? parseDouble(value) : super.getPadValue(0);
                value = header.remove(key = "MIN_VALUE");
                minValue = (value != null) ? parseDouble(value) : Double.NEGATIVE_INFINITY;
                value = header.remove(key = "MAX_VALUE");
                maxValue = (value != null) ? parseDouble(value) : Double.POSITIVE_INFINITY;
                value = header.remove(key = "XLLCENTER");
                if (value == null) {
                    value = header.remove(key = "XLLCORNER");
                    xCenter = false;
                }
                xll = parseDouble(ensureDefined(key, value));
                value = header.remove(key = "YLLCENTER");
                if (value == null) {
                    value = header.remove(key = "YLLCORNER");
                    yCenter = false;
                }
                yll = parseDouble(ensureDefined(key, value));
            } catch (NumberFormatException cause) {
                throw new IIOException(Warnings.message(this, Errors.Keys.UNPARSABLE_NUMBER_$1, key), cause);
            }
            /*
             * The binary format, which is a Geotk extension.
             */
            binaryType = DataBuffer.TYPE_UNDEFINED;
            String value = header.remove("BINARY_TYPE");
            if (value != null) {
                binaryType = DataTypes.getDataBufferType(value);
                if (binaryType == DataBuffer.TYPE_UNDEFINED) {
                    Warnings.log(this, null, AsciiGridReader.class, "readHeader",
                            Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2, "BINARY_TYPE", value);
                }
            }
            headerValid = true;
            /*
             * We should not have any entry left.
             */
            for (final String extra : header.keySet()) {
                Warnings.log(this, null, AsciiGridReader.class, "readHeader",
                        Errors.Keys.UNKNOWN_PARAMETER_$1, extra);
            }
        }
    }

    /**
     * Ensures that the value is non-null. Otherwise an exception is thrown using the given name.
     *
     * @param  name  The name of the properties to be tested.
     * @param  value The value of the properties.
     * @return The given value, guaranteed to be non-null.
     * @throws IIOException If the given value was null.
     */
    private String ensureDefined(final String name, final String value) throws IIOException {
        if (value == null || value.isEmpty()) {
            throw new ImageMetadataException(Warnings.message(this, Errors.Keys.NO_PARAMETER_$1, name));
        }
        return value;
    }

    /**
     * Reads the header from the {@linkplain #getChannel() channel}. The given buffer is used for
     * transferring data. The file encoding is assumed ASCII. The {@linkplain #isComment(String)
     * comment lines} are skipped. The separator between keys and values is assumed any of space,
     * {@code ':'} or {@code '='} character. The scan stop at the first line which seems to
     * contains a number.
     *
     * @return The header as a new, modifiable, map.
     * @throws IOException If an error occurred while reading.
     */
    private Map<String,String> readHeader() throws IOException {
        final ReadableByteChannel channel = getChannel();
        final StringBuilder       stbuff  = new StringBuilder();
        final Map<String,String>  header  = new HashMap<>();
        ByteBuffer buffer = this.buffer;
        if (buffer == null) {
            this.buffer = buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        }
        buffer.clear();
        buffer.limit(buffer.position()); // For forcing a filling of the buffer.
readHeader: while (true) {
            final int startPos = buffer.position();
            boolean skipWhitespaces = true;
            /*
             * Reads exactly one line. The buffer will be filled inside the loop.
             * This allow us to fill it again if only a few bytes have been read
             * during the first call to channel.read(buffer).
             */
readLine:   while (true) {
                if (!buffer.hasRemaining()) {
                    final int pos = buffer.position();
                    final int capacity = buffer.capacity();
                    if (pos >= capacity) {
                        throw new ImageMetadataException(Errors.format(
                                Errors.Keys.UNEXPECTED_HEADER_LENGTH_$1, capacity));
                    }
                    /*
                     * Arbitrary read a block of 512 bytes for starting, because the header is
                     * typically small (less than 256 characters). If 512 bytes is not enough,
                     * we will read more bytes up to the buffer capacity.
                     */
                    buffer.limit(Math.min(capacity, Math.max(512, 2*pos)));
                    if (channel.read(buffer) < 0) {
                        if (skipWhitespaces) {
                            break readHeader;
                        } else {
                            break readLine;
                        }
                    }
                    buffer.flip().position(pos);
                }
                char c = (char) (buffer.get() & 0xFF);
                switch (c) {
                    case '\n': break readLine;
                    case '\r': {
                        // Skip the "\n" part in "\r\n" (if any).
                        if (buffer.hasRemaining()) {
                            c = (char) (buffer.get() & 0xFF);
                            if (c != '\n') {
                                buffer.position(buffer.position() - 1);
                            }
                        }
                        break readLine;
                    }
                }
                if (skipWhitespaces) {
                    // If the first non-blank character seems to be part of a number, stop.
                    if (c >= '+' && c <= '9') { // Include +,-./ and digits
                        buffer.position(startPos);
                        break readHeader;
                    }
                    if (c > ' ') {
                        skipWhitespaces = false;
                    }
                }
                stbuff.append(c);
            }
            // At this point, a line has been read. Add it to the buffer.
            String line = stbuff.toString().trim();
            stbuff.setLength(0);
            if (!line.isEmpty() && !isComment(line)) {
                String key = line;
                String value = null;
                final int length = line.length();
                for (int i=0; i<length; i++) {
                    char c = line.charAt(i);
                    if (c <= ' ' || c == ':' || c == '=') {
                        key = line.substring(0, i).toUpperCase(Locale.US);
                        // Skip the whitespaces, if any.
                        while (c <= ' ' && ++i <= length) {
                            c = line.charAt(i);
                            if (c == ':' || c == '=') {
                                i++; // Skip the separator.
                                break;
                            }
                        }
                        value = line.substring(i).trim();
                        break;
                    }
                }
                final Object old = header.put(key, value);
                if (old != null && !old.equals(value)) {
                    throw new ImageMetadataException(Errors.format(
                            Errors.Keys.VALUE_ALREADY_DEFINED_$1, key));
                }
            }
        }
        return header;
    }

    /**
     * Returns the width in pixels of the given image within the input source.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image width.
     * @throws IOException If an error occurs reading the width information
     *         from the input source.
     */
    @Override
    public int getWidth(int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        ensureHeaderRead();
        return width;
    }

    /**
     * Returns the height in pixels of the given image within the input source.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image height.
     * @throws IOException If an error occurs reading the width information
     *         from the input source.
     */
    @Override
    public int getHeight(int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        ensureHeaderRead();
        return height;
    }

    /**
     * Returns the data type which most closely represents the "raw" internal data of the image.
     * If a {@code "BINARY_TYPE"} attribute is presents in the header, then the code corresponding
     * to that attribute is returned. Otherwise {@link DataBuffer#TYPE_FLOAT} is returned.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The data type ({@link DataBuffer#TYPE_FLOAT} by default).
     * @throws IOException If an error occurs reading the format information from the input source.
     */
    @Override
    protected int getRawDataType(int imageIndex) throws IOException {
        checkImageIndex(imageIndex);
        ensureHeaderRead();
        return (binaryType != DataBuffer.TYPE_UNDEFINED) ? binaryType : DataBuffer.TYPE_FLOAT;
    }

    /**
     * Returns metadata associated with the given image.
     *
     * @param  imageIndex The image index.
     * @return The metadata, or {@code null} if none.
     * @throws IOException If an error occurs reading the data information from the input source.
     */
    @Override
    protected SpatialMetadata createMetadata(final int imageIndex) throws IOException {
        if (imageIndex < 0) {
            // Stream metadata.
            return null;
        }
        ensureHeaderRead();
        final PixelOrientation po;
        if (xCenter) {
            po = yCenter ? PixelOrientation.CENTER : PixelOrientation.valueOf("UPPER");
        } else {
            po = yCenter ? PixelOrientation.valueOf("LEFT") : PixelOrientation.UPPER_LEFT;
            // We really want UPPER_LEFT, not LOWER_LEFT in the above condition, because
            // we are reverting the direction of the y axis in the computation of origin
            // and offset vectors.
        }
        final double[] origin = new double[] {xll, yll + scaleX * (height - (yCenter ? 1 : 0))};
        final double[] bounds = new double[] {xll + scaleY * (width - (xCenter ? 1 : 0)), yll};
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME), this, null);
        final GridDomainAccessor domain = new GridDomainAccessor(metadata);
        domain.setOrigin(origin);
        domain.addOffsetVector(scaleX, 0);
        domain.addOffsetVector(0, -scaleY);
        domain.setLimits(new int[2], new int[] {width-1, height-1});
        domain.setSpatialRepresentation(origin, bounds, null, po);
        final boolean hasRange = !Double.isInfinite(minValue) && !Double.isInfinite(maxValue);
        final boolean hasFill  = !Double.isNaN(fillValue);
        if (hasRange || hasFill) {
            final DimensionAccessor dimensions = new DimensionAccessor(metadata);
            dimensions.selectChild(dimensions.appendChild());
            if (hasRange) dimensions.setValueRange(minValue, maxValue);
            if (hasFill)  dimensions.setFillSampleValues(fillValue);
        }
        return metadata;
    }

    /**
     * Invoked automatically after the (<var>key</var>, <var>value</var>) pairs in the header
     * have been read. Subclasses can override this method in order to modify the map passed
     * in argument. They can freely add, remove of modify values.
     * <p>
     * Keys shall be upper-case, and the mandatory attributes defined in the
     * <a href="#skip-navbar_top">class javadoc</a> shall be present in the {@code header} map
     * after the completion of this method.
     * <p>
     * The default implementation does nothing.
     *
     * @param  header A modifiable map of (<var>key</var>, <var>value</var>) pairs.
     * @throws IOException If an error occurred while processing the header values.
     */
    protected void processHeader(final Map<String,String> header) throws IOException {
    }

    /**
     * Reads the image indexed by {@code imageIndex}.
     *
     * @param  imageIndex  The index of the image to be retrieved.
     * @param  param       Parameters used to control the reading process, or null.
     * @return The desired portion of the image.
     * @throws IOException if an input operation failed.
     */
    @Override
    public BufferedImage read(int imageIndex, final ImageReadParam param) throws IOException {
        clearAbortRequest();
        checkImageIndex(imageIndex);
        processImageStarted(imageIndex);
        ensureHeaderRead();
        if (binaryType != DataBuffer.TYPE_UNDEFINED) {
            /*
             * Optional Geotk extension: if a binary file is present, reads
             * that file instead than the ASCII file. This is much faster.
             */
            final BufferedImage image = readBinary(imageIndex, param);
            if (image != null) {
                return image;
            }
        }
        /*
         * Parameters check.
         */
        final int numSrcBands = 1; // To be modified in a future version if we support multi-bands.
        /*
         * Extract user's parameters.
         */
        final int[]      sourceBands; // To be used in a future version if we support multi-bands.
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
        final int width       = this.width;
        final int height      = this.height;
        final int numDstBands = (destinationBands != null) ? destinationBands.length : numSrcBands;
        final SampleConverter[] converters = new SampleConverter[numDstBands];
        final BufferedImage     image      = getDestination(imageIndex, param, width, height, converters);
        final WritableRaster    raster     = image.getRaster();
        checkReadParamBandSettings(param, numSrcBands, raster.getNumBands());

        final Rectangle srcRegion = new Rectangle();
        final Rectangle dstRegion = new Rectangle();
        computeRegions(param, width, height, image, srcRegion, dstRegion);
        final WritableRectIter iter = RectIterFactory.createWritable(raster, dstRegion);
        final int dstBand = (destinationBands != null) ? destinationBands[0] : 0;
        for (int i=dstBand; --i>=0;) {
            if (iter.nextBandDone()) {
                throw new IIOException(Errors.format(Errors.Keys.ILLEGAL_BAND_NUMBER_$1, dstBand));
            }
        }
        if (!iter.finishedBands() && !iter.finishedLines() && !iter.finishedPixels()) {
            final int                 dataType   = raster.getSampleModel().getDataType();
            final char[]              charBuffer = new char[48]; // Arbitrary length limit for a sample value.
            final ByteBuffer          buffer     = this.buffer;
            final ReadableByteChannel channel    = getChannel();
            final SampleConverter     converter  = converters[0];
            /*
             * Before to start reading, set the 'minIndex' in order to prevent new attempt
             * to read an image from this point.
             *
             * TODO: We should mark the position instead if 'seekForwardOnly' is false.
             */
            minIndex = imageIndex + 1;
            /*
             * At this point we have all the metadata needed for reading the sample values.
             * The (x,y) index below are relative to the source region to read, not to the
             * source image.
             */
            final float progressScale = 100f / ((srcRegion.x + srcRegion.height) * width);
            int sy = 1 + srcRegion.y;
loop:       for (int y=0; /* stop condition inside */; y++) {
                if (abortRequested()) {
                    processReadAborted();
                    return image;
                }
                boolean isValid = (--sy == 0);
                if (isValid) {
                    sy = sourceYSubsampling;
                }
                int sx = 1 + srcRegion.x;
                for (int x=0; x<width; x++) {
                    /*
                     * Skip whitespaces or EOL (if any), then copy the next character in the
                     * string buffer until the next space. If we are outside the region to be
                     * read, those characters will be discarded immediately except in case of
                     * error.
                     */
                    int nChar = 0;
                    while (true) {
                        if (!buffer.hasRemaining()) {
                            processImageProgress((y*width + x) * progressScale);
                            buffer.clear();
                            if (channel.read(buffer) < 0) {
                                throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
                            }
                            buffer.flip();
                        }
                        char c = (char) (buffer.get() & 0xFF);
                        if (c > ' ') {
                            if (nChar >= charBuffer.length) {
                                throw new IIOException(Warnings.message(this,
                                        Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2, "cell(" + x + ',' + y + ')',
                                        String.valueOf(charBuffer)));
                            }
                            if (c == ',') c = '.';
                            charBuffer[nChar++] = c;
                        } else if (nChar != 0) {
                            break;
                        }
                    }
                    /*
                     * At this point the sample values is available as a string.
                     * Process only if we need to parse that string.
                     */
                    if (isValid && --sx == 0) {
                        sx = sourceXSubsampling;
                        final String value = new String(charBuffer, 0, nChar);
                        try {
                            switch (dataType) {
                                case DataBuffer.TYPE_DOUBLE: {
                                    iter.setSample(converter.convert(Double.parseDouble(value)));
                                    break;
                                }
                                case DataBuffer.TYPE_FLOAT: {
                                    iter.setSample(converter.convert(Float.parseFloat(value)));
                                    break;
                                }
                                default: {
                                    iter.setSample(converter.convert(Integer.parseInt(value)));
                                    break;
                                }
                            }
                        } catch (NumberFormatException cause) {
                            throw new IIOException(Warnings.message(this,
                                    Errors.Keys.UNPARSABLE_NUMBER_$1, value), cause);
                        }
                        /*
                         * Move to the next pixel in the destination image. The reading process
                         * will stop when we have reached the last pixel (which may be sooner
                         * than the end of the current row in the input image).
                         */
                        if (iter.nextPixelDone()) {
                            if (iter.nextLineDone()) {
                                break loop;
                            }
                            iter.startPixels();
                            isValid = false;
                        }
                    }
                }
                /*
                 * At this point we finished to parse a line. 'isValid' should always be false.
                 * If not, then 'dstRegion' computation was probably inaccurate.
                 */
                assert !isValid : dstRegion;
            }
        }
        processImageComplete();
        return image;
    }

    /**
     * Reads the binary file associated with the ASCII file. This is a Geotk extension
     * enabled only if the {@code "BINARY_TYPE"} attribute is present.
     * <p>
     * Note that this method reuses the existing {@linkplain #buffer}. Consequently, if this
     * method returns a non-null image, then any previous content of the buffer is lost. If
     * this method returns {@code null}, then the previous content still valid.
     *
     * @param  input The file, URL or URI to the binary file.
     * @param  param The parameter of the image to be read.
     * @return The image, or {@code null} if this method can not process.
     * @throws IOException If an error occurred while reading the binary file.
     */
    private BufferedImage readBinary(final int imageIndex, final ImageReadParam param) throws IOException {
        Object binaryInput = IOUtilities.changeExtension(input, "raw");
        if (binaryInput == null || binaryInput == input) {
            // The input type is unknown, or the extension is already "raw".
            return null;
        }
        /*
         * The binary file is optional. In the particular case of File input,
         * we perform a test cheaper than the attempt to open the connection.
         * We also check for the existence of the RAW image reader before to
         * attempt to open the connection.
         */
        if (binaryInput instanceof File) {
            final File file = (File) binaryInput;
            if (!file.isFile() || !file.canRead()) {
                return null;
            }
        }
        ImageReader binaryReader = this.binaryReader;
        if (binaryReader == null) {
            this.binaryReader = binaryReader = new RawReader(null);
        }
        final InputStream binaryStream;
        try {
            binaryStream = IOUtilities.open(binaryInput);
        } catch (IOException e) {
            Warnings.log(this, null, AsciiGridReader.class, "readBinary", e);
            return null;
        }
        /*
         * At this point we have successfully opened a connection to the binary stream.
         * Make the buffer empty before to use it. Now we are not allowed to return null
         * anymore since we have destroyed the previous buffer content.
         */
        final BufferedImage image;
        try {
            buffer.clear().limit(0);
            final ImageInputStream in = new ChannelImageInputStream(Channels.newChannel(binaryStream), buffer);
            try (RawImageInputStream rawStream = new RawImageInputStream(in, getRawImageType(imageIndex),
                   new long[1], new Dimension[] {new Dimension(width, height)}))
            {
                binaryReader.setInput(rawStream, true, true);
                image = binaryReader.read(imageIndex, param);
            }
        } finally {
            binaryStream.close();
            binaryReader.reset();
        }
        return image;
    }

    /**
     * The reader to use for decoding the RAW file that may be provided together with the
     * ASCII image file. This is created by {@link AsciiGridReader#readBinary} only if needed.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @since 3.07
     * @module
     */
    private final class RawReader extends RawImageReader {
        /**
         * Creates a new reader. The provider is set to the ASCII grid reader provider.
         */
        RawReader(final Spi provider) {
            super(provider);
        }

        /**
         * Delegates to the stream metadata of the enclosing {@link AsciiGridReader}.
         * This is provided only for completness with {@link #getImageMetadata(int)}.
         */
        @Override
        public SpatialMetadata getStreamMetadata() throws IOException {
            return AsciiGridReader.this.getStreamMetadata();
        }

        /**
         * Delegates to the image metadata of the enclosing {@link AsciiGridReader}.
         * This is necessary for allowing {@link SpatialImageReader#getDestination}
         * to build the same color model than what it would have done if we were
         * reading with the normal ASCII reader.
         */
        @Override
        public SpatialMetadata getImageMetadata(final int imageIndex) throws IOException {
            return AsciiGridReader.this.getImageMetadata(imageIndex);
        }

        /**
         * Forwards to the enclosing image reader. Note: we do not forward
         * {@code processImageStarted()} because {@link AsciiGridReader#read}
         * has already sent this notification.
         */
        @Override
        protected void processImageComplete() {
            AsciiGridReader.this.processImageComplete();
        }

        /**
         * Forwards to the enclosing image reader.
         */
        @Override
        protected void processImageProgress(final float percentageDone) {
            AsciiGridReader.this.processImageProgress(percentageDone);
        }

        /**
         * Forwards to the enclosing image reader.
         */
        @Override
        protected void processReadAborted() {
            AsciiGridReader.this.processReadAborted();
        }
    }

    /**
     * Closes the input stream created by this reader as documented in the
     * {@linkplain org.geotoolkit.image.io.StreamImageReader#close() super-class method}.
     * If an input stream was created for reading the data from a RAW file, it is also closed.
     */
    @Override
    protected void close() throws IOException {
        headerValid = false;
        super.close(); // First in order to make sure that it is always executed.
        final ImageReader br = binaryReader;
        if (br != null) {
            br.reset();
        }
    }

    /**
     * Allows any resources held by this reader to be released.
     */
    @Override
    public void dispose() {
        buffer = null;
        final ImageReader br = binaryReader;
        if (br != null) {
            binaryReader = null;
            br.dispose();
        }
        super.dispose();
    }




    /**
     * Service provider interface (SPI) for {@code AsciiGridReader}s. This SPI provides
     * the necessary implementation for creating default {@link AsciiGridReader}s using
     * US locale and ASCII character set. The {@linkplain #locale locale} and
     * {@linkplain #charset charset} fields are ignored by the default implementation.
     * <p>
     * The default constructor initializes the fields to the values listed below.
     * Users wanting different values should create a subclass of {@code Spi} and
     * set the desired values in their constructor.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "ascii-grid"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "text/plain"}, {@code "text/x-ascii-grid"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.AsciiGridReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;{@link Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #locale}          &nbsp;</td><td>&nbsp;{@link Locale#US}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #charset}         &nbsp;</td><td>&nbsp;{@code "US-ASCII"}&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See
     *   {@linkplain org.geotoolkit.image.io.TextImageReader.Spi super-class javadoc} for remaining fields</td></tr>
     * </table>
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @see AsciiGridWriter.Spi
     *
     * @since 3.08 (derived from 3.07)
     * @module
     */
    public static class Spi extends TextImageReader.Spi {
        /**
         * The format names for the default {@link AsciiGridReader} configuration.
         */
        static final String[] NAMES = {"ASCII-Grid", "ascii-grid"};

        /**
         * The file suffixes. This replace the {@link TextImageReader.Spi#SUFFIXES} declared
         * in the parent class.
         */
        static final String[] SUFFIXES = {"asc", "ASC", "grd", "GRD", "agr", "AGR"};

        /**
         * The mime types for the default {@link AsciiGridReader} configuration.
         */
        static final String[] MIME_TYPES = {"text/plain", "text/x-ascii-grid"};

        /**
         * The provider of the corresponding image writer.
         */
        private static final String[] WRITERS = {"org.geotoolkit.image.io.plugin.AsciiGridWriter$Spi"};

        /**
         * Constructs a default {@code AsciiGridReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            suffixes        = SUFFIXES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.AsciiGridReader";
            writerSpiNames  = WRITERS;
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
            locale          = Locale.US;
            charset         = Charset.forName("US-ASCII");
            nativeStreamMetadataFormatName = null; // No stream metadata.
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
            return "ASCII grid";
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
            return new AsciiGridReader(this);
        }

        /**
         * Returns {@code true} if the given set of keywords contains at least the
         * {@code "NCOLS"} and {@code "NROWS"} elements.
         */
        @Override
        protected boolean isValidHeader(final Set<String> keywords) {
            return keywords.contains("NCOLS")     && keywords.contains("NROWS")      &&
                  (keywords.contains("XLLCORNER") || keywords.contains("XLLCENTER")) &&
                  (keywords.contains("YLLCORNER") || keywords.contains("YLLCENTER")) &&
                  (keywords.contains("CELLSIZE") || (keywords.contains("DX") && keywords.contains("DY")));
        }

        /**
         * Returns {@code true} unconditionally, because ASCII grid files don't require lines
         * of same length. This method returns {@code true} even if there is no data at all,
         * because those data can be stored in a separated RAW file (this behavior is a Geotk
         * extension)
         */
        @Override
        protected boolean isValidContent(final double[][] rows) {
            return true;
        }
    }
}
