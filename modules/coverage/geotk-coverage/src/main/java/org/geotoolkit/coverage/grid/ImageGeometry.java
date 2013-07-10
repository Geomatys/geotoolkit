/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.grid;

import java.util.Objects;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.metadata.spatial.PixelOrientation;

import org.apache.sis.util.Classes;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;


/**
 * A simple grid geometry holding the grid envelope as a {@linkplain Rectangle rectangle} and the
 * <cite>grid to CRS</cite> relationship as an {@linkplain AffineTransform affine transform}.
 * <p>
 * This class does not specify whatever the <cite>grid to CRS</cite> transform maps pixel
 * {@linkplain PixelOrientation#CENTER center} or {@linkplain PixelOrientation#UPPER_LEFT
 * upper left} corner. The OGC convention is to map pixel center, while the Java2D convention
 * is to map pixel corner. It is user responsibility to know the convention in use. For a more
 * powerful class providing support for arbitrary convention, see {@link GridGeometry2D}.
 * <p>
 * This grid geometry does not hold any
 * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System}
 * information. Consequently it is not suitable for usage with {@link GridCoverage2D}. This
 * {@code ImageGeometry} class is rather designed as a lightweight container for usage with
 * {@link RenderedImage} instances.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see GridGeometry2D
 * @see GeneralGridGeometry
 *
 * @since 2.5
 * @module
 */
@Immutable
public class ImageGeometry implements GridGeometry, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6578330391565232607L;

    /**
     * The extent of grid coordinates in the grid coverage.
     */
    private final GridEnvelope2D extent;

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
        this.extent = new GridEnvelope2D(bounds);
        if (gridToCRS.getClass() == AffineTransform2D.class) {
            // Cast only if this is exactly the AffineTransform2D class,
            // not a subclass (otherwise it could be mutable).
            this.gridToCRS = (AffineTransform2D) gridToCRS;
        } else {
            this.gridToCRS = new AffineTransform2D(gridToCRS);
        }
    }

    /**
     * Returns the image bounds in pixel coordinates.
     *
     * @since 3.20 (derived from 2.5)
     */
    @Override
    public GridEnvelope2D getExtent() {
        return getGridRange();
    }

    /**
     * @deprecated Renamed {@link #getExtent()}.
     */
    @Override
    @Deprecated
    public GridEnvelope2D getGridRange() {
        return extent.clone();
    }

    /**
     * Returns the conversion from grid coordinates to real world earth coordinates.
     */
    @Override
    public AffineTransform2D getGridToCRS() {
        return gridToCRS; // No need to clone since AffineTransform2D is immutable.
    }

    /**
     * Returns the georeferenced image envelope in "real world" coordinates. This is the
     * {@linkplain #getExtent grid extent} transformed using the {@link #getGridToCRS
     * grid to CRS} transform. The transform may maps pixel center or a corner, depending
     * on the value of the {@code orientation} argument.
     * <p>
     * According OGC specification, the transform shall maps pixel center. However Java2D usage
     * is to maps the upper-left corner. Because this {@code ImageGeometry} class is primarily
     * designed for use with {@linkplain RenderedImage rendered images}, this method allows to
     * override the OGC behavior with the Java2D one if needed.
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
        final Rectangle gr = extent;
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
        return Classes.getShortClassName(this) + '[' + extent + ", " + gridToCRS + ']';
    }

    /**
     * Returns a hash code value for this grid geometry.
     */
    @Override
    public int hashCode() {
        return extent.hashCode() ^ gridToCRS.hashCode();
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
        if (object != null && object.getClass() == getClass()) {
            final ImageGeometry that = (ImageGeometry) object;
            return Objects.equals(extent,    that.extent) &&
                   Objects.equals(gridToCRS, that.gridToCRS);
        }
        return false;
    }
}
