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
package org.geotoolkit.metadata.iso.spatial;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.io.Serializable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.metadata.spatial.PixelOrientation;
import static org.opengis.metadata.spatial.PixelOrientation.*;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.MathTransforms;


/**
 * The translation to apply for different values of {@link PixelOrientation}
 * or {@link PixelInCell}. The translation are returned by a call to one of
 * the following static method:
 * <p>
 * <ul>
 *   <li>{@link #getPixelTranslation(PixelOrientation)} for the two-dimensional case</li>
 *   <li>{@link #getPixelTranslation(PixelInCell)} for the <var>n</var>-dimensional case.</li>
 * </ul>
 * <p>
 * This class provides also a few {@code translate(...)} convenience methods, which apply the
 * pixel translation on a given {@link MathTransform} instance.
 *
 * {@section Example}
 * If the following code snippet, {@code gridToCRS} is an {@link java.awt.geom.AffineTransform}
 * from <cite>grid cell</cite> coordinates (typically pixel coordinates) to some arbitrary CRS
 * coordinates. In this example, the transform maps pixels {@linkplain PixelOrientation#CENTER
 * center}, while the {@linkplain PixelOrientation#UPPER_LEFT upper left} corner is desired.
 * This code will switch the affine transform from the <cite>pixel center</cite> to
 * <cite>upper left corner</cite> convention:
 *
 * {@preformat java
 *   final AffineTransform  gridToCRS = ...;
 *   final PixelOrientation current   = PixelOrientation.CENTER;
 *   final PixelOrientation expected  = PixelOrientation.UPPER_LEFT;
 *
 *   // Switch the transform from 'current' to 'expected' convention.
 *   final PixelTranslation source = getPixelTranslation(current);
 *   final PixelTranslation target = getPixelTranslation(expected);
 *   gridToCRS.translate(target.dx - source.dx,
 *                       target.dy - source.dy);
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @see org.geotoolkit.coverage.grid.GeneralGridGeometry#getGridToCRS(PixelInCell)
 * @see org.geotoolkit.coverage.grid.GridGeometry2D#getGridToCRS(PixelOrientation)
 *
 * @since 2.5
 * @module
 */
public final class PixelTranslation extends Static implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2616596940766158984L;

    /**
     * Math transforms created by {@link #translate(MathTransform,PixelInCell,PixelInCell)}.
     * Each element in this array will be created when first needed. Even index are translations
     * by -0.5 while odd index are translations by +0.5.
     */
    private static final MathTransform[] translations = new MathTransform[16];

    /**
     * The pixel orientation for this translation.
     */
    public final PixelOrientation orientation;

    /**
     * The translation among the <var>x</var> axis relative to pixel center.
     * The value is typically in the [-0.5 .. +0.5] range.
     */
    public final double dx;

    /**
     * The translation among the <var>y</var> axis relative to pixel center.
     * The value is typically in the [-0.5 .. +0.5] range.
     */
    public final double dy;

    /**
     * The offset for various pixel orientations. Keys must be upper-case names.
     */
    private static final Map<PixelOrientation, PixelTranslation> ORIENTATIONS = new HashMap<>(12);
    static {
        new PixelTranslation(CENTER,       0.0,  0.0);
        new PixelTranslation(UPPER_LEFT,  -0.5, -0.5);
        new PixelTranslation(UPPER_RIGHT,  0.5, -0.5);
        new PixelTranslation(LOWER_LEFT,  -0.5,  0.5);
        new PixelTranslation(LOWER_RIGHT,  0.5,  0.5);
        new PixelTranslation("LEFT",      -0.5,  0.0);
        new PixelTranslation("RIGHT",      0.5,  0.0);
        new PixelTranslation("UPPER",      0.0, -0.5);
        new PixelTranslation("LOWER",      0.0,  0.5);
    }

    /**
     * Creates a new pixel translation. The translation is added immediately in the
     * {@link #ORIENTATIONS} map (this behavior would need to be revisited if this
     * method was public).
     */
    private PixelTranslation(final PixelOrientation orientation, final double dx, final double dy) {
        this.orientation = orientation;
        this.dx = dx;
        this.dy = dy;
        if (ORIENTATIONS.put(orientation, this) != null) {
            throw new AssertionError(this);
        }
    }

    /**
     * Creates a new pixel translation. The translation is added immediately in the
     * {@link #ORIENTATIONS} map (this behavior would need to be revisited if this
     * method was public).
     */
    private PixelTranslation(final String orientation, final double dx, final double dy) {
        this(valueOf(orientation), dx, dy);
    }

    /**
     * Returns the pixel orientation for the given {@code PixelInCell} code.
     *
     * @param  anchor The {@code PixelInCell} code, or {@code null}.
     * @return The corresponding pixel orientation, or {@code null} if the argument was null.
     * @throws IllegalArgumentException if the given code is unknown.
     */
    public static PixelOrientation getPixelOrientation(final PixelInCell anchor)
            throws IllegalArgumentException
    {
        if (anchor == null) {
            return null;
        } else if (anchor.equals(PixelInCell.CELL_CENTER)) {
            return CENTER;
        } else if (anchor.equals(PixelInCell.CELL_CORNER)) {
            return UPPER_LEFT;
        } else {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "anchor", anchor));
        }
    }

    /**
     * Returns the position relative to the pixel center.
     * This method returns a value from the following table:
     * <p>
     * <table>
     *   <tr><th>Pixel in cell</th><th>offset</th></tr>
     *   <tr><td>{@link PixelInCell#CELL_CENTER  CELL_CENTER}</td><td> 0.0</td></tr>
     *   <tr><td>{@link PixelInCell#CELL_CORNER  CELL_CORNER}</td><td>-0.5</td></tr>
     * </table>
     * <p>
     * This method is typically used for <var>n</var>-dimensional grids,
     * where the number of dimension is unknown.
     *
     * @param anchor The "pixel in cell" value.
     * @return The translation for the given "pixel in cell" value.
     */
    public static double getPixelTranslation(final PixelInCell anchor) {
        if (PixelInCell.CELL_CENTER.equals(anchor)) {
            return 0;
        } else if (PixelInCell.CELL_CORNER.equals(anchor)) {
            return -0.5;
        } else {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "anchor", anchor));
        }
    }

    /**
     * Returns the specified position relative to the pixel center.
     * This method returns a value from the following table:
     * <p>
     * <table>
     *   <tr><th>Pixel orientation</th>                               <th>  x </th><th>  y </th></tr>
     *   <tr><td>{@link PixelOrientation#CENTER      CENTER}</td>     <td> 0.0</td><td> 0.0</td></tr>
     *   <tr><td>{@link PixelOrientation#UPPER_LEFT  UPPER_LEFT}</td> <td>-0.5</td><td>-0.5</td></tr>
     *   <tr><td>{@link PixelOrientation#UPPER_RIGHT UPPER_RIGHT}</td><td>+0.5</td><td>-0.5</td></tr>
     *   <tr><td>{@link PixelOrientation#LOWER_LEFT  LOWER_LEFT}</td> <td>-0.5</td><td>+0.5</td></tr>
     *   <tr><td>{@link PixelOrientation#LOWER_RIGHT LOWER_RIGHT}</td><td>+0.5</td><td>+0.5</td></tr>
     * </table>
     * <p>
     * This method can be used for grid restricted to 2 dimensions.
     *
     * @param  anchor The pixel orientation.
     * @return The position relative to the pixel center.
     * @throws IllegalArgumentException if the specified orientation is unknown.
     */
    public static PixelTranslation getPixelTranslation(final PixelOrientation anchor)
            throws IllegalArgumentException
    {
        final PixelTranslation offset = ORIENTATIONS.get(anchor);
        if (offset == null) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "anchor", anchor));
        }
        return offset;
    }

    /**
     * Returns the pixel orientation for the given offset, or {@code null} if none.
     * This is the reverse of {@link #getPixelTranslation(PixelOrientation)}.
     *
     * @param  dx The translation along <var>x</var> axis.
     * @param  dy The translation along <var>y</var> axis.
     * @return The pixel orientation of the given values, or {@code null} if none.
     */
    public static PixelOrientation getPixelOrientation(final double dx, final double dy) {
        for (final PixelTranslation candidate : ORIENTATIONS.values()) {
            if (candidate.dx == dx && candidate.dy == dy) {
                return candidate.orientation;
            }
        }
        return null;
    }

    /**
     * Translates the specified math transform according the specified pixel orientations.
     *
     * @param  gridToCRS  A math transform from <cite>pixel</cite> coordinates to any CRS.
     * @param  current    The pixel orientation of the given {@code gridToCRS} transform.
     * @param  expected   The pixel orientation of the desired transform.
     * @return The translation from {@code current} to {@code expected}.
     */
    public static MathTransform translate(final MathTransform gridToCRS,
                                          final PixelInCell current,
                                          final PixelInCell expected)
    {
        if (Objects.equals(current, expected)) {
            return gridToCRS;
        }
        if (gridToCRS == null) {
            return null;
        }
        final int dimension = gridToCRS.getSourceDimensions();
        final double offset = getPixelTranslation(expected) - getPixelTranslation(current);
        final int index;
        if (offset == -0.5) {
            index = 2*dimension;
        } else if (offset == 0.5) {
            index = 2*dimension + 1;
        } else {
            index = translations.length;
        }
        MathTransform mt;
        if (index >= translations.length) {
            mt = MathTransforms.linear(dimension, 1, offset);
        } else synchronized (translations) {
            mt = translations[index];
            if (mt == null) {
                mt = MathTransforms.linear(dimension, 1, offset);
                translations[index] = mt;
            }
        }
        return MathTransforms.concatenate(mt, gridToCRS);
    }

    /**
     * Translates the specified math transform according the specified pixel orientations.
     *
     * @param  gridToCRS  A math transform from <cite>pixel</cite> coordinates to any CRS.
     * @param  current    The pixel orientation of the given {@code gridToCRS} transform.
     * @param  expected   The pixel orientation of the desired transform.
     * @param  xDimension The dimension of <var>x</var> coordinates (pixel columns). Often 0.
     * @param  yDimension The dimension of <var>y</var> coordinates (pixel rows). Often 1.
     * @return The translation from {@code current} to {@code expected}.
     */
    public static MathTransform translate(final MathTransform gridToCRS,
                                          final PixelOrientation current,
                                          final PixelOrientation expected,
                                          final int xDimension,
                                          final int yDimension)
    {
        if (Objects.equals(current, expected)) {
            return gridToCRS;
        }
        if (gridToCRS == null) {
            return null;
        }
        final int dimension = gridToCRS.getSourceDimensions();
        if (xDimension < 0 || xDimension >= dimension) {
            throw illegalDimension("xDimension", xDimension);
        }
        if (yDimension < 0 || yDimension >= dimension) {
            throw illegalDimension("yDimension", yDimension);
        }
        if (xDimension == yDimension) {
            throw illegalDimension("xDimension", "yDimension");
        }
        final PixelTranslation source = getPixelTranslation(current);
        final PixelTranslation target = getPixelTranslation(expected);
        final double dx = target.dx - source.dx;
        final double dy = target.dy - source.dy;
        MathTransform mt;
        if (dimension == 2 && (xDimension | yDimension) == 1 && dx == dy && Math.abs(dx) == 0.5) {
            final int index = (dx >= 0) ? 5 : 4;
            synchronized (translations) {
                mt = translations[index];
                if (mt == null) {
                    mt = MathTransforms.linear(dimension, 1, dx);
                    translations[index] = mt;
                }
            }
        } else {
            final Matrix matrix = Matrices.create(dimension + 1);
            matrix.setElement(xDimension, dimension, dx);
            matrix.setElement(yDimension, dimension, dy);
            mt = MathTransforms.linear(matrix);
        }
        return MathTransforms.concatenate(mt, gridToCRS);
    }

    /**
     * Formats an exception for an illegal dimension.
     */
    private static IllegalArgumentException illegalDimension(final String name, final Object dimension) {
        return new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, name, dimension));
    }

    /**
     * Returns a string representation of this pixel translation.
     */
    @Override
    public String toString() {
        return String.valueOf(orientation) + '[' + dx + ", " + dy + ']';
    }
}
