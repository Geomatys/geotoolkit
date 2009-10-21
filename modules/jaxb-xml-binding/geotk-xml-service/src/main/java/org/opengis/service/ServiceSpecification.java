/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/ServiceSpecification.java $
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
 * Defines a service without reference to the type of specification or to its implementation.
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 * @module pending
 * * @since GeoAPI 2.5
 */
//@UML(identifier="SV_ServiceSpecification", specification=ISO_19119)
public interface ServiceSpecification {
    
   /**
    * The name of the service.
    */
    //@UML(identifier="name", obligation=MANDATORY, specification=ISO_19119)
    String getName();
    
    /**
    * The model of the service ("message" or "object").
    */
    //@UML(identifier="opModel", obligation=MANDATORY, specification=ISO_19119)
    OperationModel getOpModel();
    
    /**
     * The type of the specification of this service.
     */
    //@UML(identifier="typeSpec", obligation=MANDATORY, specification=ISO_19119)
    PlatformNeutralServiceSpecification getTypeSpec();
    
    /**
     * 
     */
    //@UML(identifier="theSV_Interface", obligation=MANDATORY, specification=ISO_19119)
    Collection<Interface> getTheSVInterface();

}
