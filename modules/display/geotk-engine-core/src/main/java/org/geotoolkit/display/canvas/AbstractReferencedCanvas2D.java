/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.display.canvas;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultDerivedCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.transform.IdentityTransform;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.Classes;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * A canvas implementation with default support for two-dimensional CRS management. This
 * default implementation uses <cite>Java2D</cite> geometry objects like {@link Shape} and
 * {@link AffineTransform}, which are somewhat lightweight objects. There is no dependency
 * toward AWT toolkit in this class (which means that this class can be used as a basis for
 * SWT renderer as well), and this class does not assume a rectangular widget.
 *
 * @module pending
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractReferencedCanvas2D extends AbstractCanvas implements ReferencedCanvas2D{

    /**
     * A set of {@link MathTransform}s from various source CRS. The target CRS must be the
     * {@linkplain #getObjectiveCRS objective CRS} for all entries. Keys are source CRS.
     * This map is used only in order to avoid the costly call to
     * {@link CoordinateOperationFactory#createOperation} as much as possible. If a
     * transformation is not available in this collection, then the usual factory will be used.
     */
    private final transient Map<CoordinateReferenceSystem,MathTransform> transforms =
            new HashMap<CoordinateReferenceSystem,MathTransform>();

    private final Rectangle2D.Double displayBounds = new Rectangle2D.Double(0, 0, 0, 0);
    private final AffineTransform objToDisp = new AffineTransform(1, 0, 0, 1, 0, 0);
    private GeneralEnvelope envelope = new GeneralEnvelope(DefaultGeographicCRS.WGS84);

    /**
     * Proportions support between X and Y axis.
     * if prop = Double.NaN then no correction will be applied
     * if prop = 1 then one unit in X will be equal to one unit in Y
     * else value will mean that prop*Y will be used
     */
    private double proportion = 1d;

    private boolean autoRepaint = false;

    private CoordinateReferenceSystem objectiveCRS2D = DefaultGeographicCRS.WGS84;
    private DerivedCRS displayCRS = null;


    protected AbstractReferencedCanvas2D(CoordinateReferenceSystem crs, Hints hints){
        super(hints);
        try {
            setObjectiveCRS(crs);
        } catch (TransformException ex) {
            throw new IllegalArgumentException("Unvalid CRS",ex);
        }
    }

    public final void repaint(){
        repaint(displayBounds);
    }

    public abstract void repaint(Shape area);

    public abstract Image getSnapShot();

    protected void setDisplayBounds(Rectangle2D rect){
        displayBounds.setRect(rect);
    }

    @Override
    public final synchronized void setObjectiveCRS(CoordinateReferenceSystem objective) throws TransformException {
        if(objective == null){
            throw new NullArgumentException("Objective CRS can not be null.");
        }
        if(CRS.equalsIgnoreMetadata(envelope.getCoordinateReferenceSystem(), objective)){
            return;
        }

        //store the visible area to restore it later
        Envelope preserve = null;
        if(!displayBounds.isEmpty()){
            preserve = new GeneralEnvelope(envelope);
        }

        try {
            resetTransform();
        } catch (NoninvertibleTransformException ex) {
            throw new TransformException("Fail to change objective CRS", ex);
        }

        final CoordinateReferenceSystem oldObjectiveCRS = envelope.getCoordinateReferenceSystem();

        envelope = new GeneralEnvelope(objective);
        objectiveCRS2D = CRSUtilities.getCRS2D(objective);
        propertyListeners.firePropertyChange(OBJECTIVE_CRS_PROPERTY, oldObjectiveCRS, envelope.getCoordinateReferenceSystem());

        if(preserve != null){
            //restore previous visible area
            preserve = CRS.transform(preserve, objectiveCRS2D);
            try {
                getController().setVisibleArea(preserve);
            } catch (NoninvertibleTransformException ex) {
                throw new TransformException("Fail to change objective CRS", ex);
            }
        }

    }

    @Override
    public final synchronized CoordinateReferenceSystem getObjectiveCRS() {
        return envelope.getCoordinateReferenceSystem();
    }

    @Override
    public final synchronized CoordinateReferenceSystem getObjectiveCRS2D() {
        return objectiveCRS2D;
    }

    @Override
    public final synchronized DerivedCRS getDisplayCRS() {
        if(displayCRS == null){
            final CoordinateReferenceSystem objCRS = getObjectiveCRS2D();
            displayCRS = new DefaultDerivedCRS("Derived - "+objCRS.getName().toString(), objCRS, getObjectiveToDisplay(), objCRS.getCoordinateSystem());
        }
        return displayCRS;
    }

    @Override
    public final synchronized AffineTransform2D getObjectiveToDisplay() {
        return new AffineTransform2D(objToDisp);
    }

    @Override
    public final synchronized Rectangle2D getDisplayBounds() {
        return (Rectangle2D) displayBounds.clone();
    }

    private void repaintIfAuto(){
        if(autoRepaint){
            repaint();
        }
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

    //only available in this package by the controller --------------------

    /**
     * @return a copy of the internal canvas visible envelope.
     */
    final Envelope getVisibleEnvelope(){
        return new GeneralEnvelope(envelope);
    }

    /**
     * Change a range in the canvas envelope.
     * Can be used to temporal or elevatio range of the map.
     */
    final void setRange(int ordinate, double min, double max){
        envelope.setRange(ordinate, min, max);
    }

    final void setAutoRepaint(boolean autoRepaint) {
        this.autoRepaint = autoRepaint;
    }

    final boolean isAutoRepaint() {
        return autoRepaint;
    }

    final void setAxisProportions(double prop) {
        this.proportion = prop;
    }

    final double getAxisProportions() {
        return proportion;
    }

    final void applyTransform(AffineTransform change){
        if (!change.isIdentity()) {
            final AffineTransform2D old = getObjectiveToDisplay();

            displayCRS = null; //clear display crs cache
            objToDisp.concatenate(change);
            XAffineTransform.roundIfAlmostInteger(objToDisp, EPS);

            //fire event and repaint
            updateEnvelope();
            propertyListeners.firePropertyChange(OBJECTIVE_TO_DISPLAY_PROPERTY, old, getObjectiveToDisplay());
            repaintIfAuto();

        }
    }

    final void resetTransform() throws NoninvertibleTransformException{
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
    final void resetTransform(final Rectangle2D preferredArea, final boolean yAxisUpward,
                         boolean preserveRotation) throws NoninvertibleTransformException{

        final Rectangle canvasBounds = getDisplayBounds().getBounds();

        if (!canvasBounds.isEmpty()) {
            canvasBounds.x = 0;
            canvasBounds.y = 0;

            if (isValid(preferredArea)) {
                final AffineTransform2D old = getObjectiveToDisplay();

                final double rotation = -XAffineTransform.getRotation(objToDisp);

                if (yAxisUpward) {
                    objToDisp.setToScale(+1, -1);
                }else {
                    objToDisp.setToIdentity();
                }

                final AffineTransform transform = setVisibleArea(preferredArea, canvasBounds);
                displayCRS = null; //clear display crs cache
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

                //fire event and repaint
                updateEnvelope();
                propertyListeners.firePropertyChange(OBJECTIVE_TO_DISPLAY_PROPERTY, old, getObjectiveToDisplay());
                repaintIfAuto();
            }
        }
    }

    //convinient method -----------------------------------------

    /**
     * Checks whether the rectangle {@code rect} is valid.  The rectangle
     * is considered invalid if its length or width is less than or equal to 0,
     * or if one of its coordinates is infinite or NaN.
     */
    private boolean isValid(final Rectangle2D rect) {
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
    private AffineTransform setVisibleArea(Rectangle2D source, Rectangle2D dest)
                                           throws IllegalArgumentException,NoninvertibleTransformException{
        /*
         * Verifies the validity of the source rectangle. An invalid rectangle will be rejected.
         * However, we will be more flexible for dest since the window could have been reduced by
         * the user.
         */
        if (!isValid(source)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.BAD_RECTANGLE_$1, source));
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
            return IdentityTransform.create(sourceCRS.getCoordinateSystem().getDimension());
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
            final LogRecord record = Loggings.getResources(getLocale()).getLogRecord(Level.FINER,
                    Loggings.Keys.INITIALIZING_TRANSFORMATION_$2,
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

}
