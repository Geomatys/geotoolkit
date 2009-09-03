/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/Interface.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/
package org.opengis.feature.catalog;

import org.opengis.annotation.Extension;
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * Class that represent an association between a particular feature type and a particular property type,
 * in order that operational effect information may be supplied for feature operations.
 * 
 * @author Guilhem Legal
 */
//@UML(identifier="FC_BoundFeatureAttribute", specification=ISO_19110)
public interface BoundFeatureAttribute {
    
    @Extension
    String getId();
    
    /**
     * Feature type involved in the binding. 
     */
     //@UML(identifier="featureType", obligation=MANDATORY, specification=ISO_19110)
     FeatureType getFeatureType();
     
     /**
     * Property type involved in the binding. 
     */
     //@UML(identifier="attribute", obligation=MANDATORY, specification=ISO_19110)
     FeatureAttribute getAttribute();

}
