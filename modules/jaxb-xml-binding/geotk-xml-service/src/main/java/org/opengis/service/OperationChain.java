/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/Port.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/

package org.opengis.service;

import java.util.Collection;
import java.util.List;
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 *
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 */
//@UML(identifier="SV_OperationChain", specification=ISO_19119)
public interface OperationChain {
    
    /**
     * The name of the operation. 
     */
    //@UML(identifier="name", obligation=MANDATORY, specification=ISO_19119)
    String getName();
    
    /**
     * 
     */
    //@UML(identifier="description", obligation=OPTIONAL, specification=ISO_19119)
    String getDescription();
    
    /**
     * 
     */
    //@UML(identifier="operation", obligation=MANDATORY, specification=ISO_19119)
    Collection<Operation> getOperation();

}
