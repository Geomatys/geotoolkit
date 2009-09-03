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

import java.util.List;
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * Operation that every instance of an associated feature type must implement.
 *
 * 
 * @author Guilhem Legal
 */
//@UML(identifier="FC_FeatureOperation", specification=ISO_19110)
public interface FeatureOperation extends PropertyType {
    
   /**
    * Name and parameters for this operation. It may contains optional returned parameters.
    * This signature is usually derived from the formal definition.
    * The signature of an operation must be unique. This is the equivalent of the UML signature. 
    */
    //@UML(identifier="signature", obligation=MANDATORY, specification=ISO_19110)
    String getSignature();

    /**
     * Formal description of the behaviour of the member, expressed in the symbol set defined by FC_FeatureCatalogue::functionalLanguage;
     * Involve operationnal parameters, and interactions with other members of the feature type. 
     */
    //@UML(identifier="formalDefinition", obligation=OPTIONAL, specification=ISO_19110)
    String getFormalDefinition();
    
    /**
     * Specifies attribute which may trigger an operation. 
     */
    //@UML(identifier="triggeredByValuesOf", obligation=OPTIONAL, specification=ISO_19110)
    List<BoundFeatureAttribute> getTriggeredByValuesOf();
    
    /**
     * Specifies attribute that may be used as input to perform an operation. 
     */
    //@UML(identifier="observesValuesOf", obligation=OPTIONAL, specification=ISO_19110)
    List<BoundFeatureAttribute> getObservesValuesOf();
    
     /**
     * Specifies attribute that will be affected by an operation. 
     */
    //@UML(identifier="affectsValuesOf", obligation=OPTIONAL, specification=ISO_19110)
    List<BoundFeatureAttribute> getAffectsValuesOf();
}
