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
 * Realizes GF_InheritanceRelation.
 * 
 * @author Guilhem Legal
 * @module pending
 */
//@UML(identifier="FC_InheritanceRelation", specification=ISO_19110)
public interface InheritanceRelation {

    @Extension
    String getId();
    
    /**
     * Text String that uniquely identifies this inheritance relation within the  feature catalogue
     * that contains this inheritance relation. 
     */
    //@UML(identifier="name", obligation=MANDATORY, specification=ISO_19110)
    String getName() ;

    /**
     * Natural language description of this inheritance relation.
     */
    //@UML(identifier="description", obligation=MANDATORY, specification=ISO_19110)
    String getDescription();

    /**
     * indicates if an instance of the supertype can be an instance of at most ones opf its subtypes.
     * 
     */
    //@UML(identifier="uniqueInstance", obligation=MANDATORY, specification=ISO_19110)
    Boolean getUniqueInstance();
    
    /**
     * Identifies one feature type to which the associated superclass feature type supplies inherited properties, 
     * associations and operation.
     */
    //@UML(identifier="subtype", obligation=MANDATORY, specification=ISO_19110)
    FeatureType getSubtype();

    /**
     * identifies one feature type from which the associated subtype class finherits properties, 
     * associations and operation.
     */
    //@UML(identifier="superType", obligation=MANDATORY, specification=ISO_19110)
    FeatureType getSupertype();

}
