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
 * Value for an enumerated feature attribute domain including its codes and interpretation.
 * 
 * @author Guilhem Legal
 * @module pending
 */
//@UML(identifier="FC_ListedValue", specification=ISO_19110)
public interface ListedValue {
    
    /**
     * Descriptive label that uniquely identifies one value of the feature attribute.
     */
    //@UML(identifier="label", obligation=MANDATORY, specification=ISO_19110)
    String getLabel();
    
    /**
     * Numeric or alphanumeric code (such as a country code) that uniquely identifies this value of the feature attribute.
     */
    //@UML(identifier="code", obligation=OPTIONAL, specification=ISO_19110)
    String getCode();
    
    /**
     * Definition of the attribute value in a natural language. If not provided, 
     * the definitionReference may specify a citation where the definition may be found,
     * and any additional information as to which definition iis to be used.
     */
    //@UML(identifier="definition", obligation=OPTIONAL, specification=ISO_19110)
    String getDefinition();
    
    /**
     * Role that links this instance to the source of its definition.
     */
    //@UML(identifier="definitionReference", obligation=OPTIONAL, specification=ISO_19110)
    DefinitionReference getDefinitionReference();

}
