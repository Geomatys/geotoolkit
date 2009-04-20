/*$************************************************************************************************
 **
 ** $Id: LayerCoverageConstraints.java 1289 2008-07-29 10:32:20Z eclesia $
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/sld/LayerCoverageConstraints.java $
 **
 ** Copyright (C) 2008 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/
package org.geotoolkit.sld;

import java.util.List;
import org.opengis.sld.CoverageConstraint;
import org.opengis.sld.LayerCoverageConstraints;

/**
 * Mutable layer coverage constraints
 * 
 * @author Johann Sorel (Geomatys)
 * @since GeoAPI 2.2
 */
public interface MutableLayerCoverageConstraints extends MutableConstraints, LayerCoverageConstraints {

    public List<CoverageConstraint> constraints();
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
        
}
