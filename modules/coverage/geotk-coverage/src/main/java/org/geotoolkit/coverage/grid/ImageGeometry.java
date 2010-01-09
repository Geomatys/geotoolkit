/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.coverage.grid;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;


/**
 * A simple grid geometry holding the grid envelope as a {@linkplain Rectangle rectangle} and the
 * <cite>grid to CRS</cite> relationship as an {@linkplain AffineTransform affine transform}.
 * This grid geometry does not hold any Coordinate Reference System information. Because of that,
 * it is not suitable to {@link GridCoverage2D} (the later rather use {@link GridGeometry2D}).
 * But it is sometime used with plain {@linkplain java.awt.image.RenderedImage rendered image}
 * instances.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @see GridGeometry2D
 * @see GeneralGridGeometry
 *
 * @since 2.5
 * @module
 */
@Immutable
public class ImageGeometry implements GridGeometry, Serializable, Cloneable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1985363181119389264L;

    /**
     * The grid envelope. This is called "grid range" for legacy raisons.
     */
    private final GridEnvelope2D gridRange;

    /**
     * The <cite>grid to CRS</cite> affine transform.
     */
    private final AffineTransform2D gridToCRS;

    /**
     * Creates a grid geometry from the specified bounds and <cite>grid to CRS</cite>
     * affine transform.
     *
     * @param bounds The image bounds in pixel coordinates.
     * @param gridToCRS The affine transform from pixel coordinates to "real world" coordinates.
     */
    public ImageGeometry(final Rectangle bounds, AffineTransform gridToCRS) {
        this.gridRange = new GridEnvelope2D(bounds);
        if (gridToCRS.getClass().equals(AffineTransform2D.class)) {
            // Cast only if this is exactly the AffineTransform2D class,
            // not a subclass (otherwise it could be mutable).
            this.gridToCRS = (AffineTransform2D) gridToCRS;
        } else {
            this.gridToCRS = new AffineTransform2D(gridToCRS);
        }
    }

    /**
     * Returns the image bounds in pixel coordinates.
     */
    @Override
    public GridEnvelope2D getGridRange() {
        return gridRange.clone();
    }

    /**
     * Returns the conversion from grid coordinates to real world earth coordinates.
     */
    @Override
    public AffineTransform2D getGridToCRS() {
        return gridToCRS; // No need to clone since AffineTransform2D is immutable.
    }

    /**
     * Returns the image envelope in "real world" coordinates. This is the {@linkplain #getGridRange
     * grid envelope} transformed using the {@link #getGridToCRS grid to CRS} transform, assuming
     * that the transform maps {@linkplain PixelOrientation#CENTER pixel center}.
     *
     * @return The image envelope in "real world" coordinates.
     *
     * @since 3.00
     */
    public Rectangle2D getEnvelope() {
        return getEnvelope(PixelOrientation.CENTER);
    }

    /**
     * Returns the georeferenced image envelope in "real world" coordinates. This is the
     * {@linkplain #getGridRange grid envelope} transformed using the {@link #getGridToCRS
     * grid to CRS} transform. The transform may maps pixel center or a corner, depending
     * on the value of the {@code orientation} argument.
     * <p>
     * According OGC specification, the transform shall maps pixel center. However Java2D usage
     * is to maps the upper-left corner. Because this {@code ImageGeometry} class is primarily
     * designed for use with {@linkplain java.awt.image.RenderedImage rendered images}, this
     * method allows to override the OGC behavior with the Java2D one if needed.
     *
     * @param  orientation Whatever the transform maps pixel center or a corner. If this argument
     *         is not provided, the default value is {@link PixelOrientation#CENTER CENTER}.
     * @return The image envelope in "real world" coordinates.
     *
     * @since 3.00
     */
    public Rectangle2D getEnvelope(final PixelOrientation orientation) {
        // Reminder: this algorithm must be consistent with GeneralEnvelope(GridEnvelope, ...).
        final PixelTranslation pt = PixelTranslation.getPixelTranslation(orientation);
        final Rectangle gr = gridRange;
        final Rectangle2D.Double envelope = new Rectangle2D.Double(
                gr.x - (pt.dx + 0.5), gr.y - (pt.dy + 0.5), gr.width, gr.height);
        return XAffineTransform.transform(gridToCRS, envelope, envelope);
    }

    /**
     * Returns a string representation of this grid geometry. The returned string
     * is implementation dependent. It is usually provided for debugging purposes.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + gridRange + ", " + gridToCRS + ']';
    }

    /**
     * Returns a hash code value for this grid geometry.
     */
    @Override
    public int hashCode() {
        return gridRange.hashCode() ^ gridToCRS.hashCode();
    }

    /**
     * Compares this grid geometry with the specified one for equality.
     *
     * @param object The object to compare with.
     * @return {@code true} if the given object is equal to this grid geometry.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass().equals(getClass())) {
            final ImageGeometry that = (ImageGeometry) object;
            return Utilities.equals(gridRange, that.gridRange) &&
                   Utilities.equals(gridToCRS, that.gridToCRS);
        }
        return false;
    }

    /**
     * Returns a clone of this image geometry.
     *
     * @return A clone of this grid geometry.
     */
    @Override
    public ImageGeometry clone() {
        try {
            return (ImageGeometry) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new AssertionError(exception); // Should never happen, since we are cloneable.
        }
    }
}
