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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;

import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.aggregate.Aggregate;
import org.opengis.geometry.primitive.Primitive;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractJTSAggregate<T extends Geometry> extends AbstractJTSGeometry implements Aggregate {

    private final Set<T> elements = new HashSet();

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
        return JTSUtils.GEOMETRY_FACTORY.buildGeometry(childParts);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<T> getElements() {
        return elements;
    }
}
