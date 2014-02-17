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

import java.util.Collection;
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
@UML(identifier="SV_OperationChainMetadata", specification=UNSPECIFIED)
public interface OperationChainMetadata {
    
    /**
     * The name, as used by the service for this chain.
     */
    //@UML(identifier="name", obligation=MANDATORY, specification=ISO_19119)
    String getName();
    
    /**
     * a narrative explanation of the service in the chain and resulting output.
     */
    //@UML(identifier="description", obligation=OPTIONAL, specification=ISO_19119)
    String getDescription();
    
     /**
     * The operations composing the chain.
     */
    //@UML(identifier="operation", obligation=OPTIONAL, specification=ISO_19119)
    Collection<OperationMetadata> getOperation();

}
