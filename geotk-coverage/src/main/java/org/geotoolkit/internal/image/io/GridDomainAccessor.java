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
package org.geotoolkit.internal.image.io;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.imageio.metadata.IIOMetadata;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.MetadataNodeAccessor;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;
import static org.geotoolkit.internal.image.io.DimensionAccessor.fixRoundingError;
import org.geotoolkit.resources.Errors;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;


/**
 * A convenience specialization of {@link MetadataNodeAccessor} for nodes related to the
 * {@code "RectifiedGridDomain"} node. This class provides also convenience methods
 * for the {@code "RectifiedGridDomain/Limits"} and the {@code "SpatialRepresentation"}
 * nodes.
 * <p>
 * For most usage, the following methods should be invoked exactly once.
 * They will take care of invoking the appropriate setter methods.
 * <p>
 * <ul>
 *   <li>{@link #setSpatialRepresentation setSpatialRepresentation}</li>
 *   <li>{@link #setRectifiedGridDomain   setRectifiedGridDomain}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 *
 * @since 3.06
 * @module
 */
public final class GridDomainAccessor extends MetadataNodeAccessor {
    private static final int X_DIMENSION = 0, Y_DIMENSION = 1;

    /**
     * The name of the single attribute to declare for node that contains array.
     * This is used mostly for the following:
     *
     * {@preformat text
     *     RectifiedGridDomain  : RectifiedGrid
     *     └───OffsetVectors    : List<double[]>
     *         └───OffsetVector : double[]
     *             └───values
     * }
     */
    public static final String ARRAY_ATTRIBUTE_NAME = "values";

    /**
     * Threshold for floating point comparisons.
     */
    private static final double EPS = 1E-10;

    /**
     * The accessor for offset vectors. Will be created only when first needed.
     */
    private MetadataNodeAccessor offsetVectors;

    /**
     * Creates a new accessor for the given metadata.
     *
     * @param metadata The Image I/O metadata. An instance of the
     *        {@link org.geotoolkit.image.io.metadata.SpatialMetadata}
     *        sub-class is recommended, but not mandatory.
     */
    public GridDomainAccessor(final IIOMetadata metadata) {
        super(metadata, GEOTK_FORMAT_NAME, "RectifiedGridDomain", null);
    }

    /**
     * Sets the limits, origin and offset vectors from the given grid geometry.
     * The <cite>grid to CRS</cite> transform needs to be linear in order to get
     * the offset vectors formatted.
     * <p>
     * The value of the {@code pixelInCell} argument has an impact on the value of the
     * {@code origin} attribute. If null, the {@link PixelInCell.CELL_CENTER} value is
     * normally assumed, but the behavior could be different if the user overridden the
     * {@link GridGeometry#getGridToCRS()} method. See
     * {@link DiscreteReferencingFactory#getAffineTransform(GridGeometry, PixelInCell)}
     * for more information.
     *
     * @param geometry      The grid geometry.
     * @param pixelInCell   The value to assign to the {@code "pointInPixel"} attribute, or {@code null}.
     * @param cellGeometry  The value to assign to the {@code "cellGeometry"} attribute, or {@code null}.
     */
    public void setGridGeometry(final GridGeometry geometry, final PixelInCell pixelInCell, final CellGeometry cellGeometry) {
        setGridGeometry(geometry, pixelInCell, cellGeometry, -1);
    }

    /**
     * @deprecated The {@code axisToReverse} argument needs to be removed.
     *
     * @param axisToReverse The axis to reverse (typically 1 for the <var>y</var> axis), or -1 if none.
     *
     * @since 3.15
     */
    @Deprecated
    public void setGridGeometry(final GridGeometry geometry, final PixelInCell pixelInCell,
            final CellGeometry cellGeometry, final int axisToReverse)
    {
        final GridExtent gridEnvelope = geometry.getExtent();
        if (gridEnvelope != null) {
            final int gridDimension = gridEnvelope.getDimension();
            final int[] lower = new int[gridDimension];
            final int[] upper = new int[gridDimension];
            for (int i=0; i<gridDimension; i++) {
                lower[i] = (int) gridEnvelope.getLow (i);
                upper[i] = (int) gridEnvelope.getHigh(i);
            }
            final MathTransform gridToCRS = geometry.getGridToCRS(PixelInCell.CELL_CENTER); // Really want pixel center.
            if (gridToCRS != null) {
                final int crsDimension = gridToCRS.getTargetDimensions();
                double[] center = new double[Math.max(crsDimension, gridDimension)];
                for (int i=0; i<gridDimension; i++) {
                    center[i] = 0.5 * (lower[i] + upper[i]);
                }
                try {
                    gridToCRS.transform(center, 0, center, 0, 1);
                    center = fixRoundingError(ArraysExt.resize(center, crsDimension));
                    for (int i=0; i<center.length; i++) {
                        if (Double.isNaN(center[i])) {
                            CoordinateReferenceSystem crs;
                            if (geometry instanceof GridGeometry) {
                                crs = ((GridGeometry) geometry).getCoordinateReferenceSystem();
                            } else if (geometry instanceof CoordinateReferenceSystem) {
                                crs = (CoordinateReferenceSystem) geometry;
                            } else {
                                continue;
                            }
                            CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(i);
                        }
                    }
                    setSpatialRepresentation(center, cellGeometry, PixelTranslation.getPixelOrientation(pixelInCell));
                } catch (TransformException e) {
                    // Should not happen. If it happen anyway, this is not a fatal error.
                    // The above metadata will be missing from the IIOMetadata object, but
                    // they were mostly for information purpose anyway.
                    Logging.unexpectedException(null, GridDomainAccessor.class, "setGridGeometry", e);
                }
            }
            setLimits(lower, upper);
        }
        /*
         * Now set the origin and offset vectors, which are inferred from the gridToCRS matrix.
         * This is possible only if the transform is affine.
         */
        final Matrix matrix = getAffineTransform(geometry, pixelInCell);
        if (matrix != null) {
            if (axisToReverse >= 0) {
                if (gridEnvelope == null) {
                    return; // Can't write a correct origin without this information.
                }
                long span = gridEnvelope.getSize(axisToReverse);
                if (pixelInCell == null || pixelInCell.equals(PixelInCell.CELL_CENTER)) {
                    span--;
                }
                reverseAxisDirection(matrix, axisToReverse, span);
            }
            final int gridDimension = matrix.getNumCol() - 1;
            final int crsDimension  = matrix.getNumRow() - 1;
            final double[] origin = new double[crsDimension];
            for (int j=0; j<crsDimension; j++) {
                origin[j] = matrix.getElement(j, gridDimension);
            }
            setOrigin(fixRoundingError(origin));

            clearOffsetVectors();
            final double[] vector = new double[gridDimension];
            for (int j=0; j<crsDimension; j++) {
                for (int i=0; i<gridDimension; i++) {
                    vector[i] = matrix.getElement(j, i);
                }
                addOffsetVector(fixRoundingError(vector));
            }
        }
    }

    /**
     * Sets the {@code "low"} and {@code "high"} attributes of the
     * {@code "RectifiedGridDomain/Limits"} node to the given values.
     *
     * @param low  The value to be assigned to the {@code "low"} attribute.
     * @param high The value to be assigned to the {@code "high"} attribute.
     */
    public void setLimits(final int[] low, final int[] high) {
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(this, "Limits", null);
        accessor.setAttribute("low",  low);
        accessor.setAttribute("high", high);
    }

    /**
     * Gets the {@code "low"} and {@code "high"} attributes of the
     * {@code "RectifiedGridDomain/Limits"} node.
     *
     * @return int[][] :
     *  int[0] is the lower bounds,
     *  int[1] is the upper bounds
     */
    public int[][] getLimits() {
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(this, "Limits", null);
        return new int[][]{
            accessor.getAttributeAsIntegers("low", false),
            accessor.getAttributeAsIntegers("high", false)
        };
    }

    /**
     * Sets the origin and offset vectors from the given affine transform.
     *
     * @param gridToCRS The affine transform to use for setting the origin and offset vectors.
     *
     * @since 3.19
     */
    public void setGridToCRS(final AffineTransform gridToCRS) {
        final double[] vector = new double[] {
            gridToCRS.getTranslateX(), // X_DIMENSION
            gridToCRS.getTranslateY()  // Y_DIMENSION
        };
        setOrigin(fixRoundingError(vector));
        clearOffsetVectors();
        vector[X_DIMENSION]=gridToCRS.getScaleX(); vector[Y_DIMENSION]=gridToCRS.getShearY(); addOffsetVector(fixRoundingError(vector));
        vector[X_DIMENSION]=gridToCRS.getShearX(); vector[Y_DIMENSION]=gridToCRS.getScaleY(); addOffsetVector(fixRoundingError(vector));
    }

    /**
     * Sets the {@code "origin"} attribute to the given value.
     *
     * @param values The value to be assigned to the {@code "origin"} attribute.
     */
    public void setOrigin(final double... values) {
        setAttribute("origin", values);
    }

    /**
     * Appends a new {@code "OffsetVector"} node with the {@code "values"}
     * attribute set to the given array.
     *
     * @param values The value to be assigned to the {@code "values"} attribute
     *        of a new {@code "OffsetVector"} node.
     */
    public void addOffsetVector(final double... values) {
        MetadataNodeAccessor accessor = offsetVectors;
        if (accessor == null) {
            offsetVectors = accessor = new MetadataNodeAccessor(this, "OffsetVectors", "OffsetVector");
        }
        accessor.selectChild(accessor.appendChild());
        accessor.setAttribute(ARRAY_ATTRIBUTE_NAME, values);
    }

    /**
     * Remove all children of {@code "OffsetVector"} node.
     */
    public void clearOffsetVectors() {
        MetadataNodeAccessor accessor = offsetVectors;
        if (accessor == null) {
            offsetVectors = accessor = new MetadataNodeAccessor(this, "OffsetVectors", "OffsetVector");
        }
        accessor.removeChildren();
    }

    /**
     * Sets the values of the {@code "SpatialRepresentation"} attributes.
     *
     * @param centrePoint  The value to assign to the {@code "centrePoint"}  attribute.
     * @param cellGeometry The value to assign to the {@code "cellGeometry"} attribute, or {@code null}.
     * @param pointInPixel The value to assign to the {@code "pointInPixel"} attribute, or {@code null}.
     */
    public void setSpatialRepresentation(final double[] centrePoint, final CellGeometry cellGeometry,
            final PixelOrientation pointInPixel)
    {
        final MetadataNodeAccessor accessor = new MetadataNodeAccessor(
                metadata, GEOTK_FORMAT_NAME, "SpatialRepresentation", null);
        accessor.setAttribute("numberOfDimensions", centrePoint.length);
        accessor.setAttribute("centrePoint",  centrePoint);
        accessor.setAttribute("pointInPixel", pointInPixel);
        accessor.setAttribute("cellGeometry", cellGeometry);
    }

    /**
     * Convenience methods which set every attributes handled by this accessor.
     * This method is the most generic one for the two-dimensional case.
     *
     * @param gridToCRS    The conversion from grid to CRS.
     * @param bounds       The image bounds.
     * @param cellGeometry The value to assign to the {@code "cellGeometry"} attribute.
     * @param pointInPixel The value to assign to the {@code "pointInPixel"} attribute.
     */
    public void setAll(final AffineTransform gridToCRS, final Rectangle bounds,
            final CellGeometry cellGeometry, final PixelOrientation pointInPixel)
    {
        setGridToCRS(gridToCRS);
        setLimits(new int[] {
                      bounds.x,  // X_POSITION
                      bounds.y}, // Y_POSITION
                  new int[] {
                      bounds.x + bounds.width  - 1,   // X_POSITION
                      bounds.y + bounds.height - 1}); // Y_POSITION
        final double[] centrePoint = new double[] {
                bounds.getCenterX(),  // X_POSITION
                bounds.getCenterY()}; // Y_POSITION
        gridToCRS.transform(centrePoint, 0, centrePoint, 0, 1);
        /*
         * Get an estimation of the envelope size (the diagonal length actually),
         * in order to estimate a threshold value for trapping zeros.
         */
        Point2D span = new Point2D.Double(
                bounds.getWidth(),    // X_POSITION
                bounds.getHeight());  // Y_POSITION
        span = gridToCRS.deltaTransform(span, span);
        final double tolerance = Math.hypot(span.getX(), span.getY()) * EPS;
        for (int i=0; i<centrePoint.length; i++) {
            centrePoint[i] = adjustForRoundingError(centrePoint[i], tolerance);
        }
        setSpatialRepresentation(centrePoint, cellGeometry, pointInPixel);
    }

    //
    // Methods below this point are convenience specializations for the case
    // where only the bounds are specified, instead than the affine transform.
    //

    /**
     * Checks the length of the given array against the expected value.
     * In case of mismatch, an {@link IllegalArgumentException} is thrown.
     */
    private static void checkDimension(final String name, final int length, final int expected) {
        if (length != expected) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.MismatchedDimension_3, name, length, expected));
        }
    }

    /**
     * Sets the values of the {@code "SpatialRepresentation"} attributes. This method computes
     * the {@code "centrePoint"} attribute from the given {@code "origin"} and {@code "bounds"}
     * because this method is typically invoked together with the {@link #setRectifiedGridDomain}
     * method.
     *
     * @param origin       The {@code origin} argument given to the {@code setRectifiedGridDomain} method.
     * @param bounds       The {@code bounds} argument given to the {@code setRectifiedGridDomain} method.
     * @param cellGeometry The value to assign to the {@code "cellGeometry"} attribute.
     * @param pointInPixel The value to assign to the {@code "pointInPixel"} attribute.
     */
    public void setSpatialRepresentation(final double[] origin, final double[] bounds,
            final CellGeometry cellGeometry, final PixelOrientation pointInPixel)
    {
        final int crsDim = origin.length;
        checkDimension("bounds", bounds.length, crsDim);
        final double[] centrePoint = new double[crsDim];
        for (int i=0; i<crsDim; i++) {
            final double tolerance = EPS * (bounds[i] - origin[i]);
            centrePoint[i] = adjustForRoundingError(0.5 * (origin[i] + bounds[i]), tolerance);
        }
        setSpatialRepresentation(centrePoint, cellGeometry, pointInPixel);
    }

    /**
     * Sets the values of the {@code "RectifiedGridDomain"} attributes of offset vectors.
     * This convenience method invokes the following methods with values computes from
     * the arguments:
     * <p>
     * <ul>
     *   <li>{@link #setLimits}</li>
     *   <li>{@link #setOrigin}</li>
     *   <li>{@link #addOffsetVector}</li>
     * </ul>
     *
     * {@section Grid and CRS dimensions}
     * The dimension of the grid (named {@code gridDim} below) is typically equals to the dimension
     * of the CRS (named {@code crsDim} below). But in some cases the CRS dimension may be greater
     * than the grid dimension (the converse is not allowed however).
     * See {@link org.opengis.coverage.grid.RectifiedGrid} for more information.
     *
     * @param origin
     *          The coordinate of the pixel at the {@code low} index.
     *          The length of this array shall be equals to the {@code crsDim}.
     * @param bounds
     *          The coordinate of the pixel at the {@code high} index. The length of this array
     *          shall be equals to the {@code crsDim}. The ordinate values are often greater than
     *          {@code origin}, but not always. For example if the direction of the <var>y</var>
     *          axis in the CRS space has the opposite direction than the corresponding axis in
     *          the pixel space, then the {@code bounds} value is smaller than {@code origin}.
     * @param low
     *          The smaller pixel index, or {@code null} for an array filled with zeros.
     *          If non-null, the array length shall be equals to {@code gridDim}.
     * @param high
     *          The largest pixel index, <strong>inclusive</strong>.
     *          The array length shall be equals to {@code gridDim}.
     * @param gridToCrsDim
     *          If non-null, an array of length {@code gridDim} where, for the grid dimension
     *          {@code i}, the CRS dimension to use is {@code gridToCrsDim[i]}. For example if
     *          the first grid dimension is for the <var>y</var> axis in the CRS space and the
     *          second grid dimension is for the <var>x</var> axis in the CRS space (in other
     *          words if the axes shall be swapped), then this array shall be {@code {1,0}}.
     *          A null value is equivalent to an {@code {0,1,2,3...}} array.
     * @param pixelCenter
     *          {@code true} if the {@code origin} and {@code bounds} coordinates map pixel center
     *          (with both coordinates inclusive), or {@code false} if they are a bounding box
     *          which contains the totality of the grid.
     */
    public void setRectifiedGridDomain(final double[] origin, final double[] bounds, int[] low,
            final int[] high, final int[] gridToCrsDim, final boolean pixelCenter)
    {
        final int crsDim  = origin.length;
        final int gridDim = high.length;
        if (low == null) {
            low = new int[gridDim];
        }
        checkDimension("low",    low.length,   gridDim);
        checkDimension("bounds", bounds.length, crsDim);
        if (crsDim < gridDim) {
            checkDimension("origin", crsDim, gridDim);
        }
        if (gridToCrsDim != null) {
            checkDimension("gridToCrsDim", gridToCrsDim.length, gridDim);
        }
        setOrigin(origin);
        clearOffsetVectors();
        final double[] vector = new double[crsDim];
        for (int i=0; i<gridDim; i++) {
            final int j = (gridToCrsDim != null) ? gridToCrsDim[i] : i;
            int span = high[i] - low[i];
            if (!pixelCenter) {
                span++;
            }
            vector[j] = adjustForRoundingError((bounds[i] - origin[i]) / span, 0);
            addOffsetVector(vector);
            vector[j] = 0;
        }
        setLimits(low, high);
    }

    /**
     * Convenience method invoking {@link #setSpatialRepresentation setSpatialRepresentation} and
     * {@link #setRectifiedGridDomain setRectifiedGridDomain} for a two-dimensional bounding box.
     * <p>
     * Note that the value of the {@code yBound} parameter can be lower than the value of the
     * {@code yOrigin} parameter, in which case the scale factor for the <var>y</var> coordinates
     * will be negative.
     *
     * @param xOrigin The first  ordinate of the {@code origin} parameter.
     * @param yOrigin The second ordinate of the {@code origin} parameter.
     * @param xBound  The first  ordinate of the {@code bounds} parameter.
     * @param yBound  The second ordinate of the {@code bounds} parameter.
     * @param width   The number of pixels along the <var>x</var> axis.
     * @param height  The number of pixels along the <var>y</var> axis.
     * @param cellGeometry The value to assign to the {@code "cellGeometry"} attribute.
     * @param pixelCenter {@code true} if the {@code origin} and {@code bounds} coordinates map
     *          pixel center (with both coordinates inclusive), or {@code false} if they are a
     *          bounding box which contains the totality of the grid.
     */
    public void setAll(final double xOrigin, final double yOrigin, final double xBound,
            final double yBound, final int width, final int height, final boolean pixelCenter,
            final CellGeometry cellGeometry)
    {
        final double[] origin = new double[] {xOrigin, yOrigin};   // X_POSITION, Y_POSITION
        final double[] bounds = new double[] {xBound,  yBound};    // X_POSITION, Y_POSITION
        final int[]    high   = new int[]    {width-1, height-1};  // X_POSITION, Y_POSITION
        setRectifiedGridDomain(origin, bounds, null, high, null, pixelCenter);
        setSpatialRepresentation(origin, bounds, cellGeometry,
                pixelCenter ? PixelOrientation.CENTER : PixelOrientation.UPPER_LEFT);
    }

    /**
     * Work around for rounding error, to be invoked only for values resulting from a computation.
     */
    private static double adjustForRoundingError(double value, final double tolerance) {
        value = MetadataHelper.INSTANCE.adjustForRoundingError(value);
        if (Math.abs(value) <= tolerance) {
            value = 0;
        }
        return value;
    }

    private static Matrix getAffineTransform(final GridGeometry geometry, PixelInCell pixelInCell) {
        MathTransform tr;
        if (pixelInCell != null && geometry instanceof GridGeometry) {
            tr = ((GridGeometry) geometry).getGridToCRS(pixelInCell);
            pixelInCell = null; // Indicates that the offset is already applied.
        } else {
            tr = geometry.getGridToCRS(PixelInCell.CELL_CENTER);
        }
        Matrix gridToCRS = MathTransforms.getMatrix(tr);
        /*
         * If the caller asked for the pixel corner rather than pixel center,
         * applies a translation of 0.5 pixel along all dimensions.
         */
        if (gridToCRS != null && pixelInCell != null) {
            final double offset = PixelTranslation.getPixelTranslation(pixelInCell);
            if (offset != 0) {
                final int lastColumn = gridToCRS.getNumCol() - 1;
                for (int j=gridToCRS.getNumRow(); --j>=0;) {
                    double sum = 0;
                    for (int i=0; i<lastColumn; i++) {
                        sum += offset * gridToCRS.getElement(j, i);
                    }
                    sum += gridToCRS.getElement(j, lastColumn); // Do it last for reducing rounding errors.
                    gridToCRS.setElement(j, lastColumn, sum);
                }
            }
        }
        return gridToCRS;
    }

    /**
     * Modifies the given matrix in order to reverse the direction of the axis at the given
     * dimension. The matrix is assumed affine, but this is not verified.
     *
     * @param matrix    The matrix to modify.
     * @param dimension The dimension of the axis to reverse.
     * @param span      The envelope span at the dimension of the axis to be reversed,
     *                  in units of the source coordinate system.
     *
     * @deprecated No replacement, since experience has shown that this operation causes more problems
     *             than solutions.
     */
    @Deprecated
    private static void reverseAxisDirection(final Matrix matrix, final int dimension, final double span) {
        final int numRows = matrix.getNumRow();
        final int lastCol = matrix.getNumCol() - 1;
        for (int j=0; j<numRows; j++) {
            final double scale = matrix.getElement(j, dimension);
            if (scale != 0) {
                // The formula below still work with scale=0, but we don't want
                // to change the scale sign from positive zero to negative zero.
                matrix.setElement(j, dimension, -scale);
                matrix.setElement(j, lastCol, matrix.getElement(j, lastCol) + scale*span);
            }
        }
    }
}
