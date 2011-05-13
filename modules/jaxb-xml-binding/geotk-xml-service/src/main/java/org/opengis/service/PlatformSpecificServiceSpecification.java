/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/PlatformSpecificServiceSpecification.java $
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
 * Defines the implementation of a specific type of service.
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 * @module pending
 * * @since GeoAPI 2.1
 */
@UML(identifier="SV_PlatformSpecificServiceSpecification", specification=UNSPECIFIED)
public interface PlatformSpecificServiceSpecification extends PlatformNeutralServiceSpecification {
    
    /**
     * 
     */
    //@UML(identifier="DCP", obligation=MANDATORY, specification=ISO_19119)
    DCPList getDCP();
    
    /**
     * 
     */
    //@UML(identifier="implementation", obligation=MANDATORY, specification=ISO_19119)
    Collection<Service> getImplementation(); 

}
