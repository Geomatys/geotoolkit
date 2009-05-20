/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.isowrapper.geometries;

import java.util.List;
import java.util.Set;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfacePatch;

/**
 *
 * @author sorel
 */
public class ISOJTSSurface extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.Polygon> implements Surface{

    public ISOJTSSurface(com.vividsolutions.jts.geom.Polygon pl) {
        super(pl);
    }

    @Override
    public List<? extends SurfacePatch> getPatches() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrientableSurface[] getProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SurfaceBoundary getBoundary() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Surface getPrimitive() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CompositeSurface getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getOrientation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Primitive> getContainedPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Primitive> getContainingPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Complex> getComplexes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] getUpNormal(DirectPosition point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getPerimeter() {
        return jtsGeometry.getLength();
    }

    @Override
    public double getArea() {
        return jtsGeometry.getArea();
    }

}