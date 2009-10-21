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
import org.opengis.annotation.UML;
import org.opengis.util.MemberName;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 *
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 * @module pending
 */
//@UML(identifier="SV_Operation", specification=ISO_19119)
public interface Operation {
    
    /**
     * The name of the operation. 
     */
    //@UML(identifier="operationName", obligation=MANDATORY, specification=ISO_19119)
    MemberName getOperationName();
    
    /**
     * A list of operation on witch the operation depends on. 
     */
    //@UML(identifier="dependsOn", obligation=OPTIONAL, specification=ISO_19119)
    Collection<Operation> getDependsOn();
    
    /**
     * The parameter of the operation.
     */
    //@UML(identifier="parameter", obligation=OPTIONAL, specification=ISO_19119)
    Parameter getParameter();
    

}
