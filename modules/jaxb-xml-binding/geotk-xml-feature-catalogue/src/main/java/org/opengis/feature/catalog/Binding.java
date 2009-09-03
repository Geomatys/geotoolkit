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
 * Class that is used to describe the specifics of how a property type is bound to a particular feature type;
 * used as an association class for the association MemberOf between feature type and property type.
 * 
 * @author Guilhem Legal
 */
//@UML(identifier="FC_Binding", specification=ISO_19110)
public interface Binding {
    
    /**
     * Description of how a property type is bound to a particular feature type. t
     */
     //@UML(identifier="description", obligation=OPTIONAL, specification=ISO_19110)
     String getDescription();

}
