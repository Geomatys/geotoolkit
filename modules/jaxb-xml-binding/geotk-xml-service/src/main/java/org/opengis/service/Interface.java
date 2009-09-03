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

import java.util.Collection;
import org.opengis.annotation.UML;
import org.opengis.util.TypeName;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * Named Set of operations that characterize the behaviour of an entity.
 *
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 */
//@UML(identifier="SV_Interface", specification=ISO_19119)
public interface Interface {
    
    /**
     * 
     */
    //@UML(identifier="typeName", obligation=MANDATORY, specification=ISO_19119)
    TypeName getTypeName();
    
    /**
     * 
     */
    //@UML(identifier="theSV_Port", obligation=OPTIONAL, specification=ISO_19119)
    Collection<Port> getTheSVPort();
    
    /**
     * 
     */
    //@UML(identifier="operation", obligation=MANDATORY, specification=ISO_19119)
    Operation getOperation();

}
