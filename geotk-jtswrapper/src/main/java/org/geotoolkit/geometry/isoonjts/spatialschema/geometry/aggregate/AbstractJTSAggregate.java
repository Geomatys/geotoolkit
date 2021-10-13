/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/aggregate/AggregateImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import java.util.*;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;

import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.aggregate.Aggregate;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractJTSAggregate<T extends Geometry> extends AbstractJTSGeometry implements Aggregate {

    private Set<T> elements = new LinkedHashSet();

    public AbstractJTSAggregate() {
        super();
    }

    public AbstractJTSAggregate(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected org.locationtech.jts.geom.Geometry computeJTSPeer() {
        final List<org.locationtech.jts.geom.Geometry> childParts = new ArrayList<org.locationtech.jts.geom.Geometry>();
        for(Geometry prim : elements) {
            if(prim instanceof JTSGeometry){
                final JTSGeometry jtsGeom = (JTSGeometry) prim;
                final org.locationtech.jts.geom.Geometry geom = jtsGeom.getJTSGeometry();
                if (geom != null) {
                    childParts.add(geom);
                }
            }else{
                throw new IllegalStateException("Only JTSGeometries are allowed in the JTSAggregate class.");
            }
        }

        org.locationtech.jts.geom.Geometry result = null;
        // we want a multi geometry event if there is only one geometry
        if (childParts.size() == 1) {
            org.locationtech.jts.geom.Geometry geom = childParts.get(0);
            if (geom instanceof LineString) {
                result =  JTSUtils.GEOMETRY_FACTORY.createMultiLineString(new LineString[] {(LineString)geom});
            } if (geom instanceof Polygon) {
                result = JTSUtils.GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {(Polygon)geom});
            } if (geom instanceof Point) {
                result = JTSUtils.GEOMETRY_FACTORY.createMultiPoint(new Point[] {(Point)geom});
            }
        }

        if (result == null) {
            result = JTSUtils.GEOMETRY_FACTORY.buildGeometry(childParts);
        }
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
    public Set<T> getElements() {
        return elements;
    }

    public void setElements(final Set<T> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("elements:").append('\n');
        for (Geometry g : elements) {
            sb.append(g).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object instanceof AbstractJTSAggregate && super.equals(object)) {
            AbstractJTSAggregate that = (AbstractJTSAggregate) object;
            return Objects.equals(this.elements, that.elements);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + (this.elements != null ? this.elements.hashCode() : 0);
        return hash;
    }
}
