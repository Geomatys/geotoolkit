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

import java.util.Set;
import org.geotoolkit.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.UnmodifiableGeometryException;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.PrimitiveBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISOJTSPoint extends AbstractISOJTSGeometry<com.vividsolutions.jts.geom.Point> implements Point{

    public ISOJTSPoint(com.vividsolutions.jts.geom.Point point, CoordinateReferenceSystem crs) {
        super(point,crs);
    }

    @Override
    public DirectPosition getDirectPosition() {
        return new DirectPosition2D(jtsGeometry.getX(), jtsGeometry.getY());
    }

    @Override
    public void setDirectPosition(DirectPosition position) throws UnmodifiableGeometryException {
        throw new UnmodifiableGeometryException("geometry is immutable");
    }

    @Override
    public void setPosition(DirectPosition position) throws UnmodifiableGeometryException {
        throw new UnmodifiableGeometryException("geometry is immutable");
    }

    @Override
    public PrimitiveBoundary getBoundary() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Bearing getBearing(Position toPoint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrientablePrimitive[] getProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Primitive> getContainedPrimitives() {
        return EMPTY_PRIMITIVE_SET;
    }

    @Override
    public Set<Primitive> getContainingPrimitives() {
        return EMPTY_PRIMITIVE_SET;
    }

    @Override
    public Set<Complex> getComplexes() {
        return EMPTY_COMPLEXE_SET;
    }

    @Override
    public Composite getComposite() {
        return null;
    }

    @Override
    public DirectPosition getPosition() {
        return getDirectPosition();
    }


}
