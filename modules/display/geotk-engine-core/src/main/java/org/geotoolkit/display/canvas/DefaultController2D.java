/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultController2D implements CanvasController2D{

    private static final Logger LOGGER = Logging.getLogger(DefaultController2D.class);

    /**
     * Small number for floating point comparaisons.
     */
    private static final double EPS = 1E-12;

    private final ReferencedCanvas2D canvas;

    private double proportion = 1d;
    
    private boolean autoRepaint = false;

    private final Date[] dateRange = new Date[2];
    private final Double[] elevationRange = new Double[2];
    private Unit<Length> elevationUnit = SI.METRE;
    
    public DefaultController2D(ReferencedCanvas2D canvas){
        this.canvas = canvas;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTitle(InternationalString title) {
        canvas.setTitle(title);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setObjectiveCRS(CoordinateReferenceSystem crs) throws TransformException {
        canvas.setObjectiveCRS(crs);
    }

    /**
     * Declares that the {@link Component} need to be repainted. This method can be invoked
     * from any thread (it doesn't need to be the <cite>Swing</cite> thread). Note that this
     * method doesn't invoke any {@link #flushOffscreenBuffer} method; this is up to the caller
     * to invokes the appropriate method.
     */
    @Override
    public void repaint(){
        canvas.repaint();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset() throws NoninvertibleTransformException{
        reset(canvas.getContainer().getGraphicsEnvelope2D(), canvas.getDisplayBounds().getBounds(), true,false);
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
    protected final void reset(final Rectangle2D preferredArea, final Rectangle canvasBounds, 
            final boolean yAxisUpward, boolean preserveRotation) throws NoninvertibleTransformException{
        canvas.getMonitor().stopRendering();
        if (!canvasBounds.isEmpty()) {
            canvasBounds.x = 0;
            canvasBounds.y = 0;

            final double rotation = getRotation();

            if (isValid(preferredArea)) {
                final AffineTransform change = canvas.objectiveToDisplay.createInverse();
                
                if (yAxisUpward) {
                    canvas.objectiveToDisplay.setToScale(+1, -1);
                }else {
                    canvas.objectiveToDisplay.setToIdentity();
                }

                final AffineTransform transform = setVisibleArea(preferredArea, canvasBounds);
                change.concatenate(canvas.objectiveToDisplay);
                canvas.objectiveToDisplay.concatenate(transform);
                change.concatenate(transform);

                if(preserveRotation){
                    if(autoRepaint){
                        autoRepaint = false;
                        rotate(-rotation);
                        autoRepaint = true;
                    }else{
                        rotate(-rotation);
                    }
                }

                if (!change.isIdentity() && autoRepaint) {
                    repaint();
                }

            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Point2D getDisplayCenter(){
        Rectangle bounds = canvas.getDisplayBounds().getBounds();
        bounds.x = 0;
        bounds.y = 0;
        Point2D center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
        return center;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DirectPosition getCenter(){
        Point2D center = getDisplayCenter();
        try {
            center = canvas.objectiveToDisplay.inverseTransform(center, center);
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
            //TODO : propager l'exception
        }
        return new GeneralDirectPosition(center);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setCenter(DirectPosition center)  {
        DirectPosition oldCenter = getCenter();
        double diffX = center.getOrdinate(0) - oldCenter.getOrdinate(0);
        double diffY = center.getOrdinate(1) - oldCenter.getOrdinate(1);
        try {
            translateObjective(diffX, diffY);
        } catch (NoninvertibleTransformException ex) {
            //TODO should add the throw error in geoapi
            LOGGER.log(Level.SEVERE, null, ex);
        }
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
        dest = XAffineTransform.inverseTransform(canvas.objectiveToDisplay, dest, null);
        
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
        XAffineTransform.round(change, EPS);
        return change;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AffineMatrix3 getTransform(){
        return canvas.objectiveToDisplay;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAutoRepaint(final boolean auto) {
        this.autoRepaint = auto;
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isAutoRepaint() {
        return autoRepaint;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setAxisProportions(double prop){
        this.proportion = prop;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getAxisProportions() {
        return proportion;
    }
    
    // Relative position operations --------------------------------------------
    /**
     * {@inheritDoc }
     */
    @Override
    public void rotate(double r) throws NoninvertibleTransformException{
        rotate(r, getDisplayCenter());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void rotate(double r, Point2D center) throws NoninvertibleTransformException{
        final AffineTransform change = canvas.objectiveToDisplay.createInverse();
        
        if (center != null) {
            final double centerX = center.getX();
            final double centerY = center.getY();

            change.translate(+centerX, +centerY);
            change.rotate(-r);
            change.translate(-centerX, -centerY);
        }

        change.concatenate(canvas.objectiveToDisplay);
        XAffineTransform.round(change, EPS);
        transform(change);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void scale(double s) throws NoninvertibleTransformException{
        scale(s, getDisplayCenter());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void scale(double s, Point2D center) throws NoninvertibleTransformException{
        
        final AffineTransform change = canvas.objectiveToDisplay.createInverse();
        

        if (center != null) {
            final double centerX = center.getX();
            final double centerY = center.getY();

            change.translate(+centerX, +centerY);
            change.scale(s,s);
            change.translate(-centerX, -centerY);
        }

        change.concatenate(canvas.objectiveToDisplay);
        XAffineTransform.round(change, EPS);
        transform(change);

    }   
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void translateDisplay(double x, double y) throws NoninvertibleTransformException{
        final AffineTransform change = canvas.objectiveToDisplay.createInverse();
        

        change.translate(x,y);

        change.concatenate(canvas.objectiveToDisplay);
        XAffineTransform.round(change, EPS);
        transform(change);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void translateObjective(double x, double y) throws NoninvertibleTransformException{
        Point2D dispCenter = getDisplayCenter();
        DirectPosition center = getCenter();
        Point2D objCenter = new Point2D.Double(center.getOrdinate(0) + x, center.getOrdinate(1) + y);
        objCenter = canvas.objectiveToDisplay.transform(objCenter,objCenter);

        translateDisplay(dispCenter.getX() - objCenter.getX(), dispCenter.getY() - objCenter.getY());
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void transform(AffineTransform change){
        canvas.getMonitor().stopRendering();

        if (!change.isIdentity()) {
            canvas.objectiveToDisplay.concatenate(change);
            XAffineTransform.round(canvas.objectiveToDisplay, EPS);
            if(autoRepaint)repaint();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void transformPixels(final AffineTransform change){
        if (!change.isIdentity()) {
            final AffineTransform logical;
            try {
                logical = canvas.objectiveToDisplay.createInverse();
            } catch (NoninvertibleTransformException exception) {
                throw new IllegalStateException(exception);
            }
            logical.concatenate(change);
            logical.concatenate(canvas.objectiveToDisplay);
            XAffineTransform.round(logical, EPS);
            transform(logical);
        }
    }
    
    
    // Absolute position operations --------------------------------------------
    /**
     * {@inheritDoc }
     */
    @Override
    public void setRotation(double r) throws NoninvertibleTransformException{
        double rotation = getRotation();
        rotate(rotation-r);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getRotation(){
        return -XAffineTransform.getRotation(canvas.objectiveToDisplay);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setScale(double newScale) throws NoninvertibleTransformException{
        double oldScale = XAffineTransform.getScale(canvas.objectiveToDisplay);
        double diff = newScale/oldScale;
        scale(diff);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getScale() {
        return XAffineTransform.getScale(canvas.objectiveToDisplay);

        //TODO : which one to keep
//        final double m00 = objectiveToDisplay.getScaleX();
//        final double m11 = objectiveToDisplay.getScaleY();
//        final double m01 = objectiveToDisplay.getShearX();
//        final double m10 = objectiveToDisplay.getShearY();
//        return Math.sqrt(m00 * m00 + m11 * m11 + m01 * m01 + m10 * m10);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDisplayVisibleArea(Rectangle2D dipsEnv){
        try {
            Shape shp = getTransform().createInverse().createTransformedShape(dipsEnv);
            setVisibleArea(shp.getBounds2D());
        } catch (NoninvertibleTransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setVisibleArea(Envelope env) throws NoninvertibleTransformException{
        Rectangle2D rect2D = new Rectangle2D.Double(env.getMinimum(0), env.getMinimum(1), env.getSpan(0), env.getSpan(1));
        reset(rect2D, canvas.getDisplayBounds().getBounds(), true,false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setVisibleArea(final Rectangle2D logicalBounds) throws IllegalArgumentException,NoninvertibleTransformException {
        reset(logicalBounds, canvas.getDisplayBounds().getBounds(), true,true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTemporalRange(Date startDate, Date endDate) {
        Date[] old = this.dateRange;

        if(startDate == null){
            this.dateRange[0] = null;
        }else{
            this.dateRange[0] = new Date(startDate.getTime());
        }

        if(endDate == null){
            this.dateRange[1] = null;
        }else{
            this.dateRange[1] = new Date(endDate.getTime());
        }

        if(autoRepaint){
            repaint();
        }

        canvas.getPropertyListeners().firePropertyChange(TEMPORAL_PROPERTY, old.clone(), this.dateRange.clone());

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Date[] getTemporalRange() {
        Date[] range = new Date[2];

        if(this.dateRange[0] == null){
            range[0] = null;
        }else{
            range[0] = new Date(this.dateRange[0].getTime());
        }

        if(this.dateRange[1] == null){
            range[1] = null;
        }else{
            range[1] = new Date(this.dateRange[1].getTime());
        }

        return range;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setElevationRange(Double min, Double max, Unit<Length> unit) {
        this.elevationRange[0] = min;
        this.elevationRange[1] = max;
        this.elevationUnit = unit;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Double[] getElevationRange() {
        return elevationRange.clone();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Unit<Length> getElevationUnit() {
        return elevationUnit;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setGeographicScale(double scale){
        double currentScale = getGeographicScale();
        double factor = currentScale/scale;
        try {
            scale(factor);
        } catch (NoninvertibleTransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getGeographicScale(){
        final Point2D center = getDisplayCenter();
        final double[] P1 = new double[]{center.getX(),center.getY()};
        final double[] P2 = new double[]{P1[0],P1[1]+1};

        try {
            AffineTransform trs = canvas.objectiveToDisplay.createInverse();
            trs.transform(P1, 0, P1, 0, 1);
            trs.transform(P2, 0, P2, 0, 1);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        final CoordinateReferenceSystem crs = canvas.getObjectiveCRS();
        final Unit unit = crs.getCoordinateSystem().getAxis(0).getUnit();

        final double distance;
        if(unit.isCompatible(SI.METRE)){
            final Point2D p1 = new Point2D.Double(P1[0], P1[1]);
            final Point2D p2 = new Point2D.Double(P2[0], P2[1]);
            final UnitConverter conv = unit.getConverterTo(SI.METRE);
            distance = conv.convert(p1.distance(p2));
        }else{
            try{
                final GeodeticCalculator gc = new GeodeticCalculator(crs);
                final GeneralDirectPosition pos1 = new GeneralDirectPosition(crs);
                pos1.setOrdinate(0, P1[0]);
                pos1.setOrdinate(1, P1[1]);
                final GeneralDirectPosition pos2 = new GeneralDirectPosition(crs);
                pos2.setOrdinate(0, P2[0]);
                pos2.setOrdinate(1, P2[1]);
                gc.setStartingPosition(pos1);
                gc.setDestinationPosition(pos2);
                distance = Math.abs(gc.getOrthodromicDistance());
            }catch(Exception ex){
                LOGGER.log(Level.WARNING, "Current mpa bounds is out of geodetic calculation, will return scale 1.0");
                return 1;
            }
        }

        final double displayToDevice = 1f / 72f * 0.0254f;
        return distance / displayToDevice;
    }

    // Convinient methods ------------------------------------------------------
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

}
