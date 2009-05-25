/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISOJTSSurface extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.Polygon> implements Surface{

    public ISOJTSSurface(com.vividsolutions.jts.geom.Polygon pl, CoordinateReferenceSystem crs) {
        super(pl,crs);
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