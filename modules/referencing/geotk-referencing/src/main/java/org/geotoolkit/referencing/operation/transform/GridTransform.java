/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import java.util.Arrays;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferDouble;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.IterationStrategy;

import static org.geotoolkit.util.Utilities.hash;
import static org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive;


/**
 * Transforms a set of coordinate points using bilinear interpolation in a grid.
 *
 *
 * {@section Input and output coordinates}
 *
 * First, "<cite>real world</cite>" input coordinates (<var>x</var>, <var>y</var>) are optionally
 * converted to <cite>grid</cite> coordinates (<var>xi</var>, <var>yi</var>), which are zero-based
 * index in the two-dimensional grid. This conversion is applied only if a "real world" envelope
 * was given to the constructor, otherwise the input coordinates are assumed to be directly grid
 * coordinates.
 *
 * {@note This <cite>real world</cite> to <cite>grid</cite> transform is affine, but is still
 *        performed internally by this class in a simplified form (no rotation or axis swapping)
 *        rather than delegated to an <code>AffineTransform</code> instance because this class
 *        needs the original coordinates if the grid values are offset to be added, as in NADCON
 *        grids.}
 *
 * Output coordinates are the values stored in the grid at the specified grid coordinate. If the
 * grid ordinates are non-integer values, then output coordinates are interpolated using a bilinear
 * interpolation. If the grid ordinates are outside the grid domain ([0 &hellip; width-2] &times;
 * [0 &hellip; height-2] where {@linkplain #width} and {@linkplain #height} are the number of
 * columns and rows in the grid), then output coordinates are extrapolated.
 * <p>
 * In the case of a {@link GridType#LOCALIZATION LOCALIZATION} grid, we are done. But in the case
 * of {@link GridType#OFFSET OFFSET}, {@link GridType#NADCON NADCON} or {@link GridType#NTv2 NTv2}
 * grids, the above coordinates are added to the input coordinates in order to get the final output
 * coordinates.
 *
 *
 * {@section Invertibility}
 *
 * By default {@code GridTransform}s are not invertible. However some subclasses like
 * {@link GridTransform2D} and the one created by
 * {@link org.geotoolkit.referencing.operation.builder.LocalizationGrid}
 * provide conditional support for inverse transforms.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simon Reynard (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
@Immutable
public class GridTransform extends AbstractMathTransform implements Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7973466015425546562L;

    /**
     * Number of source dimensions.
     */
    private static final int srcDim = 2;

    /**
     * Number of columns in the grid.
     */
    protected final int width;

    /**
     * Number of rows in the grid.
     */
    protected final int height;

    /**
     * The "real world" coordinate which correspond to the (0,0) grid coordinate.
     */
    private final double xOrigin, yOrigin;

    /**
     * The scale factor by which to multiply the "real world" coordinate in order
     * to get the grid coordinate.
     */
    private final double scaleX, scaleY;

    /**
     * Whatever the grid values are directly the target coordinates or offsets
     * to apply on source coordinates.
     */
    final GridType type;

    /**
     * The grid of values. The {@linkplain DataBuffer#getSize size} is equal to the product
     * of the {@linkplain #width} and {@linkplain #height}. The number of banks determines the
     * {@linkplain #getTargetDimensions target dimension}, which is typically 2. The buffer type
     * is typically {@link DataBufferFloat} or {@link DataBufferDouble}, but is not restricted to.
     * <p>
     * When a {@code transform} method is invoked, a target value is interpolated for
     * every banks in the data buffer. Values in each data buffer bank are stored in
     * <a href="http://en.wikipedia.org/wiki/Row-major_order">row-major order</a>.
     */
    protected final transient DataBuffer grid;

    /**
     * Constructs a {@linkplain GridType#LOCALIZATION localization} grid using the specified data.
     * This convenience method creates the intermediate {@link DataBufferFloat} object from the
     * supplied data assuming that the first valid values is located at index 0. If this is not
     * the case, user can create a {@code DataBufferFloat} object directly in order to gain more
     * control.
     *
     * @param  width  Number of columns in the grid.
     * @param  height Number of rows in the grid.
     * @param  data   Data stored in <a href="http://en.wikipedia.org/wiki/Row-major_order">row-major
     *                order</a>, one array for each target dimension.
     * @return The math transform backed by the given grid.
     */
    public static GridTransform create(final int width, final int height, final float[]... data) {
        return create(GridType.LOCALIZATION, new DataBufferFloat(data, width*height),
                      new Dimension(width, height), null);
    }

    /**
     * Constructs a {@linkplain GridType#LOCALIZATION localization} grid using the specified data.
     * This convenience method creates the intermediate {@link DataBufferDouble} object from the
     * supplied data assuming that the first valid values is located at index 0. If this is not
     * the case, user can create a {@code DataBufferDouble} object directly in order to gain more
     * control.
     *
     * @param  width  Number of columns in the grid.
     * @param  height Number of rows in the grid.
     * @param  data   Data stored in <a href="http://en.wikipedia.org/wiki/Row-major_order">row-major
     *                order</a>, one array for each target dimension.
     * @return The math transform backed by the given grid.
     */
    public static GridTransform create(final int width, final int height, final double[]... data) {
        return create(GridType.LOCALIZATION, new DataBufferDouble(data, width*height),
                      new Dimension(width, height), null);
    }

    /**
     * Constructs a grid using the specified data.
     *
     * @param type   Whatever the grid values are directly the target coordinates or offsets
     *               to apply on source coordinates.
     * @param grid   The grid of values. It must complies with the conditions documented in the
     *               {@link #grid} field.
     * @param size   Number of columns ({@linkplain Dimension#width width}) and rows
     *               ({@linkplain Dimension#height height}) in the grid.
     * @param area   Grid envelope in "real world" coordinates, or {@code null} if none. The
     *               minimal (<var>x</var>,<var>y</var>) coordinate will maps the (0,0) grid
     *               coordinate, and the maximal (<var>x</var>,<var>y</var>) coordinate will
     *               maps the ({@code size.width}, {@code size.height}) grid coordinate.
     * @return The math transform backed by the given grid.
     */
    public static GridTransform create(final GridType type,  final DataBuffer grid,
                                       final Dimension size, final Rectangle2D area)
    {
        if (grid.getNumBanks() == 2) {
            return new GridTransform2D(type, grid, size, area);
        }
        return new GridTransform(type, grid, size, area);
    }

    /**
     * Constructs a grid using the specified data.
     *
     * @param type   Whatever the grid values are directly the target coordinates or offsets
     *               to apply on source coordinates.
     * @param grid   The grid of values. It must complies with the conditions documented in the
     *               {@link #grid} field.
     * @param size   Number of columns ({@linkplain Dimension#width width}) and rows
     *               ({@linkplain Dimension#height height}) in the grid.
     * @param area   Grid envelope in "real world" coordinates, or {@code null} if none. The
     *               minimal (<var>x</var>,<var>y</var>) coordinate will maps the (0,0) grid
     *               coordinate, and the maximal (<var>x</var>,<var>y</var>) coordinate will
     *               maps the ({@linkplain #width}, {@linkplain #height}) grid coordinate.
     */
    protected GridTransform(final GridType type,  final DataBuffer grid,
                            final Dimension size, final Rectangle2D area)
    {
        this.grid   = grid;
        this.type   = type;
        this.width  = size.width;
        this.height = size.height;
        ensureStrictlyPositive("width",  width);
        ensureStrictlyPositive("height", height);
        if (grid.getSize() != width*height) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
        }
        if (area != null) {
            final double longitudeSign = (type == GridType.NTv2) ? -1 : +1;
            xOrigin = area.getMinX() * longitudeSign;
            yOrigin = area.getMinY();
            scaleX  = width  / area.getWidth() * longitudeSign;
            scaleY  = height / area.getHeight();
        } else {
            xOrigin = 0;
            yOrigin = 0;
            scaleX  = 1;
            scaleY  = 1;
        }
    }

    /**
     * Returns the dimension of input points, which is 2.
     */
    @Override
    public final int getSourceDimensions() {
        return srcDim;
    }

    /**
     * Returns the dimension of output points. This is the number of banks in
     * the underlying {@linkplain #grid}.
     */
    @Override
    public final int getTargetDimensions() {
        return grid.getNumBanks();
    }

    /**
     * Returns {@code false} since this transform is not the identity one.
     */
    @Override
    public boolean isIdentity() {
        return false;
    }

    /**
     * Transforms a source coordinate into target coordinate. The transformation will
     * involve bilinear interpolations if the source ordinates are not integer values.
     * This method can also estimate the derivative at the location of the transformed
     * points.
     *
     * @param  srcPts  The source coordinate.
     * @param  srcOff  Index of the first valid ordinate in the {@code srcPts} array.
     * @param  dstPts  Where to store the target coordinate, or {@code null}.
     * @param  dstOff  Index where to store the first ordinate.
     * @param derivate {@code true} for computing the derivative, or {@code false} if not needed.
     * @return The matrix of the transform derivative at the given source position, or {@code null}
     *         if the {@code derivate} argument is {@code false}.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts,       int dstOff,
                            final boolean derivate)
    {
        /*
         * Following code is a copy-and-paste of:
         *
         *    transform(null, srcPts, srcOff, null, dstPts, dstOff, 1);
         *
         * with the loop over many points and the management of overlapping array removeds,
         * since they are managed by the caller. This code is copied because this method is
         * invoked often during inverse transformations.
         *
         * In addition, derivative calculation has been in-lined.
         */
        final double x = srcPts[srcOff  ];
        final double y = srcPts[srcOff+1];
        final double xi = (x - xOrigin) * scaleX;
        final double yi = (y - yOrigin) * scaleY;
        final int col = Math.max(Math.min((int) xi, width  - 2), 0);
        final int row = Math.max(Math.min((int) yi, height - 2), 0);
        final int offset00 = col + row*width;
        final int offset01 = offset00 + width;
        final int offset10 = offset00 + 1;
        final int offset11 = offset01 + 1;
        final double dx = xi - col;
        final double dy = yi - row;
        final int dstDim = grid.getNumBanks();
        final Matrix derivative = derivate ? Matrices.createDiagonal(dstDim, srcDim) : null;
        for (int j=0; j<dstDim; j++) {
            final double v00 = (grid.getElemDouble(j, offset00));
            final double v01 = (grid.getElemDouble(j, offset01));
            final double v10 = (grid.getElemDouble(j, offset10));
            final double v11 = (grid.getElemDouble(j, offset11));
            if (derivative != null) {
                double dxj = (v10 - v00); dxj += ((v11 - v01) - dxj) * dy;
                double dyj = (v01 - v00); dyj += ((v11 - v10) - dyj) * dx;
                switch (type) {
                    case NTv2:
                    case NADCON: {
                        dxj /= -3600;
                        dyj /= +3600;
                        break;
                    }
                }
                derivative.setElement(j, 0, dxj);
                derivative.setElement(j, 1, dyj);
            }
            if (dstPts != null) {
                final double v0  = (v10 - v00) * dx + v00;
                final double v1  = (v11 - v01) * dx + v01;
                double value = v0 + (v1-v0) * dy;
                switch (type) {
                    case OFFSET: {
                        switch (j) {
                            case 0: value += x; break;
                            case 1: value += y; break;
                        }
                        break;
                    }
                    case NTv2:
                    case NADCON: {
                        switch (j) {
                            case 0: value = x - value/3600; break;
                            case 1: value = y + value/3600; break;
                        }
                        break;
                    }
                }
                dstPts[dstOff++] = value;
            }
        }
        return derivative;
    }

    /**
     * Transforms a source coordinate into target coordinate. The transformation will
     * involve bilinear interpolations if the source ordinates are not integer values.
     *
     * @param  srcPts  The source coordinates.
     * @param  srcOff  Index of the first valid ordinate in the {@code srcPts} array.
     * @param  dstPts  Where to store the target coordinates.
     * @param  dstOff  Index where to store the first ordinate.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        transform(null, srcPts, srcOff, null, dstPts, dstOff, numPts);
    }

    /**
     * Transforms a source coordinate into target coordinate. The transformation will
     * involve bilinear interpolations if the source ordinates are not integer values.
     *
     * @param  srcPts  The source coordinates.
     * @param  srcOff  Index of the first valid ordinate in the {@code srcPts} array.
     * @param  dstPts  Where to store the target coordinates.
     * @param  dstOff  Index where to store the first ordinate.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        transform(srcPts, null, srcOff, dstPts, null, dstOff, numPts);
    }

    /**
     * Transforms a source coordinate into target coordinate. The transformation will
     * involve bilinear interpolations if the source ordinates are not integer values.
     *
     * @param  srcPts  The source coordinates.
     * @param  srcOff  Index of the first valid ordinate in the {@code srcPts} array.
     * @param  dstPts  Where to store the target coordinates.
     * @param  dstOff  Index where to store the first ordinate.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final float [] dstPts, int dstOff, int numPts)
    {
        transform(null, srcPts, srcOff, dstPts, null, dstOff, numPts);
    }

    /**
     * Transforms a source coordinate into target coordinate. The transformation will
     * involve bilinear interpolations if the source ordinates are not integer values.
     *
     * @param  srcPts  The source coordinates.
     * @param  srcOff  Index of the first valid ordinate in the {@code srcPts} array.
     * @param  dstPts  Where to store the target coordinates.
     * @param  dstOff  Index where to store the first ordinate.
     */
    @Override
    public void transform(final float [] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        transform(srcPts, null, srcOff, null, dstPts, dstOff, numPts);
    }

    /**
     * Implementation of direct transformation.
     */
    private void transform(float[] srcPts1, double[] srcPts2, int srcOff,
                           float[] dstPts1, double[] dstPts2, int dstOff, int numPts)
    {
        final int maxCol = width  - 2;
        final int maxRow = height - 2;
        final int dstDim = grid.getNumBanks();
        /*
         * Determine how to iterate over the coordinates in the special case where some values
         * overlap. For example we may iterate in reverse order (from higher index to lower index),
         * or we may compute the target coordinates in a temporary buffer to be copied later.
         */
        int offFinal = 0;
        Object dstFinal = null;
        boolean descending = false;
        if ((srcPts2 != null) ? srcPts2 == dstPts2 : srcPts1 == dstPts1) {
            switch (IterationStrategy.suggest(srcOff, srcDim, dstOff, dstDim, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts-1)*srcDim;
                    dstOff += (numPts-1)*dstDim;
                    descending = true;
                    break;
                }
                default: // Following is a reasonable default for any unknown cases.
                case BUFFER_SOURCE: {
                    final int upper = srcOff + numPts*srcDim;
                    if (srcPts1 != null) {
                        srcPts1 = Arrays.copyOfRange(srcPts1, srcOff, upper);
                    } else {
                        srcPts2 = Arrays.copyOfRange(srcPts2, srcOff, upper);
                    }
                    srcOff = 0;
                    break;
                }
                case BUFFER_TARGET: {
                    final int length = numPts * dstDim;
                    if (dstPts1 != null) {
                        dstFinal = dstPts1;
                        dstPts1 = new float[length];
                    } else {
                        dstFinal = dstPts2;
                        dstPts2 = new double[length];
                    }
                    offFinal = dstOff;
                    dstOff = 0;
                    break;
                }
            }
        }
        /*
         * Now process to the interpolations. Note that this code has similarities with the
         * implementation of derivative(DirectPosition) method. Same variable names are used
         * on purpose, for making comparisons easier.
         */
        while (--numPts >= 0) {
            final double x, y;
            if (srcPts2 != null) {
                x = srcPts2[srcOff++];
                y = srcPts2[srcOff++];
            } else {
                x = srcPts1[srcOff++];
                y = srcPts1[srcOff++];
            }
            final double xi = (x - xOrigin) * scaleX;
            final double yi = (y - yOrigin) * scaleY;
            final int col = Math.max(Math.min((int) xi, maxCol), 0);
            final int row = Math.max(Math.min((int) yi, maxRow), 0);
            final int offset00 = col + row*width;
            final int offset01 = offset00 + width; // Une ligne plus bas
            final int offset10 = offset00 + 1;     // Une colonne à droite
            final int offset11 = offset01 + 1;     // Une colonne à droite, une ligne plus bas
            final double dx = xi - col;
            final double dy = yi - row;
            for (int j=0; j<dstDim; j++) {
                final double v00 = (grid.getElemDouble(j, offset00));
                final double v01 = (grid.getElemDouble(j, offset01));
                final double v0  = (grid.getElemDouble(j, offset10) - v00) * dx + v00;
                final double v1  = (grid.getElemDouble(j, offset11) - v01) * dx + v01;
                double value = v0 + (v1-v0) * dy;
                switch (type) {
                    case OFFSET: {
                        switch (j) {
                            case 0: value += x; break;
                            case 1: value += y; break;
                        }
                        break;
                    }
                    case NTv2:
                    case NADCON: {
                        switch (j) {
                            case 0: value = x - value/3600; break;
                            case 1: value = y + value/3600; break;
                        }
                        break;
                    }
                }
                if (dstPts2 != null) {
                    dstPts2[dstOff++] = value;
                } else {
                    dstPts1[dstOff++] = (float) value;
                }
            }
            if (descending) {
                srcOff -= 2*srcDim;
                dstOff -= 2*dstDim;
            }
        }
        /*
         * At this point the interpolations are completed. However if we stored the values
         * in a temporary array, then we need to copy them to their final location now.
         */
        if (dstFinal != null) {
            final int length;
            final Object dstPts;
            if (dstPts1 != null) {
                dstPts = dstPts1;
                length = dstPts1.length;
            } else {
                dstPts = dstPts2;
                length = dstPts2.length;
            }
            System.arraycopy(dstPts, 0, dstFinal, offFinal, length);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(xOrigin, hash(yOrigin, hash(scaleX, hash(scaleY, hash(width, hash(height,
               hash(grid.getNumBanks(), super.computeHashCode())))))));
    }

    /**
     * Compares this transform with the specified object for equality. This method returns
     * {@code true} if the two objects are of the same class and have the same {@linkplain #width},
     * {@linkplain #height}, {@linkplain #getTargetDimensions target dimension} and the same values
     * in all valid grid cells. Note that the grids are not required to be of the same type
     * ({@link DataBuffer#TYPE_FLOAT TYPE_FLOAT}, {@link DataBuffer#TYPE_DOUBLE TYPE_DOUBLE},
     * <i>etc.</i>) if the values casted to the {@code double} type are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (super.equals(object, mode)) {
            final GridTransform that = (GridTransform) object;
            if (width == that.width && height == that.height &&
                Utilities.equals(xOrigin, that.xOrigin) &&
                Utilities.equals(yOrigin, that.yOrigin) &&
                Utilities.equals(scaleX,  that.scaleX)  &&
                Utilities.equals(scaleY,  that.scaleY))
            {
                final int dstDim = grid.getNumBanks();
                if (dstDim == that.grid.getNumBanks()) {
                    final int size = width * height;
                    for (int b=0; b<dstDim; b++) {
                        for (int i=0; i<size; i++) {
                            final double va = this.grid.getElemDouble(b,i);
                            final double vb = that.grid.getElemDouble(b,i);
                            if (!Utilities.equals(va, vb)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Serializes this grid. This method processes {@link #grid} in a special way because
     * the default JDK implementations are not serializable.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        final int size = grid.getSize();
        final int[] offsets = grid.getOffsets();
        final Class<? extends DataBuffer> type = grid.getClass();
        final Object data; // Assigned to DataBuffer.getBankData(), which returns a clone.
        try {
            data = type.getMethod("getBankData", (Class<?>[]) null).invoke(grid, (Object[]) null);
            for (int i=Array.getLength(data); --i>=0;) {
                Object bank = Array.get(data, i);
                final int offset = offsets[i];
                if (offset != 0 || Array.getLength(bank) != size) {
                    final Object original = bank;
                    bank = Array.newInstance(original.getClass().getComponentType(), size);
                    System.arraycopy(original, offset, bank, 0, size);
                    Array.set(data, i, bank);
                }
            }
        } catch (ReflectiveOperationException e) {
            NotSerializableException exception = new NotSerializableException(type.getCanonicalName());
            exception.initCause(e);
            throw exception;
        }
        out.writeObject(type);
        out.writeObject(data);
    }

    /**
     * Deserializes this grid. This method processes {@link #grid} in a special way because
     * the default JDK implementations are not serializable.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int    size = width * height;
        final Object type = in.readObject();
        final Object data = in.readObject();
        try {
            final Class<? extends DataBuffer> classe = ((Class<?>) type).asSubclass(DataBuffer.class);
            final DataBuffer buffer = classe.getConstructor(data.getClass(), Integer.TYPE).newInstance(data, size);
            final Field field = GridTransform.class.getDeclaredField("grid");
            field.setAccessible(true);
            field.set(this, buffer);
        } catch (ReflectiveOperationException e) {
            InvalidObjectException exception = new InvalidObjectException(
                    Errors.format(Errors.Keys.UNSUPPORTED_DATA_TYPE));
            exception.initCause(e);
            throw exception;
        }
    }
}
