/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.concurrent.CancellationException;
import javax.imageio.IIOParam;

import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.Localized;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.InvalidGridGeometryException;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.geometry.GeneralDirectPosition;


/**
 * Base class of {@link GridCoverageReader} and {@link GridCoverageWriter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.12
 * @module
 */
public abstract class GridCoverageStore implements Localized {
    /**
     * The dimension of <var>x</var> ordinates, which is {@value}. This is used for example with
     * multi-dimensional dataset (e.g. cubes), in order to determine which dataset dimension to
     * associated the {@link java.awt.image.RenderedImage#getWidth() image width}.
     *
     * @since 3.14
     */
    static final int X_DIMENSION = 0;

    /**
     * The dimension of <var>y</var> ordinates, which is {@value}. This is used for example with
     * multi-dimensional dataset (e.g. cubes), in order to determine which dataset dimension to
     * associated the {@link java.awt.image.RenderedImage#getHeight() image height}.
     *
     * @since 3.14
     */
    static final int Y_DIMENSION = 1;

    /**
     * Minimal image width and height, in pixels. If the user requests a smaller image,
     * then the request will be expanded to that size. The current setting is the minimal
     * size required for allowing bicubic interpolations.
     */
    private static final int MIN_SIZE = 4;

    /**
     * Small values for rounding errors in floating point calculations. This value shall not be
     * too small, otherwise {@link #computeBounds} fails to correct for rounding errors and we
     * get a region to read bigger than necessary. Experience suggests that 1E-6 is too small,
     * while 1E-5 seems okay.
     */
    private static final double EPS = 1E-5;

    /**
     * The locale to use for formatting messages, or {@code null} for a default locale.
     */
    Locale locale;

    /**
     * {@code true} if a request to abort the current read or write operation has been made.
     * Subclasses should set this field to {@code false} at the begining of each read or write
     * operation, and pool the value regularly during the operation.
     *
     * @see #abort()
     */
    protected volatile boolean abortRequested;

    /**
     * Creates a new instance.
     */
    protected GridCoverageStore() {
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
     * @param locale The new locale to use.
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
    String formatErrorMessage(final Exception e) {
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
    final String formatErrorMessage(final Object path, final Exception e, final boolean isWriting) {
        String message = e.getLocalizedMessage();
        if (IOUtilities.canProcessAsPath(path)) {
            final String cause = message;
            message = Errors.getResources(locale).getString(isWriting ?
                    Errors.Keys.CANT_WRITE_$1 : Errors.Keys.CANT_READ_$1, IOUtilities.name(path));
            if (cause != null && cause.indexOf(' ') > 0) { // Append only if we have a sentence.
                message = message + '\n' + cause;
            }
        }
        return message;
    }

    /**
     * Returns the "<cite>Grid to CRS</cite>" conversion as an affine transform.
     * The conversion will map upper-left corner, as in Java2D conventions.
     *
     * @param  gridGeometry The grid geometry from which to extract the conversion.
     * @return The "<cite>grid to CRS</cite>" conversion.
     * @throws InvalidGridGeometryException If the conversion is not affine.
     */
    private AffineTransform getGridToCRS(final GridGeometry2D gridGeometry) throws InvalidGridGeometryException {
        final MathTransform gridToCRS = gridGeometry.getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        if (gridToCRS instanceof AffineTransform) {
            return (AffineTransform) gridToCRS;
        }
        throw new InvalidGridGeometryException(formatErrorMessage(Errors.Keys.NOT_AN_AFFINE_TRANSFORM));
    }

    /**
     * Converts geodetic parameters to pixel parameters and stores the result in the given
     * {@code IIOParam} object. This is a convenience method provided to subclasses implementation.
     * The default implementation invokes the following methods:
     * <p>
     * <ul>
     *   <li>{@link IIOParam#setSourceRegion(Rectangle)}</li>
     *   <li>{@link IIOParam#setSourceSubsampling(int, int, int, int)}</li>
     * </ul>
     * <p>
     * All other attributes are left unchanged.
     *
     * @param  gridGeometry The grid geometry of the source as a whole (without subsampling).
     * @param  geodeticParam Parameters containing the geodetic envelope and the resolution
     *         to use for reading the source.
     * @param  pixelParam The object where to set the source region in subsampling to
     *         use for reading the source.
     * @return The image bounds, returned for convenience. This is the same value than the
     *         one provided by {@link IIOParam#getSourceRegion()} after this method call.
     * @throws CoverageStoreException If the region can not be computed.
     *
     * @since 3.14
     */
    protected Rectangle geodeticToPixelCoordinates(final GridGeometry2D gridGeometry,
            final GridCoverageStoreParam geodeticParam, final IIOParam pixelParam)
            throws CoverageStoreException
    {
        final Rectangle imageBounds;
        final double[] resolution = geodeticParam.getResolution();
        try {
            imageBounds = computeBounds(gridGeometry, geodeticParam.getValidEnvelope(),
                    resolution, geodeticParam.getCoordinateReferenceSystem());
        } catch (Exception e) { // There is many different exceptions thrown by the above.
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (imageBounds == null) {
            throw new CoverageStoreException(formatErrorMessage(Errors.Keys.EMPTY_ENVELOPE));
        }
        pixelParam.setSourceRegion(imageBounds);
        int sx=1, sy=1;
        if (resolution != null) {
            sx = (int) resolution[X_DIMENSION]; // Really 0, not gridGeometry.axisDimensionX
            sy = (int) resolution[Y_DIMENSION]; // Really 1, not gridGeometry.axisDimensionY
        }
        pixelParam.setSourceSubsampling(sx, sy, 0, 0);
        return imageBounds;
    }

    /**
     * Computes the region to read in pixel coordinates. The main purpose of this method is to
     * be invoked just before an image is read, but it could also be invoked by some informative
     * methods like {@code getGridGeometry(GridCoverageReadParam)} (if we decide to add such method).
     *
     * @param gridGeometry  The grid geometry for the whole coverage,
     *                      as provided by {@link GridCoverageReader#getGridGeometry(int)}.
     * @param envelope      The region to read in "real world" coordinates, or {@code null}.
     *                      The CRS of this envelope doesn't need to be the coverage CRS.
     * @param resolution    On input, the requested resolution or {@code null}. On output
     *                      (if non-null), the subsampling to use for reading the image.
     * @param sourceCRS     The CRS of the {@code resolution} parameter, or {@code null}.
     *                      This is usually also the envelope CRS.
     * @return The region to be read in pixel coordinates, or {@code null} if the coverage
     *         can't be read because the region to read is empty.
     */
    private Rectangle computeBounds(final GridGeometry2D gridGeometry, Envelope envelope,
            final double[] resolution, final CoordinateReferenceSystem sourceCRS)
            throws InvalidGridGeometryException, NoninvertibleTransformException, TransformException, FactoryException
    {
        final Rectangle gridRange = gridGeometry.getGridRange2D();
        final int width  = gridRange.width;
        final int height = gridRange.height;
        final AffineTransform gridToCRS = getGridToCRS(gridGeometry);
        final AffineTransform crsToGrid = gridToCRS.createInverse();
        /*
         * Get the full coverage envelope in the coverage CRS. The returned shape is likely
         * (but not garanteed) to be an instance of Rectangle2D. It can be freely modified.
         */
        Shape shapeToRead = XAffineTransform.transform(gridToCRS, gridRange, false); // Will be clipped later.
        Rectangle2D geodeticBounds = (shapeToRead instanceof Rectangle2D) ?
                (Rectangle2D) shapeToRead : shapeToRead.getBounds2D();
        if (geodeticBounds.isEmpty()) {
            return null;
        }
        /*
         * Transform the envelope if needed. We will remember the MathTransform because it will
         * be needed for transforming the resolution later. Then, check if the requested region
         * (requestEnvelope) intersects the coverage region (shapeToRead).
         */
        final CoordinateReferenceSystem targetCRS = gridGeometry.getCoordinateReferenceSystem2D();
        MathTransform toTargetCRS = null;
        if (sourceCRS != null && targetCRS != null && !CRS.equalsIgnoreMetadata(sourceCRS, targetCRS)) {
            final CoordinateOperation op =
                    CRS.getCoordinateOperationFactory(true).createOperation(sourceCRS, targetCRS);
            toTargetCRS = op.getMathTransform();
            if (toTargetCRS.isIdentity()) {
                toTargetCRS = null;
            } else if (envelope != null) {
                envelope = CRS.transform(op, envelope);
            }
        }
        if (envelope != null) {
            final XRectangle2D requestRect = XRectangle2D.createFromExtremums(
                    envelope.getMinimum(X_DIMENSION), envelope.getMinimum(Y_DIMENSION),
                    envelope.getMaximum(X_DIMENSION), envelope.getMaximum(Y_DIMENSION));
            if (requestRect.isEmpty() || !XRectangle2D.intersectInclusive(requestRect, geodeticBounds)) {
                return null;
            }
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
                    shapeToRead = area;
                    geodeticBounds = shapeToRead.getBounds2D();
                }
                if (geodeticBounds.isEmpty()) {
                    return null;
                }
            }
        }
        /*
         * Transforms ["real world" envelope] --> [region in pixel coordinates] and computes the
         * subsampling from the desired resolution.  Note that we transform 'shapeToRead' (which
         * is a generic shape) rather than Rectangle2D instances, because operating on Shape can
         * give a smaller envelope when the transform contains rotation terms.
         */
        double sx = geodeticBounds.getWidth();  // "Real world" size of the region to be read.
        double sy = geodeticBounds.getHeight(); // Need to be extracted before the line below.
        shapeToRead = XAffineTransform.transform(crsToGrid, shapeToRead, shapeToRead != gridRange);
        final RectangularShape imageRegion = (shapeToRead instanceof RectangularShape) ?
                (RectangularShape) shapeToRead : shapeToRead.getBounds2D();
        sx = imageRegion.getWidth()  / sx;  // (sx,sy) are now conversion factors
        sy = imageRegion.getHeight() / sy;  // from "real world" to pixel coordinates.
        final int xSubsampling;
        final int ySubsampling;
        if (resolution != null) {
            /*
             * Transform the resolution if needed. The code below assume that the target
             * dimension (always 2) is smaller than the source dimension.
             */
            double[] transformed = resolution;
            if (toTargetCRS != null) {
                final double[] center = new double[toTargetCRS.getSourceDimensions()];
                center[X_DIMENSION] = imageRegion.getCenterX();
                center[Y_DIMENSION] = imageRegion.getCenterY();
                gridToCRS.transform(center, 0, center, 0, 1);
                toTargetCRS.inverse().transform(center, 0, center, 0, 1);
                transformed = CRS.deltaTransform(toTargetCRS, new GeneralDirectPosition(center), resolution);
            }
            sx *= transformed[X_DIMENSION];
            sy *= transformed[Y_DIMENSION];
            xSubsampling = Math.max(1, Math.min(width /MIN_SIZE, (int) (sx + EPS)));
            ySubsampling = Math.max(1, Math.min(height/MIN_SIZE, (int) (sy + EPS)));
            resolution[X_DIMENSION] = xSubsampling;
            resolution[Y_DIMENSION] = ySubsampling;
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
        return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    /**
     * Cancels the read or write operation which is currently under progress in an other thread.
     * The operation will throw a {@link CancellationException}, unless it had the time to complete.
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
        locale = null;
        abortRequested = false;
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
        locale = null;
        abortRequested = false;
    }
}
