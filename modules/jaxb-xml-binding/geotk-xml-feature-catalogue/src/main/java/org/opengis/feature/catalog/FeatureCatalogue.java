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

import java.util.Date;
import java.util.List;
import org.opengis.annotation.Extension;
import org.opengis.annotation.UML;
import org.opengis.metadata.citation.ResponsibleParty;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * A feature catalogue contains its identification and contact information, 
 * and definition of some number of feature types with other information necessary for those definitions.
 * 
 * @author Guilhem Legal
 * @module pending
 */
//@UML(identifier="FC_FeatureCatalogue", specification=ISO_19110)
public interface FeatureCatalogue {
    
     /**
     * an unique identifier used in the XML instance. 
     */
     @Extension
     String getId();
     
    /**
     * name for this feature catalogue. 
     */
     //@UML(identifier="name", obligation=MANDATORY, specification=ISO_19110)
     String getName();
     
     /**
     * Subject domain(s) of feature types defined in this feature catalogue. 
     */
     //@UML(identifier="scope", obligation=MANDATORY, specification=ISO_19110)
     List<String> getScope();
     
     /**
     * Description of kind(s) of use to whitch this feature catalogue may be put. 
     */
     //@UML(identifier="fieldOfApplication", obligation=OPTIONAL, specification=ISO_19110)
     List<String> getFieldOfApplication();
     
     /**
      * Version number of this feature catalogue, 
      * which may include both a major version number or letter and a sequence of minor release numbers or letters,
      * such as "3.2.4a". The format of this attribute may difer between cataloguing authorities.
      */
     //@UML(identifier="versionNumber", obligation=MANDATORY, specification=ISO_19110)
     String getVersionNumber();
     
     /**
      * Effective date of this feature catalogue.
      */
     //@UML(identifier="versionDate", obligation=MANDATORY, specification=ISO_19110)
     Date getVersionDate();
     
     /**
      * Name, address, country, and telecommunications address of person or organization having primary responsability.
      * for the intellectual content of this feature catalogue.
      */
     //@UML(identifier="producer", obligation=MANDATORY, specification=ISO_19110)
     ResponsibleParty getProducer();
     
      /**
       * Formal functionnal language in which the feature operation formal definition occurs in this feature catalogue.
       * Obligation Conditional : Mandatory if feature operation formal definition occurs in feature catalogue. 
       */
     //@UML(identifier="functionalLanguage", obligation=CONDITIONAL, specification=ISO_19110)
     String getFunctionalLanguage();
     
     /**
       * Role that links this feature catalogue to the feature types that is contains. 
       */
     //@UML(identifier="featureType", obligation=MANDATORY, specification=ISO_19110)
     List<FeatureType> getFeatureType();
     
     /**
       * Role that links this feature catalogue to the sources of definitions of feature types, 
      *  property types, and listed values that it contains. 
       */
     //@UML(identifier="definitionSource", obligation=OPTIONAL, specification=ISO_19110)
     List<DefinitionSource> getDefinitionSource();
     
     

}
