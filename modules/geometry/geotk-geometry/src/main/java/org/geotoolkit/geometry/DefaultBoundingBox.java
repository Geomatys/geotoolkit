

package org.geotoolkit.geometry;

import org.geotoolkit.referencing.CRS;

import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultBoundingBox extends GeneralEnvelope implements BoundingBox,Envelope{

    public DefaultBoundingBox(CoordinateReferenceSystem crs){
        super(crs);
    }

    public DefaultBoundingBox(Envelope env){
        super(env);
    }

    public DefaultBoundingBox(BoundingBox bounds, CoordinateReferenceSystem crs){
        super(crs);
        setBounds(bounds);
    }

    @Override
    public void setBounds(BoundingBox bounds) {
        for(int dim=0;dim<bounds.getDimension();dim++){
            setRange(dim, bounds.getMinimum(dim),bounds.getMaximum(dim));
        }
    }

    @Override
    public double getMinX() {
        return getMinimum(0);
    }

    @Override
    public double getMaxX() {
        return getMaximum(0);
    }

    @Override
    public double getMinY() {
        return getMinimum(1);
    }

    @Override
    public double getMaxY() {
        return getMaximum(1);
    }

    @Override
    public double getWidth() {
        return getSpan(0);
    }

    @Override
    public double getHeight() {
        return getSpan(1);
    }

    @Override
    public void include(BoundingBox bounds) {
        add(bounds);
    }

    @Override
    public void include(double x, double y) {
        add(new DirectPosition2D(getCoordinateReferenceSystem(), x, y));
    }

    @Override
    public boolean intersects(BoundingBox bounds) {
        return intersects(bounds, true);
    }

    @Override
    public boolean contains(BoundingBox bounds) {
        return contains(bounds, true);
    }

    @Override
    public boolean contains(double x, double y) {
        return contains(new DirectPosition2D(this.getCoordinateReferenceSystem(), x, y));
    }

    @Override
    public BoundingBox toBounds(CoordinateReferenceSystem targetCRS) throws TransformException {
        return new DefaultBoundingBox(CRS.getEnvelope(targetCRS));
    }

}
