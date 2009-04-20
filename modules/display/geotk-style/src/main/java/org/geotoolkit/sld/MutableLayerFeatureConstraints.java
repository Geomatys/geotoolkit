/*$************************************************************************************************
 **
 ** $Id: LayerFeatureConstraints.java 1289 2008-07-29 10:32:20Z eclesia $
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/sld/LayerFeatureConstraints.java $
 **
 ** Copyright (C) 2008 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/
package org.geotoolkit.sld;

import java.util.List;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.LayerFeatureConstraints;

/**
 * Mutable layer feature constraints
 * 
 * @author Johann Sorel (Geomatys)
 * @since GeoAPI 2.2
 */
public interface MutableLayerFeatureConstraints extends MutableConstraints, LayerFeatureConstraints {

    List<FeatureTypeConstraint> constraints();
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
        
}
