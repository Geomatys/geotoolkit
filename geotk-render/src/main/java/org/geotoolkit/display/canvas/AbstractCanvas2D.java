/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2013, Geomatys
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

package org.geotoolkit.display.canvas;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import static java.lang.Math.abs;
import static java.lang.Math.rint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.internal.shared.AxisDirections;
import org.apache.sis.referencing.internal.shared.GeodeticObjectBuilder;
import org.apache.sis.referencing.internal.shared.AffineTransform2D;
import org.apache.sis.referencing.operation.provider.Affine;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultDerivedCRS;
import org.apache.sis.referencing.operation.DefaultConversion;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCanvas2D extends AbstractCanvas{
    /**
     * The operation method used by {@link #getDisplayCRS()}.
     * This is a temporary constant, as we will probably need to replace the creation
     * of a {@link DefaultDerivedCRS} by something else. After that replacement, this
     * constant will be removed.
     */
    private static final Affine DISPLAY_TO_OBJECTIVE_OPERATION = new Affine();

    public static final class AxisFinder implements Comparator<CoordinateSystemAxis>{

        private final CoordinateSystemAxis crs;

        public AxisFinder(CoordinateSystemAxis crs) {
            this.crs = crs;
        }

        @Override
        public int compare(CoordinateSystemAxis o1, CoordinateSystemAxis o2) {
            if(o1.getName().getCode().equals(crs.getName().getCode())){
                return 0;
            }
            return -1;
        }

    }

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas2D#getGridGeometry geometry} changed.
     */
    public static final String GRIDGEOMETRY_KEY = "GridGeometry";

    /**
     * A set of {@link MathTransform}s from various source CRS. The target CRS must be the
     * {@linkplain #getObjectiveCRS objective CRS} for all entries. Keys are source CRS.
     * This map is used only in order to avoid the costly call to
     * {@link CoordinateOperationFactory#createOperation} as much as possible. If a
     * transformation is not available in this collection, then the usual factory will be used.
     */
    private final transient Map<CoordinateReferenceSystem,MathTransform> transforms = new HashMap<>();

    private GridGeometry gridGeometry;
    private GridGeometry gridGeometry2d;
    private boolean proportion = true;
    private boolean autoRepaint = false;

    public AbstractCanvas2D() {
        this(new Hints());
    }

    public AbstractCanvas2D(Hints hints) {
        this(CommonCRS.WGS84.normalizedGeographic(),hints);
    }

    public AbstractCanvas2D(CoordinateReferenceSystem crs, Hints hints) {
        super(hints);
        ArgumentChecks.ensureNonNull("Objective CRS", crs);

        final int idx = getHorizontalIndex(crs);
        final int dim = crs.getCoordinateSystem().getDimension();
        final long[] low = new long[dim];
        final long[] high = new long[dim];
        high[idx] = 100;
        high[idx+1] = 100;
        final GridExtent extent = new GridExtent(null, low, high, true);
        final MatrixSIS matrix = Matrices.createDiagonal(dim+1, dim+1);
        final LinearTransform gridToCrs = MathTransforms.linear(matrix);
        gridGeometry = gridGeometry2d = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, crs);
    }

    /**
     * NOTE : this is an incomplete gridGeometry, do not use it yet.
     *
     * Get global N dimension grid geometry.
     * @return GridGeometry, never null
     */
    public final GridGeometry getGridGeometry() {
        return gridGeometry;
    }

    /**
     *
     * Set global N dimension grid geometry.
     * @param gridGeometry new grid geometry
     */
    public final void setGridGeometry(GridGeometry gridGeometry) throws FactoryException {
        ArgumentChecks.ensureNonNull("gridGeometry", gridGeometry);
        if (this.gridGeometry.equals(gridGeometry)) return;
        // Before updating and notifying users, we must ensure that user provided a 2D geometry
        gridGeometry2d = reduceTo2D(gridGeometry)
                .orElseThrow(() -> new IllegalArgumentException("Not enough information in given geometry to ensure it's 2D: "+gridGeometry));

        final GridGeometry old = this.gridGeometry;
        this.gridGeometry = gridGeometry;
        firePropertyChange(GRIDGEOMETRY_KEY, old, gridGeometry);

        repaintIfAuto();
    }

    /**
     * TODO: replace when SIS will provide a new slice operation on gridGeometries.
     *
     * @param source THe geometry to get a 2D view of.
     * @return An empty shell if given geometry does not provide enough information to deduce a 2D slice. Otherwise, a
     * 2D view of the source geometry.
     * @throws FactoryException If something goes wrong while analysing source geometry CRS or transform.
     */
    private static Optional<GridGeometry> reduceTo2D(final GridGeometry source) throws FactoryException {
        if (source.getDimension() == 2) {
            return Optional.of(source);
        } else if (source.isDefined(GridGeometry.EXTENT)) {
            final int[] space2d = source.getExtent().getSubspaceDimensions(2);
            return Optional.ofNullable(source.selectDimensions(space2d));
        } else if (source.isDefined(GridGeometry.CRS | GridGeometry.GRID_TO_CRS)) {
            final CoordinateReferenceSystem crs = source.getCoordinateReferenceSystem();
            final int east = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), AxisDirection.EAST);
            if (east < 0) return Optional.empty();

            final int north = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), AxisDirection.NORTH);
            int[] orderedAxes = {Math.min(east, north), Math.max(east, north)};

            final CoordinateReferenceSystem crs2d = CRS.selectDimensions(crs, orderedAxes);
            final MathTransform gridToCRS = source.getGridToCRS(PixelInCell.CELL_CENTER);
            final TransformSeparator sep = new TransformSeparator(gridToCRS);
            sep.addTargetDimensions(orderedAxes);
            final MathTransform gridToCRS2D = sep.separate();
            //we are expecting axis index to be preserved from grid to crs
            final GridExtent extent = source.getExtent().selectDimensions(sep.getSourceDimensions());

            return Optional.of(new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCRS2D, crs2d));
        }

        return Optional.empty();
    }

    /**
     * Get 2 dimension grid geometry.
     * This grid geometry only has the 2D CRS part of the global grid geometry.
     *
     * @return GridGeometry 2D, never null
     */
    public final GridGeometry getGridGeometry2D() {
        return gridGeometry2d;
    }

    public final CoordinateReferenceSystem getObjectiveCRS() {
        return gridGeometry.getCoordinateReferenceSystem();
    }

    public final void setObjectiveCRS(final CoordinateReferenceSystem crs) throws TransformException, FactoryException {
        ArgumentChecks.ensureNonNull("Objective CRS", crs);
        if (CRS.equivalent(gridGeometry.getCoordinateReferenceSystem(), crs)) {
            return;
        }

        //store the visible area to restore it later
        final GeneralEnvelope preserve = new GeneralEnvelope(gridGeometry.getEnvelope());

        final int newDim = crs.getCoordinateSystem().getDimension();
        final Envelope env = ReferencingUtilities.transform(preserve, crs);
        final int oldidx = getHorizontalIndex(gridGeometry.getCoordinateReferenceSystem());
        final int idx = getHorizontalIndex(crs);
        final GridExtent oldExtent = gridGeometry.getExtent();
        final long[] oldlow = oldExtent.getLow().getCoordinateValues();
        final long[] oldhigh = oldExtent.getHigh().getCoordinateValues();
        final long[] low = new long[newDim];
        final long[] high = new long[newDim];
        low[idx] = oldlow[oldidx];
        low[idx+1] = oldlow[oldidx+1];
        high[idx] = oldhigh[oldidx];
        high[idx+1] = oldhigh[oldidx+1];
        final GridExtent extent = new GridExtent(null, low, high, true);

        GridGeometry gridGeometry = new GridGeometry(extent, env, GridOrientation.HOMOTHETY);
        if (proportion) {
            gridGeometry = preserverRatio(gridGeometry);
        }
        setGridGeometry(gridGeometry);
    }

    /**
     * Rebuild grid geometry and configure horizontal axis scales to the same factor.
     *
     * @param gridGeometry original grid geometry
     * @return ratio preserved grid geometry
     */
    public static GridGeometry preserverRatio(GridGeometry gridGeometry) {
        final CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
        final GridExtent extent = gridGeometry.getExtent();
        final Envelope envelope = gridGeometry.getEnvelope();
        final int idx = getHorizontalIndex(crs);
        final long width = extent.getSize(idx);
        final long height = extent.getSize(idx+1);
        final double sx = envelope.getSpan(idx) / width;
        final double sy = envelope.getSpan(idx+1) / height;
        if (sx != sy) {
            final GeneralEnvelope env = new GeneralEnvelope(envelope);
            if (sx < sy) {
                double halfSpan = (sy * width / 2.0);
                double median = env.getMedian(idx);
                env.setRange(idx, median - halfSpan, median + halfSpan);
            } else {
                double halfSpan = (sx * height / 2.0);
                double median = env.getMedian(idx+1);
                env.setRange(idx+1, median - halfSpan, median + halfSpan);
            }
            gridGeometry = new GridGeometry(extent, env, GridOrientation.HOMOTHETY);
        }
        return gridGeometry;
    }

    public final CoordinateReferenceSystem getObjectiveCRS2D() {
        return getGridGeometry2D().getCoordinateReferenceSystem();
    }

    public final CoordinateReferenceSystem getDisplayCRS() {
        /*
         * TODO: will need a way to avoid the cast below. In my understanding, DerivedCRS may not be the appropriate
         *       CRS to create after all, because in ISO 19111 a DerivedCRS is more than just a base CRS with a math
         *       transform. A DerivedCRS may also "inherit" some characteritics of the base CRS. For example if the
         *       base CRS is a VerticalCRS, then the DerivedCRS may also implement VerticalCRS.
         *
         *       I'm not yet sure what should be the appropriate kind of CRS to create here. ImageCRS? EngineeringCRS?
         *       How to express the relationship to the base CRS is also not yet determined.
         */
        final SingleCRS objCRS2D = (SingleCRS) getObjectiveCRS2D();
        final Map<String,?> name = Collections.singletonMap(DefaultDerivedCRS.NAME_KEY, "Derived - "+objCRS2D.getName().toString());
        final CoordinateReferenceSystem displayCRS = DefaultDerivedCRS.create(name, objCRS2D,
                new DefaultConversion(name, DISPLAY_TO_OBJECTIVE_OPERATION, getObjectiveToDisplay(), null),
                objCRS2D.getCoordinateSystem());
        return displayCRS;
    }

    /**
     * @return a snapshot objective To display transform, in Pixel CENTER
     */
    public final AffineTransform2D getObjectiveToDisplay() {
        try {
            MathTransform gridToCRS = getGridGeometry2D().getGridToCRS(PixelInCell.CELL_CENTER);
            return (AffineTransform2D) gridToCRS.inverse();
        } catch (org.opengis.referencing.operation.NoninvertibleTransformException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    public final AffineTransform2D getDisplayToObjective() throws NoninvertibleTransformException {
        MathTransform gridToCRS = getGridGeometry2D().getGridToCRS(PixelInCell.CELL_CENTER);
        return (AffineTransform2D) gridToCRS;
    }

    public final Rectangle2D getDisplayBounds() {
        return toRectangle(getGridGeometry2D().getExtent());
    }

    public void setDisplayBounds(Rectangle2D bounds) {
        ArgumentChecks.ensureNonNull("Display bounds", bounds);

        final GridGeometry gridGeometry = getGridGeometry();
        final GridExtent extent = gridGeometry.getExtent();
        final int idx = getHorizontalIndex(gridGeometry.getCoordinateReferenceSystem());

        final long[] low = extent.getLow().getCoordinateValues();
        low[idx] = (long) bounds.getMinX();
        low[idx+1] = ((long) bounds.getMinY());
        final long[] high = extent.getHigh().getCoordinateValues();
        high[idx] = ((long) bounds.getMaxX()) - 1;
        high[idx+1] = ((long) bounds.getMaxY()) - 1;
        final GridExtent newExt = new GridExtent(null, low, high, true);

        final GridGeometry newGrid = new GridGeometry(newExt, PixelInCell.CELL_CENTER, gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER), gridGeometry.getCoordinateReferenceSystem());
        try {
            setGridGeometry(newGrid);
        } catch (FactoryException ex) {
            //we are just changing the size, this should not cause the exception
            //should not happen with current parameters
            throw new BackingStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Set the proportions support between X and Y axis.
     * if false then no correction will be applied
     * if true then one unit in X will be equal to one unit in Y
     */
    public final void setAxisProportions(final boolean prop) {
        this.proportion = prop;
    }

    /**
     *
     * @return the X/Y proportion
     */
    public final boolean getAxisProportions() {
        return proportion;
    }

    public final void setAutoRepaint(final boolean autoRepaint) {
        this.autoRepaint = autoRepaint;
    }

    public final boolean isAutoRepaint() {
        return autoRepaint;
    }

    /**
     * Changes the {@linkplain AffineTransform} by applying a concatenate affine transform.
     * The {@code change} transform
     * must express a change in logical units, for example, a translation in metres.
     *
     * @param  change The affine transform change, as an affine transform in logical coordinates. If
     *         {@code change} is the identity transform, then this method does nothing and
     *         listeners are not notified.
     */
    public final void applyTransform(AffineTransform change){
        if(change.isIdentity()) return;
        AffineTransform objToDisp = new AffineTransform(getObjectiveToDisplay());
        objToDisp.concatenate(change);
        roundIfAlmostInteger(objToDisp);

        setTransform(objToDisp);
    }

    /**
     * Change a range in the canvas envelope.
     * Can be used to temporal or elevation range of the map.
     */
    private void setRange(final int ordinate, final double min, final double max) throws FactoryException{
        GeneralEnvelope envelope = new GeneralEnvelope(gridGeometry.getEnvelope());
        if (envelope.getMinimum(ordinate) == min && envelope.getMaximum(ordinate) == max) {
            //same values
            return;
        }
        envelope.setRange(ordinate, min, max);

        final GridExtent extent = gridGeometry.getExtent();
        final GridGeometry newgrid = new GridGeometry(extent, envelope, GridOrientation.HOMOTHETY);
        setGridGeometry(newgrid);
    }

    /**
     * Returns true if any data has been painteed.
     * Background, decorations or legends are not considered datas.
     * Datas include features and coverages.
     *
     * @return true if a data has been painted
     */
    public final boolean repaint(){
        return repaint(getDisplayBounds());
    }

    /**
     * Returns true if any data has been painteed.
     * Background, decorations or legends are not considered datas.
     * Datas include features and coverages.
     *
     * @return true if a data has been painted
     */
    public abstract boolean repaint(Shape area);

    private void repaintIfAuto(){
        if (autoRepaint) {
            repaint();
        }
    }

    public abstract Image getSnapShot();

    ////////////////////////////////////////////////////////////////////////////
    // Next methods are convinient methods which always end up by calling applyTransform(trs)
    ////////////////////////////////////////////////////////////////////////////

    private void setTransform(final AffineTransform objToDisp) {
        final AffineTransform2D old = getObjectiveToDisplay();

        if (!old.equals(objToDisp)) {
            try {
                final GridGeometry gridGeometry = getGridGeometry();
                final GridExtent extent = gridGeometry.getExtent();
                final CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
                final int idx = getHorizontalIndex(crs);
                final MathTransform gridToCRS = gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER);

                final List<MathTransform> components = new ArrayList<>();
                if (idx > 0) {
                    final TransformSeparator sep = new TransformSeparator(gridToCRS);
                    sep.addSourceDimensionRange(0, idx);
                    sep.addTargetDimensionRange(0, idx);
                    components.add(sep.separate());
                }
                components.add(new AffineTransform2D(objToDisp).inverse());
                if (idx+2 < extent.getDimension()) {
                    final TransformSeparator sep = new TransformSeparator(gridToCRS);
                    sep.addSourceDimensionRange(idx+2, extent.getDimension());
                    sep.addTargetDimensionRange(idx+2, extent.getDimension());
                    components.add(sep.separate());
                }

                final MathTransform newGridToCrs = MathTransforms.compound(components.toArray(new MathTransform[components.size()]));
                final GridGeometry newGrid = new GridGeometry(extent, PixelInCell.CELL_CENTER, newGridToCrs, crs);
                setGridGeometry(newGrid);
            } catch (FactoryException | org.opengis.referencing.operation.NoninvertibleTransformException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Reinitializes the affine transform {@link #zoom} in order to cancel any zoom, rotation or
     * translation. The argument {@code yAxisUpward} indicates whether the <var>y</var> axis should
     * point upwards.  The value {@code false} lets it point downwards. This method is offered
     * for convenience sake for derived classes which want to redefine {@link #reset()}.
     *
     * @param yAxisUpward {@code true} if the <var>y</var> axis should point upwards rather than
     *        downwards.
     */
    private void resetTransform(final Rectangle2D preferredArea, final boolean yAxisUpward,
                         final boolean preserveRotation) throws NoninvertibleTransformException{

        final Rectangle canvasBounds = getDisplayBounds().getBounds();
        if (canvasBounds.isEmpty()) return;
        if (!isValid(preferredArea)) return;

        canvasBounds.x = 0;
        canvasBounds.y = 0;

        AffineTransform objToDisp = new AffineTransform();
        final double rotation = -AffineTransforms2D.getRotation(objToDisp);

        // Upward ??? Upward means goes up, but here we make it go down. I'm confused...
        if (yAxisUpward) {
            objToDisp.setToScale(+1, -1);
        } else {
            objToDisp.setToIdentity();
        }

        final AffineTransform transform = setVisibleArea(objToDisp, preferredArea, canvasBounds);
        objToDisp.concatenate(transform);

        if (preserveRotation) {
            final Rectangle2D displayBounds = getDisplayBounds();
            final double centerX = displayBounds.getCenterX();
            final double centerY = displayBounds.getCenterY();
            final AffineTransform change = objToDisp.createInverse();

            change.translate(+centerX, +centerY);
            change.rotate(rotation);
            change.translate(-centerX, -centerY);

            change.concatenate(objToDisp);
            roundIfAlmostInteger(change);
            objToDisp.concatenate(change);
        }

        //transform is in corner at this point, conver it to pixel center
        final AffineTransform dispToObj = new AffineTransform(objToDisp);
        dispToObj.invert();
        AffineTransform translate = new AffineTransform( (AffineTransform) PixelTranslation.translate(new AffineTransform2D(dispToObj), PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER));
        translate.invert();
        setTransform(translate);
    }

    //convinient method -----------------------------------------

    /**
     * Checks whether the rectangle {@code rect} is valid.  The rectangle
     * is considered invalid if its length or width is less than or equal to 0,
     * or if one of its coordinates is infinite or NaN.
     */
    private static boolean isValid(final Rectangle2D rect) {
        if (rect == null) {
            return false;
        }
        final double x = rect.getX();
        final double y = rect.getY();
        final double w = rect.getWidth();
        final double h = rect.getHeight();
        return (x > Double.NEGATIVE_INFINITY && x < Double.POSITIVE_INFINITY &&
                y > Double.NEGATIVE_INFINITY && y < Double.POSITIVE_INFINITY &&
                w > 0                        && w < Double.POSITIVE_INFINITY &&
                h > 0                        && h < Double.POSITIVE_INFINITY);
    }

    /**
     * Defines the limits of the visible part, in logical coordinates.  This method will modify the
     * zoom and the translation in order to display the specified region. If {@link #zoom} contains
     * a rotation, this rotation will not be modified.
     *
     * @param  source Logical coordinates of the region to be displayed.
     * @param  dest Pixel coordinates of the region of the window in which to
     *         draw (normally {@link #getDisplayBounds()}).
     * @param  mask A mask to {@code OR} with the {@link #type} for determining which
     *         kind of transformation are allowed. The {@link #type} is not modified.
     * @return Change to apply to the affine transform {@link #zoom}.
     * @throws IllegalArgumentException if {@code source} is empty.
     */
    private AffineTransform setVisibleArea(AffineTransform objToDisp, final Rectangle2D source, Rectangle2D dest)
                                           throws IllegalArgumentException,NoninvertibleTransformException{
        /*
         * Verifies the validity of the source rectangle. An invalid rectangle will be rejected.
         * However, we will be more flexible for dest since the window could have been reduced by
         * the user.
         */
        if (!isValid(source)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EmptyRectangle_1, source));
        }
        if (!isValid(dest)) {
            return new AffineTransform();
        }

        /*
         * Converts the destination into logical coordinates.  We can then perform
         * a zoom and a translation which would put {@code source} in {@code dest}.
         */
        dest = AffineTransforms2D.inverseTransform(objToDisp, dest, null);

        final double sourceWidth  = source.getWidth ();
        final double sourceHeight = source.getHeight();
        final double   destWidth  =   dest.getWidth ();
        final double   destHeight =   dest.getHeight();
              double           sx = destWidth / sourceWidth;
              double           sy = destHeight / sourceHeight;


        //switch among the Axis proportions requested
        if (!proportion) {
            //we dont respect X/Y proportions
        } else {
            /*
             * Standardizes the horizontal and vertical scales,
             * if such a standardization has been requested.
             */
            if (sy * sourceWidth < destWidth) {
                sx = sy;
            } else if (sx * sourceHeight < destHeight) {
                sy = sx;
            }
        }

        final AffineTransform change = AffineTransform.getTranslateInstance(dest.getCenterX(),dest.getCenterY());
        change.scale(sx,sy);
        change.translate(-source.getCenterX(), -source.getCenterY());
        roundIfAlmostInteger(change);
        return change;
    }

    /**
     * Constructs a transform between two coordinate reference systems.
     *
     * @param  sourceCRS The source coordinate reference system.
     * @param  targetCRS The target coordinate reference system.
     * @param  sourceClassName  The caller class name, for logging purpose only.
     * @param  sourceMethodName The caller method name, for logging purpose only.
     * @return A transform from {@code sourceCRS} to {@code targetCRS}.
     * @throws FactoryException if the transform can't be created.
     *
     * @see DisplayObject#getRenderingHint(java.awt.RenderingHints.Key)
     * @see DisplayObject#setRenderingHint(java.awt.RenderingHints.Key, java.lang.Object)
     */
    public final synchronized MathTransform getMathTransform(final CoordinateReferenceSystem sourceCRS,
                                                      final CoordinateReferenceSystem targetCRS,
                                                      final Class<?> sourceClassName,
                                                      final String sourceMethodName)
            throws FactoryException {
        /*
         * Fast check for a very common case. We will use the more general (but slower)
         * 'equalsIgnoreMetadata(...)' version implicitly in the call to factory method.
         */
        if (sourceCRS == targetCRS) {
            return MathTransforms.identity(sourceCRS.getCoordinateSystem().getDimension());
        }
        MathTransform tr;
        /*
         * Checks if the math transform is available in the cache. A majority of transformations
         * will be from 'graphicCRS' to 'objectiveCRS' to 'displayCRS'.  The cache looks for the
         * 'graphicCRS' to 'objectiveCRS' transform.
         */
        final CoordinateReferenceSystem objectiveCRS = getObjectiveCRS();
        final boolean cachedTransform = CRS.equivalent(targetCRS, objectiveCRS);
        if (cachedTransform) {
            tr = transforms.get(sourceCRS);
            if (tr != null) {
                return tr;
            }
        }
        /*
         * If one of the CRS is a derived CRS, then check if we can use directly its conversion
         * from base without using the costly coordinate operation factory. This check is worth
         * to be done since it is a very common situation. A majority of transformations will be
         * from 'objectiveCRS' to 'displayCRS', which is the case we test first. The converse
         * (transformations from 'displayCRS' to 'objectiveCRS') is less frequent and can be
         * handled by the 'transform' cache, which is why we let the factory check for it.
         */
        if (targetCRS instanceof DerivedCRS) {
            final var derivedCRS = (DerivedCRS) targetCRS;
            if (CRS.equivalent(sourceCRS, derivedCRS.getBaseCRS())) {
                return derivedCRS.getConversionFromBase().getMathTransform();
            }
        }
        /*
         * Now that we failed to reuse a pre-existing transform, ask to the factory
         * to create a new one. A message is logged in order to trace down the amount
         * of coordinate operations created.
         */
        final Logger logger = getLogger();
        if (logger.isLoggable(Level.FINER)) {
            // FINER is the default level for entering, returning, or throwing an exception.
            final LogRecord record = Loggings.getResources(Locale.getDefault()).createLogRecord(Level.FINER,
                    Loggings.Keys.InitializingTransformation_2,
                    toString(sourceCRS), toString(targetCRS));
            record.setSourceClassName (sourceClassName.getName());
            record.setSourceMethodName(sourceMethodName);
            logger.log(record);
        }


        tr = CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform();

        if (cachedTransform) {
            transforms.put(sourceCRS, tr);
        }
        return tr;
    }

    /**
     * Returns a string representation of a coordinate reference system. This method is
     * used for formatting a logging message in {@link #getMathTransform}.
     */
    private static String toString(final CoordinateReferenceSystem crs) {
        return Classes.getShortClassName(crs) + "[\"" + crs.getName().getCode() + "\"]";
    }

    ////////////////////////////////////////////////////////////////////////////
    // Next methods are convinient methods which always end up by calling applyTransform(trs)
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the center of the canvas in objective CRS.
     *
     * @return DirectPosition : center of the canvas
     * @throws java.awt.geom.NoninvertibleTransformException
     * @throws org.opengis.referencing.operation.TransformException
     */
    public DirectPosition getObjectiveCenter() throws NoninvertibleTransformException, TransformException {
        final Point2D center = getDisplayCenter();
        getObjectiveToDisplay().inverseTransform(center, center);
        final GeneralDirectPosition pt = new GeneralDirectPosition(getObjectiveCRS2D());
        pt.setCoordinates(center.getX(), center.getY());
        return pt;
    }

    public final Point2D getDisplayCenter() {
        final Rectangle2D rect = getDisplayBounds();
        return new Point2D.Double(rect.getCenterX(), rect.getCenterY());
    }

    /**
     * @return visible envelope of the canvas, in Objective CRS
     */
    public final Envelope getVisibleEnvelope() {
        return gridGeometry.getEnvelope();
    }

    /**
     * @return visible envelope of the canvas, in Objective CRS 2D
     * @throws org.opengis.referencing.operation.TransformException
     */
    public final Envelope getVisibleEnvelope2D() throws TransformException {
        final CoordinateReferenceSystem objectiveCRS2D = getObjectiveCRS2D();
        return Envelopes.transform(getVisibleEnvelope(), objectiveCRS2D);
    }

    public final void rotate(final double r) throws NoninvertibleTransformException {
        rotate(r, getDisplayCenter());
    }

    public final void rotate(final double r, final Point2D center) throws NoninvertibleTransformException {
        final AffineTransform2D objToDisp = getObjectiveToDisplay();
        final AffineTransform change = objToDisp.createInverse();

        if (center != null) {
            final double centerX = center.getX();
            final double centerY = center.getY();
            change.translate(+centerX, +centerY);
            change.rotate(-r);
            change.translate(-centerX, -centerY);
        }

        change.concatenate(objToDisp);
        roundIfAlmostInteger(change);
        applyTransform(change);
    }

    /**
     * Change scale by a precise amount.
     *
     * @param s : multiplication scale factor
     * @throws java.awt.geom.NoninvertibleTransformException
     */
    public final void scale(final double s) throws NoninvertibleTransformException {
        scale(s, getDisplayCenter());
    }

    /**
     * @param center in Display CRS
     */
    public final void scale(final double s, final Point2D center) throws NoninvertibleTransformException {
        final AffineTransform2D objToDisp = getObjectiveToDisplay();
        final AffineTransform change = objToDisp.createInverse();

        if (center != null) {
            final double centerX = center.getX();
            final double centerY = center.getY();
            change.translate(+centerX, +centerY);
            change.scale(s, s);
            change.translate(-centerX, -centerY);
        }

        change.concatenate(objToDisp);
        roundIfAlmostInteger(change);
        applyTransform(change);
    }

    /**
     * Translate of x and y amount in display units.
     *
     * @param x : translation against the X axy
     * @param y : translation against the Y axy
     * @throws java.awt.geom.NoninvertibleTransformException
     */
    public final void translateDisplay(final double x, final double y) throws NoninvertibleTransformException {
        final AffineTransform2D objToDisp = getObjectiveToDisplay();
        final AffineTransform change = objToDisp.createInverse();
        change.translate(x, y);
        change.concatenate(objToDisp);
        roundIfAlmostInteger(change);
        applyTransform(change);
    }

    public final void translateObjective(final double x, final double y) throws NoninvertibleTransformException, TransformException {
        final Point2D dispCenter = getDisplayCenter();
        final DirectPosition center = getObjectiveCenter();
        Point2D objCenter = new Point2D.Double(center.getCoordinate(0) + x, center.getCoordinate(1) + y);
        objCenter = getObjectiveToDisplay().transform(objCenter, objCenter);
        translateDisplay(dispCenter.getX() - objCenter.getX(), dispCenter.getY() - objCenter.getY());
    }

    /**
     * Changes the {@linkplain #AffineTransform} by applying an affine transform. The {@code change} transform
     * must express a change in pixel units, for example, a scrolling of 6 pixels toward right.
     *
     * @param  change The zoom change, as an affine transform in pixel coordinates. If
     *         {@code change} is the identity transform, then this method does nothing
     *         and listeners are not notified.
     *
     * @since 2.1
     */
    public final void transformPixels(final AffineTransform change) {
        if (!change.isIdentity()) {
            final AffineTransform2D objToDisp = getObjectiveToDisplay();
            final AffineTransform logical;
            try {
                logical = objToDisp.createInverse();
            } catch (NoninvertibleTransformException exception) {
                throw new IllegalStateException(exception);
            }
            logical.concatenate(change);
            logical.concatenate(objToDisp);
            roundIfAlmostInteger(logical);
            applyTransform(logical);
        }
    }

    public final void setRotation(final double r) throws NoninvertibleTransformException {
        double rotation = getRotation();
        rotate(rotation - r);
    }

    public final double getRotation() {
        return -AffineTransforms2D.getRotation(getObjectiveToDisplay());
    }

    public final void setScale(final double newScale) throws NoninvertibleTransformException {
        final double oldScale = AffineTransforms2D.getScale(getObjectiveToDisplay());
        scale(newScale / oldScale);
    }

    /**
     * Returns the current scale factor. A value of 1/100 means that 100 metres
     * are displayed as 1 pixel (provided that the logical coordinates of {@code #getArea} are
     * expressed in metres). Scale factors for X and Y axes can be computed separately using the
     * following equations:
     *
     * <table cellspacing=3><tr>
     * <td width=50%><IMG src="doc-files/scaleX.png"></td>
     * <td width=50%><IMG src="doc-files/scaleY.png"></td>
     * </tr></table>
     *
     * @return scale
     */
    public final double getScale() {
        return AffineTransforms2D.getScale(getObjectiveToDisplay());
    }

    /**
     * Get objective to display transform at canvas center.
     * @return AffineTransform
     */
    public final AffineTransform getCenterTransform(){
        final Rectangle2D rect = getDisplayBounds();
        final double centerX = rect.getCenterX();
        final double centerY = rect.getCenterY();
        final AffineTransform trs = new AffineTransform(1, 0, 0, 1, -centerX + 0.5, -centerY + 0.5);
        final AffineTransform objToDisp = getObjectiveToDisplay().clone();
        trs.concatenate(objToDisp);
        return trs;
    }

    /**
     * Set objective to display transform at canvas center.
     */
    public final void setCenterTransform(AffineTransform trs) {

        final Rectangle2D rect = getDisplayBounds();
        final double centerX = rect.getCenterX();
        final double centerY = rect.getCenterY();
        final AffineTransform centerTrs = new AffineTransform(1, 0, 0, 1, centerX - 0.5, centerY - 0.5);
        centerTrs.concatenate(trs);
        setTransform(centerTrs);
    }

    public final void setDisplayVisibleArea(final Rectangle2D dipsEnv) {
        try {
            Shape shp = getObjectiveToDisplay().createInverse().createTransformedShape(dipsEnv);
            setVisibleArea(shp.getBounds2D());
        } catch (NoninvertibleTransformException ex) {
            getLogger().log(Level.WARNING, null, ex);
        }
    }

    public void setVisibleArea(final Envelope env) throws NoninvertibleTransformException, TransformException {
        if (env == null) return;
        final CoordinateReferenceSystem envCRS = env.getCoordinateReferenceSystem();
        if (envCRS == null) return;
        final CoordinateReferenceSystem envCRS2D = CRSUtilities.getCRS2D(envCRS);
        Envelope env2D = Envelopes.transform(env, envCRS2D);

        //check that the provided envelope is in the canvas crs
        final CoordinateReferenceSystem canvasCRS2D = getObjectiveCRS2D();
        if (!CRS.equivalent(canvasCRS2D,envCRS2D)) {
            env2D = Envelopes.transform(env2D, canvasCRS2D);
        }

        //configure the 2D envelope
        Rectangle2D rect2D = new Rectangle2D.Double(env2D.getMinimum(0), env2D.getMinimum(1), env2D.getSpan(0), env2D.getSpan(1));
        resetTransform(rect2D, true,false);

        //set the extra xis if some exist
        int index=0;
        List<SingleCRS> dcrss = CRS.getSingleComponents(envCRS);

        // Following loop is a temporary hack for decomposing Geographic3D into Geographic2D + ellipsoidal height.
        // This is a wrong thing to do according international standards; we will revisit in a future version.
        for (int i = dcrss.size(); --i >= 0;) {
            SingleCRS crs = dcrss.get(i);
            SingleCRS hcrs = CRS.getHorizontalComponent(crs);
            if (hcrs != null && hcrs != crs) {
                SingleCRS vcrs = CRS.getVerticalComponent(envCRS, true);
                if (vcrs != null && hcrs.getCoordinateSystem().getDimension()
                                  + vcrs.getCoordinateSystem().getDimension()
                                  == crs.getCoordinateSystem().getDimension())
                {
                    dcrss = new ArrayList<>(dcrss);
                    dcrss.set(i, hcrs);
                    dcrss.add(i+1, vcrs);
                }
            }
        }
        for (CoordinateReferenceSystem dcrs : dcrss) {
            if (dcrs.getCoordinateSystem().getDimension() == 1) {
                final CoordinateSystemAxis axis = dcrs.getCoordinateSystem().getAxis(0);
                final AxisFinder finder = new AxisFinder(axis);
                final int cindex = getAxisIndex(finder);
                if (cindex >= 0) {
                    setAxisRange(env.getMinimum(index), env.getMaximum(index), finder, dcrs);
                }
            }
            index += dcrs.getCoordinateSystem().getDimension();
        }
    }

    /**
     * Defines the limits of the visible part, in logical coordinates.  This method will modify the
     * zoom and the translation in order to display the specified region. If {@link #zoom} contains
     * a rotation, this rotation will not be modified.
     *
     * @param  logicalBounds Logical coordinates of the region to be displayed.
     * @throws IllegalArgumentException if {@code source} is empty.
     * @throws java.awt.geom.NoninvertibleTransformException
     */
    public final void setVisibleArea(final Rectangle2D logicalBounds) throws IllegalArgumentException, NoninvertibleTransformException {
        resetTransform(logicalBounds, true,true);
    }

    /**
     * Set the scale, in a ground unit manner, relation between map display size
     * and real ground unit meters;
     */
    public final void setGeographicScale(final double scale) throws TransformException {
        double currentScale = getGeographicScale();
        double factor = currentScale / scale;
        try {
            scale(factor);
        } catch (NoninvertibleTransformException ex) {
            getLogger().log(Level.WARNING, null, ex);
        }
    }

    /**
     * Returns the geographic scale, in a ground unit manner, relation between map display size
     * and real ground unit meters.
     *
     * @throws org.opengis.referencing.operation.TransformException
     * @throws IllegalStateException If the affine transform used for conversion is in
     *                               illegal state.
     */
    public final double getGeographicScale() throws TransformException {
        return CanvasUtilities.getGeographicScale(getDisplayCenter(), getObjectiveToDisplay(), getObjectiveCRS2D());
    }

    public final Date[] getTemporalRange() {
        final int index = getTemporalAxisIndex();
        if (index >= 0) {
            final Envelope envelope = getVisibleEnvelope();
            final Date[] range = new Date[2];
            final double min = envelope.getMinimum(index);
            final double max = envelope.getMaximum(index);
            range[0] = Double.isInfinite(min) ? null : new Date((long)min);
            range[1] = Double.isInfinite(max) ? null : new Date((long)max);
            return range;
        }
        return null;
    }

    public final Double[] getElevationRange() {
        final int index = getElevationAxisIndex();
        if (index >= 0) {
            final Envelope envelope = getVisibleEnvelope();
            return new Double[]{envelope.getMinimum(index), envelope.getMaximum(index)};
        }
        return null;
    }

    public final Unit<Length> getElevationUnit() {
        final int index = getElevationAxisIndex();
        if (index >= 0) {
            return (Unit<Length>) getObjectiveCRS().getCoordinateSystem().getAxis(index).getUnit();
        }
        return null;
    }

    //convinient methods -------------------------------------------------

    /**
     * Find the elevation axis index or -1 if there is none.
     */
    private int getElevationAxisIndex() {
        final CoordinateReferenceSystem objCrs = getObjectiveCRS();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final AxisDirection direction = cs.getAxis(i).getDirection();
            final Unit unit = cs.getAxis(i).getUnit();
            if (direction == AxisDirection.UP || direction == AxisDirection.DOWN && (unit != null && unit.isCompatible(Units.METRE))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the temporal axis index or -1 if there is none.
     */
    private int getTemporalAxisIndex() {
        final CoordinateReferenceSystem objCrs = getObjectiveCRS();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final AxisDirection direction = cs.getAxis(i).getDirection();
            if (direction == AxisDirection.FUTURE || direction == AxisDirection.PAST) {
                return i;
            }
        }
        return -1;
    }


    public final Double[] getAxisRange(final Comparator<CoordinateSystemAxis> comparator) {
        final int index = getAxisIndex(comparator);
        if (index >= 0) {
            final Envelope envelope = getVisibleEnvelope();
            return new Double[]{envelope.getMinimum(index), envelope.getMaximum(index)};
        }
        return null;
    }

    public final void setAxisRange(final Double min, final Double max,
            final Comparator<CoordinateSystemAxis> comparator, CoordinateReferenceSystem axisCrs) throws TransformException {
        try {
            int index = getAxisIndex(comparator);
            if(index < 0 && (min!=null || max!=null)){
                //no elevation axis, add one
                CoordinateReferenceSystem crs = getObjectiveCRS();
                crs = appendCRS(crs, axisCrs);
                setObjectiveCRS(crs);
                index = getElevationAxisIndex();
            }

            if (index >= 0) {
                if(min!=null || max!=null){
                    setRange(index,
                        (min!=null)?min:Double.NEGATIVE_INFINITY,
                        (max!=null)?max:Double.POSITIVE_INFINITY);
                }else{
                    //remove this dimension
                    CoordinateReferenceSystem crs = getObjectiveCRS();
                    crs = removeCRS(crs, axisCrs);
                    setObjectiveCRS(crs);
                }
            }
        } catch(FactoryException ex) {
            throw new TransformException("", ex);
        }
    }

    /**
     * Search an axis index.
     * Comparator must return 0 when found.
     *
     * @return -1 if not found
     */
    public final int getAxisIndex(final Comparator<CoordinateSystemAxis> comparator) {
        final CoordinateReferenceSystem objCrs = getObjectiveCRS();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final CoordinateSystemAxis axi = cs.getAxis(i);
            if(comparator.compare(axi, axi) == 0) return i;
        }
        return -1;
    }

    private CoordinateReferenceSystem appendCRS(final CoordinateReferenceSystem crs, final CoordinateReferenceSystem toAdd) throws FactoryException{
        if(crs instanceof CompoundCRS){
            final CompoundCRS orig = (CompoundCRS) crs;
            final List<CoordinateReferenceSystem> lst = new ArrayList<>(orig.getComponents());
            lst.add(toAdd);
            return new GeodeticObjectBuilder().addName(orig.getName().getCode())
                                              .createCompoundCRS(lst.toArray(new CoordinateReferenceSystem[lst.size()]));
        } else {
            return new GeodeticObjectBuilder().addName(crs.getName().getCode() + ' ' + toAdd.getName().getCode())
                                              .createCompoundCRS(crs, toAdd);
        }

    }

    private CoordinateReferenceSystem removeCRS(final CoordinateReferenceSystem crs, final CoordinateReferenceSystem toRemove) throws FactoryException{
        if(crs instanceof CompoundCRS){
            final CompoundCRS orig = (CompoundCRS) crs;
            final List<CoordinateReferenceSystem> lst = new ArrayList<>(orig.getComponents());
            lst.remove(toRemove);
            if(lst.size() == 1){
                return lst.get(0);
            }
            return new GeodeticObjectBuilder().addName(orig.getName().getCode())
                                              .createCompoundCRS(lst.toArray(new CoordinateReferenceSystem[lst.size()]));
        }else{
            return crs;
        }
    }

    private static int getHorizontalIndex(CoordinateReferenceSystem envCRS) {
        //set the extra xis if some exist
        int index=0;
        final List<SingleCRS> dcrss = CRS.getSingleComponents(envCRS);

        // Following loop is a temporary hack for decomposing Geographic3D into Geographic2D + ellipsoidal height.
        // This is a wrong thing to do according international standards; we will revisit in a future version.
        for (SingleCRS crs : dcrss) {
            SingleCRS hcrs = CRS.getHorizontalComponent(crs);
            if (hcrs != null) {
                return index;
            }
            index += crs.getCoordinateSystem().getDimension();
        }
        throw new RuntimeException("Coordinate system has no horizontal component");
    }

    /**
     * If scale and shear coefficients are close to integers, replaces their current values by their rounded values.
     * The scale and shear coefficients are handled in a "all or nothing" way; either all of them or none are rounded.
     * The translation terms are handled separately, provided that the scale and shear coefficients have been rounded.
     *
     * <p>This rounding up is useful for example in order to speedup image displays.</p>
     *
     * @param  tr  the transform to round. Rounding will be applied in place.
     */
    private static void roundIfAlmostInteger(final AffineTransform tr) {
        double r;
        final double m00, m01, m10, m11;
        if (abs((m00 = rint(r=tr.getScaleX())) - r) <= EPS &&
            abs((m01 = rint(r=tr.getShearX())) - r) <= EPS &&
            abs((m11 = rint(r=tr.getScaleY())) - r) <= EPS &&
            abs((m10 = rint(r=tr.getShearY())) - r) <= EPS)
        {
            /*
             * At this point the scale and shear coefficients can been rounded to integers.
             * Continue only if this rounding does not make the transform non-invertible.
             */
            if ((m00!=0 || m01!=0) && (m10!=0 || m11!=0)) {
                double m02, m12;
                if (abs((r = rint(m02=tr.getTranslateX())) - m02) <= EPS) m02=r;
                if (abs((r = rint(m12=tr.getTranslateY())) - m12) <= EPS) m12=r;
                tr.setTransform(m00, m10, m01, m11, m02, m12);
            }
        }
    }

    public static Rectangle toRectangle(final GridExtent extent) {
        final int dimension = extent.getDimension();
        if (dimension != 2) throw new MismatchedDimensionException("Only 2D extents can be converted to rectangle, but input dimension is "+ dimension);
        return new Rectangle(
                Math.toIntExact(extent.getLow(0)),
                Math.toIntExact(extent.getLow(1)),
                Math.toIntExact(extent.getSize(0)),
                Math.toIntExact(extent.getSize(1))
        );
    }
}
