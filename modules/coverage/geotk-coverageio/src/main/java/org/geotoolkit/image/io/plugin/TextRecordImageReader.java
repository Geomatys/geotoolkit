/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;

import org.geotoolkit.util.Version;
import org.geotoolkit.io.LineFormat;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.image.io.TextImageReader;
import org.geotoolkit.image.io.SampleConverter;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.io.LineReader;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Image decoder for text files storing pixel values as records.
 * Such text files use one line (record) by pixel. Each line contains
 * at least 3 columns (in arbitrary order):
 * <p>
 * <ul>
 *   <li>Pixel's <var>x</var> coordinate.</li>
 *   <li>Pixel's <var>y</var> coordinate.</li>
 *   <li>An arbitrary number of pixel values.</li>
 * </ul>
 * <p>
 * For example, some Sea Level Anomaly (SLA) files contains rows of longitude
 * (degrees), latitude (degrees), SLA (cm), East/West current (cm/s) and
 * North/South current (cm/s), as below:
 *
 * {@preformat text
 *     45.1250 -29.8750    -7.28     10.3483     -0.3164
 *     45.1250 -29.6250    -4.97     11.8847      3.6192
 *     45.1250 -29.3750    -2.91      3.7900      3.0858
 *     45.1250 -29.1250    -3.48     -5.1833     -5.0759
 *     45.1250 -28.8750    -4.36     -1.8129    -16.3689
 *     45.1250 -28.6250    -3.91      7.5577    -24.6801
 *     (...etc...)
 * }
 *
 * From this decoder point of view, the two first columns (<var>longitude</var> and <var>latitude</var>)
 * are pixel's logical coordinate (<var>x</var>,<var>y</var>), while the three last
 * columns are three image's bands. The whole file contains only one image, unless
 * {@link #getNumImages} has been overridden. All (<var>x</var>,<var>y</var>)
 * coordinates belong to pixel's center. This decoder will automatically translate
 * (<var>x</var>,<var>y</var>) coordinates from logical space to pixel space.
 * <p>
 * By default, {@code TextRecordImageReader} assumes that <var>x</var> and
 * <var>y</var> coordinates appear in column #0 and 1 respectively. It also assumes
 * that numeric values are encoded using current defaults {@link java.nio.charset.Charset}
 * and {@link java.util.Locale}, and that there is no pad value. The easiest way to change
 * the default setting is to create a {@link Spi} subclass. There is no need to subclass
 * {@code TextRecordImageReader}, unless you want more control on the decoding process.
 *
 * {@section Example}
 * The text of the left side is an extract of a list of (<var>longitude</var>, <var>latitude</var>,
 * <var>elevation of the ocean floor</var>) records. The image on the right side is the image
 * produced by {@code TextRecordImageReader} when reading such file.
 *
 * <table cellpadding='24'>
 * <tr valign="top"><td><pre>
 * # Longitude Latitude Altitude
 *   59.9000   -30.0000   -3022
 *   59.9333   -30.0000   -3194
 *   59.9667   -30.0000   -3888
 *   60.0000   -30.0000   -3888
 *   45.0000   -29.9667   -2502
 *   45.0333   -29.9667   -2502
 *   45.0667   -29.9667   -2576
 *   45.1000   -29.9667   -2576
 *   45.1333   -29.9667   -2624
 *   45.1667   -29.9667   -2690
 *   45.2000   -29.9667   -2690
 *   45.2333   -29.9667   -2692
 *   45.2667   -29.9667   -2606
 *   45.3000   -29.9667   -2606
 *   45.3333   -29.9667   -2528</pre>etc...</td>
 * <td><img src="doc-files/Sandwell.jpeg"></td>
 * </tr></table>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @since 3.08 (derived from 1.2)
 * @module
 */
public class TextRecordImageReader extends TextImageReader {
    /**
     * Small factor for working around rounding error.
     */
    private static final float EPS = 1E-5f;

    /**
     * Interval in bytes between calls to {@link #processImageProgress(float)}.
     */
    private static final int PROGRESS_INTERVAL = 4096;

    /**
     * If {@code true}, then {@link BufferedReader} are filled with {@code NaN} values before
     * to read the pixels. If {@code false}, then pixel values in location not defined by the
     * file will keep their old value.
     */
    private static final boolean CLEAR = true;

    /**
     * The data for all images, {@code null} if no data have been read yet. The length of this
     * array is the number of images. Each element is the data of the image at the corresponding
     * index.
     * <p>
     * if {@link #seekForwardOnly} is {@code true}, then the element are cleared to {@code null}
     * when the reader advance to the next image.
     */
    private TextRecordList[] data;

    /**
     * Index of the next image to read.
     */
    private int nextImageIndex;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     */
    public TextRecordImageReader(final Spi provider) {
        super(provider);
    }

    /**
     * Returns the grid tolerance (epsilon) value.
     */
    private float getGridTolerance() {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).gridTolerance : EPS;
    }

    /**
     * Returns the column number for <var>x</var> values. The default implementation returns
     * {@link TextRecordImageReader.Spi#xColumn}, or 0 if no service prodiver were specified
     * to the constructor. Subclasses should override this method if this information should
     * be obtained in an other way.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The column number for <var>x</var> values.
     * @throws IOException If an error occurs while reading the from the input source.
     */
    protected int getColumnX(final int imageIndex) throws IOException {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).xColumn : 0;
    }

    /**
     * Returns the column number for <var>y</var> values. The default implementation returns
     * {@link TextRecordImageReader.Spi#yColumn}, or 1 if no service prodiver were specified
     * to the constructor. Subclasses should override this method if this information should
     * be obtained in an other way.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The column number for <var>y</var> values.
     * @throws IOException If an error occurs while reading the from the input source.
     */
    protected int getColumnY(final int imageIndex) throws IOException {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).yColumn : 1;
    }

    /**
     * Ensures that the given value is positive. This is used for checking the values returned
     * by {@link #getColumnX(int)} and {@link #getColumnY(int)}. In case of negative number, we
     * throw an {@link IIOException} instead of {@link IllegalArgumentException} because this
     * is not a method argument provided by the user.
     */
    private static void ensurePositive(final String name, final int column) throws IIOException {
        if (column < 0) {
            throw new IIOException(Errors.format(Errors.Keys.NEGATIVE_COLUMN_$2, name, column));
        }
    }

    /**
     * Returns the number of bands available for the specified image. The default
     * implementation reads the image immediately and counts the number of columns
     * after the geodetic coordinate columns.
     *
     * @param  imageIndex  The image index.
     * @throws IOException if an error occurs while reading the information from the input source.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        return getRecords(imageIndex, false).getNumBands();
    }

    /**
     * Returns the width in pixels of the given image within the input source.
     * Invoking this method forces the reading of the whole image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image width.
     * @throws IOException If an error occurs while reading the width information
     *         from the input source.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        final TextRecordList records = getRecords(imageIndex, false);
        return records.getPointCount(records.xColumn);
    }

    /**
     * Returns the height in pixels of the given image within the input source.
     * Invoking this method forces the reading of the whole image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image height.
     * @throws IOException If an error occurs while reading the height information
     *         from the input source.
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        final TextRecordList records = getRecords(imageIndex, false);
        return records.getPointCount(records.yColumn);
    }

    /**
     * Returns an approximation of the length of the stream portion from the first given image
     * to the second given image (inclusive). This implementation assumes that every image have
     * the same length. If the length can not be computed, then this method returns -1.
     * <p>
     * Many text formats store only one image. Getting the actual number of images is possible
     * by costly since it typically require parsing the file until some separator are found.
     * Consequently this method uses the actual number of images only if it is cheap to obtain,
     * otherwise it assumes that the stream contains only one image.
     * <p>
     * The returned information is approximative and should be used only for the purpose
     * of updating progress listeners.
     *
     * @param  fromImage The index of the first image (inclusive).
     * @param  toIndex The index of the last image (inclusive) for which to measure the stream length.
     * @return The number of bytes in the given portion of the stream, or -1 if unknown.
     * @throws IOException If an error occurred while reading the stream.
     *
     * @see #getStreamLength()
     *
     * @since 3.08
     */
    private long getStreamLength(final int fromImage, final int toImage) throws IOException {
        long length = getStreamLength();
        if (length > 0) {
            final int numImages = getNumImages(false);
            if (numImages > 0) {
                length = length * (toImage - fromImage + 1) / numImages;
            }
        }
        return length;
    }

    /**
     * Retourne la position du flot spécifié, ou {@code -1} si cette position est
     * inconnue. Note: la position retournée est <strong>approximative</strong>.
     * Elle est utile pour afficher un rapport des progrès, mais sans plus.
     *
     * @param  reader Flot dont on veut connaître la position.
     * @return Position approximative du flot, ou {@code -1}
     *         si cette position n'a pas pu être obtenue.
     * @throws IOException si l'opération a échouée.
     */
    private static long getStreamPosition(final Reader reader) throws IOException {
        return (reader instanceof LineReader) ? ((LineReader) reader).getPosition() : -1;
    }

    /**
     * Returns metadata associated with the given image.
     * Calling this method may force loading of full image.
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
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME), this, null);
        /*
         * Computes the smallest bounding box containing the full image in user coordinates.
         * This implementation searches for minimum and maximum values in x and y columns as
         * returned by getColumnX() and getColumnY(). Reminder: xmax and ymax are INCLUSIVE
         * in the code below, as well as (width-1) and (height-1).
         */
        final TextRecordList records = getRecords(imageIndex, false);
        final int    xColumn  = records.xColumn;
        final int    yColumn  = records.yColumn;
        final int    width    = records.getPointCount(xColumn);
        final int    height   = records.getPointCount(yColumn);
        final double xmin     = records.getMinimum(xColumn);
        final double ymin     = records.getMinimum(yColumn);
        final double xmax     = records.getMaximum(xColumn);
        final double ymax     = records.getMaximum(yColumn);
        final double padValue = getPadValue(imageIndex);
        final GridDomainAccessor domain = new GridDomainAccessor(metadata);
        // Note: the swapping of ymax and ymin below is intentional,
        // since values on the y axis are increasing downward.
        domain.setAll(xmin, ymax, xmax, ymin, width, height, true, null);
        /*
         * Now adds the valid range of sample values for each band.
         */
        final DimensionAccessor dimensions = new DimensionAccessor(metadata);
        final int numBands = records.getNumBands();
        for (int band=0; band<numBands; band++) {
            final int column = records.getColumnForBand(band);
            dimensions.selectChild(dimensions.appendChild());
            dimensions.setValueRange(records.getMinimum(column), records.getMaximum(column));
            dimensions.setFillSampleValues(padValue);
        }
        return metadata;
    }

    /**
     * Invoked during {@link #read read} operation for rounding a record of values. The default
     * implementation does nothing. The main purpose of this method is to give to subclasses an
     * opportunity to fix rounding errors in latitude and longitude coordinates.
     *
     * {@section Example}
     * Assume that the longitudes in a file are likely to have an interval of 1/6° but are written
     * with only 3 decimal digits. In such case, the {@linkplain #getColumnX x} values look like
     * {@code 10.000}, {@code 10.167}, {@code 10.333}, <i>etc.</i>, which can leads to an
     * error of 0.001° in longitude. This error may cause {@code TextRecordImageReader} to fails
     * validation tests and throws an {@link javax.imageio.IIOException}: "<cite>Points dont seem
     * to be distributed on a regular grid</cite>".
     * <p>
     * A work around is to multiply the <var>x</var> and <var>y</var> coordinates by 6, round to
     * the nearest integer and divide them by 6 as in the code below (which is doing the same
     * process to latitude):
     *
     * {@preformat java
     *     int xColumn = getColumnX();
     *     int yColumn = getColumnY();
     *     values[xColumn] = XMath.roundIfAlmostInteger(values[xColumn] * 6, 3) / 6;
     *     values[yColumn] = XMath.roundIfAlmostInteger(values[yColumn] * 6, 3) / 6;
     * }
     *
     * @param values The values to round in place.
     */
    protected void round(double[] values) {
    }

    /**
     * Returns the records for the image at the given index. If this image has already been read
     * in a previous call to this method, then the cached {@code TextRecordList} will be returned
     * immediately. Otherwise the records will be read from the input source. This will force
     * the loading of every images before the given {@code imageIndex} that has not yet been
     * read. Those previous images will be discarded immediately if {@link #seekForwardOnly}
     * is {@code true}.
     *
     * @param  imageIndex The image index.
     * @param  allowCancel {@code false} if cancelation should throw an exception.
     * @return The list of records for the requested image (never {@code null}).
     * @throws IOException if an error occurred while reading, including badly formatted numbers.
     * @throws IndexOutOfBoundsException If the given image index is outside the range of index
     *         that this method can process.
     */
    private TextRecordList getRecords(final int imageIndex, final boolean allowCancel) throws IOException {
        clearAbortRequest();
        checkImageIndex(imageIndex);
        if (imageIndex >= nextImageIndex) {
            processImageStarted(imageIndex);
            final BufferedReader reader = getReader();
            final long streamOrigin = getStreamPosition(reader);
            final long streamLength = getStreamLength(nextImageIndex, imageIndex);
            for (; nextImageIndex <= imageIndex; nextImageIndex++) {
                /*
                 * If there is some image before this one, trim their internal array in order to
                 * reduce memory usage. Note that the image to be read below will not be trimmed
                 * at the end of this method because in typical usage, it will not be keept (i.e.
                 * we usually load only one image, or if we want many images we typically set
                 * seekForwardOnly to true).
                 */
                if (seekForwardOnly) {
                    minIndex = nextImageIndex;
                }
                if (nextImageIndex != 0 && data != null) {
                    final TextRecordList records = data[nextImageIndex-1];
                    if (records != null) {
                        if (seekForwardOnly) {
                            data[nextImageIndex-1] = null;
                        } else {
                            records.trimToSize();
                        }
                    }
                }
                /*
                 * Parse the line read from the input source. Those lines will be immediately
                 * discarded if seekForwardOnly is true and the current image index is lower
                 * than the requested one (because we need to parse previous images before to
                 * reach the requested one).
                 */
                int linePosition = 0;
                int nextProgress = 1;
                double[]         values     = null;
                TextRecordList   records    = null;
                final boolean    memorize   = (nextImageIndex == imageIndex) || !seekForwardOnly;
                final int        xColumn    = getColumnX   (nextImageIndex); ensurePositive("x", xColumn);
                final int        yColumn    = getColumnY   (nextImageIndex); ensurePositive("y", yColumn);
                final double     padValue   = getPadValue  (nextImageIndex);
                final LineFormat lineFormat = getLineFormat(nextImageIndex);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (abortRequested()) {
                        processReadAborted();
                        if (!allowCancel || records == null) {
                            throw new IIOException(Errors.format(Errors.Keys.CANCELED_OPERATION));
                        }
                        return records;
                    }
                    linePosition++;
                    if (isComment(line)) {
                        continue;
                    }
                    try {
                        if (lineFormat.setLine(line) == 0) {
                            continue;
                        }
                        values = lineFormat.getValues(values);
                    } catch (ParseException exception) {
                        throw new IIOException(getPositionString(exception.getLocalizedMessage()), exception);
                    }
                    /*
                     * Modify (if needed) the values of the record we just read, replacing pad
                     * values by NaN and fixing the geodetic coordinates for rounding errors
                     * (if the user overridden the 'round' method).
                     */
                    for (int i=0; i<values.length; i++) {
                        if (i != xColumn && i != yColumn && values[i] == padValue) {
                            values[i] = Double.NaN;
                        }
                    }
                    round(values);
                    if (memorize) {
                        if (records == null) {
                            records = new TextRecordList(values,
                                    Math.max(8, (int) (streamLength / (line.length() + 1)) + 1),
                                    xColumn, yColumn, getGridTolerance());
                        }
                        records.add(values);
                    }
                    /*
                     * Report progress.
                     */
                    if (linePosition >= nextProgress) {
                        final long position = getStreamPosition(reader) - streamOrigin;
                        processImageProgress(position * (100f / streamLength));
                        nextProgress += (int) ((long) PROGRESS_INTERVAL * linePosition / position);
                    }
                }
                /*
                 * At this point, we have finished reading the image.
                 * Ensure that we have enough data (2 records is a minimum).
                 */
                if (records != null) {
                    final int lineCount = records.getLineCount();
                    if (lineCount < 2) {
                        throw new IIOException(getPositionString(Errors.format(
                                Errors.Keys.FILE_HAS_TOO_FEW_DATA)));
                    }
                    if (data == null) {
                        data = new TextRecordList[imageIndex+1];
                    } else if (data.length <= imageIndex) {
                        data = Arrays.copyOf(data, imageIndex*2);
                    }
                    data[nextImageIndex] = records;
                }
            }
            processImageComplete();
        }
        /*
         * Following should never be null if checkImageIndex(int) did its work properly.
         * We check nevertheless as a safety (user could have overridden checkImageIndex(int)
         * for instance).
         */
        if (data != null && imageIndex < data.length) {
            final TextRecordList records = data[imageIndex];
            if (records != null) {
                return records;
            }
        }
        throw new IndexOutOfBoundsException(String.valueOf(imageIndex));
    }

    /**
     * Reads the image indexed by {@code imageIndex} and returns it as a buffered image.
     *
     * @param  imageIndex The index of the image to be retrieved.
     * @param  param Parameters used to control the reading process, or {@code null}.
     * @return The desired portion of the image.
     * @throws IOException If an error occurs during reading.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        final TextRecordList records = getRecords(imageIndex, true);
        final int xColumn     = records.xColumn;
        final int yColumn     = records.yColumn;
        final int width       = records.getPointCount(xColumn);
        final int height      = records.getPointCount(yColumn);
        final int numSrcBands = records.getNumBands();
        /*
         * Extracts user's parameters
         */
        final int[]         srcBands;
        final int[]         dstBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        if (param != null) {
            srcBands           = param.getSourceBands();
            dstBands           = param.getDestinationBands();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
        } else {
            srcBands = null;
            dstBands = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        /*
         * Initializes...
         */
        final int numDstBands = (dstBands != null) ? dstBands.length :
                                (srcBands != null) ? srcBands.length : numSrcBands;
        final SampleConverter[] converters = new SampleConverter[numDstBands];
        final BufferedImage image = getDestination(imageIndex, param, width, height, converters);
        checkReadParamBandSettings(param, numSrcBands, image.getSampleModel().getNumBands());

        final Rectangle srcRegion = new Rectangle();
        final Rectangle dstRegion = new Rectangle();
        computeRegions(param, width, height, image, srcRegion, dstRegion);
        final int sourceXMin   = srcRegion.x;
        final int sourceYMin   = srcRegion.y;
        final int sourceWidth  = srcRegion.width;
        final int sourceHeight = srcRegion.height;
        final int destinationXOffset = dstRegion.x;
        final int destinationYOffset = dstRegion.y;

        final WritableRaster raster = image.getRaster();
        final int columnCount = records.columnCount;
        final int dataCount   = records.getDataCount();
        final float[] data    = records.getData();
        final double  xmin    = records.getMinimum(xColumn);
        final double  ymin    = records.getMinimum(yColumn);
        final double  xmax    = records.getMaximum(xColumn);
        final double  ymax    = records.getMaximum(yColumn);
        final double  scaleX  = (width -1) / (xmax - xmin);
        final double  scaleY  = (height-1) / (ymax - ymin);
        /*
         * Clears the image area. All values are set to NaN.
         */
        if (CLEAR) {
            final int maxX = dstRegion.width  + destinationXOffset;
            final int maxY = dstRegion.height + destinationYOffset;
            for (int b = (dstBands != null) ? dstBands.length : numDstBands; --b>=0;) {
                final int band = (dstBands != null) ? dstBands[b] : b;
                for (int y=destinationYOffset; y<maxY; y++) {
                    for (int x=destinationXOffset; x<maxX; x++) {
                        raster.setSample(x, y, band, Float.NaN);
                    }
                }
            }
        }
        /*
         * Computes column numbers corresponding to source bands,
         * and start storing values into the image.
         */
        final int[] columns = new int[(srcBands!=null) ? srcBands.length : numDstBands];
        for (int i=0; i<columns.length; i++) {
            columns[i] = records.getColumnForBand(srcBands != null ? srcBands[i] : i);
        }
        for (int i=0; i<dataCount; i+=columnCount) {
            /*
             * Converts the (x,y) geodetic coordinate into pixel coordinate into the source image.
             * Then convert the result from a pixel coordinate in the source image to a coordinate
             * in the destination image. Note that the conversion from geodetic to pixel coordinates
             * assume that the y axis is increasing downward, as often with images.
             */
            int x = (int) Math.round((data[i+xColumn] - xmin) * scaleX) - sourceXMin;
            if (x >= 0 && x < sourceWidth && (x % sourceXSubsampling) == 0) {
                int y = (int) Math.round((ymax - data[i+yColumn]) * scaleY) - sourceYMin;
                if (y >= 0 && y < sourceHeight && (y % sourceYSubsampling) == 0) {
                    x = x / sourceXSubsampling + destinationXOffset;
                    y = y / sourceYSubsampling + destinationYOffset;
                    for (int j=0; j<columns.length; j++) {
                        final int db = (dstBands != null ? dstBands[j] : j);
                        raster.setSample(x, y, db, converters[j].convert(data[i+columns[j]]));
                    }
                }
            }
        }
        return image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void close() throws IOException {
        data = null;
        nextImageIndex = 0;
        super.close();
    }




    /**
     * Service provider interface (SPI) for {@code TextRecordImageReader}s. This SPI provides
     * necessary implementation for creating default {@link TextRecordImageReader} using default
     * locale and character set. The default constructor initializes the fields to the values
     * listed below:
     * <p>
     * <table border="1" cellspacing="0">
     *   <tr bgcolor="lightblue"><th>Field</th><th>Value</th></tr>
     *   <tr><td>&nbsp;{@link #names}           &nbsp;</td><td>&nbsp;{@code "records"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #MIMETypes}       &nbsp;</td><td>&nbsp;{@code "text/plain"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #pluginClassName} &nbsp;</td><td>&nbsp;{@code "org.geotoolkit.image.io.plugin.TextRecordImageReader"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #vendorName}      &nbsp;</td><td>&nbsp;{@code "Geotoolkit.org"}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #version}         &nbsp;</td><td>&nbsp;{@link Version#GEOTOOLKIT}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #xColumn}         &nbsp;</td><td>&nbsp;{@code 0}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #yColumn}         &nbsp;</td><td>&nbsp;{@code 1}&nbsp;</td></tr>
     *   <tr><td>&nbsp;{@link #gridTolerance}   &nbsp;</td><td>&nbsp;May vary.&nbsp;</td></tr>
     *   <tr><td colspan="2" align="center">See
     *   {@linkplain org.geotoolkit.image.io.TextImageReader.Spi super-class javadoc} for remaining fields</td></tr>
     * </table>
     * <p>
     * Subclasses can set some fields at construction time in order to
     * tune the reader to a particular environment, e.g.:
     *
     * {@preformat java
     *     public final class CLSImageReaderSpi extends TextRecordImageReader.Spi {
     *         public CLSImageReaderSpi() {
     *             names      = new String[] {"CLS"};
     *             MIMETypes  = new String[] {"text/x-records-CLS"};
     *             vendorName = "Institut de Recherche pour le Développement";
     *             version    = "1.0";
     *             locale     = Locale.US;
     *             charset    = Charset.forName("ISO-8859-1"); // ISO-LATIN-1
     *             padValue   = -9999;
     *         }
     *     }
     * }
     *
     * {@note fields <code>vendorName</code> and <code>version</code> are only informatives.}
     *
     * There is no need to override any method in this example. However, developers
     * can gain more control by creating subclasses of {@link TextRecordImageReader}
     * and {@code Spi}.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.08
     *
     * @since 3.08 (derived from 2.1)
     * @module
     */
    public static class Spi extends TextImageReader.Spi {
        /**
         * The format names for the default {@link TextRecordImageReader} configuration.
         */
        private static final String[] NAMES = {"records"};

        /**
         * The mime types for the default {@link TextRecordImageReader} configuration.
         */
        private static final String[] MIME_TYPES = {"text/plain"};

        /**
         * 0-based column number for <var>x</var> values. The default value is 0.
         *
         * @see TextRecordImageReader#getColumnX
         */
        protected int xColumn;

        /**
         * 0-based column number for <var>y</var> values. The default value is 1.
         *
         * @see TextRecordImageReader#getColumnY
         */
        protected int yColumn;

        /**
         * A tolerance factor during decoding, between 0 and 1. During decoding, the image reader
         * computes cell's width and height (i.e. the smallest non-null difference between ordinates
         * in a given column: <var>x</var> for cell's width and <var>y</var> for cell's height).
         * Then, it checks if every coordinate points fall on a grid having this cell's size. If
         * a point depart from more than {@code gridTolerance} percent of cell's width or height,
         * an exception is thrown.
         * <p>
         * {@code gridTolerance} should be a small number like {@code 1E-5f}
         * or {@code 1E-3f}. The later is more tolerant than the former.
         */
        protected float gridTolerance = EPS;

        /**
         * Constructs a default {@code TextRecordImageReader.Spi}. The fields are initialized as
         * documented in the <a href="#skip-navbar_top">class javadoc</a>. Subclasses can modify
         * those values if desired.
         * <p>
         * For efficiency reasons, the above fields are initialized to shared arrays. Subclasses
         * can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.plugin.TextRecordImageReader";
            vendorName      = "Geotoolkit.org";
            version         = Version.GEOTOOLKIT.toString();
            xColumn         = 0;
            yColumn         = 1;
            gridTolerance   = EPS;
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
            return Descriptions.getResources(locale).getString(Descriptions.Keys.CODEC_GRID);
        }

        /**
         * Returns an instance of the ImageReader implementation associated
         * with this service provider.
         *
         * @param  extension An optional extension object, which may be null.
         * @return An image reader instance.
         * @throws IOException if the attempt to instantiate the reader fails.
         */
        @Override
        public ImageReader createReaderInstance(final Object extension) throws IOException {
            return new TextRecordImageReader(this);
        }

        /**
         * Returns {@code true} if the content of the first few rows seems valid, or {@code false}
         * otherwise. The default implementation performs the same check than the
         * {@linkplain org.geotoolkit.image.io.TextImageReader.Spi#isValidContent(double[][])
         * super-class}, and additionaly checks if the (<var>x</var>, <var>y</var>) values seem
         * distributed on a regular grid.
         *
         * @param rows The first few rows.
         * @return {@code true} if the given rows seem to have a valid content.
         */
        @Override
        protected boolean isValidContent(final double[][] rows) {
            /*
             * The 12 lines limit is arbitrary and may change in future version.
             * We ask for a minimal amount of lines in order to have reasonable
             * chances to determine if the data are distributed on a regular grid.
             * This limit should be safe if the average line length is lower than
             * 80 characters (usually they are about 15 characters).
             */
            if (rows.length < 12 || !super.isValidContent(rows)) {
                return false;
            }
            final TextRecordList records = new TextRecordList(rows[0], rows.length, xColumn, yColumn, gridTolerance);
            for (int i=1; i<rows.length; i++) {
                records.add(rows[i]);
            }
            try {
                records.getPointCount(records.xColumn);
                records.getPointCount(records.yColumn);
            } catch (IIOException e) {
                return false;
            }
            return true;
        }

        /**
         * Invoked by {@link #isValidColumnCount(int)} for determining if the given number of
         * columns is valid. Note that this is the number of columns in the data file, not to
         * be confused with the image width.
         *
         * @since 3.07
         */
        @Override
        protected boolean isValidColumnCount(final int count) {
            return count >= (xColumn == yColumn ? 2 : 3);
        }

        /**
         * Invoked when this Service Provider is registered. This method
         * {@linkplain ServiceRegistry#setOrdering(Class, Object, Object) sets the ordering}
         * of this {@code TextRecordImageReader.Spi} before {@link TextMatrixImageReader.Spi},
         * because the later is generic enough for claiming to be able to read records file.
         *
         * @param registry The registry where is service is registered.
         * @param category The category for which this service is registered.
         */
        @Override
        public void onRegistration(final ServiceRegistry registry, final Class<?> category) {
            super.onRegistration(registry, category);
            if (category.equals(ImageReaderSpi.class)) {
                final TextMatrixImageReader.Spi matrixProvider =
                        registry.getServiceProviderByClass(TextMatrixImageReader.Spi.class);
                if (matrixProvider != null) {
                    registry.setOrdering(ImageReaderSpi.class, this, matrixProvider);
                }
            }
        }
    }
}
