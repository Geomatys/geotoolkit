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
import java.util.List;
import org.opengis.annotation.UML;
import org.opengis.metadata.citation.ResponsibleParty;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * 
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 * @since GeoAPI 2.1
 * @Deprecated deleted in amd.1:2008  
 */
//@UML(identifier="SV_ServiceProvider", specification=ISO_19119)
@Deprecated
public interface ServiceProvider {

    /**
     * 
     */
    //@UML(identifier="serviceContact", obligation=MANDATORY, specification=ISO_19119)
    Collection<ResponsibleParty> getServiceContact();
    
    /**
     * A unique identifier for the service provider organization.
     */
    //@UML(identifier="providerName", obligation=MANDATORY, specification=ISO_19119)
    String getProviderName();
    
    /**
     * 
     */
    //@UML(identifier="services", obligation=MANDATORY, specification=ISO_19119)
    Collection<ServiceIdentification> getServices();
    
    
}
