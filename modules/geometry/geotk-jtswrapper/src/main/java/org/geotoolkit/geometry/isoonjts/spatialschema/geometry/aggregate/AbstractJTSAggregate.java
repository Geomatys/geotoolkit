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

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;

import org.geotoolkit.util.Utilities;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.aggregate.Aggregate;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
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
    protected com.vividsolutions.jts.geom.Geometry computeJTSPeer() {
        List<com.vividsolutions.jts.geom.Geometry> childParts = new ArrayList<com.vividsolutions.jts.geom.Geometry>();
        for(Geometry prim : elements) {
            if(prim instanceof JTSGeometry){
                JTSGeometry jtsGeom = (JTSGeometry) prim;
                childParts.add(jtsGeom.getJTSGeometry());
            }else{
                throw new IllegalStateException("Only JTSGeometries are allowed in the JTSAggregate class.");
            }
        }

        com.vividsolutions.jts.geom.Geometry result = null;
        // we want a multi geometry event if there is only one geometry
        if (childParts.size() == 1) {
            com.vividsolutions.jts.geom.Geometry geom = childParts.get(0);
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
            return Utilities.equals(this.elements, that.elements);
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
