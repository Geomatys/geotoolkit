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

import java.util.List;
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * Relationship that links instances of this feature type with instances of the same or of different feature type.
 * the memberOf-linkBetween association in the general Feature Model is not directly implemented here,
 * since it can be easily derived from combining the Role and MemberOf  associations.
 * 
 * @author Guilhem Legal
 * @module pending
 */
//@UML(identifier="FC_FeatureAssociation", specification=ISO_19110)
public interface FeatureAssociation extends FeatureType {

    /**
     * Roles that are a part of this association.
     */
    //@UML(identifier="role", obligation=MANDATORY, specification=ISO_19110)
    List<AssociationRole> getRole();
}
