/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.util.Version;
import org.geotoolkit.io.LineFormat;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;


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
 * From this decoder point of view, the two first columns (longitude and latitude)
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
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.06
 *
 * @since 1.2
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
    private RecordList[] data;

    /**
     * Index of the next image to read.
     */
    private int nextImageIndex;

    /**
     * Average number of characters (including EOL) for a record. This is used for estimating
     * the percentage of progress done so far. This will be updated after each image read. The
     * value 0 means that it has not yet been computed.
     */
    private float averageLineLength;

    /**
     * Constructs a new image reader.
     *
     * @param provider the provider that is invoking this constructor, or {@code null} if none.
     */
    public TextRecordImageReader(final ImageReaderSpi provider) {
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
     * {@link TextRecordImageReader.Spi#xColumn}. Subclasses should override this method if
     * this information should be obtained in an other way.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The column number for <var>x</var> values.
     * @throws IOException If an error occurs reading the from the input source.
     */
    protected int getColumnX(final int imageIndex) throws IOException {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).xColumn : 0;
    }

    /**
     * Invokes {@link #getColumnX} and checks the result.
     */
    private int getCheckedColumnX(final int imageIndex) throws IOException {
        final int xColumn = getColumnX(imageIndex);
        if (xColumn < 0) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NEGATIVE_COLUMN_$2, "x", xColumn));
        }
        return xColumn;
    }

    /**
     * Returns the column number for <var>y</var> values. The default implementation returns
     * {@link TextRecordImageReader.Spi#yColumn}. Subclasses should override this method if
     * this information should be obtained in an other way.
     *
     * @param  imageIndex The index of the image to be queried.
     * @return The column number for <var>y</var> values.
     * @throws IOException If an error occurs reading the from the input source.
     */
    protected int getColumnY(final int imageIndex) throws IOException {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).yColumn : 1;
    }

    /**
     * Invokes {@link #getColumnY} and checks the result.
     */
    private int getCheckedColumnY(final int imageIndex) throws IOException {
        final int yColumn = getColumnY(imageIndex);
        if (yColumn < 0) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NEGATIVE_COLUMN_$2, "y", yColumn));
        }
        return yColumn;
    }

    /**
     * Returns the column number where to get the data for the given band. In most typical
     * cases, this method just add 2 to the given value in order to skip the longitude and
     * latitude columns. However this method is robust to the cases where the longitude and
     * latitude columns are not in their usual place.
     *
     * @param  imageIndex Index of the image to read.
     * @param  band Index of the band to read.
     * @return Index of the column in a record to read.
     * @throws IOException If an I/O operation was required and failed.
     */
    private int getColumnForBand(final int imageIndex, int band) throws IOException {
        final int xColumn = getCheckedColumnX(imageIndex);
        final int yColumn = getCheckedColumnY(imageIndex);
        if (band >= Math.min(xColumn, yColumn)) band++;
        if (band >= Math.max(xColumn, yColumn)) band++;
        return band;
    }

    /**
     * Sets the input source. It should be one of the following object, in preference order:
     * {@link java.io.File}, {@link java.net.URL}, {@link java.io.BufferedReader},
     * {@link java.io.Reader}, {@link java.io.InputStream} or
     * {@link javax.imageio.stream.ImageInputStream}.
     */
    @Override
    public void setInput(final Object  input,
                         final boolean seekForwardOnly,
                         final boolean ignoreMetadata)
    {
        clear();
        super.setInput(input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Returns the number of bands available for the specified image. The default
     * implementation reads the image immediately and counts the number of columns
     * after the longitude and latitude columns.
     *
     * @param  imageIndex  The image index.
     * @throws IOException if an error occurs reading the information from the input source.
     */
    @Override
    public int getNumBands(final int imageIndex) throws IOException {
        return getRecords(imageIndex).columnCount -
                (getCheckedColumnX(imageIndex) == getCheckedColumnY(imageIndex) ? 1 : 2);
    }

    /**
     * Returns the width in pixels of the given image within the input source.
     * Invoking this method forces the reading of the whole image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image width.
     * @throws IOException If an error occurs reading the width information from the input source.
     */
    @Override
    public int getWidth(final int imageIndex) throws IOException {
        final RecordList records = getRecords(imageIndex);
        return records.getPointCount(records.xColumn);
    }

    /**
     * Returns the height in pixels of the given image within the input source.
     * Invoking this method forces the reading of the whole image.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return Image height.
     * @throws IOException If an error occurs reading the height information from the input source.
     */
    @Override
    public int getHeight(final int imageIndex) throws IOException {
        final RecordList records = getRecords(imageIndex);
        return records.getPointCount(records.yColumn);
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
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE, this, null);
        /*
         * Computes the smallest bounding box containing the full image in user coordinates.
         * This implementation searches for minimum and maximum values in x and y columns as
         * returned by getColumnX() and getColumnY(). Reminder: xmax and ymax are INCLUSIVE
         * in the code below, as well as (width-1) and (height-1).
         */
        final RecordList records = getRecords(imageIndex);
        final int xColumn        = getCheckedColumnX(imageIndex);
        final int yColumn        = getCheckedColumnY(imageIndex);
        final int width          = records.getPointCount(xColumn);
        final int height         = records.getPointCount(yColumn);
        final double xmin        = records.getMinimum(xColumn);
        final double ymin        = records.getMinimum(yColumn);
        final double xmax        = records.getMaximum(xColumn);
        final double ymax        = records.getMaximum(yColumn);
        final double padValue    = getPadValue(imageIndex);
        final GridDomainAccessor domain = new GridDomainAccessor(metadata);
        domain.setAll(xmin, ymin, xmax, ymax, width, height, true, null);
        /*
         * Now adds the valid range of sample values for each band.
         */
        final DimensionAccessor dimensions = new DimensionAccessor(metadata);
        final int numBands = records.columnCount - (xColumn == yColumn ? 1 : 2);
        for (int band=0; band<numBands; band++) {
            final int column = getColumnForBand(imageIndex, band);
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
     * {@code 10.000}, {@code 10.167}, {@code 10.333}, <cite>etc.</cite>, which can leads to an
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
     * Retourne les données de l'image à l'index spécifié. Si cette image avait déjà été lue, ses
     * données seront retournées immédiatement.  Sinon, cette image sera lue ainsi que toutes les
     * images qui précèdent {@code imageIndex} et qui n'avaient pas encore été lues. Que ces
     * images précédentes soient mémorisées ou oubliées dépend de {@link #seekForwardOnly}.
     *
     * @param  imageIndex Index de l'image à lire.
     * @return Les données de l'image. Cette méthode ne retourne jamais {@code null}.
     * @throws IOException si une erreur est survenue lors de la lecture du flot,
     *         ou si des nombres n'étaient pas correctement formatés dans le flot.
     * @throws IndexOutOfBoundsException si l'index spécifié est en dehors des
     *         limites permises ou si aucune image n'a été conservée à cet index.
     */
    private RecordList getRecords(final int imageIndex) throws IOException {
        clearAbortRequest();
        checkImageIndex(imageIndex);
        if (imageIndex >= nextImageIndex) {
            processImageStarted(imageIndex);
            final BufferedReader reader = getReader();
            final long          origine = getStreamPosition(reader);
            final long           length = getStreamLength(nextImageIndex, imageIndex+1);
            long   nextProgressPosition = (origine>=0 && length>0) ? 0 : Long.MAX_VALUE;
            for (; nextImageIndex <= imageIndex; nextImageIndex++) {
                /*
                 * Réduit la consommation de mémoire des images précédentes. On ne réduit
                 * pas celle de l'image courante,  puisque la plupart du temps le tableau
                 * sera bientôt détruit de toute façon.
                 */
                if (seekForwardOnly) {
                    minIndex = nextImageIndex;
                }
                if (nextImageIndex != 0 && data != null) {
                    final RecordList records = data[nextImageIndex-1];
                    if (records != null) {
                        if (seekForwardOnly) {
                            data[nextImageIndex-1] = null;
                        } else {
                            records.trimToSize();
                        }
                    }
                }
                /*
                 * Procède à la lecture de chacune des lignes de données. Que ces lignes
                 * soient mémorisées ou pas dépend de l'image que l'on est en train de
                 * décoder ainsi que de la valeur de {@link #seekForwardOnly}.
                 */
                double[]    values = null;
                RecordList records = null;
                final boolean    keep       = (nextImageIndex == imageIndex) || !seekForwardOnly;
                final int        xColumn    = getCheckedColumnX(nextImageIndex);
                final int        yColumn    = getCheckedColumnY(nextImageIndex);
                final double     padValue   = getPadValue      (nextImageIndex);
                final LineFormat lineFormat = getLineFormat    (nextImageIndex);
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (isComment(line) || lineFormat.setLine(line) == 0) {
                            continue;
                        }
                        values = lineFormat.getValues(values);
                        for (int i=0; i<values.length; i++) {
                            if (i != xColumn && i != yColumn && values[i] == padValue) {
                                values[i] = Double.NaN;
                            }
                        }
                        round(values);
                        if (keep) {
                            if (records == null) {
                                if (averageLineLength == 0) {
                                    averageLineLength = 10.4f; // Empirical measurement.
                                }
                                final int expectedLineCount = Math.max(8, Math.min(65536,
                                        Math.round(length / (averageLineLength * values.length))));
                                records = new RecordList(values, expectedLineCount,
                                        xColumn, yColumn, getGridTolerance());
                            }
                            records.add(values);
                        }
                        final long position = getStreamPosition(reader) - origine;
                        if (position >= nextProgressPosition) {
                            processImageProgress(position * (100f / length));
                            nextProgressPosition = position + PROGRESS_INTERVAL;
                            if (abortRequested()) {
                                processReadAborted();
                                return records;
                            }
                        }
                    }
                } catch (ParseException exception) {
                    throw new IIOException(getPositionString(exception.getLocalizedMessage()), exception);
                }
                /*
                 * Après la lecture d'une image, vérifie s'il y avait un nombre suffisant de lignes.
                 * Une exception sera lancée si l'image ne contenait pas au moins deux lignes. On
                 * ajustera ensuite le nombre moyen de caractères par données.
                 */
                if (records != null) {
                    final int lineCount = records.getLineCount();
                    if (lineCount < 2) {
                        throw new IIOException(getPositionString(Errors.format(
                                Errors.Keys.FILE_HAS_TOO_FEW_DATA)));
                    }
                    if (data == null) {
                        data = new RecordList[imageIndex+1];
                    } else if (data.length <= imageIndex) {
                        data = Arrays.copyOf(data, imageIndex+1);
                    }
                    data[nextImageIndex] = records;
                    final float meanDatumLength = (getStreamPosition(reader) - origine) / (float) records.getDataCount();
                    if (meanDatumLength > 0) {
                        averageLineLength = meanDatumLength;
                    }
                }
            }
            processImageComplete();
        }
        /*
         * Une fois les lectures terminées, retourne les données de l'image
         * demandée. Une exception sera lancée si ces données n'ont pas été
         * conservées.
         */
        if (data != null && imageIndex < data.length) {
            final RecordList records = data[imageIndex];
            if (records != null) {
                return records;
            }
        }
        throw new IndexOutOfBoundsException(String.valueOf(imageIndex));
    }

    /**
     * Reads the image indexed by {@code imageIndex} and returns it as a complete buffered image.
     *
     * @param  imageIndex the index of the image to be retrieved.
     * @param  param Parameters used to control the reading process, or {@code null}.
     * @return the desired portion of the image.
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
        final int        xColumn = getCheckedColumnX(imageIndex);
        final int        yColumn = getCheckedColumnY(imageIndex);
        final RecordList records = getRecords(imageIndex);
        final int          width = records.getPointCount(xColumn);
        final int         height = records.getPointCount(yColumn);
        final int    numSrcBands = records.columnCount - (xColumn==yColumn ? 1 : 2);
        /*
         * Extracts user's parameters
         */
        final int[]         srcBands;
        final int[]         dstBands;
        final int sourceXSubsampling;
        final int sourceYSubsampling;
        final int subsamplingXOffset;
        final int subsamplingYOffset;
        final int destinationXOffset;
        final int destinationYOffset;
        if (param != null) {
            srcBands           = param.getSourceBands();
            dstBands           = param.getDestinationBands();
            final Point offset = param.getDestinationOffset();
            sourceXSubsampling = param.getSourceXSubsampling();
            sourceYSubsampling = param.getSourceYSubsampling();
            subsamplingXOffset = param.getSubsamplingXOffset();
            subsamplingYOffset = param.getSubsamplingYOffset();
            destinationXOffset = offset.x;
            destinationYOffset = offset.y;
        } else {
            srcBands    = null;
            dstBands    = null;
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
            subsamplingXOffset = 0;
            subsamplingYOffset = 0;
            destinationXOffset = 0;
            destinationYOffset = 0;
        }
        /*
         * Initializes...
         */
        final int numDstBands = (dstBands!=null) ? dstBands.length :
                                (srcBands!=null) ? srcBands.length : numSrcBands;
        final BufferedImage image = getDestination(imageIndex, param, width, height, null); // TODO
        checkReadParamBandSettings(param, numSrcBands, image.getSampleModel().getNumBands());

        final Rectangle    srcRegion = new Rectangle();
        final Rectangle    dstRegion = new Rectangle();
        computeRegions(param, width, height, image, srcRegion, dstRegion);
        final int         sourceXMin = srcRegion.x;
        final int         sourceYMin = srcRegion.y;
        final int         sourceXMax = srcRegion.width  + sourceXMin;
        final int         sourceYMax = srcRegion.height + sourceYMin;

        final WritableRaster  raster = image.getRaster();
        final int        rasterWidth = raster.getWidth();
        final int       rasterHeigth = raster.getHeight();
        final int        columnCount = records.columnCount;
        final int          dataCount = records.getDataCount();
        final float[]           data = records.getData();
        final double            xmin = records.getMinimum(xColumn);
        final double            ymin = records.getMinimum(yColumn);
        final double            xmax = records.getMaximum(xColumn);
        final double            ymax = records.getMaximum(yColumn);
        final double          scaleX = (width -1)/(xmax-xmin);
        final double          scaleY = (height-1)/(ymax-ymin);
        /*
         * Clears the image area. All values are set to NaN.
         */
        if (CLEAR) {
            final int minX = dstRegion.x;
            final int minY = dstRegion.y;
            final int maxX = dstRegion.width  + minX;
            final int maxY = dstRegion.height + minY;
            for (int b=(dstBands!=null) ? dstBands.length : numDstBands; --b>=0;) {
                final int band = (dstBands!=null) ? dstBands[b] : b;
                for (int y=minY; y<maxY; y++) {
                    for (int x=minX; x<maxX; x++) {
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
            columns[i] = getColumnForBand(imageIndex, srcBands!=null ? srcBands[i] : i);
        }
        for (int i=0; i<dataCount; i+=columnCount) {
            /*
             * On convertit maintenant la coordonnée (x,y) logique en coordonnée pixel. Cette
             * coordonnée pixel se réfère à l'image "source";  elle ne se réfère pas encore à
             * l'image destination. Elle doit obligatoirement être entière. Plus loin, nous
             * tiendrons compte du "subsampling".
             */
            final double fx = (data[i+xColumn] - xmin) * scaleX; // (fx,fy) may be NaN: Use
            final double fy = (ymax - data[i+yColumn]) * scaleY; // "!(abs(...)<=tolerance)".
            int           x = (int) Math.round(fx); // This conversion is not the same than
            int           y = (int) Math.round(fy); // getTransform(), but it should be ok.
            if (x >= sourceXMin && x < sourceXMax && y >= sourceYMin && y < sourceYMax) {
                x -= subsamplingXOffset;
                y -= subsamplingYOffset;
                if ((x % sourceXSubsampling) == 0 && (y % sourceYSubsampling) == 0) {
                    x = x/sourceXSubsampling + (destinationXOffset - sourceXMin);
                    y = y/sourceYSubsampling + (destinationYOffset - sourceYMin);
                    if (x<rasterWidth && y<rasterHeigth) {
                        for (int j=0; j<columns.length; j++) {
                            raster.setSample(x, y, (dstBands!=null ? dstBands[j] : j), data[i+columns[j]]);
                        }
                    }
                }
            }
        }
        return image;
    }

    /**
     * Supprime les données de toutes les images
     * qui avait été conservées en mémoire.
     */
    private void clear() {
        data              = null;
        nextImageIndex    = 0;
        averageLineLength = 0;
    }

    /**
     * Restores the {@code TextRecordImageReader} to its initial state.
     */
    @Override
    public void reset() {
        clear();
        super.reset();
    }




    /**
     * Service provider interface (SPI) for {@link TextRecordImageReader}s. This SPI provides
     * necessary implementation for creating default {@link TextRecordImageReader} using default
     * locale and character set. Subclasses can set some fields at construction time in order to
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
     *             charset    = Charset.forName("ISO-LATIN-1");
     *             padValue   = 9999;
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
     * @author Martin Desruisseaux (IRD)
     * @version 3.06
     *
     * @since 2.1
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
         * Constructs a default {@code TextRecordImageReader.Spi}. This constructor
         * provides the following defaults in addition to the defaults defined in the
         * super-class constructor:
         * <p>
         * <ul>
         *   <li>{@link #names}           = {@code "records"}</li>
         *   <li>{@link #MIMETypes}       = {@code "text/plain"}</li>
         *   <li>{@link #pluginClassName} = {@code "org.geotoolkit.image.io.text.TextRecordImageReader"}</li>
         *   <li>{@link #vendorName}      = {@code "Geotoolkit.org"}</li>
         *   <li>{@link #xColumn}         = {@code 0}</li>
         *   <li>{@link #yColumn}         = {@code 1}</li>
         * </ul>
         * <p>
         * For efficienty reasons, the above fields are initialized to shared arrays. Subclasses
         * can assign new arrays, but should not modify the default array content.
         */
        public Spi() {
            names           = NAMES;
            MIMETypes       = MIME_TYPES;
            pluginClassName = "org.geotoolkit.image.io.text.TextRecordImageReader";
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
         * Returns {@code true} if the specified row length is valid. The default implementation
         * returns {@code true} if the row seems "short", where "short" is arbitrary fixed to 10
         * columns. This is an arbitrary choice, which is why this method is not public. It may
         * be changed in any future Geotk version.
         */
        @Override
        boolean isValidColumnCount(final int count) {
            return count >= (xColumn == yColumn ? 2 : 3) && count <= 10;
        }
    }
}
