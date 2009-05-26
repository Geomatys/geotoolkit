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

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.aggregate.MultiPoint;

/**
 */
public class JTSMultiPoint extends JTSAggregate
	implements MultiPoint {

    public JTSMultiPoint() {
        this(null);
    }

    public JTSMultiPoint(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    public JTSMultiPoint clone() {
        return (JTSMultiPoint) super.clone();
    }
}
