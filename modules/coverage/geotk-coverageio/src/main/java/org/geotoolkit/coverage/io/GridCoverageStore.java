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
package org.geotoolkit.coverage.io;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.AffineTransform;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOParam;

import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Localized;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.logging.LogProducer;
import org.apache.sis.util.logging.PerformanceLevel;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.AxisRangeType;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.InvalidGridGeometryException;
import org.geotoolkit.display.shape.XRectangle2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.Envelopes;

import static org.geotoolkit.image.io.MultidimensionalImageStore.*;
import static org.geotoolkit.internal.InternalUtilities.adjustForRoundingError;


/**
 * Base class of {@link GridCoverageReader} and {@link GridCoverageWriter}. This base class
 * provides common functionalities to {@linkplain #setLocale(Locale) set the locale} used
 * for error messages, {@linkplain #abort() abort} a reading or writing process, and
 * {@linkplain #reset() reset} or {@linkplain #dispose() dispose} the reader or writer.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.12
 * @module
 */
public abstract class GridCoverageStore implements LogProducer, Localized {
    /**
     * Set to {@code true} for allowing debug information to be send to the
     * {@linkplain System#out standard output stream}.
     */
    @Debug
    static final boolean DEBUG = false;

    /**
     * The logger to use for logging messages during read and write operations.
     *
     * @since 3.15
     */
    static final Logger LOGGER = Logging.getLogger(GridCoverageStore.class);

    /**
     * Minimal image width and height, in pixels. If the user requests a smaller image,
     * then the request will be expanded to that size. The current setting is the minimal
     * size required for allowing bicubic interpolations.
     */
    private static final int MIN_SIZE = 4;

    /**
     * Small values for rounding errors in floating point calculations. This value shall not be
     * too small, otherwise {@link #geodeticToPixelCoordinates} fails to correct for rounding
     * errors and we get a region to read bigger than necessary. Experience suggests that 1E-6
     * is too small, while 1E-5 seems okay.
     */
    private static final double EPS = 1E-5;

    /**
     * The hints to use for fetching factories. This is initialized to the system defaults.
     */
    private final Hints hints;

    /**
     * The logging level to use for read and write operations. If {@code null}, then the
     * level shall be selected by {@link PerformanceLevel#forDuration(long, TimeUnit)}.
     *
     * @since 3.15
     */
    private Level logLevel;

    /**
     * The locale to use for formatting messages, or {@code null} for a default locale.
     */
    Locale locale;

    /**
     * If {@code true}, the {@link #geodeticToPixelCoordinates geodeticToPixelCoordinates} method
     * will not compute the difference between the requested coverage and the actual coverage that
     * can be specified through {@link IIOParam}.
     * <p>
     * The value of this field is typically {@code true} for {@link GridCoverageReader} (readers
     * can returns a coverage having a different envelope and resolution than the requested one)
     * and {@code false} for {@link GridCoverageWriter} (writers have to write the coverage
     * exactly as requested). However subclasses can change the value of this field, for example
     * if they want to implement a stricter coverage reader.
     *
     * @see #geodeticToPixelCoordinates(GridGeometry2D, GridCoverageStoreParam, IIOParam)
     *
     * @since 3.14
     */
    boolean ignoreGridTransforms;

    /**
     * The transform from the grid to be written to the source grid. This is the transform to be
     * used in a resampling operation before to create the destination image if the user request
     * shall be honored as specified.
     * <p>
     * This field is computed by the {@link #geodeticToPixelCoordinates geodeticToPixelCoordinates}
     * method only if {@link #ignoreGridTransforms} is {@code false}.
     *
     * @since 3.14
     */
    transient MathTransform destGridToSource;

    /**
     * {@code true} if a request to abort the current read or write operation has been made.
     * Subclasses should set this field to {@code false} at the beginning of each read or write
     * operation, and pool the value regularly during the operation.
     *
     * @see #abort()
     */
    protected volatile boolean abortRequested;

    /**
     * Creates a new instance.
     */
    protected GridCoverageStore() {
        hints = new Hints();
    }

    /**
     * Returns {@code true} if logging is enabled.
     */
    final boolean isLoggable() {
        Level level = logLevel;
        if (level == null) {
            level = PerformanceLevel.SLOWEST;
        }
        return LOGGER.isLoggable(level);
    }

    /**
     * Returns the logging level is explicitely set, or the {@link Level#FINE} level
     * otherwise. This is used for logging operation that are not performance measurement.
     *
     * @since 3.16
     */
    final Level getFineLevel() {
        final Level level = logLevel;
        return (level != null) ? level : PerformanceLevel.FINE;
    }

    /**
     * Returns the logging level to use for an operation of the given duration.
     *
     * @param  duration The duration, in nanoseconds.
     * @return The logging level to use.
     *
     * @since 3.16
     */
    final Level getLogLevel(final long duration) {
        Level level = logLevel;
        if (level == null) {
            level = PerformanceLevel.forDuration(duration, TimeUnit.NANOSECONDS);
        }
        return level;
    }

    /**
     * Returns the logging level to use for read and write operations.
     * The default value is one of the {@link PerformanceLevel} constants,
     * determined according the duration of the operation.
     *
     * @return The current logging level.
     *
     * @since 3.15
     */
    @Override
    public Level getLogLevel() {
        final Level level = logLevel;
        return (level != null) ? level : PerformanceLevel.PERFORMANCE;
    }

    /**
     * Sets the logging level to use for read and write operations. A {@code null}
     * value restores the default level documented in the {@link #getLogLevel()} method.
     *
     * @param level The new logging level, or {@code null} for the default.
     *
     * @since 3.15
     */
    @Override
    public void setLogLevel(final Level level) {
        logLevel = level;
    }

    /**
     * If the given object is an instance of {@link LogProducer}, copies the log level.
     *
     * @param object The object on which to set the log level.
     *
     * @since 3.15
     */
    final void copyLevel(final Object object) {
        if (object instanceof LogProducer) {
            ((LogProducer) object).setLogLevel(logLevel);
        }
    }

    /**
     * Returns the locale to use for formatting warnings and error messages,
     * or {@code null} for the {@linkplain Locale#getDefault() default}.
     *
     * @return The current locale, or {@code null}.
     *
     * @see javax.imageio.ImageReader#getLocale()
     * @see javax.imageio.ImageWriter#getLocale()
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the current locale of this coverage reader or writer to the given value. A value of
     * {@code null} removes any previous setting, and indicates that the reader or writer should
     * localize as it sees fit.
     *
     * @param locale The new locale to use, or {@code null} for a default one.
     *
     * @see javax.imageio.ImageReader#setLocale(Locale)
     * @see javax.imageio.ImageWriter#setLocale(Locale)
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Returns the locale from the given list which is equals to the given locale, or which
     * is using the same language.
     *
     * @param  locale The user supplied locale.
     * @param  list The list of locales allowed by the reader or the writer.
     * @return The locale from the given list which is equals, or using the same language,
     *         than the specified locale.
     *
     * @since 3.14
     */
    static Locale select(final Locale locale, final Locale[] list) {
        if (locale != null && list != null) {
            for (int i=list.length; --i>=0;) {
                final Locale candidate = list[i];
                if (locale.equals(candidate)) {
                    return candidate;
                }
            }
            final String language = getISO3Language(locale);
            if (language != null) {
                for (int i=list.length; --i>=0;) {
                    final Locale candidate = list[i];
                    if (language.equals(getISO3Language(candidate))) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the ISO language code for the specified locale, or {@code null} if not available.
     * This is used for finding a match when the locale given to the {@link #setLocale(Locale)}
     * method does not match exactly the locale supported by the image reader or writer. In such
     * case, we will pickup a locale for the same language even if it is not the same country.
     */
    private static String getISO3Language(final Locale locale) {
        try {
            return locale.getISO3Language();
        } catch (MissingResourceException exception) {
            return null;
        }
    }

    /**
     * Returns a localized string for the specified error key.
     *
     * @param key One of the constants declared in the {@link Errors.Keys} inner class.
     */
    final String formatErrorMessage(final int key) {
        return Errors.getResources(locale).getString(key);
    }

    /**
     * Returns an error message for the given exception, using the current input or output.
     * This method is overridden by {@link GridCoverageReader} and {@link GridCoverageWriter},
     * which will format a better message including the input or output path.
     */
    String formatErrorMessage(final Throwable e) {
        return e.getLocalizedMessage();
    }

    /**
     * Returns an error message for the given exception. If the input or output is known, then
     * this method returns "<cite>Can't read/write 'the name'</cite>" followed by the cause
     * message. Otherwise it returns the localized message of the given exception.
     *
     * @param path The input or output.
     * @param e The exception which occurred.
     * @param isWriting {@code false} If reading, or {@code true} if writing.
     */
    final String formatErrorMessage(final Object path, final Throwable e, final boolean isWriting) {
        String message = e.getLocalizedMessage();
        if (IOUtilities.canProcessAsPath(path)) {
            final String cause = message;
            message = Errors.getResources(locale).getString(isWriting ?
                    Errors.Keys.CANT_WRITE_FILE_1 : Errors.Keys.CANT_READ_FILE_1, IOUtilities.name(path));
            if (cause != null && cause.indexOf(' ') > 0) { // Append only if we have a sentence.
                message = message + '\n' + cause;
            }
        }
        return message;
    }

    /**
     * Converts geodetic parameters to pixel parameters and stores the result in the given
     * {@code IIOParam} object. This method expects a {@code gridGeometry} argument, which
     * is the grid geometry of the source coverage. Coordinate transformations are applied
     * as needed if the parameters given in the {@code geodeticParam} argument use a different
     * CRS. Then the source region and the source subsampling (in pixel units) are computed in
     * such a way that the image to be read contains fully the requested image at a resolution
     * equals or better than the requested resolution. Next, the source region and subsampling
     * are clipped to the image bounds and maximal resolution, and the following methods are
     * invoked with the result:
     * <p>
     * <ul>
     *   <li>{@link IIOParam#setSourceRegion(Rectangle)}</li>
     *   <li>{@link IIOParam#setSourceSubsampling(int, int, int, int)}</li>
     * </ul>
     * <p>
     * As a consequence of the reprojection, rounding to pixel coordinates and clipping, the
     * source region and subsampling may not match exactly the requested envelope and resolution.
     * Callers are responsible for checking the resulting grid geometry and perform themselves a
     * resampling operation if they need exactly the requested grid geometry.
     *
     * @param  gridGeometry The grid geometry of the source as a whole (without subsampling).
     * @param  geodeticParam Parameters containing the geodetic envelope and the resolution
     *         to use for reading the source.
     * @param  pixelParam The object where to set the source region in subsampling to
     *         use for reading the source.
     * @return The transform from the grid that the user requested in the {@code geodeticParam},
     *         to the grid that he will get when reading the image using the {@code pixelParam}.
     *         If the conversion from the geodetic to pixel parameters can produce exactly the
     *         requested image, then the returned transform is approximatively an identity one.
     *         If the {@link #ignoreGridTransforms} field is {@code true}, then the transform
     *         is not computed and this method returns {@code null}.
     * @throws CoverageStoreException If the region can not be computed.
     *
     * @since 3.14
     */
    MathTransform2D geodeticToPixelCoordinates(final GridGeometry2D gridGeometry,
            final GridCoverageStoreParam geodeticParam, final IIOParam pixelParam)
            throws CoverageStoreException
    {
        final boolean needsLongitudeShift = AxisRangeType.POSITIVE_LONGITUDE.indexIn(
                gridGeometry.getCoordinateReferenceSystem().getCoordinateSystem()) >= 0;
        final MathTransform2D destToExtractedGrid;
        try {
            destToExtractedGrid = geodeticToPixelCoordinates(gridGeometry,
                    geodeticParam.getValidEnvelope(needsLongitudeShift),
                    geodeticParam.getResolution(),
                    geodeticParam.getCoordinateReferenceSystem(),
                    pixelParam);
        } catch (CoverageStoreException e) {
            throw e;
        } catch (Exception e) { // There is many different exceptions thrown by the above.
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        return destToExtractedGrid;
    }

    /**
     * Computes the region to read in pixel coordinates. The main purpose of this method is to
     * be invoked just before an image is read or written, but it could also be invoked by some
     * informative methods like {@code ImageCoverageReader.getGridGeometry(GridCoverageReadParam)}
     * if we decided to add such method.
     *
     * @param gridGeometry  The grid geometry of the source coverage as a whole,
     *                      as provided by {@link GridCoverageReader#getGridGeometry(int)}.
     * @param envelope      The region to read in "real world" coordinates, or {@code null}.
     *                      The CRS of this envelope doesn't need to be the coverage CRS.
     * @param resolution    The requested resolution or {@code null}.
     * @param requestCRS    The CRS of the {@code resolution} parameter, or {@code null}.
     *                      Should also be the envelope CRS, but the code is tolerant to mismatch.
     * @return The transform from the requested grid to the actual grid, or {@code null} if
     *         {@link #ignoreGridTransforms} is {@code true}.
     * @throws CoverageStoreException If the region to read is empty.
     */
    private MathTransform2D geodeticToPixelCoordinates(
            final GridGeometry2D            gridGeometry,
            final GeneralEnvelope           envelope,
            final double[]                  resolution,
            final CoordinateReferenceSystem requestCRS,
            final IIOParam                  imageParam)
            throws InvalidGridGeometryException, TransformException, FactoryException, CoverageStoreException
    {
        final Rectangle       gridExtent = gridGeometry.getExtent2D();
        final MathTransform2D gridToCRS  = gridGeometry.getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        final MathTransform2D crsToGrid  = gridToCRS.inverse();
        /*
         * Get the full coverage envelope in the coverage CRS. The returned shape is likely
         * (but not guaranteed) to be an instance of Rectangle2D. It can be freely modified.
         *
         * IMPLEMENTATION NOTE: It could have been more efficient to compute the transform
         * from the requested envelope to the source grid (by concatenation of the various
         * transformation steps), so we could have performed only one shape transformation.
         * However we want to use the CRS.transform(CoordinateOperation, ...) method rather
         * than the CRS.transform(MathTransform, ...) method, in order to handle the cases
         * where the requested region is over a geographic pole.
         */
        Shape shapeToRead = gridToCRS.createTransformedShape(gridExtent); // Will be clipped later.
        Rectangle2D geodeticBounds = (shapeToRead instanceof Rectangle2D) ?
                (Rectangle2D) shapeToRead : shapeToRead.getBounds2D();
        ensureNonEmpty(geodeticBounds);
        /*
         * Transform the envelope if needed. We will remember the MathTransform because it will
         * be needed for transforming the resolution later. Then, check if the requested region
         * (requestEnvelope) intersects the coverage region (shapeToRead).
         */
        Envelope envelopeInDataCRS = envelope;
        MathTransform requestToDataCRS = null;
        final CoordinateReferenceSystem dataCRS = gridGeometry.getCoordinateReferenceSystem2D();
        if (requestCRS != null && dataCRS != null && !CRS.equalsIgnoreMetadata(requestCRS, dataCRS)) {
            final CoordinateOperation op = createOperation(requestCRS, dataCRS);
            requestToDataCRS = op.getMathTransform();
            if (requestToDataCRS.isIdentity()) {
                requestToDataCRS = null;
            } else {
                envelopeInDataCRS = Envelopes.transform(op, envelope);
            }
        }
        if (envelopeInDataCRS != null) {
            final XRectangle2D requestRect = XRectangle2D.createFromExtremums(
                    envelopeInDataCRS.getMinimum(X_DIMENSION), envelopeInDataCRS.getMinimum(Y_DIMENSION),
                    envelopeInDataCRS.getMaximum(X_DIMENSION), envelopeInDataCRS.getMaximum(Y_DIMENSION));
            /*
             * If the requested envelope contains fully the coverage bounds, we can ignore it
             * (we will read the full coverage). Otherwise if the coverage contains fully the
             * requested region, the requested region become the new bounds. Otherwise we need
             * to compute the intersection.
             */
            if (!requestRect.contains(geodeticBounds)) {
                requestRect.intersect(geodeticBounds);
                if (shapeToRead == geodeticBounds || shapeToRead.contains(requestRect)) {
                    shapeToRead = geodeticBounds = requestRect;
                } else {
                    // Use Area only if 'shapeToRead' is something more complicated than a
                    // Rectangle2D. Note that the above call to Rectangle2D.intersect(...)
                    // is still necessary because 'requestRect' may had infinite values
                    // before the call to Rectangle2D.intersect(...), and infinite values
                    // are not handled well by Area.
                    final Area area = new Area(shapeToRead);
                    area.intersect(new Area(requestRect));
                    geodeticBounds = (shapeToRead = area).getBounds2D();
                }
                if (geodeticBounds.isEmpty()) {
                    throw new DisjointCoverageDomainException(formatErrorMessage(Errors.Keys.REQUESTED_ENVELOPE_DO_NOT_INTERSECT));
                }
            }
        }
        /*
         * Transforms ["real world" envelope] --> [region in pixel coordinates] and computes the
         * subsampling from the desired resolution.  Note that we transform 'shapeToRead' (which
         * is a generic shape) rather than Rectangle2D instances, because operating on Shape can
         * give a smaller envelope when the transform contains rotation terms.
         */
        if (crsToGrid instanceof AffineTransform) {
            shapeToRead = XAffineTransform.transform((AffineTransform) crsToGrid, shapeToRead,
                    shapeToRead != geodeticBounds); // boolean telling whatever we can overwrite.
        } else {
            shapeToRead = crsToGrid.createTransformedShape(shapeToRead);
        }
        final RectangularShape imageRegion = (shapeToRead instanceof RectangularShape) ?
                (RectangularShape) shapeToRead : shapeToRead.getBounds2D();
        // 'shapeToRead' and 'imageRegion' now contain coordinates in units of source grid.
        int width  = gridExtent.width;
        int height = gridExtent.height;
        final int xSubsampling;
        final int ySubsampling;
        if (resolution != null) {
            /*
             * Transform the resolution if needed. The code below assumes that the target
             * dimension (always 2) is smaller than the source dimension.
             */
            double[] resolutionInDataCRS = resolution;
            if (requestToDataCRS != null) {
                // Create 'center' with a length large enough for containing the target coordinate.
                // We invoke getSourceDimensions() below because we will use the inverse transform.
                final double[] center = new double[requestToDataCRS.getSourceDimensions()];
                center[X_DIMENSION] = imageRegion.getCenterX();
                center[Y_DIMENSION] = imageRegion.getCenterY();
                gridToCRS.transform(center, 0, center, 0, 1);
                requestToDataCRS.inverse().transform(center, 0, center, 0, 1);
                resolutionInDataCRS = CRS.deltaTransform(requestToDataCRS,
                        new GeneralDirectPosition(center), resolution);
            }
            final double sx = resolutionInDataCRS[X_DIMENSION] * (imageRegion.getWidth()  / geodeticBounds.getWidth());
            final double sy = resolutionInDataCRS[Y_DIMENSION] * (imageRegion.getHeight() / geodeticBounds.getHeight());
            xSubsampling = Math.max(1, Math.min(width  / MIN_SIZE, (int) (sx + EPS)));
            ySubsampling = Math.max(1, Math.min(height / MIN_SIZE, (int) (sy + EPS)));
        } else {
            xSubsampling = 1;
            ySubsampling = 1;
        }
        /*
         * Makes sure that the image region is contained inside the RenderedImage valid bounds.
         * We need to ensure that in order to prevent Image Reader to perform its own clipping
         * (at least for the minimal X and Y values), which would cause the gridToCRS transform
         * to be wrong. In addition we also ensure that the resulting image has the minimal size.
         * If the subsampling will cause an expansion of the envelope, we distribute the expansion
         * on each side of the envelope rather than expanding only the bottom and right side (this
         * is the purpose of the (delta % subsampling) - 1 part).
         */
        int xmin = (int) Math.floor(imageRegion.getMinX() + EPS);
        int ymin = (int) Math.floor(imageRegion.getMinY() + EPS);
        int xmax = (int) Math.ceil (imageRegion.getMaxX() - EPS);
        int ymax = (int) Math.ceil (imageRegion.getMaxY() - EPS);
        int delta = xmax - xmin;
        delta = Math.max(MIN_SIZE * xSubsampling - delta, (delta % xSubsampling) - 1);
        if (delta > 0) {
            final int r = delta & 1;
            delta >>>= 1;
            xmin -= delta;
            xmax += delta + r;
        }
        delta = ymax - ymin;
        delta = Math.max(MIN_SIZE * ySubsampling - delta, (delta % ySubsampling) - 1);
        if (delta > 0) {
            final int r = delta & 1;
            delta >>>= 1;
            ymin -= delta;
            ymax += delta + r;
        }
        if (xmin < 0)      xmin = 0;
        if (ymin < 0)      ymin = 0;
        if (xmax > width)  xmax = width;
        if (ymax > height) ymax = height;
        width  = xmax - xmin;
        height = ymax - ymin;
        /*
         * All the configuration in the IIOParam object happen here.
         */
        if (imageParam != null) {
            imageParam.setSourceRegion(new Rectangle(xmin, ymin, width, height));
            imageParam.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
        }
        if (ignoreGridTransforms) {
            return null;
        }

        // ---------------------------------------------------------------------------------
        //   From this point, the 'geodeticToPixelCoordinates' work is done. The remainder
        //   of this method is about the computation of math transform holding the errors.
        // ---------------------------------------------------------------------------------

        /*
         * For the remaining of this method, we will need a non-null request envelope. If no
         * envelope were explicitly specified, we will compute the envelope that the user is
         * assumed to want using the full envelope of source data. Note that while the
         * envelopes could be n-dimensional, the 2D part is enough for this method.
         */
        final Envelope2D validEnvelope;
        if (envelope == null || resolution == null) {
            CoordinateReferenceSystem crs = CRSUtilities.getCRS2D(requestCRS); // X_DIMENSION, Y_DIMENSION
            if (crs == null) {
                crs = dataCRS; // 'dataCRS' is already 2D.
            } else if (dataCRS != null && !CRS.equalsIgnoreMetadata(dataCRS, crs)) {
                final CoordinateOperation op = createOperation(dataCRS, crs);
                geodeticBounds = Envelopes.transform(op, geodeticBounds, geodeticBounds);
                ensureNonEmpty(geodeticBounds);
            }
            validEnvelope = new Envelope2D(crs, geodeticBounds);
        } else {
            validEnvelope = null;
        }
        /*
         * We also need to ensure that the request envelope is associated with the request CRS.
         * Finally, we will need the clipped envelope if the resolution is null, in order to
         * compute a default value.
         */
        final Envelope requestEnvelope;
        if (envelope == null) {
            requestEnvelope = validEnvelope;
        } else {
            // Ensure that the user-supplied envelope defines a CRS,
            // since we will need it for creating a GridGeometry2D.
            CoordinateReferenceSystem crs = requestCRS;
            if (crs == null) {
                if (envelope.getDimension() == 2) {
                    crs = dataCRS; // == gridGeometry.getCoordinateReferenceSystem2D()
                } else {
                    crs = gridGeometry.getCoordinateReferenceSystem();
                }
            }
            envelope.setCoordinateReferenceSystem(crs);
            requestEnvelope = envelope;
            // If the resolution is null, we will need the clipped envelope.
            // Otherwise 'validEnvelope' should be null.
            if (validEnvelope != null) {
                final XRectangle2D bounds = XRectangle2D.createFromExtremums(
                        requestEnvelope.getMinimum(X_DIMENSION),
                        requestEnvelope.getMinimum(Y_DIMENSION),
                        requestEnvelope.getMaximum(X_DIMENSION),
                        requestEnvelope.getMaximum(Y_DIMENSION));
                bounds.intersect(validEnvelope);
                validEnvelope.setRect(bounds);
                ensureNonEmpty(validEnvelope);
            }
        }
        /*
         * Compute the transform from the destination grid to the extracted grid. The destination
         * grid is the one requested by the user.  The extracted grid is the region of the source
         * grid which is read when using the IIOParam setting (including subsampling).
         *
         * 1) First, compute the affine transform from the grid that the user asked
         *    for (computed from the envelope and the resolution) to the request CRS.
         *    As a side effect, we get the destination grid geometry.
         *
         * 2) Next, concatenate the above transform with the 'requestToDataCRS' and
         *    'crsToGrid' transforms (computed at the begining of this method). The
         *    result is the transform from the destination grid to the source grid.
         *
         * 3) Finally, concatenate the above transform with the "source grid to extracted
         *    grid" transform. The later is computed from the source region and subsampling
         *    parameters given to the IIOParam object.
         *
         * Those three steps are the subject of all the remaining code in this method.
         * A few intermediate products are created as a side effect, in particular the
         * grid geometry of the target grid. Those information are useful for writting
         * process (they are typically ignored for reading process).
         */
        final boolean flipX, flipY;
        if (requestCRS != null || dataCRS != null) {
            CoordinateSystem cs = (requestCRS != null ? requestCRS : dataCRS).getCoordinateSystem();
            flipX =  isOpposite(cs.getAxis(X_DIMENSION).getDirection());
            flipY = !isOpposite(cs.getAxis(Y_DIMENSION).getDirection());
        } else {
            flipX = false;
            flipY = true;
        }
        final int requestDimension = requestEnvelope.getDimension();
        final Matrix m = Matrices.create(requestDimension + 1, 3);
        m.setElement(requestDimension, 2, 1);
        /*
         * Set the translation terms for all known dimensions. In the case of axes having reversed
         * direction (typically the Y axis), we take the maximum rather than the minimum. The scale
         * factor will be reversed later.
         */
        for (int i=requestDimension; --i>=0;) {
            final double t;
            if ((flipX && i == X_DIMENSION) || (flipY && i == Y_DIMENSION)) {
                t = requestEnvelope.getMaximum(i);
            } else {
                t = requestEnvelope.getMinimum(i);
            }
            m.setElement(i, 2, adjustForRoundingError(t));
        }
        /*
         * Set the scale factors, which are the resolution. If the resolution was not specified,
         * we will compute the scale factor from the envelope assuming that the target image will
         * have the same number of pixels than the region read from the source image.
         */
        double scaleX, scaleY;
        if (resolution != null) {
            scaleX = resolution[X_DIMENSION];
            scaleY = resolution[Y_DIMENSION];
        } else {
            scaleX = validEnvelope.getSpan(X_DIMENSION) / width;
            scaleY = validEnvelope.getSpan(Y_DIMENSION) / height;
            // No need to take subsampling in account, since it is 1 in this case.
        }
        if (flipX) scaleX = -scaleX;
        if (flipY) scaleY = -scaleY;
        scaleX = adjustForRoundingError(scaleX);
        scaleY = adjustForRoundingError(scaleY);
        m.setElement(X_DIMENSION, X_DIMENSION, scaleX);
        m.setElement(Y_DIMENSION, Y_DIMENSION, scaleY);
        /*
         * At this point, we are ready to create the 'gridToCRS' transform of the target coverage.
         * We take this opportunity for creating the full grid envelope of the requested coverage.
         * Note that the grid envelope is empty if the transform from grid to envelope as infinite
         * coefficient values. This happen for example with Mercator projection close to poles.
         */
        MathTransform destToExtractedGrid = MathTransforms.linear(m);
        computeRequestedBounds(destToExtractedGrid, requestEnvelope, requestCRS);
        /*
         * Concatenate the transforms. We get the transform from the grid that the user
         * requested to the grid actually used in the source image, assuming the source
         * grid was read using the values we have set in the IIOParam object.
         */
        if (requestToDataCRS == null) {
            final int sourceDim = destToExtractedGrid.getTargetDimensions();
            if (sourceDim != 2) {
                requestToDataCRS = MathTransforms.dimensionFilter(sourceDim, new int[] {X_DIMENSION, Y_DIMENSION});
            }
        }
        if (requestToDataCRS != null) {
            destToExtractedGrid = MathTransforms.concatenate(destToExtractedGrid, requestToDataCRS);
        }
        destGridToSource = destToExtractedGrid = MathTransforms.concatenate(destToExtractedGrid, crsToGrid);
        if (xSubsampling != 1 || ySubsampling != 1 || xmin != 0 || ymin != 0) {
            scaleX = 1d / xSubsampling;
            scaleY = 1d / ySubsampling;
            destToExtractedGrid = MathTransforms.concatenate(destToExtractedGrid,
                    new AffineTransform2D(scaleX, 0, 0, scaleY,
                            (double) -xmin / (double) xSubsampling,
                            (double) -ymin / (double) ySubsampling));
        }
        return (MathTransform2D) destToExtractedGrid;
    }

    /**
     * A callback invoked by {@link #geodeticToPixelCoordinates geodeticToPixelCoordinates},
     * for {@link GridCoverageWriter} usage only.  The value computed by this method is not
     * of interest to {@link GridCoverageReader}, because the readers are allowed to return
     * a coverage different than the requested one. However writers are required to write
     * the coverage as requested, and consequently need more informations.
     * <p>
     * An additional reason for avoiding to implement this method in readers case is that the
     * {@link GridGeometry2D} creation may fail if the given math transform is non-invertible,
     * for example because the matrix is not square. It would cause a useless failure for read
     * operations.
     */
    void computeRequestedBounds(MathTransform destToExtractedGrid, Envelope requestEnvelope,
            CoordinateReferenceSystem requestCRS) throws TransformException, CoverageStoreException
    {
        // Implementation provided by GridCoverageWriter only.
    }

    /**
     * Ensures that the given rectangle is not empty.
     */
    private void ensureNonEmpty(final Rectangle2D envelope) throws CoverageStoreException {
        if (envelope.isEmpty()) {
            throw new CoverageStoreException(formatErrorMessage(Errors.Keys.EMPTY_ENVELOPE_2D));
        }
    }

    /**
     * Returns the coordinate operation from the given source CRS to the given target CRS.
     * This method is invoked when the CRS requested by the user is not the same than the
     * data CRS.
     * <p>
     * This method is invoked in places where a bounding box really needs to be transformed
     * using a {@link CoordinateOperation} object, not a {@link MathTransform}, because the
     * region to transform could be over a pole. In such cases, the {@link CRS} utility class
     * can perform additional checks provided that the transform is performed using a coordinate
     * operation.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException if the operation creation failed.
     */
    private CoordinateOperation createOperation(
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS) throws FactoryException
    {
        return CRS.getCoordinateOperationFactory(
                Boolean.TRUE.equals(hints.get(Hints.LENIENT_DATUM_SHIFT)))
                .createOperation(sourceCRS, targetCRS);
    }

    /**
     * Returns {@code true} if the given direction is opposite to the "standard" direction.
     * In this context, "standard" means increasing values toward up or right.
     */
    private static boolean isOpposite(AxisDirection direction) {
        boolean isOpposite = AxisDirections.isOpposite(direction);
        direction = AxisDirections.absolute(direction);
        if (AxisDirection.ROW_POSITIVE.equals(direction) ||
            AxisDirection.DISPLAY_DOWN.equals(direction))
        {
            isOpposite = !isOpposite;
        }
        return isOpposite;
    }

    /**
     * Returns {@code true} if the given transform is null or the identity transform.
     * In the particular case of an affine transform, an arbitrary tolerance threshold
     * is used. The threshold shall be chosen in order to work well with the transforms
     * returned by the {@link #geodeticToPixelCoordinates} method, despite rounding errors.
     */
    static boolean isIdentity(final MathTransform2D transform) {
        return (transform == null) || transform.isIdentity() ||
                (transform instanceof AffineTransform &&
                 XAffineTransform.isIdentity((AffineTransform) transform, EPS));
    }

    /**
     * Cancels the read or write operation which is currently under progress in an other thread.
     * Invoking this method will cause a {@link CancellationException} to be thrown in the reading
     * or writing thread (not this thread), unless the operation had the time to complete.
     *
     * {@section Note for implementors}
     * Subclasses should set the {@link #abortRequested} field to {@code false} at the beginning
     * of each read or write operation, and poll the value regularly during the operation.
     *
     * @see #abortRequested
     * @see javax.imageio.ImageReader#abort()
     * @see javax.imageio.ImageWriter#abort()
     */
    public void abort() {
        abortRequested = true;
    }

    /**
     * Throws {@link CancellationException} if a request to abort the current read or write
     * operation has been made since this object was instantiated or {@link #abortRequested}
     * has been cleared.
     *
     * @throws CancellationException If the {@link #abort()} method has been invoked.
     */
    final void checkAbortState() throws CancellationException {
        if (abortRequested) {
            throw new CancellationException(formatErrorMessage(Errors.Keys.CANCELED_OPERATION));
        }
    }

    /**
     * Restores this reader or writer to its initial state.
     *
     * @throws CoverageStoreException if an error occurs while restoring to the initial state.
     *
     * @see javax.imageio.ImageReader#reset()
     * @see javax.imageio.ImageWriter#reset()
     */
    public void reset() throws CoverageStoreException {
        locale           = null;
        destGridToSource = null;
        abortRequested   = false;
    }

    /**
     * Allows any resources held by this reader or writer to be released. The result of calling
     * any other method subsequent to a call to this method is undefined.
     * <p>
     * Subclass implementations shall ensure that all resources, especially JCBC connections,
     * are released.
     *
     * @throws CoverageStoreException if an error occurs while disposing resources.
     *
     * @see javax.imageio.ImageReader#dispose()
     * @see javax.imageio.ImageWriter#dispose()
     */
    public void dispose() throws CoverageStoreException {
        locale           = null;
        destGridToSource = null;
        abortRequested   = false;
    }
}
