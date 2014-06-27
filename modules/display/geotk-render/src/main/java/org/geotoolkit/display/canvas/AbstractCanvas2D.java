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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultDerivedCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCanvas2D extends AbstractCanvas{

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
     * {@linkplain AbstractCanvas2D#getObjectiveCRS canvas crs} changed.
     */
    public static final String OBJECTIVE_CRS_KEY = "ObjectiveCRS";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas2D#getEnvelope } changed.
     */
    public static final String ENVELOPE_KEY = "Envelope";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas2D#getObjectiveToDisplay transform} changed.
     */
    public static final String TRANSFORM_KEY = "Transform";

    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AbstractCanvas2D#getDisplayBounds rectangle} changed.
     */
    public static final String BOUNDS_KEY = "Bounds";

    /**
     * A set of {@link MathTransform}s from various source CRS. The target CRS must be the
     * {@linkplain #getObjectiveCRS objective CRS} for all entries. Keys are source CRS.
     * This map is used only in order to avoid the costly call to
     * {@link CoordinateOperationFactory#createOperation} as much as possible. If a
     * transformation is not available in this collection, then the usual factory will be used.
     */
    private final transient Map<CoordinateReferenceSystem,MathTransform> transforms = new HashMap<>();

    /**
     * Contains the canvas bounds.
     */
    private final Rectangle2D displayBounds = new Rectangle2D.Double(0,0,1,1);
    private final AffineTransform objToDisp = new AffineTransform();
    private CoordinateReferenceSystem objectiveCRS;
    private CoordinateReferenceSystem objectiveCRS2D;
    private double proportion = 1;
    private boolean autoRepaint = false;

    private GeneralEnvelope envelope;

    //navigation constraint
    private double minscale = Double.NaN;
    private double maxscale = Double.NaN;

    public AbstractCanvas2D() {
        this(new Hints());
    }

    public AbstractCanvas2D(Hints hints) {
        this(DefaultGeographicCRS.WGS84,hints);
    }

    public AbstractCanvas2D(CoordinateReferenceSystem crs, Hints hints) {
        super(hints);
        ArgumentChecks.ensureNonNull("Objective CRS", crs);
        objectiveCRS = crs;
        try {
            objectiveCRS2D = CRSUtilities.getCRS2D(objectiveCRS);
        } catch (TransformException ex) {
            getLogger().log(Level.WARNING, null, ex);
        }
        envelope = new GeneralEnvelope(objectiveCRS);
    }

    public CoordinateReferenceSystem getObjectiveCRS() {
        return objectiveCRS;
    }

    public void setObjectiveCRS(final CoordinateReferenceSystem crs) throws TransformException{
        ArgumentChecks.ensureNonNull("Objective CRS", crs);
        if(CRS.equalsIgnoreMetadata(objectiveCRS, crs)){
            return;
        }

        //store the visible area to restore it later
        GeneralEnvelope preserve = null;
        if(!displayBounds.isEmpty()){
            preserve = new GeneralEnvelope(envelope);
        }

        try {
            resetTransform();
        } catch (NoninvertibleTransformException ex) {
            throw new TransformException("Fail to change objective CRS", ex);
        }

        final CoordinateReferenceSystem oldCRS = objectiveCRS;
        objectiveCRS = crs;
        envelope = new GeneralEnvelope(objectiveCRS);
        objectiveCRS2D = CRSUtilities.getCRS2D(objectiveCRS);
        firePropertyChange(OBJECTIVE_CRS_KEY, oldCRS, crs);

        if(preserve != null){
            //restore previous visible area
            GeneralEnvelope env = new GeneralEnvelope(CRS.transform(preserve, objectiveCRS2D));
            if(!isValid(env)) env = null;

            //try to normalize before reproject
            if(env == null && preserve.normalize()){
                env = new GeneralEnvelope(CRS.transform(preserve, objectiveCRS2D));
            }
            if(!isValid(env)) env = null;

            //try to reduce to domain before reproject
            if(env == null){
                final Envelope domain = CRS.getEnvelope(preserve.getCoordinateReferenceSystem());
                if(domain != null){
                    preserve.intersect(domain);
                    env = new GeneralEnvelope(CRS.transform(preserve, objectiveCRS2D));
                }
            }
            if(!isValid(env)) env = null;

            //fall back on crs domain
            if(env == null){
                final Envelope domain = CRS.getEnvelope(objectiveCRS2D);
                if(domain!=null){
                    env = new GeneralEnvelope(domain);
                }
            }

            try {
                if(env != null){
                    setVisibleArea(env);
                }else{
                    //what can we do ?
                }

            } catch (NoninvertibleTransformException ex) {
                throw new TransformException("Fail to change objective CRS", ex);
            }
        }

    }

    public CoordinateReferenceSystem getObjectiveCRS2D() {
        return objectiveCRS2D;
    }

    public CoordinateReferenceSystem getDisplayCRS() {
        final CoordinateReferenceSystem objCRS2D = getObjectiveCRS2D();
        final CoordinateReferenceSystem displayCRS = new DefaultDerivedCRS("Derived - "+objCRS2D.getName().toString(),
                objCRS2D, getObjectiveToDisplay(), objCRS2D.getCoordinateSystem());
        return displayCRS;
    }

    /**
     * @return a snapshot objective To display transform.
     */
    public AffineTransform2D getObjectiveToDisplay() {
        return new AffineTransform2D(objToDisp);
    }

    public AffineTransform2D getDisplayToObjective() throws NoninvertibleTransformException {
        return new AffineTransform2D(objToDisp.createInverse());
    }

    public Rectangle2D getDisplayBounds() {
        return displayBounds.getBounds2D();
    }

    public void setDisplayBounds(Rectangle2D bounds){
        ArgumentChecks.ensureNonNull("Display bounds", bounds);
        if(bounds.equals(displayBounds)) return;

        final Rectangle2D oldRec = displayBounds.getBounds2D();
        this.displayBounds.setRect(bounds);

        //fire event
        firePropertyChange(BOUNDS_KEY, oldRec, bounds.getBounds2D());
    }

    /**
     * Set the proportions support between X and Y axis.
     * if prop = Double.NaN then no correction will be applied
     * if prop = 1 then one unit in X will be equal to one unit in Y
     * else value will mean that prop*Y will be used
     */
    public void setAxisProportions(final double prop) {
        this.proportion = prop;
    }

    /**
     *
     * @return the X/Y proportion
     */
    public double getAxisProportions() {
        return proportion;
    }

    public void setAutoRepaint(final boolean autoRepaint) {
        this.autoRepaint = autoRepaint;
    }

    public boolean isAutoRepaint() {
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
    public void applyTransform(AffineTransform change){
        if(change.isIdentity()) return;
        objToDisp.concatenate(change);
        XAffineTransform.roundIfAlmostInteger(objToDisp, EPS);
        updateEnvelope();

        fixScale:
        if(!Double.isNaN(minscale) || !Double.isNaN(maxscale)){
            double scale = CanvasUtilities.computeSEScale(envelope, objToDisp, getDisplayBounds().getBounds());

            final Point2D center = getDisplayCenter();
            if(center== null) break fixScale;
            final double centerX = center.getX();
            final double centerY = center.getY();

            try {
                change = objToDisp.createInverse();
            } catch (NoninvertibleTransformException ex) {
                getLogger().log(Level.WARNING, null, ex);
                break fixScale;
            }

            double correction = Double.NaN;
            if(!Double.isNaN(maxscale) && scale>maxscale){
                correction = scale/maxscale;

            }
            if(!Double.isNaN(minscale) && scale<minscale){
                correction = scale/minscale;
            }

            if(!Double.isNaN(correction)){
                change.translate(+centerX, +centerY);
                change.scale(correction, correction);
                change.translate(-centerX, -centerY);
                change.concatenate(objToDisp);

                objToDisp.concatenate(change);
                XAffineTransform.roundIfAlmostInteger(objToDisp, EPS);
                updateEnvelope();
            }

        }

        firePropertyChange(TRANSFORM_KEY, null, change);
        repaintIfAuto();
    }

    private void updateEnvelope() {
        final Rectangle2D canvasDisplayBounds = getDisplayBounds();
        final Rectangle2D canvasObjectiveBounds;
        try {
            canvasObjectiveBounds = objToDisp.createInverse().createTransformedShape(canvasDisplayBounds).getBounds2D();
        } catch (NoninvertibleTransformException ex) {
            getLogger().log(Level.SEVERE, "Failed to calculate canvas objective bounds", ex);
            return;
        }
        envelope.setRange(0, canvasObjectiveBounds.getMinX(), canvasObjectiveBounds.getMaxX());
        envelope.setRange(1, canvasObjectiveBounds.getMinY(), canvasObjectiveBounds.getMaxY());
    }

    /**
     * Change a range in the canvas envelope.
     * Can be used to temporal or elevation range of the map.
     */
    private void setRange(final int ordinate, final double min, final double max){
        if(envelope.getMinimum(ordinate) == min && envelope.getMaximum(ordinate) == max){
            //same values
            return;
        }
        
        final GeneralEnvelope old = new GeneralEnvelope(envelope);
        envelope.setRange(ordinate, min, max);
        final GeneralEnvelope nw = new GeneralEnvelope(envelope);
        repaintIfAuto();
        firePropertyChange(ENVELOPE_KEY, old, nw);
    }

    public final void repaint(){
        repaint(displayBounds);
    }

    public abstract void repaint(Shape area);

    private void repaintIfAuto(){
        if(autoRepaint){
            repaint();
        }
    }

    public abstract Image getSnapShot();

    /**
     * Set minimum scale do display.
     * Scale is in SE scale.
     * @param minscale
     */
    public void setMinscale(double minscale) {
        this.minscale = minscale;
    }

    /**
     * Get minimum scale do display.
     * Scale is in SE scale.
     * @return min scale
     */
    public double getMinscale() {
        return minscale;
    }

    /**
     * Set maximum scale do display.
     * Scale is in SE scale.
     * @param maxscale
     */
    public void setMaxscale(double maxscale) {
        this.maxscale = maxscale;
    }

    /**
     * Get maximum scale do display.
     * Scale is in SE scale.
     * @return max scale
     */
    public double getMaxscale() {
        return maxscale;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Next methods are convinient methods which always end up by calling applyTransform(trs)
    ////////////////////////////////////////////////////////////////////////////

    private void setTransform(final AffineTransform transform){
        final AffineTransform2D old = getObjectiveToDisplay();

        if(!old.equals(transform)){
            objToDisp.setTransform(transform);
            updateEnvelope();
            repaintIfAuto();
        }
    }

    private void resetTransform() throws NoninvertibleTransformException{
        resetTransform(new Rectangle(1, 1), true, false);
    }

    /**
     * Reinitializes the affine transform {@link #zoom} in order to cancel any zoom, rotation or
     * translation. The argument {@code yAxisUpward} indicates whether the <var>y</var> axis should
     * point upwards.  The value {@code false} lets it point downwards. This method is offered
     * for convenience sake for derived classes which want to redefine {@link #reset()}.
     *
     * @param canvasBounds Coordinates, in pixels, of the screen space in which to draw.
     *        This argument will usually be
     *        <code>{@link #getZoomableBounds(Rectangle) getZoomableBounds}(null)</code>.
     * @param yAxisUpward {@code true} if the <var>y</var> axis should point upwards rather than
     *        downwards.
     */
    private void resetTransform(final Rectangle2D preferredArea, final boolean yAxisUpward,
                         final boolean preserveRotation) throws NoninvertibleTransformException{

        final Rectangle canvasBounds = getDisplayBounds().getBounds();
        if(canvasBounds.isEmpty()) return;
        if(!isValid(preferredArea)) return;

        canvasBounds.x = 0;
        canvasBounds.y = 0;

        final double rotation = -XAffineTransform.getRotation(objToDisp);

        if (yAxisUpward) {
            objToDisp.setToScale(+1, -1);
        }else {
            objToDisp.setToIdentity();
        }

        final AffineTransform transform = setVisibleArea(preferredArea, canvasBounds);
        objToDisp.concatenate(transform);

        if(preserveRotation){
            final double centerX = displayBounds.getCenterX();
            final double centerY = displayBounds.getCenterY();
            final AffineTransform change = objToDisp.createInverse();

            change.translate(+centerX, +centerY);
            change.rotate(rotation);
            change.translate(-centerX, -centerY);

            change.concatenate(objToDisp);
            XAffineTransform.roundIfAlmostInteger(change, EPS);
            objToDisp.concatenate(change);
        }
        updateEnvelope();
        firePropertyChange(TRANSFORM_KEY, null, null);
        repaintIfAuto();
    }

    //convinient method -----------------------------------------

    private static boolean isValid(GeneralEnvelope env){
        if(env == null) return false;

        if(env.isAllNaN() || env.isEmpty()) return false;

        if(Double.isInfinite(env.getMinimum(0)) || Double.isInfinite(env.getMinimum(1)) ||
           Double.isInfinite(env.getMaximum(0)) || Double.isInfinite(env.getMaximum(1)) ){
            return false;
        }
        return true;
    }

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
    private AffineTransform setVisibleArea(final Rectangle2D source, Rectangle2D dest)
                                           throws IllegalArgumentException,NoninvertibleTransformException{
        /*
         * Verifies the validity of the source rectangle. An invalid rectangle will be rejected.
         * However, we will be more flexible for dest since the window could have been reduced by
         * the user.
         */
        if (!isValid(source)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EMPTY_RECTANGLE_1, source));
        }
        if (!isValid(dest)) {
            return new AffineTransform();
        }

        /*
         * Converts the destination into logical coordinates.  We can then perform
         * a zoom and a translation which would put {@code source} in {@code dest}.
         */
        dest = XAffineTransform.inverseTransform(objToDisp, dest, null);

        final double sourceWidth  = source.getWidth ();
        final double sourceHeight = source.getHeight();
        final double   destWidth  =   dest.getWidth ();
        final double   destHeight =   dest.getHeight();
              double           sx = destWidth / sourceWidth;
              double           sy = destHeight / sourceHeight;


        //switch among the Axis proportions requested
        if( Double.isNaN(proportion) ){
            //we dont respect X/Y proportions
        }else if( proportion == 1){
            /*
             * Standardizes the horizontal and vertical scales,
             * if such a standardization has been requested.
             */
            if (sy * sourceWidth < destWidth) {
                sx = sy;
            } else if (sx * sourceHeight < destHeight) {
                sy = sx;
            }
        }else{
            sy = proportion*sx;
        }

        final AffineTransform change = AffineTransform.getTranslateInstance(dest.getCenterX(),dest.getCenterY());
        change.scale(sx,sy);
        change.translate(-source.getCenterX(), -source.getCenterY());
        XAffineTransform.roundIfAlmostInteger(change, EPS);
        return change;
    }

    /**
     * Constructs a transform between two coordinate reference systems. If a
     * {@link Hints#COORDINATE_OPERATION_FACTORY} has been provided, then the specified
     * {@linkplain CoordinateOperationFactory coordinate operation factory} will be used.
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
     * @see Hints#COORDINATE_OPERATION_FACTORY
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
        final boolean cachedTransform = CRS.equalsIgnoreMetadata(targetCRS, objectiveCRS);
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
        if (targetCRS instanceof GeneralDerivedCRS) {
            final GeneralDerivedCRS derivedCRS = (GeneralDerivedCRS) targetCRS;
            if (CRS.equalsIgnoreMetadata(sourceCRS, derivedCRS.getBaseCRS())) {
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
            final LogRecord record = Loggings.getResources(Locale.getDefault()).getLogRecord(Level.FINER,
                    Loggings.Keys.INITIALIZING_TRANSFORMATION_2,
                    toString(sourceCRS), toString(targetCRS));
            record.setSourceClassName (sourceClassName.getName());
            record.setSourceMethodName(sourceMethodName);
            logger.log(record);
        }


        tr = CRS.findMathTransform(sourceCRS, targetCRS, true);
        //TODO I used the CRS utility class, the following commented code, raises bursa wolf errors
//        CoordinateOperatetCoordinateOperationFactory();
//        tr = factory.createOperation(sourceCRSionFactory factory = getCoordinateOperationFactory();
//        tr = factory.createOperation(sourceCRS, targetCRS).getMathTransform();

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
        pt.setCoordinate(center.getX(), center.getY());
        return pt;
    }

    public void setObjectiveCenter(DirectPosition center) throws NoninvertibleTransformException, TransformException, FactoryException {

        final DirectPosition oldCenter = getObjectiveCenter();

        final CoordinateReferenceSystem candidateCRS = center.getCoordinateReferenceSystem();
        if(candidateCRS != null && !CRS.equalsIgnoreMetadata(candidateCRS, oldCenter.getCoordinateReferenceSystem())){
            final MathTransform trs = CRS.findMathTransform(candidateCRS,oldCenter.getCoordinateReferenceSystem());
            center = trs.transform(center, null);
        }

        final double diffX = center.getOrdinate(0) - oldCenter.getOrdinate(0);
        final double diffY = center.getOrdinate(1) - oldCenter.getOrdinate(1);
        translateObjective(diffX, diffY);
    }

    public Point2D getDisplayCenter() {
        final Rectangle2D rect = getDisplayBounds();
        return new Point2D.Double(rect.getCenterX(), rect.getCenterY());
    }

    /**
     * @return visible envelope of the canvas, in Objective CRS
     */
    public Envelope getVisibleEnvelope() {
        return new GeneralEnvelope(envelope);
    }

    /**
     * @return visible envelope of the canvas, in Objective CRS 2D
     * @throws org.opengis.referencing.operation.TransformException
     */
    public Envelope getVisibleEnvelope2D() throws TransformException {
        final CoordinateReferenceSystem objectiveCRS2D = getObjectiveCRS2D();
        return CRS.transform(getVisibleEnvelope(), objectiveCRS2D);
    }

    public void rotate(final double r) throws NoninvertibleTransformException {
        rotate(r, getDisplayCenter());
    }

    public void rotate(final double r, final Point2D center) throws NoninvertibleTransformException {
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
        XAffineTransform.roundIfAlmostInteger(change, EPS);
        applyTransform(change);
    }

    /**
     * Change scale by a precise amount.
     *
     * @param s : multiplication scale factor
     * @throws java.awt.geom.NoninvertibleTransformException
     */
    public void scale(final double s) throws NoninvertibleTransformException {
        scale(s, getDisplayCenter());
    }

    /**
     *
     * @param s
     * @param center in Display CRS
     * @throws NoninvertibleTransformException
     */
    public void scale(final double s, final Point2D center) throws NoninvertibleTransformException {
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
        XAffineTransform.roundIfAlmostInteger(change, EPS);
        applyTransform(change);
    }

    /**
     * Translate of x and y amount in display units.
     *
     * @param x : translation against the X axy
     * @param y : translation against the Y axy
     * @throws java.awt.geom.NoninvertibleTransformException
     */
    public void translateDisplay(final double x, final double y) throws NoninvertibleTransformException {
        final AffineTransform2D objToDisp = getObjectiveToDisplay();
        final AffineTransform change = objToDisp.createInverse();
        change.translate(x, y);
        change.concatenate(objToDisp);
        XAffineTransform.roundIfAlmostInteger(change, EPS);
        applyTransform(change);
    }

    public void translateObjective(final double x, final double y) throws NoninvertibleTransformException, TransformException {
        final Point2D dispCenter = getDisplayCenter();
        final DirectPosition center = getObjectiveCenter();
        Point2D objCenter = new Point2D.Double(center.getOrdinate(0) + x, center.getOrdinate(1) + y);
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
    public void transformPixels(final AffineTransform change) {
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
            XAffineTransform.roundIfAlmostInteger(logical, EPS);
            applyTransform(logical);
        }
    }

    public void setRotation(final double r) throws NoninvertibleTransformException {
        double rotation = getRotation();
        rotate(rotation - r);
    }

    public double getRotation() {
        return -XAffineTransform.getRotation(getObjectiveToDisplay());
    }

    public void setScale(final double newScale) throws NoninvertibleTransformException {
        final double oldScale = XAffineTransform.getScale(getObjectiveToDisplay());
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
    public double getScale() {
        return XAffineTransform.getScale(getObjectiveToDisplay());
    }

    /**
     * Get objective to display transform at canvas center.
     * @return AffineTransform
     */
    public AffineTransform getCenterTransform(){
        final Rectangle2D rect = getDisplayBounds();
        final double centerX = rect.getCenterX();
        final double centerY = rect.getCenterY();
        final AffineTransform trs = new AffineTransform(1, 0, 0, 1, -centerX, -centerY);
        final AffineTransform objToDisp = getObjectiveToDisplay().clone();
        trs.concatenate(objToDisp);
        return trs;
    }

    /**
     * Set objective to display transform at canvas center.
     * @param trs
     */
    public void setCenterTransform(AffineTransform trs) {

        final Rectangle2D rect = getDisplayBounds();
        final double centerX = rect.getCenterX();
        final double centerY = rect.getCenterY();
        final AffineTransform centerTrs = new AffineTransform(1, 0, 0, 1, centerX, centerY);
        centerTrs.concatenate(trs);

        setTransform(centerTrs);
    }

    public void setDisplayVisibleArea(final Rectangle2D dipsEnv) {
        try {
            Shape shp = getObjectiveToDisplay().createInverse().createTransformedShape(dipsEnv);
            setVisibleArea(shp.getBounds2D());
        } catch (NoninvertibleTransformException ex) {
            getLogger().log(Level.WARNING, null, ex);
        }
    }

    public void setVisibleArea(final Envelope env) throws NoninvertibleTransformException, TransformException {
        if(env == null) return;
        final CoordinateReferenceSystem envCRS = env.getCoordinateReferenceSystem();
        if(envCRS == null) return;
        final CoordinateReferenceSystem envCRS2D = CRSUtilities.getCRS2D(envCRS);
        Envelope env2D = CRS.transform(env, envCRS2D);

        //check that the provided envelope is in the canvas crs
        final CoordinateReferenceSystem canvasCRS2D = getObjectiveCRS2D();
        if(!CRS.equalsIgnoreMetadata(canvasCRS2D,envCRS2D)){
            env2D = CRS.transform(env2D, canvasCRS2D);
        }

        //configure the 2D envelope
        Rectangle2D rect2D = new Rectangle2D.Double(env2D.getMinimum(0), env2D.getMinimum(1), env2D.getSpan(0), env2D.getSpan(1));
        resetTransform(rect2D, true,false);

        
        final CoordinateSystem cs = envCRS.getCoordinateSystem();

        //set the extra xis if some exist
        int index=0;
        final List<CoordinateReferenceSystem> dcrss = ReferencingUtilities.decompose(envCRS);
        for(CoordinateReferenceSystem dcrs : dcrss){
            if(dcrs.getCoordinateSystem().getDimension()==1){
                final CoordinateSystemAxis axis = dcrs.getCoordinateSystem().getAxis(0);
                final AxisFinder finder = new AxisFinder(axis);
                final int cindex = getAxisIndex(finder);
                if(cindex>=0){
                    setAxisRange(env.getMinimum(index), env.getMaximum(index), finder, dcrs);
                }
            }
            index += dcrs.getCoordinateSystem().getDimension();
        }
        
//        for(int i=0, n= cs.getDimension(); i<n;i++){
//            final CoordinateSystemAxis axis = cs.getAxis(i);
//            final AxisDirection ad = axis.getDirection();
//            if(ad.equals(AxisDirection.FUTURE) || ad.equals(AxisDirection.PAST)){
//                //found a temporal axis
//                final double minT = env.getMinimum(i);
//                final double maxT = env.getMaximum(i);
//                setTemporalRange(toDate(minT), toDate(maxT));
//            } else if(ad.equals(AxisDirection.UP) || ad.equals(AxisDirection.DOWN)){
//                //found a vertical axis
//                final double minT = env.getMinimum(i);
//                final double maxT = env.getMaximum(i);
//                //todo should use the axis unit
//                setElevationRange(minT, maxT, SI.METRE);
//            }
//        }
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
    public void setVisibleArea(final Rectangle2D logicalBounds) throws IllegalArgumentException, NoninvertibleTransformException {
        resetTransform(logicalBounds, true,true);
    }

    /**
     * Set the scale, in a ground unit manner, relation between map display size
     * and real ground unit meters;
     * @param scale
     * @throws org.opengis.referencing.operation.TransformException
     */
    public void setGeographicScale(final double scale) throws TransformException {
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
     * @return
     * @throws org.opengis.referencing.operation.TransformException
     * @throws IllegalStateException If the affine transform used for conversion is in
     *                               illegal state.
     */
    public double getGeographicScale() throws TransformException {
        return CanvasUtilities.getGeographicScale(getDisplayCenter(), getObjectiveToDisplay(), getObjectiveCRS2D());
    }

    public void setTemporalRange(final Date startDate, final Date endDate) throws TransformException {
        int index = getTemporalAxisIndex();
        if(index < 0 && (startDate!=null || endDate!=null) ){
            //no temporal axis, add one
            CoordinateReferenceSystem crs = getObjectiveCRS();
            crs = appendCRS(crs, DefaultTemporalCRS.JAVA);
            setObjectiveCRS(crs);
            index = getTemporalAxisIndex();
        }

        if (index >= 0) {

            if(startDate!=null || endDate!=null){
                setRange(index,
                    (startDate!=null)?startDate.getTime():Double.NEGATIVE_INFINITY,
                    (endDate!=null)?endDate.getTime():Double.POSITIVE_INFINITY);
            }else{
                //remove this dimension
                CoordinateReferenceSystem crs = getObjectiveCRS();
                crs = removeCRS(crs, DefaultTemporalCRS.JAVA);
                setObjectiveCRS(crs);
            }

        }
    }

    public Date[] getTemporalRange() {
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

    public void setElevationRange(final Double min, final Double max, final Unit<Length> unit) throws TransformException {
        int index = getElevationAxisIndex();
        if(index < 0 && (min!=null || max!=null)){
            //no elevation axis, add one
            CoordinateReferenceSystem crs = getObjectiveCRS();
            crs = appendCRS(crs, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
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
                crs = removeCRS(crs, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
                setObjectiveCRS(crs);
            }
        }
    }

    public Double[] getElevationRange() {
        final int index = getElevationAxisIndex();
        if (index >= 0) {
            final Envelope envelope = getVisibleEnvelope();
            return new Double[]{envelope.getMinimum(index), envelope.getMaximum(index)};
        }
        return null;
    }

    public Unit<Length> getElevationUnit() {
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
            if (direction == AxisDirection.UP || direction == AxisDirection.DOWN && (unit != null && unit.isCompatible(SI.METRE))) {
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


    public Double[] getAxisRange(final Comparator<CoordinateSystemAxis> comparator) {
        final int index = getAxisIndex(comparator);
        if (index >= 0) {
            final Envelope envelope = getVisibleEnvelope();
            return new Double[]{envelope.getMinimum(index), envelope.getMaximum(index)};
        }
        return null;
    }

    public void setAxisRange(final Double min, final Double max,
            final Comparator<CoordinateSystemAxis> comparator, CoordinateReferenceSystem axisCrs) throws TransformException {
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
    }

    /**
     * Search an axis index.
     * Comparator must return 0 when found.
     *
     * @param comparator
     * @return -1 if not found
     */
    public int getAxisIndex(final Comparator<CoordinateSystemAxis> comparator) {
        final CoordinateReferenceSystem objCrs = getObjectiveCRS();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final CoordinateSystemAxis axi = cs.getAxis(i);
            if(comparator.compare(axi, axi) == 0) return i;
        }
        return -1;
    }

    private CoordinateReferenceSystem appendCRS(final CoordinateReferenceSystem crs, final CoordinateReferenceSystem toAdd){
        if(crs instanceof CompoundCRS){
            final CompoundCRS orig = (CompoundCRS) crs;
            final List<CoordinateReferenceSystem> lst = new ArrayList<>(orig.getComponents());
            lst.add(toAdd);
            return new DefaultCompoundCRS(orig.getName().getCode(), lst.toArray(new CoordinateReferenceSystem[lst.size()]));
        }else{
            return new DefaultCompoundCRS(crs.getName().getCode()+" "+toAdd.getName().getCode(),crs, toAdd);
        }

    }

    private CoordinateReferenceSystem removeCRS(final CoordinateReferenceSystem crs, final CoordinateReferenceSystem toRemove){
        if(crs instanceof CompoundCRS){
            final CompoundCRS orig = (CompoundCRS) crs;
            final List<CoordinateReferenceSystem> lst = new ArrayList<>(orig.getComponents());
            lst.remove(toRemove);
            if(lst.size() == 1){
                return lst.get(0);
            }
            return new DefaultCompoundCRS(orig.getName().getCode(), lst.toArray(new CoordinateReferenceSystem[lst.size()]));
        }else{
            return crs;
        }

    }


    private static Date toDate(final double d){
        if(Double.isNaN(d)){
            return null;
        }else{
            return new Date((long)d);
        }
    }


}
