/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/Port.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/

package org.opengis.service;

import org.opengis.annotation.UML;
import org.opengis.util.MemberName;
import org.opengis.util.TypeName;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 *
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 */
//@UML(identifier="SV_Parameter", specification=ISO_19119)
public interface Parameter {
    
    /**
     * The name, as used by the service for this parameter. 
     */
    //@UML(identifier="name", obligation=MANDATORY, specification=ISO_19119)
    MemberName getName();
    
    /**
     *  indication if the parameter is an input to the service, an output or both.
     */
    //@UML(identifier="direction", obligation=OPTIONAL, specification=ISO_19119)
    ParameterDirection getDirection();
    
    /**
     *  a narrative explanation of the role of the parameter.
     */
    //@UML(identifier="description", obligation=OPTIONAL, specification=ISO_19119)
    String getDescription();
    
    /**
     *  indication if the parameter is required.
     */
    //@UML(identifier="optionnality", obligation=MANDATORY, specification=ISO_19119)
    String getOptionality();
    
    /**
     *  indication if more than one value of the parameter may be provided.
     */
    //@UML(identifier="repeatability", obligation=MANDATORY, specification=ISO_19119)
    Boolean getRepeatability();
    
    /**
     * 
     */
    //@UML(identifier="valueType", obligation=MANDATORY, specification=ISO_19119)
    TypeName getValueType();
    
    

}
