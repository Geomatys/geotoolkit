/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultCanvasController2D extends AbstractCanvasController implements CanvasController2D {

    private static final double DEFAULT_DPI = 90d;

    protected final AbstractReferencedCanvas2D canvas;

    public DefaultCanvasController2D(final AbstractReferencedCanvas2D canvas){
        this.canvas = canvas;
    }

    @Override
    public void repaint() {
        canvas.repaint();
    }

    @Override
    public void setAutoRepaint(final boolean auto) {
        canvas.setAutoRepaint(auto);
    }

    @Override
    public boolean isAutoRepaint() {
        return canvas.isAutoRepaint();
    }

    @Override
    public void setCenter(final DirectPosition center) {
        try {
            final DirectPosition oldCenter = getCenter();
            final double diffX = center.getOrdinate(0) - oldCenter.getOrdinate(0);
            final double diffY = center.getOrdinate(1) - oldCenter.getOrdinate(1);
            translateObjective(diffX, diffY);
        } catch (NoninvertibleTransformException ex) {
            throw new IllegalStateException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void setObjectiveCRS(final CoordinateReferenceSystem crs) throws TransformException {
       canvas.setObjectiveCRS(crs);
    }

    @Override
    public void reset() throws NoninvertibleTransformException {
        canvas.resetTransform();
    }

    @Override
    public Point2D getDisplayCenter() {
        final Rectangle2D rect = canvas.getDisplayBounds();
        return new Point2D.Double(rect.getCenterX(), rect.getCenterY());
    }

    @Override
    public DirectPosition getCenter() throws NoninvertibleTransformException {
        final Point2D center = getDisplayCenter();
        canvas.getObjectiveToDisplay().inverseTransform(center, center);
        return new GeneralDirectPosition(center);
    }

    @Override
    public Envelope getVisibleEnvelope() {
        return canvas.getVisibleEnvelope();
    }

    @Override
    public Envelope getVisibleEnvelope2D() throws TransformException {
        final CoordinateReferenceSystem objectiveCRS2D = canvas.getObjectiveCRS2D();
        return CRS.transform(getVisibleEnvelope(), objectiveCRS2D);
    }

    @Override
    public void setAxisProportions(final double prop) {
        canvas.setAxisProportions(prop);
    }

    @Override
    public double getAxisProportions() {
        return canvas.getAxisProportions();
    }

    @Override
    public AffineTransform2D getTransform() {
        return canvas.getObjectiveToDisplay();
    }

    @Override
    public void rotate(final double r) throws NoninvertibleTransformException {
        rotate(r, getDisplayCenter());
    }

    @Override
    public void rotate(final double r, final Point2D center) throws NoninvertibleTransformException {
        final AffineTransform2D objToDisp = canvas.getObjectiveToDisplay();
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
        transform(change);
    }

    @Override
    public void scale(final double s) throws NoninvertibleTransformException {
        scale(s, getDisplayCenter());
    }

    @Override
    public void scale(final double s, final Point2D center) throws NoninvertibleTransformException {
        final AffineTransform2D objToDisp = canvas.getObjectiveToDisplay();
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
        transform(change);
    }

    @Override
    public void translateDisplay(final double x, final double y) throws NoninvertibleTransformException {
        final AffineTransform2D objToDisp = canvas.getObjectiveToDisplay();
        final AffineTransform change = objToDisp.createInverse();
        change.translate(x, y);
        change.concatenate(objToDisp);
        XAffineTransform.roundIfAlmostInteger(change, EPS);
        transform(change);
    }

    @Override
    public void translateObjective(final double x, final double y) throws NoninvertibleTransformException {
        final Point2D dispCenter = getDisplayCenter();
        final DirectPosition center = getCenter();
        Point2D objCenter = new Point2D.Double(center.getOrdinate(0) + x, center.getOrdinate(1) + y);
        objCenter = canvas.getObjectiveToDisplay().transform(objCenter, objCenter);
        translateDisplay(dispCenter.getX() - objCenter.getX(), dispCenter.getY() - objCenter.getY());
    }

    @Override
    public void transform(final AffineTransform change) {
        canvas.applyTransform(change);
    }

    @Override
    public void transformPixels(final AffineTransform change) {        
        if (!change.isIdentity()) {
            final AffineTransform2D objToDisp = canvas.getObjectiveToDisplay();
            final AffineTransform logical;
            try {
                logical = objToDisp.createInverse();
            } catch (NoninvertibleTransformException exception) {
                throw new IllegalStateException(exception);
            }
            logical.concatenate(change);
            logical.concatenate(objToDisp);
            XAffineTransform.roundIfAlmostInteger(logical, EPS);
            transform(logical);
        }
    }

    @Override
    public void setRotation(final double r) throws NoninvertibleTransformException {
        double rotation = getRotation();
        rotate(rotation - r);
    }

    @Override
    public double getRotation() {
        return -XAffineTransform.getRotation(canvas.getObjectiveToDisplay());
    }

    @Override
    public void setScale(final double newScale) throws NoninvertibleTransformException {
        final double oldScale = XAffineTransform.getScale(canvas.getObjectiveToDisplay());
        scale(newScale / oldScale);
    }

    @Override
    public double getScale() {
        return XAffineTransform.getScale(canvas.getObjectiveToDisplay());
    }

    @Override
    public void setDisplayVisibleArea(final Rectangle2D dipsEnv) {
        try {
            Shape shp = canvas.getObjectiveToDisplay().createInverse().createTransformedShape(dipsEnv);
            setVisibleArea(shp.getBounds2D());
        } catch (NoninvertibleTransformException ex) {
            canvas.getLogger().log(Level.WARNING, null, ex);
        }
    }

    @Override
    public void setVisibleArea(final Envelope env) throws NoninvertibleTransformException, TransformException {
        final CoordinateReferenceSystem envCRS = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem envCRS2D = CRSUtilities.getCRS2D(envCRS);
        Envelope env2D = CRS.transform(env, envCRS2D);

        //check that the provided envelope is in the canvas crs
        final CoordinateReferenceSystem canvasCRS2D = canvas.getObjectiveCRS2D();
        if(!CRS.equalsIgnoreMetadata(canvasCRS2D,envCRS2D)){
            env2D = CRS.transform(env2D, canvasCRS2D);
        }

        //configure the 2D envelope
        Rectangle2D rect2D = new Rectangle2D.Double(env2D.getMinimum(0), env2D.getMinimum(1), env2D.getSpan(0), env2D.getSpan(1));
        canvas.resetTransform(rect2D, true,false);

        //set the temporal and elevation if some
        final CoordinateSystem cs = envCRS.getCoordinateSystem();

        for(int i=0, n= cs.getDimension(); i<n;i++){
            final CoordinateSystemAxis axis = cs.getAxis(i);
            final AxisDirection ad = axis.getDirection();
            if(ad.equals(AxisDirection.FUTURE) || ad.equals(AxisDirection.PAST)){
                //found a temporal axis
                final double minT = env.getMinimum(i);
                final double maxT = env.getMaximum(i);
                setTemporalRange(toDate(minT), toDate(maxT));
            } else if(ad.equals(AxisDirection.UP) || ad.equals(AxisDirection.DOWN)){
                //found a vertical axis
                final double minT = env.getMinimum(i);
                final double maxT = env.getMaximum(i);
                //todo should use the axis unit
                setElevationRange(minT, maxT, SI.METRE);
            }
        }
    }

    @Override
    public void setVisibleArea(final Rectangle2D logicalBounds) throws IllegalArgumentException, NoninvertibleTransformException {
        canvas.resetTransform(logicalBounds, true,true);
    }

    @Override
    public void setGeographicScale(final double scale) throws TransformException {
        double currentScale = getGeographicScale();
        double factor = currentScale / scale;
        try {
            scale(factor);
        } catch (NoninvertibleTransformException ex) {
            canvas.getLogger().log(Level.WARNING, null, ex);
        }
    }

    @Override
    public double getGeographicScale() throws TransformException {
        final Point2D center = getDisplayCenter();
        final double[] P1 = new double[]{center.getX(), center.getY()};
        final double[] P2 = new double[]{P1[0], P1[1] + 1};

        final AffineTransform trs;
        try {
            trs = canvas.getObjectiveToDisplay().createInverse();
        } catch (NoninvertibleTransformException ex) {
            throw new TransformException(ex.getLocalizedMessage(), ex);
        }
        trs.transform(P1, 0, P1, 0, 1);
        trs.transform(P2, 0, P2, 0, 1);

        final CoordinateReferenceSystem crs = canvas.getObjectiveCRS2D();
        final Unit unit = crs.getCoordinateSystem().getAxis(0).getUnit();

        final double distance;
        if (unit.isCompatible(SI.METRE)) {
            final Point2D p1 = new Point2D.Double(P1[0], P1[1]);
            final Point2D p2 = new Point2D.Double(P2[0], P2[1]);
            final UnitConverter conv = unit.getConverterTo(SI.METRE);
            distance = conv.convert(p1.distance(p2));
        } else {
            /*
             * If the latitude ordinates (for example) are outside the +/-90°
             * range, translate the points in order to bring them back in the
             * domain of validity.
             */
            final CoordinateSystem cs = crs.getCoordinateSystem();
            for (int i = cs.getDimension(); --i >= 0;) {
                final CoordinateSystemAxis axis = cs.getAxis(i);
                double delta = P1[i] - axis.getMaximumValue();
                if (delta > 0) {
                    P1[i] -= delta;
                    P2[i] -= delta;
                }
                delta = P2[i] - axis.getMaximumValue();
                if (delta > 0) {
                    P1[i] -= delta;
                    P2[i] -= delta;
                }
                delta = axis.getMinimumValue() - P1[i];
                if (delta > 0) {
                    P1[i] += delta;
                    P2[i] += delta;
                }
                delta = axis.getMinimumValue() - P2[i];
                if (delta > 0) {
                    P1[i] += delta;
                    P2[i] += delta;
                }
            }
            final GeodeticCalculator gc = new GeodeticCalculator(crs);
            final GeneralDirectPosition pos1 = new GeneralDirectPosition(crs);
            pos1.setOrdinate(0, P1[0]);
            pos1.setOrdinate(1, P1[1]);
            final GeneralDirectPosition pos2 = new GeneralDirectPosition(crs);
            pos2.setOrdinate(0, P2[0]);
            pos2.setOrdinate(1, P2[1]);
            try {
                gc.setStartingPosition(pos1);
                gc.setDestinationPosition(pos2);
            } catch (TransformException ex) {
                throw new TransformException(ex.getLocalizedMessage(), ex);
            } catch (IllegalArgumentException ex) {
                //might happen when changing projection and moving the area.
                //the coordinate can be out of the crs area, which causes this exception
                throw new TransformException(ex.getLocalizedMessage(), ex);
            }
            distance = Math.abs(gc.getOrthodromicDistance());
        }

        final double displayToDevice = 1f / DEFAULT_DPI * 0.0254f;
        return distance / displayToDevice;
    }

    @Override
    public void setTemporalRange(final Date startDate, final Date endDate) throws TransformException {
        int index = getTemporalAxisIndex();
        if(index < 0){
            //no temporal axis, add one
            CoordinateReferenceSystem crs = canvas.getObjectiveCRS();
            crs = appendCRS(crs, DefaultTemporalCRS.JAVA);
            setObjectiveCRS(crs);
            index = getTemporalAxisIndex();
        }

        if (index >= 0) {
            canvas.setRange(index, startDate.getTime(), endDate.getTime());
        }
    }

    @Override
    public Date[] getTemporalRange() {
        final int index = getTemporalAxisIndex();
        if (index >= 0) {
            final Envelope envelope = canvas.getVisibleEnvelope();
            final Date[] range = new Date[2];
            range[0] = new Date((long) envelope.getMinimum(index));
            range[1] = new Date((long) envelope.getMaximum(index));
            return range;
        }
        return null;
    }

    @Override
    public void setElevationRange(final Double min, final Double max, final Unit<Length> unit) throws TransformException {
        int index = getElevationAxisIndex();
        if(index < 0){
            //no elevation axis, add one
            CoordinateReferenceSystem crs = canvas.getObjectiveCRS();
            crs = appendCRS(crs, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
            setObjectiveCRS(crs);
            index = getElevationAxisIndex();
        }

        if (index >= 0) {
            canvas.setRange(index, min, max);
        }
    }

    @Override
    public Double[] getElevationRange() {
        final int index = getElevationAxisIndex();
        if (index >= 0) {
            final Envelope envelope = canvas.getVisibleEnvelope();
            return new Double[]{envelope.getMinimum(index), envelope.getMaximum(index)};
        }
        return null;
    }

    @Override
    public Unit<Length> getElevationUnit() {
        final int index = getElevationAxisIndex();
        if (index >= 0) {
            return (Unit<Length>) canvas.getObjectiveCRS().getCoordinateSystem().getAxis(index).getUnit();
        }
        return null;
    }

    //convinient methods -------------------------------------------------

    /**
     * Find the elevation axis index or -1 if there is none.
     */
    private int getElevationAxisIndex() {
        final CoordinateReferenceSystem objCrs = canvas.getObjectiveCRS();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final AxisDirection direction = cs.getAxis(i).getDirection();
            if (direction == AxisDirection.UP || direction == AxisDirection.DOWN) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the temporal axis index or -1 if there is none.
     */
    private int getTemporalAxisIndex() {
        final CoordinateReferenceSystem objCrs = canvas.getObjectiveCRS();
        final CoordinateSystem cs = objCrs.getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final AxisDirection direction = cs.getAxis(i).getDirection();
            if (direction == AxisDirection.FUTURE || direction == AxisDirection.PAST) {
                return i;
            }
        }
        return -1;
    }

    private CoordinateReferenceSystem appendCRS(final CoordinateReferenceSystem crs, final CoordinateReferenceSystem toAdd){
        if(crs instanceof CompoundCRS){
            final CompoundCRS orig = (CompoundCRS) crs;
            final List<CoordinateReferenceSystem> lst = new ArrayList<CoordinateReferenceSystem>(orig.getComponents());
            lst.add(toAdd);
            return new DefaultCompoundCRS(orig.getName().getCode(), lst.toArray(new CoordinateReferenceSystem[lst.size()]));
        }else{
            return new DefaultCompoundCRS(crs.getName().getCode()+" "+toAdd.getName().getCode(),crs, toAdd);
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
