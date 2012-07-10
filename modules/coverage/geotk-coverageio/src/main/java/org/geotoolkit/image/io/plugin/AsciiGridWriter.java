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

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.DataBuffer;
import java.util.Locale;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import javax.imageio.IIOImage;
import javax.imageio.IIOException;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.media.jai.iterator.RectIter;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.util.Strings;
import org.geotoolkit.image.ImageDimension;
import org.geotoolkit.image.io.TextImageWriter;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SampleDimension;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.collection.XCollections.isNullOrEmpty;


/**
 * Writer for the ASCII Grid format. As the "ASCII" name implies, the data file are written in
 * US-ASCII character encoding no matter what the {@link Spi#charset} value is. In addition, the
 * US locale is enforced no matter what the {@link Spi#locale} value is. The default implementation
 * writes only the header attribute defined below:
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
 *     <td>&nbsp;Mandatory, unless {@code DX} and {@code DY} are allowed.&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code DX} and {@code DY}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Forbidden if {@linkplain #setStrictCellSize strict cell size}
 *         has been set to {@code false}&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@code NODATA_VALUE}&nbsp;</td>
 *     <td>&nbsp;Floating point&nbsp;</td>
 *     <td>&nbsp;Optional, default to -9999&nbsp;</td>
 *   </tr>
 * </table>
 * <p>
 * The {@code DX} and {@code DY} attributes are non-standard, but recognized by the GDAL library
 * and Golden Surfer as <a href="http://www.gdal.org/frmt_various.html#AAIGrid">documented here</a>.
 * The default {@code AsciiGridWriter} behavior is to use those parameters if the image has rectangular
 * pixels, unless <code>{@linkplain #setStrictCellSize(boolean) setStrictCellSize}(true)</code> is
 * invoked.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @see <a href="http://daac.ornl.gov/MODIS/ASCII_Grid_Format_Description.html">ASCII Grid Format Description</a>
 * @see <a href="http://en.wikipedia.org/wiki/ESRI_grid">ESRI Grid on Wikipedia</a>
 * @see AsciiGridReader
 *
 * @since 3.08 (derived from 3.07)
 * @module
 */
public class AsciiGridWriter extends TextImageWriter {
    /**
     * The default fill value. This is part of the ASCII grid format specification.
     */
    private static final String DEFAULT_FILL = "-9999";

    /**
     * {@code true} if attempts to write an image with non-square pixels should throw an
     * exception, or {@code false} for allowing the use of the {@code DX} and {@code DY}
     * in such case.
     */
    private boolean strictCellSize;

    /**
     * Constructs a new image writer.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     */
    protected AsciiGridWriter(final Spi provider) {
        super(provider);
    }

    /**
     * Sets whatever the policy about the {@code CELLSIZE} attribute is to be strict.
     * If {@code true}, attempts to write an image with non-square pixels will throw an
     * exception. If {@code false} (the default) and an image has rectangular pixels, then
     * the {@code DX} and {@code DY} attributes will be used instead of {@code CELLSIZE}
     * and a warning will be emitted.
     * <p>
     * The {@code DX} and {@code DY} attributes are non-standard, but recognized by the GDAL
     * library and Golden Surfer. The default value is {@code false}, thus allowing creation
     * of non-standard ASCII grid file.
     *
     * @param strict {@code true} if attempts to write an image with non-square pixels should
     *        throw an exception, or {@code false} for emitting a warning instead.
     */
    public void setStrictCellSize(final boolean strict) {
        strictCellSize = strict;
    }

    /**
     * Returns the value set by the last call to {@link #setStrictCellSize(boolean)}.
     * The default value is {@code false}.
     *
     * @return {@code true} if attempts to write an image with non-square pixels should
     *         throw an exception, or {@code false} for emitting a warning instead.
     */
    public boolean getStrictCellSize() {
        return strictCellSize;
    }

    /**
     * Fills the given {@code header} map with values extracted from the given image metadata.
     * The {@code "NCOLS"} and {@code "NROWS"} attributes are already defined when this method
     * is invoked. This method is responsible for filling the remaining attributes.
     *
     * @param  metadata The metadata.
     * @param  header The map in which to store the (<var>key</var>, <var>value</var>) pairs
     *         to be written.
     * @return The fill value, or {@code Double#NaN} if none.
     * @throws IOException If the metadata can not be prepared.
     */
    private String prepareHeader(final SpatialMetadata metadata, final Map<String,String> header,
            final ImageWriteParam param) throws IOException
    {
        final MetadataHelper   helper    = new MetadataHelper(this);
        final Georectified     spatialRp = metadata.getInstanceForType(Georectified .class);
        final RectifiedGrid    domain    = metadata.getInstanceForType(RectifiedGrid.class);
        final PixelOrientation ptInPixel = (spatialRp != null) ? spatialRp.getPointInPixel() : null;
        final AffineTransform  gridToCRS = helper.getAffineTransform(domain, param);
        String xll = "XLLCORNER";
        String yll = "YLLCORNER";
        // Test UPPER_LEFT corder, not LOWER_LEFT, because the Y axis has been
        // reverted (i.e. the corresponding value in OffsetVectors is negative).
        if (ptInPixel != null && !ptInPixel.equals(PixelOrientation.UPPER_LEFT)) {
            if (ptInPixel.equals(PixelOrientation.CENTER)) {
                xll = "XLLCENTER";
                yll = "YLLCENTER";
            } else if (ptInPixel.equals(PixelOrientation.valueOf("UPPER"))) {
                yll = "YLLCENTER";
            } else if (ptInPixel.equals(PixelOrientation.valueOf("LEFT"))) {
                xll = "XLLCENTER";
            } else {
                throw new ImageMetadataException(Warnings.message(this,
                        Errors.Keys.ILLEGAL_PARAMETER_VALUE_$2, "pointInPixel", ptInPixel));
            }
        }
        header.put(xll, String.valueOf(gridToCRS.getTranslateX()));
        header.put(yll, String.valueOf(gridToCRS.getTranslateY()));
        /*
         * Use the CELLSIZE attribute if the pixels are square, or the DX, DY attibutes
         * if they are rectangular and we are allowed to use those non-standard attributes.
         */
        try {
            header.put("CELLSIZE", String.valueOf(helper.getCellSize(gridToCRS)));
        } catch (IIOException e) {
            final Dimension2D size;
            if (strictCellSize || (size = helper.getCellDimension(gridToCRS)) == null) {
                throw e;
            }
            Warnings.log(this, null, AsciiGridWriter.class, "writeHeader", e);
            header.put("DX", String.valueOf(size.getWidth()));
            header.put("DY", String.valueOf(size.getHeight()));
        }
        /*
         * Get the fill sample value, which is optional. The default defined by
         * the ASCII grid format is -9999.
         */
        String fillValue = DEFAULT_FILL;
        final List<SampleDimension> dimensions = metadata.getListForType(SampleDimension.class);
        if (!isNullOrEmpty(dimensions)) {
            final SampleDimension dim = dimensions.get(0);
            if (dim != null) {
                final double[] fillValues = dim.getFillSampleValues();
                if (fillValues != null && fillValues.length != 0) {
                    final double value = fillValues[0];
                    if (!Double.isNaN(value)) {
                        fillValue = Strings.trimFractionalPart(String.valueOf(value));
                        header.put("NODATA_VALUE", fillValue);
                    }
                }
            }
        }
        return fillValue;
    }

    /**
     * Invoked by the {@link #write write} method for appending the header to the output
     * stream. Subclasses can override this method in order to modify the header content.
     * The {@code header} map given in argument can be freely modified.
     *
     * @param  header The content of the header to be written.
     * @param  out The streal where to write the header.
     * @throws IOException If an error occurred while writing the header.
     *
     * @todo Overriding not yet allowed. We are waiting to see if this API is really appropriate.
     */
    private void writeHeader(final Map<String,String> header, final BufferedWriter out) throws IOException {
        int length = 0;
        for (final String key : header.keySet()) {
            final int lg = key.length();
            if (lg > length) {
                length = lg;
            }
        }
        boolean first = true; // Do not write the line separator for the first line.
        for (final Map.Entry<String,String> entry : header.entrySet()) {
            if (!first) {
                out.write('\n');
            }
            first = false;
            final String key = entry.getKey();
            out.write(key);
            out.write(Strings.spaces(2 + Math.max(0, length - key.length())));
            out.write(entry.getValue());
        }
        // We intentionally omit the line separator for the last line,
        // because the write(...) method below will add it itself.
    }

    /**
     * Appends a complete image stream containing a single image.
     *
     * @param  streamMetadata The stream metadata (ignored in default implementation).
     * @param  image The image or raster to be written.
     * @param  parameters The write parameters, or null if the whole image will be written.
     * @throws IOException If an error occurred while writing to the stream.
     */
    @Override
    public void write(final IIOMetadata streamMetadata, final IIOImage image,
                      final ImageWriteParam parameters) throws IOException
    {
        processImageStarted();
        final BufferedWriter out  = getWriter(parameters);
        final ImageDimension size = computeSize(image, parameters);
        /*
         * Write the header.
         */
        final Map<String,String> header = new LinkedHashMap<String,String>(8);
        header.put("NCOLS", String.valueOf(size.width ));
        header.put("NROWS", String.valueOf(size.height));
        final SpatialMetadata metadata = convertImageMetadata(image.getMetadata(), null, parameters);
        String fillValue = DEFAULT_FILL;
        if (metadata != null) {
            fillValue = prepareHeader(metadata, header, parameters);
        }
        writeHeader(header, out);
        /*
         * Write the pixel values.
         */
        final RectIter iterator      = createRectIter(image, parameters);
        final int      dataType      = getSampleModel(image, parameters).getDataType();
        final float    progressScale = 100f / size.getNumSampleValues();
        int numSampleValues = 0, nextProgress = 0;
        boolean moreBands = false;
        if (!iterator.finishedBands()) do {
            if (moreBands) {
                out.write('\n'); // Separate bands by a blank line.
            }
            if (!iterator.finishedLines()) do {
                if (numSampleValues >= nextProgress) {
                    // Informs about progress only every 2000 numbers.
                    processImageProgress(progressScale * numSampleValues);
                    nextProgress = numSampleValues + 2000;
                }
                if (abortRequested()) {
                    processWriteAborted();
                    return;
                }
                char separator = '\n';
                if (!iterator.finishedPixels()) do {
                    final String value;
                    switch (dataType) {
                        case DataBuffer.TYPE_DOUBLE: {
                            final double v = iterator.getSampleDouble();
                            value = Double.isNaN(v) ? fillValue : Double.toString(v);
                            break;
                        }
                        case DataBuffer.TYPE_FLOAT: {
                            final float v = iterator.getSampleFloat();
                            value = Float.isNaN(v) ? fillValue : Float.toString(v);
                            break;
                        }
                        default: {
                            value = Integer.toString(iterator.getSample());
                            break;
                        }
                        case DataBuffer.TYPE_USHORT:
                        case DataBuffer.TYPE_BYTE: {
                            value = Integer.toString(iterator.getSample() & 0x7FFFFFFF);
                            break;
                        }
                    }
                    out.write(separator);
                    out.write(value);
                    separator = ' ';
                } while (!iterator.nextPixelDone());
                numSampleValues += size.width;
                iterator.startPixels();
            } while (!iterator.nextLineDone());
            iterator.startLines();
            moreBands = true;
        } while (!iterator.nextBandDone());
        out.write('\n');
        out.flush();
        processImageComplete();
    }




    /**
     * Service provider interface (SPI) for {@code AsciiGridWriter}s. This SPI provides
     * the necessary implementation for creating default {@link AsciiGridWriter}s using
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
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.AsciiGridWriter"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;Value of {@link org.geotoolkit.util.Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #locale}          &nbsp;</td><td>&nbsp;{@link Locale#US}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #charset}         &nbsp;</td><td>&nbsp;{@code "US-ASCII"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #lineSeparator}   &nbsp;</td><td>&nbsp;{@code "\n"}&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See
     *   {@linkplain org.geotoolkit.image.io.TextImageWriter.Spi super-class javadoc} for remaining fields</td></tr>
     * </table>
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.08
     *
     * @see AsciiGridReader.Spi
     *
     * @since 3.08 (derived from 3.07)
     * @module
     */
    public static class Spi extends TextImageWriter.Spi {
        /**
         * The provider of the corresponding image reader.
         */
        private static final String[] READERS = {"org.geotoolkit.image.io.plugin.AsciiGridReader$Spi"};

        /**
         * Constructs a default {@code AsciiGridWriter.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can
         * modify those values if desired.
         * <p>
         * For efficiency reasons, the above fields are initialized to shared arrays.
         * Subclasses can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = AsciiGridReader.Spi.NAMES;
            MIMETypes       = AsciiGridReader.Spi.MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.AsciiGridWriter";
            readerSpiNames  = READERS;
            locale          = Locale.US;
            charset         = Charset.forName("US-ASCII");
            lineSeparator   = "\n";
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
         * Returns an instance of the {@code ImageWriter} implementation associated
         * with this service provider.
         *
         * @param  extension An optional extension object, which may be null.
         * @return An image writer instance.
         * @throws IOException if the attempt to instantiate the writer fails.
         */
        @Override
        public ImageWriter createWriterInstance(final Object extension) throws IOException {
            return new AsciiGridWriter(this);
        }
    }
}
