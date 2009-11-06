/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/aggregate/MultiPointImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate;

import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.GeometryAdapter;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.aggregate.MultiPoint;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JTSMultiPoint extends AbstractJTSAggregate<Point>	implements MultiPoint {

    public JTSMultiPoint() {
        this(null);
    }

    public JTSMultiPoint(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSMultiPoint clone() {
        return (JTSMultiPoint) super.clone();
    }

    @XmlElement(name="pointMember", namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(GeometryAdapter.class)
    @Override
    public Set<Point> getElements() {
        return super.getElements();
    }

}
