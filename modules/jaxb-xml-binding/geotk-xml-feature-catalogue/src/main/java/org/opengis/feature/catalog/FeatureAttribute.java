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
import org.opengis.util.TypeName;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * characteristic of a feature type.
 * 
 * @author Guilhem Legal
 */
//@UML(identifier="FC_FeatureAttribute", specification=ISO_19110)
public interface FeatureAttribute extends PropertyType {
    
    /**
     * numeric or alphanumeric code that uniquely identifies the feature attribute within the feature catalogue. 
     */
     //@UML(identifier="code", obligation=OPTIONAL, specification=ISO_19110)
     String getCode();
     
     /**
      *  Unit of measure used for values of this feature attribute.
      
     //@UML(identifier="valueMeasurementUnit", obligation=OPTIONAL, specification=ISO_19110)
     UnitOfMeasure getValueMeasurementUnit();*/
     
     /**
      * permissible values of this feature attribute. If present, then  this feature attribute is enumerated
      * (such as with a code list). If not present, then this feature attribute is not enumerated.
      * obligation CONDITIONAL : Mandatory if feature attribute valueType is not given.
      */
     //@UML(identifier="listedValue", obligation=CONDITIONAL, specification=ISO_19110)
     List<ListedValue> getListedValue();
     
     /**
      * type of the value of this feature attribute; a name from some namespace. 
      * Implementation of this International Standard shall specify which namespace implementation is to be used.
      * obligation CONDITIONAL : Mandatory if feature attribute listedValue is empty.
      */
     //@UML(identifier="valueType", obligation=CONDITIONAL, specification=ISO_19110)
     TypeName getValueType(); 

}
