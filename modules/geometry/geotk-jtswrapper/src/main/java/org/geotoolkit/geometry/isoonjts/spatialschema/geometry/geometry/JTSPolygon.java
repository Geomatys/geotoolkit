/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry;

import java.util.List;

import java.util.Set;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfacePatch;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.util.Utilities;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfaceInterpolation;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.Primitive;

@XmlType(name="PolygonType", namespace="http://www.opengis.net/gml")
public class JTSPolygon extends JTSSurfacePatch implements Polygon, Primitive {
    
    //*************************************************************************
    //  Fields
    //*************************************************************************
    
    // Why the hell is this a list???
    private List spanningSurface;

    //*************************************************************************
    //  Constructors
    //*************************************************************************

    public JTSPolygon() {
        this(null, null);
    }
    
    public JTSPolygon(final SurfaceBoundary boundary) {
        // We only support planar polygons
        this(boundary, null);
    }

    public JTSPolygon(final SurfaceBoundary boundary, final List spanningSurface) {
        super(SurfaceInterpolation.PLANAR, boundary);
        this.spanningSurface = spanningSurface;
    }

    //*************************************************************************
    //  implement the *** interface
    //*************************************************************************
    
    @Override
    public int getNumDerivativesOnBoundary() {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public com.vividsolutions.jts.geom.Geometry computeJTSPeer() {
        SurfaceBoundary boundary = getBoundary();
        Ring exterior = boundary.getExterior();
        List interiors = boundary.getInteriors();
        com.vividsolutions.jts.geom.Geometry g = ((JTSGeometry) exterior).getJTSGeometry();
        int numHoles = (interiors != null) ? interiors.size() : 0;
        com.vividsolutions.jts.geom.LinearRing jtsExterior = JTSUtils.GEOMETRY_FACTORY.createLinearRing(g.getCoordinates());
        com.vividsolutions.jts.geom.LinearRing [] jtsInterior = new com.vividsolutions.jts.geom.LinearRing[numHoles];
        for (int i=0; i<numHoles; i++) {
            com.vividsolutions.jts.geom.Geometry g2 =
                ((JTSGeometry) interiors.get(i)).getJTSGeometry();
            jtsInterior[i] = JTSUtils.GEOMETRY_FACTORY.createLinearRing(g2.getCoordinates());
        }
        com.vividsolutions.jts.geom.Polygon result =
            JTSUtils.GEOMETRY_FACTORY.createPolygon(jtsExterior, jtsInterior);
        CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        if (crs != null) {
            final int srid = SRIDGenerator.toSRID(crs, Version.V1);
            result.setSRID(srid);
        }
        return result;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PolyhedralSurface getSurface() {
        return (PolyhedralSurface) super.getSurface();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List getSpanningSurface() {
        // Why the hell is this a list???
        return spanningSurface;
    }
    
    public boolean isValid() {
    	com.vividsolutions.jts.geom.Polygon poly = (com.vividsolutions.jts.geom.Polygon)
			this.getJTSGeometry();
    	return poly.isValid();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this)
            return true;

        if (object instanceof JTSPolygon && super.equals(object)) {
            JTSPolygon that = (JTSPolygon) object;
            return Utilities.equals(this.spanningSurface, that.spanningSurface);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + (this.spanningSurface != null ? this.spanningSurface.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (spanningSurface != null) {
            sb.append("spanningSurface:").append('\n');
            for (Object o : spanningSurface) {
                sb.append(o).append('\n');
            }
        }
        return sb.toString();
    }

    public Set<Primitive> getContainedPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Primitive> getContainingPrimitives() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Complex> getComplexes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Composite getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OrientablePrimitive[] getProxy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
