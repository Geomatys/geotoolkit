/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.text;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
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

import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;
import org.geotoolkit.image.io.SampleConverter;
import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.util.Version;
import org.geotoolkit.resources.Errors;


/**
 * Reader for the ASCII Grid format. As the "ASCII" name implies, the data files are read in
 * US-ASCII character encoding no matter what the {@link Spi#charset} value is. In addition,
 * the US locale is enforced no matter what the {@link Spi#locale} value is.
 * <p>
 * ASCII grid files contains a header before the actual data. The header contains (<var>key</var>
 * <var>value</var>) pairs, one pair per line and using the space as the separator between key and
 * value. The valid keys are listed in table below. Note that Geotk adds three extensions to the
 * standard ASCII grid format:
 * <p>
 * <ul>
 *   <li>{@linkplain #isComment(String) Comment lines} and empty lines are ignored.</li>
 *   <li>The {@code '='} and {@code ':'} characters can be used as a separator between
 *       the keys and the values.</li>
 *   <li>The {@code "MIN_VALUE"} and {@code "MAX_VALUE"} attributes are Geotk extensions
 *       not defined in the ASCII Grid standard. While optional, they are quite convenient
 *       for setting the color space.</li>
 * </ul>
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
 *     <td>&nbsp;Mandatory&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code NODATA_VALUE}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Optional, default to -9999&nbsp;</td>
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
 * </table>
 * <p>
 * Subclasses can add their own (<var>key</var>, <var>value</var>) pairs, or modify the values
 * of the ones defined above, by overriding the {@link #processHeader(Map)} method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @see <a href="http://daac.ornl.gov/MODIS/ASCII_Grid_Format_Description.html">ASCII Grid Format Description</a>
 * @see AsciiGridWriter
 *
 * @since 3.07
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
     * The {@code CELLSIZE} attribute.
     * This value is valid only if {@link #headerValid} is {@code true}.
     */
    private double cellsize;

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
     * The buffer used for data transfert. This is created only when first needed.
     * If more than one image is read with the same reader, this buffer will be
     * recycled for each image.
     */
    private transient ByteBuffer buffer;

    /**
     * Constructs a new image reader.
     *
     * @param provider the provider that is invoking this constructor, or {@code null} if none.
     */
    protected AsciiGridReader(final ImageReaderSpi provider) {
        super(provider);
    }

    /**
     * Reads the header, if it was not already read. If successful, then all the instance
     * variables declared in this class should be assigned to their final value.
     *
     * @throws IOException If an error occured while reading the header.
     */
    private void ensureHeaderRead() throws IOException {
        if (!headerValid) {
            xCenter = true;
            yCenter = true;
            final Map<String,String> header = readHeader();
            processHeader(header);
            String key = null;
            try {
                width    = Integer.parseInt  (ensureDefined(key = "NCOLS",    header.remove(key)));
                height   = Integer.parseInt  (ensureDefined(key = "NROWS",    header.remove(key)));
                cellsize = Double.parseDouble(ensureDefined(key = "CELLSIZE", header.remove(key)));
                String value = header.remove(key = "NODATA_VALUE");
                fillValue = (value != null) ? Double.parseDouble(value) : super.getPadValue(0);
                value = header.remove(key = "MIN_VALUE");
                minValue = (value != null) ? Double.parseDouble(value) : Double.NEGATIVE_INFINITY;
                value = header.remove(key = "MAX_VALUE");
                maxValue = (value != null) ? Double.parseDouble(value) : Double.POSITIVE_INFINITY;
                value = header.remove(key = "XLLCENTER");
                if (value == null) {
                    value = header.remove(key = "XLLCORNER");
                    xCenter = false;
                }
                xll = Double.parseDouble(ensureDefined(key, value));
                value = header.remove(key = "YLLCENTER");
                if (value == null) {
                    value = header.remove(key = "YLLCORNER");
                    yCenter = false;
                }
                yll = Double.parseDouble(ensureDefined(key, value));
            } catch (NumberFormatException cause) {
                final IIOException ex = new IIOException(error(Errors.Keys.UNPARSABLE_NUMBER_$1, key));
                ex.initCause(cause);
                throw ex;
            }
            headerValid = true;
            /*
             * We should not have any entry left.
             */
            if (!header.isEmpty()) {
                throw new IIOException(error(Errors.Keys.UNKNOW_PARAMETER_$1, header.keySet().iterator().next()));
            }
        }
    }

    /**
     * Ensures that the value is non-null. Otherwise an exception is thrown using the given name.
     *
     * @param  name  The name of the properties to be tested.
     * @param  value The value of the properties.
     * @return The given value, garanteed to be non-null.
     * @throws IIOException If the given value was null.
     */
    private String ensureDefined(final String name, final String value) throws IIOException {
        if (value == null || value.length() == 0) {
            throw new IIOException(error(Errors.Keys.MISSING_PARAMETER_$1, name));
        }
        return value;
    }

    /**
     * Reads the header from the {@linkplain #getChannel() channel}. The given buffer is used for
     * transfering data. The file encoding is assumed ASCII. The {@linkplain #isComment(String)
     * comment lines} are skipped. The separator between keys and values is assumed any of space,
     * {@code ':'} or {@code '='} character. The scan stop at the first line which seems to
     * contains a number.
     *
     * @return The header as a new, modifiable, map.
     * @throws IOException If an error occured while reading.
     */
    private Map<String,String> readHeader() throws IOException {
        final ReadableByteChannel channel = getChannel();
        final StringBuilder       stbuff  = new StringBuilder();
        final Map<String,String>  header  = new HashMap<String,String>();
        ByteBuffer buffer = this.buffer;
        if (buffer == null) {
            this.buffer = buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        }
        buffer.clear();
        buffer.limit(buffer.position()); // For forcing a filling of the buffer.
        while (true) {
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
                        throw new IIOException(Errors.format(Errors.Keys.HEADER_UNEXPECTED_LENGTH_$1, capacity));
                    }
                    /*
                     * Arbitrary read a block of 512 bytes for starting, because the header is
                     * typically small (less than 256 characters). If 512 bytes is not enough,
                     * we will read more bytes up to the buffer capacity.
                     */
                    buffer.limit(Math.min(capacity, Math.max(512, 2*pos)));
                    if (channel.read(buffer) < 0) {
                        throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
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
                        return header;
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
            if (line.length() != 0 && !isComment(line)) {
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
                    throw new IIOException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_$1, key));
                }
            }
        }
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
        ensureHeaderRead();
        return height;
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
        final double[] origin = new double[] {xll, yll + cellsize * (height - (yCenter ? 1 : 0))};
        final double[] bounds = new double[] {xll + cellsize * (width - (xCenter ? 1 : 0)), yll};
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE, this, null);
        final GridDomainAccessor domain = new GridDomainAccessor(metadata);
        domain.setOrigin(origin);
        domain.addOffsetVector(cellsize, 0);
        domain.addOffsetVector(0, -cellsize);
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
     * @throws IOException If an error occured while processing the header values.
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
        processImageStarted(imageIndex);
        ensureHeaderRead();
        /*
         * Parameters check.
         */
        final int numSrcBands = 1; // To be modified in a future version if we support multi-bands.
        final int numDstBands = 1;
        checkImageIndex(imageIndex);
        checkReadParamBandSettings(param, numSrcBands, numDstBands);
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
        final int               width      = this.width;
        final int               height     = this.height;
        final SampleConverter[] converters = new SampleConverter[numDstBands];
        final BufferedImage     image      = getDestination(imageIndex, param, width, height, converters);
        final WritableRaster    raster     = image.getRaster();
        final Rectangle         srcRegion  = new Rectangle();
        final Rectangle         dstRegion  = new Rectangle();
        computeRegions(param, width, height, image, srcRegion, dstRegion);
        final WritableRectIter dstIter = RectIterFactory.createWritable(raster, dstRegion);
        final int dstBand = (destinationBands != null) ? destinationBands[0] : 0;
        for (int i=dstBand; --i>=0;) {
            dstIter.nextBand();
        }
        if (!dstIter.finishedBands() && !dstIter.finishedLines() && !dstIter.finishedPixels()) {
            final int                 dataType   = raster.getSampleModel().getDataType();
            final char[]              charBuffer = new char[48]; // Arbitrary length limit for a sample value.
            final ByteBuffer          buffer     = this.buffer;
            final ReadableByteChannel channel    = getChannel();
            final SampleConverter     converter  = converters[dstBand];
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
                     * read, those characters will be discarted immediatly except in case of
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
                        final char c = (char) (buffer.get() & 0xFF);
                        if (c > ' ') {
                            if (nChar >= charBuffer.length) {
                                throw new IIOException(error(Errors.Keys.BAD_PARAMETER_$2,
                                        "cell(" + x + ',' + y + ')',
                                        String.valueOf(charBuffer)));
                            }
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
                                    dstIter.setSample(converter.convert(Double.parseDouble(value)));
                                    break;
                                }
                                case DataBuffer.TYPE_FLOAT: {
                                    dstIter.setSample(converter.convert(Float.parseFloat(value)));
                                    break;
                                }
                                default: {
                                    dstIter.setSample(converter.convert(Integer.parseInt(value)));
                                    break;
                                }
                            }
                        } catch (NumberFormatException cause) {
                            final IIOException e = new IIOException(error(Errors.Keys.UNPARSABLE_NUMBER_$1, value));
                            e.initCause(cause);
                            throw e;
                        }
                        /*
                         * Move to the next pixel in the destination image. The reading process
                         * will stop when we have reached the last pixel (which may be sooner
                         * than the end of the current row in the input image).
                         */
                        if (dstIter.nextPixelDone()) {
                            if (dstIter.nextLineDone()) {
                                break loop;
                            }
                            dstIter.startPixels();
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
     * Allows any resources held by this reader to be released.
     */
    @Override
    public void dispose() {
        buffer = null;
        super.dispose();
    }




    /**
     * Service provider interface (SPI) for {@link AsciiGridReader}s. This SPI provides
     * the necessary implementation for creating default {@link AsciiGridReader}s using
     * US locale and ASCII character set. The {@linkplain #locale locale} and
     * {@linkplain #charset charset} fields are ignored by the default implementation.
     * <p>
     * The {@linkplain #Spi default constructor} initializes the fields to the values listed
     * below. Users wanting different values should create a subclass of {@code Spi} and set
     * the desired values in their constructor.
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "ascii-grid"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "text/plain"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.text.AsciiGridReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;{@link Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #locale}          &nbsp;</td><td>&nbsp;{@link Locale#US}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #charset}         &nbsp;</td><td>&nbsp;{@code "US-ASCII"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #padValue}        &nbsp;</td><td>&nbsp;{@code -9999}&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See {@linkplain TextImageReader.Spi super-class javadoc} for remaining fields</td></tr>
     * </table>
     * <p>
     * Note that the {@code padValue} is used as the default value if no {@code NODATA_VALUE}
     * attribute is specified in the file header. The -9999 value is conform to ASCII Grid
     * convention.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.07
     *
     * @see AsciiGridWriter.Spi
     *
     * @since 3.07
     * @module
     */
    public static class Spi extends TextImageReader.Spi {
        /**
         * The format names for the default {@link AsciiGridReader} configuration.
         */
        static final String[] NAMES = {"ascii-grid"};

        /**
         * The file suffixes. This replace the {@link TextImageReader.Spi#SUFFIXES} declared
         * in the parent class.
         */
        static final String[] SUFFIXES = {"asc", "ASC", "grd", "GRD"};

        /**
         * The mime types for the default {@link AsciiGridReader} configuration.
         */
        static final String[] MIME_TYPES = {"text/plain"};

        /**
         * Constructs a default {@code AsciiGridReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficienty reasons, the fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            suffixes        = SUFFIXES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.text.AsciiGridReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
            locale          = Locale.US;
            charset         = Charset.forName("US-ASCII");
            padValue        = -9999;
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
            return keywords.contains("NCOLS") && keywords.contains("NROWS");
        }
    }
}
