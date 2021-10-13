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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.complex.CompositeSurface;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * The {@code PolyhedralSurfaceImpl} class/interface...
 *
 * @author SYS Technologies
 * @author dillard
 * @version $Revision $
 * @module
 */
@XmlType(name="PolyHedralSurfaceType", namespace="http://www.opengis.net/gml")
public class JTSPolyhedralSurface extends AbstractJTSGeometry implements PolyhedralSurface {
    private static final long serialVersionUID = 4153619785904408034L;

    @XmlElement(name = "polygonPatches", namespace = "http://www.opengis.net/gml")
    protected final List<JTSPolygon> patches = new ArrayList();

    public JTSPolyhedralSurface() {
        this(null);
    }

    /**
     * Creates a new {@code PolyhedralSurfaceImpl}.
     * @param crs
     */
    public JTSPolyhedralSurface(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SurfaceBoundary getBoundary() {
        return (SurfaceBoundary) super.getBoundary();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<JTSPolygon> getPatches() {
        return patches;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double [] getUpNormal(final DirectPosition point) {
        return new double [] { 0, 0, 1 };
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getPerimeter() {
        return getJTSGeometry().getBoundary().getLength();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getArea() {
        return getJTSGeometry().getArea();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CompositeSurface getComposite() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getOrientation() {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Surface getPrimitive() {
        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set getComplexes() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set getContainingPrimitives() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrientableSurface[] getProxy() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set getContainedPrimitives() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected org.locationtech.jts.geom.Geometry computeJTSPeer() {
        if (patches.size() > 1) {
            //throw new UnsupportedOperationException("This implementation does not support surfaces with multiple patches.");
            final org.locationtech.jts.geom.Polygon[] polygons =
                new org.locationtech.jts.geom.Polygon[patches.size()];
            for (int i = 0; i < patches.size(); i++) {
                final JTSGeometry jtsGeometry = (JTSGeometry) patches.get(i);
                polygons[i] = (org.locationtech.jts.geom.Polygon) jtsGeometry.getJTSGeometry();
            }
            return JTSUtils.GEOMETRY_FACTORY.createMultiPolygon(polygons);
        }
        return ((JTSGeometry) patches.get(0)).getJTSGeometry();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSPolyhedralSurface clone() {
        return (JTSPolyhedralSurface) super.clone();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this)
            return true;

        if (object instanceof JTSPolyhedralSurface && super.equals(object)) {
            JTSPolyhedralSurface that = (JTSPolyhedralSurface) object;
            return Objects.equals(this.patches, that.patches);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 67 * hash + (this.patches != null ? this.patches.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (patches != null) {
            for (JTSPolygon patche : patches) {
                sb.append("patche:").append(patche).append('\n');
            }
        }
        return sb.toString();
    }
}
