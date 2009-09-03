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
import org.opengis.annotation.Extension;
import org.opengis.annotation.UML;
import org.opengis.util.LocalName;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * class of real world phenomena with common properties.
 * 
 * @author Guilhem Legal
 */
//@UML(identifier="FC_FeatureType", specification=ISO_19110)
public interface FeatureType {

    @Extension
    String getId();
    
    /**
     * Text string that uniquely identifies this feature type within the feature catalogue that contains this feature type.
     */
    //@UML(identifier="typeName", obligation=MANDATORY, specification=ISO_19110)
    LocalName getTypeName();
    
    /**
     * Definition of the feature type in a natural language.
     * This attribute is required if the definition is not provided by FC_FeatureCatalogue::definitionSource.
     * If not provided, the definitionReference should specify a citation where the definition may be found,
     * and any additional information as to which definition is to be used.
     */
    //@UML(identifier="definition", obligation=CONDITIONAL, specification=ISO_19110)
    String getDefinition();
    
     /**
     * Code that uniquely identifies this feature type within the feature catalogue that contains this feature type.
     */
    //@UML(identifier="code", obligation=OPTIONAL, specification=ISO_19110)
    String getCode();
    
     /**
     * Indicates if the feature type is abstract or not.
     */
    //@UML(identifier="isAbstract", obligation=MANDATORY, specification=ISO_19110)
    Boolean getIsAbstract();
    
     /**
     * equivalent name(s) of this feature type.
     */
    //@UML(identifier="aliases", obligation=OPTIONAL, specification=ISO_19110)
    List<LocalName> getAliases();
    
     /**
      * Role that links this feature type to a set of superclasses from which it inherits its operations,
      * associations and properties.
      */
    //@UML(identifier="inheritsFrom", obligation=OPTIONAL, specification=ISO_19110)
    List<InheritanceRelation> getInheritsFrom();
    
    /**
     * Role that links this feature type to a set of subclasses which inherits its operations,
     * associations and properties.
     */
    //@UML(identifier="inheritsTo", obligation=OPTIONAL, specification=ISO_19110)
    List<InheritanceRelation> getInheritsTo();
    
     /**
     * Role that links this feature type to the feature catalogue that contains it.
     */
    //@UML(identifier="featureCatalogue", obligation=MANDATORY, specification=ISO_19110)
    FeatureCatalogue getFeatureCatalogue();
    
    /**
     * Role that links this feature type to the property types that it contains.
     * The association class FC_Binding describes particular information regarding the use of this property type within this feature type.
     */
    //@UML(identifier="carrierOfCharacteristics", obligation=OPTIONAL, specification=ISO_19110)
     List<PropertyType> getCarrierOfCharacteristics();
    
    /**
     * role tat links this feature type to the constraints placed upon it.
     */
    //@UML(identifier="constrainedBy", obligation=OPTIONAL, specification=ISO_19110)
    List<Constraint> getConstrainedBy();
    
    /**
     * role tat links this feature type to the source of its definition.
     */
    //@UML(identifier="definitionReference", obligation=OPTIONAL, specification=ISO_19110)
    DefinitionReference getDefinitionReference();
    
}
