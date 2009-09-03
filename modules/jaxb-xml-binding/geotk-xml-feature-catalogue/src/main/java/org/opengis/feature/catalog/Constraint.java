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
 * class for defining constraints types.
 *
 * 
 * @author Guilhem Legal
 */
//@UML(identifier="FC_Constraint", specification=ISO_19110)
public interface Constraint {
    
    /**
     * Description of the constraint that is being applied. 
     */
    //@UML(identifier="description", obligation=MANDATORY, specification=ISO_19110)
    String getDescription();

}
