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

import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * 
 * Role of the feature association.
 * 
 * @author Guilhem Legal
 * @module pending
 */
//@UML(identifier="FC_AssociationRole", specification=ISO_19110)
public interface AssociationRole extends PropertyType {

    /**
     * Type of association role, indicating whether this role acts as a "is part of" or 'is a member of" semantics.
     * default value: "ordinary" 
     * 
     */
    //@UML(identifier="type", obligation=MANDATORY, specification=ISO_19110)
    RoleType getType();

    /**
     * indicates if the instances of this association role within the containing feature instance are ordered or not,
     * with FALSE = "not ordered" and TRUE = "ordered".
     * If TRUE, the FC_PropertyType::definition shall contain an explanation of the meaning of the order.
     * default value: FALSE
     */
    //@UML(identifier="isOrdered", obligation=MANDATORY, specification=ISO_19110)
    Boolean getIsOrdered();
    
    /**
     * indicates whether this role is navigable from the source feature to the target feature of the association.
     * default value: TRUE
     */
     //@UML(identifier="isNavigable", obligation=MANDATORY, specification=ISO_19110)
     Boolean getIsNavigable();


    /**
     * relation of which this association role is a part.
     * 
     */
    //@UML(identifier="relation", obligation=MANDATORY, specification=ISO_19110)
    FeatureAssociation getRelation();

    /**
     * Type of the target value of this association    
     */
    //@UML(identifier="valueType", obligation=MANDATORY, specification=ISO_19110)
    FeatureType getValueType();
    

}
