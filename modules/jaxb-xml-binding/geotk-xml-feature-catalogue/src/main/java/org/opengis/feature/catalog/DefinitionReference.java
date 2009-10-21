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
package org.opengis.feature.catalog;

import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * class that links a data instance to the source of its definition.
 *
 * 
 * @author Guilhem Legal
 * @module pending
 */
//@UML(identifier="FC_DefinitionReference", specification=ISO_19110)
public interface DefinitionReference {
    
    /**
     * Additional information to help locate the definition in the source document.
     * The format of this information is specific to the strcuture of the source document.
     */
    //@UML(identifier="sourceIdentifier", obligation=OPTIONAL, specification=ISO_19110)
    String getSourceIdentifier(); 
    
    /**
     * Role that link this definition reference to the citation for the source document.
     */
    //@UML(identifier="definitionSource", obligation=MANDATORY, specification=ISO_19110)
    DefinitionSource getDefinitionSource();

}
