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
import org.geotoolkit.util.Multiplicity;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * 
 * Abstract class for feature properties.
 * 
 * @author Guilhem Legal
 */
//@UML(identifier="FC_PropertyType", specification=ISO_19110)
public interface PropertyType {

     /**
     * an unique identifier used in the XML instance. 
     */
     @Extension
     String getId();
     
    /**
     * Member name that locates this member within a feature type. 
     */
    //@UML(identifier="memberName", obligation=MANDATORY, specification=ISO_19110)
    LocalName getMemberName();
    
    /**
     * Definition of the member in a natural language. This attribute is required if the definition is not provided by
     * FC_FeatureCalaogue::definitionSource. If not provided, 
     * the definitionReference should specify a citation where the definition may be found,
     * and any additional information as to which is to be used.
     */
    //@UML(identifier="definition", obligation=CONDITIONAL, specification=ISO_19110)
    String getDefinition();
    
    /**
     * Cardinality of the member in the feature class. 
     * If this is an attribute or operation, the default cardinality is 1.
     * If this is an association role, then the default cardinality is 0..*.
     * For operations, this is the number of return values possible.
     * This is an elaboration of the GFM to allow for complete specifications for various programming and data definition language.
     */
    //@UML(identifier="cardinality", obligation=MANDATORY, specification=ISO_19110)
    Multiplicity getCardinality();
    
    /**
     * Role that links the operations, attributes and associations roles with feature types that contain them.
     * The association class FC_Binding describes particular information regarding the use of this poperty type within this feature type.
     * This is a "strong agregation" or composition in the general Feature Model (ISO 19109). Here it ios realized by a "weak agragation".
     * This is valid since a weak agragation can be converted to a strong aggragation by replicating the value of the target role for each use.
     * In this case, the property type represent a value, and the realization of the GFM's property type would create a GF_PropertyType whose identity
     * is the combination of the FC_PropertyType and the owning FC_FeatureType.
     */
    //@UML(identifier="featureType", obligation=OPTIONAL, specification=ISO_19110)
    FeatureType getFeatureType();
    
     /**
      * Role that links this property type to the constraints placed upon it. 
      */
    //@UML(identifier="constrainedBy", obligation=OPTIONAL, specification=ISO_19110)
    List<Constraint> getConstrainedBy();
    
    /**
     * Role that links this instance to the source of its definition. 
     */
    //@UML(identifier="definitionReference", obligation=OPTIONAL, specification=ISO_19110)
    DefinitionReference getDefinitionReference();
    
}
