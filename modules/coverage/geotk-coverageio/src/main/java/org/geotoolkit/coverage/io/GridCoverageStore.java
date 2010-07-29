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
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.AffineTransform;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.concurrent.CancellationException;
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

import org.geotoolkit.util.Localized;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;
import org.geotoolkit.referencing.operation.transform.ConcatenatedTransform;
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
     * too small, otherwise {@link #geodeticToPixelCoordinates} fails to correct for rounding
     * errors and we get a region to read bigger than necessary. Experience suggests that 1E-6
     * is too small, while 1E-5 seems okay.
     */
    private static final double EPS = 1E-5;

    /**
     * The locale to use for formatting messages, or {@code null} for a default locale.
     */
    Locale locale;

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
     * @throws CoverageStoreException If the region can not be computed.
     *
     * @since 3.14
     */
    protected MathTransform2D geodeticToPixelCoordinates(final GridGeometry2D gridGeometry,
            final GridCoverageStoreParam geodeticParam, final IIOParam pixelParam)
            throws CoverageStoreException
    {
        final MathTransform2D destToSourceGrid;
        final double[] resolution = geodeticParam.getResolution();
        try {
            destToSourceGrid = geodeticToPixelCoordinates(gridGeometry, geodeticParam.getValidEnvelope(),
                    resolution, geodeticParam.getCoordinateReferenceSystem(), pixelParam);
        } catch (Exception e) { // There is many different exceptions thrown by the above.
            throw new CoverageStoreException(formatErrorMessage(e), e);
        }
        if (destToSourceGrid == null) {
            throw new CoverageStoreException(formatErrorMessage(Errors.Keys.EMPTY_ENVELOPE));
        }
        return destToSourceGrid;
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
     * @return The transform from the requested grid to the actual grid, or {@code null}
     *         if the coverage can't be read because the region to read is empty.
     */
    private static MathTransform2D geodeticToPixelCoordinates(
            final GridGeometry2D            gridGeometry,
            final Envelope                  envelope,
            final double[]                  resolution,
            final CoordinateReferenceSystem requestCRS,
            final IIOParam                  imageParam)
            throws InvalidGridGeometryException, TransformException, FactoryException
    {
        final Rectangle       gridRange = gridGeometry.getGridRange2D();
        final MathTransform2D gridToCRS = gridGeometry.getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        final MathTransform2D crsToGrid = gridToCRS.inverse();
        /*
         * Get the full coverage envelope in the coverage CRS. The returned shape is likely
         * (but not garanteed) to be an instance of Rectangle2D. It can be freely modified.
         *
         * IMPLEMENTATION NOTE: It could have been more efficient to compute the transform
         * from the requested envelope to the source grid (by concatenation of the various
         * transformation steps), so we could have performed only one shape transformation.
         * However we want to use the CRS.transform(CoordinateOperation, ...) method rather
         * than the CRS.transform(MathTransform, ...) method, in order to handle the cases
         * where the requested region is over a geographic pole.
         */
        Shape shapeToRead = gridToCRS.createTransformedShape(gridRange); // Will be clipped later.
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
        Envelope envelopeInDataCRS = envelope;
        MathTransform requestToDataCRS = null;
        final CoordinateReferenceSystem dataCRS = gridGeometry.getCoordinateReferenceSystem2D();
        if (requestCRS != null && dataCRS != null && !CRS.equalsIgnoreMetadata(requestCRS, dataCRS)) {
            CoordinateOperation op = CRS.getCoordinateOperationFactory(true).createOperation(requestCRS, dataCRS);
            requestToDataCRS = op.getMathTransform();
            if (requestToDataCRS.isIdentity()) {
                requestToDataCRS = null;
            } else {
                envelopeInDataCRS = CRS.transform(op, envelope);
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
        if (crsToGrid instanceof AffineTransform) {
            shapeToRead = XAffineTransform.transform((AffineTransform) crsToGrid, shapeToRead,
                    shapeToRead != geodeticBounds); // boolean telling whatever we can overwrite.
        } else {
            shapeToRead = crsToGrid.createTransformedShape(shapeToRead);
        }
        final RectangularShape imageRegion = (shapeToRead instanceof RectangularShape) ?
                (RectangularShape) shapeToRead : shapeToRead.getBounds2D();
        // 'shapeToRead' and 'imageRegion' now contain coordinates in units of source grid.
        int width  = gridRange.width;
        int height = gridRange.height;
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
        /*
         * Compute the transform from target grid to source grid.  The target grid is the
         * one requested by the user, not the one that we would get if the parameters set
         * in the IIOParam object are honored.
         *
         * 1) First, compute the affine transform from the grid that the user asked
         *    for (computed from the envelope and the resolution) to the request CRS.
         * 2) Next, concatenate the above transform with the 'requestToDataCRS' and
         *    'crsToGrid' transforms computed above in this method.
         */
        final boolean flipX, flipY;
        final int requestDimension;
        if (requestCRS != null || dataCRS != null) {
            CoordinateSystem cs = (requestCRS != null ? requestCRS : dataCRS).getCoordinateSystem();
            flipX =  isOpposite(cs.getAxis(X_DIMENSION).getDirection());
            flipY = !isOpposite(cs.getAxis(Y_DIMENSION).getDirection());
            requestDimension = cs.getDimension();
        } else {
            flipX = false;
            flipY = true;
            requestDimension = crsToGrid.getSourceDimensions();
        }
        final Matrix m = MatrixFactory.create(requestDimension + 1, 3);
        m.setElement(requestDimension, 2, 1);
        /*
         * Set the translation terms for all known dimensions. In the case of axes having reversed
         * direction (typically the Y axis), we take the maximum rather than the minimum. The scale
         * factor will be reversed later.
         */
        if (envelope != null) {
            for (int i=0; i<requestDimension; i++) {
                final double t;
                if ((flipX && i == X_DIMENSION) || (flipY && i == Y_DIMENSION)) {
                    t = envelope.getMaximum(i);
                } else {
                    t = envelope.getMinimum(i);
                }
                m.setElement(i, 2, t);
            }
        } else {
            if (requestToDataCRS != null) {
                final CoordinateOperation op = CRS.getCoordinateOperationFactory(true)
                        .createOperation(dataCRS, CRSUtilities.getCRS2D(requestCRS));
                geodeticBounds = CRS.transform(op, geodeticBounds, geodeticBounds);
            }
            m.setElement(X_DIMENSION, 2, flipX ? geodeticBounds.getMaxX() : geodeticBounds.getMinX());
            m.setElement(Y_DIMENSION, 2, flipY ? geodeticBounds.getMaxY() : geodeticBounds.getMinY());
        }
        /*
         * Set the scale factors, which are the resolution. If the resolution was not specified,
         * we will compute the scale factor from the envelope assuming that the target image will
         * have the same number of pixels than the region read from the source image.
         */
        double sx, sy;
        if (resolution != null) {
            sx = resolution[X_DIMENSION];
            sy = resolution[Y_DIMENSION];
        } else {
            if (envelope != null) {
                sx = envelope.getSpan(X_DIMENSION);
                sy = envelope.getSpan(Y_DIMENSION);
            } else {
                sx = geodeticBounds.getWidth();
                sy = geodeticBounds.getHeight();
            }
            sx /= width; // No need to take subsampling in account, since it is 1 in this case.
            sy /= height;
        }
        if (flipX) sx = -sx;
        if (flipY) sy = -sy;
        m.setElement(X_DIMENSION, X_DIMENSION, sx);
        m.setElement(Y_DIMENSION, Y_DIMENSION, sy);
        /*
         * Concatenate the transforms. We get the transform from what the grid that the user
         * requested to the grid actually used in the source image, assuming the source grid
         * was read using the values we have set in the IIOParam object.
         */
        MathTransform destToSourceGrid = ProjectiveTransform.create(m);
        // At this point, targetToSourceGrid == (targetGrid to targetCRS).
        if (requestToDataCRS != null) {
            destToSourceGrid = ConcatenatedTransform.create(destToSourceGrid, requestToDataCRS);
        }
        // At this point, targetToSourceGrid == (targetGrid to sourceCRS).
        MathTransform crsToSubGrid = crsToGrid;
        if (xSubsampling != 1 || ySubsampling != 1 || xmin != 0 || ymin != 0) {
            sx = 1d / xSubsampling;
            sy = 1d / ySubsampling;
            crsToSubGrid = ConcatenatedTransform.create(crsToSubGrid,
                    new AffineTransform2D(sx, 0, 0, sy, -xmin / xSubsampling, -ymin / ySubsampling));
        }
        // At this point, targetToSourceGrid == (targetGrid to sourceGrid).
        destToSourceGrid = ConcatenatedTransform.create(destToSourceGrid, crsToSubGrid);
        /*
         * Debugging code, to be enabled for testing purpose only.
         */
        if (false) {
            Object print = destToSourceGrid;
            if (print instanceof LinearTransform) {
                print = ((LinearTransform) destToSourceGrid).getMatrix();
            }
            System.out.println(print);
        }
        return (MathTransform2D) destToSourceGrid;
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
     * returned by the {@link #geodeticToPixelCoordinates} method.
     */
    static boolean isIdentity(final MathTransform2D transform) {
        return (transform == null) || transform.isIdentity() ||
                (transform instanceof AffineTransform &&
                 XAffineTransform.isIdentity((AffineTransform) transform, 1E-10));
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
