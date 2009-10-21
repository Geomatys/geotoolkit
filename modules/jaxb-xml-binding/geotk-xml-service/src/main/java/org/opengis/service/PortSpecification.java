/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/OperationChainMetadata.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/

package org.opengis.service;

import java.net.URL;
import org.opengis.annotation.UML;
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
//@UML(identifier="SV_PortSpecification", specification=ISO_19119)
public interface PortSpecification {

    /**
     * 
     */
    //@UML(identifier="binding", obligation=MANDATORY, specification=ISO_19119)
    DCPList getBinding();
    
    /**
     * 
     */
    //@UML(identifier="address", obligation=MANDATORY, specification=ISO_19119)
    URL getAddress();
    
}
