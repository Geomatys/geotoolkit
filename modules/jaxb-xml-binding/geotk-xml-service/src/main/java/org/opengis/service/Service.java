/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/Service.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/

package org.opengis.service;

import java.util.Collection;
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * An implementation of a service.
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 * @module pending
 * * @since GeoAPI 2.1
 */
//@UML(identifier="SV_Service", specification=ISO_19119)
public interface Service {

    /**
     * 
     */
    //@UML(identifier="specification", obligation=MANDATORY, specification=ISO_19119)
    Collection<PlatformSpecificServiceSpecification> getSpecification();
    
    /**
     * 
     */
    //@UML(identifier="theSV_Port", obligation=MANDATORY, specification=ISO_19119)
    Collection<Port> getTheSVPort();
}
