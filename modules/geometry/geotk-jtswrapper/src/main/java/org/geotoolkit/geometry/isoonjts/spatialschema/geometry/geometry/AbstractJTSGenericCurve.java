/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/geometry/GenericCurveImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.opengis.geometry.coordinate.GenericCurve;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * The {@code GenericCurveImpl} class/interface...
 * 
 * @author SYS Technologies
 * @author crossley
 * @version $Revision $
 * @module pending
 */
public abstract class AbstractJTSGenericCurve extends AbstractJTSGeometry implements GenericCurve, JTSGeometry {
    public AbstractJTSGenericCurve() {

    }

    public AbstractJTSGenericCurve(final CoordinateReferenceSystem crs) {
        super(crs);
    }
}
