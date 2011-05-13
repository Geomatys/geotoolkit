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
package org.opengis.service;

import org.opengis.annotation.UML;
import org.opengis.util.ScopedName;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * 
 *
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * @module pending
 */
@UML(identifier="SV_CoupledResource", specification=UNSPECIFIED)
public interface CoupledResource {
    
    /**
     * The name of the service operation.
     */
     //@UML(identifier="operationName", obligation=MANDATORY, specification=ISO_19119)
     String getOperationName();
     
    /**
     * name of the identifier of a given tightly coupled dataset.
     */
     //@UML(identifier="identifier", obligation=MANDATORY, specification=ISO_19119)
     String getIdentifier();
    
    /**
     *
     */
     //@UML(identifier="scopedName", obligation=MANDATORY, specification=ISO_19119)
     ScopedName getScopedName();

}
