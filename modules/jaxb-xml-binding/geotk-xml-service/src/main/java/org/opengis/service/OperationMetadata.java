/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/OperationMetadata.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/

package org.opengis.service;

import java.util.Collection;
import org.opengis.annotation.UML;
import org.opengis.metadata.citation.OnlineResource;
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
@UML(identifier="SV_OperationMetadata", specification=UNSPECIFIED)
public interface OperationMetadata {
    
    /**
     * a unique identifier for this interface. 
     */
    //@UML(identifier="operationName", obligation=MANDATORY, specification=ISO_19119)
    String getOperationName();
    
    /**
     * Distributed computing platforms on which the operation has been implemented. 
     */
    //@UML(identifier="DCP", obligation=MANDATORY, specification=ISO_19119)
    Collection<DCPList> getDCP();
    
    /**
     * Free text description of the intent of the operation and the results of the operation. 
     */
    //@UML(identifier="OperationDescription", obligation=OPTIONAL, specification=ISO_19119)
    String getOperationDescription();
    
    /**
     * the name used to invok this interface within the context of the DCP. 
     * The name is identical for all DCPs. 
     */
    //@UML(identifier="invocationName", obligation=OPTIONAL, specification=ISO_19119)
    String getInvocationName();
    
    /**
     * The parameters that are required for this interface.
     */
    //@UML(identifier="parameters", obligation=OPTIONAL, specification=ISO_19119)
    Collection<Parameter> getParameters();
    
    /**
     * handle for accesing the service interface.
     */
    //@UML(identifier="connectPoint", obligation=MANDATORY, specification=ISO_19119)
    Collection<OnlineResource> getConnectPoint();
    
    /**
     * list of operation that must be completed immediatly before current operation is invoked,
     * structured as a list for capturing alternate predecesor path and sets for capturing parallel predecessor paths.
     */
    //@UML(identifier="dependsOn", obligation=OPTIONAL, specification=ISO_19119)
    Collection<OperationMetadata> getDependsOn();

}
