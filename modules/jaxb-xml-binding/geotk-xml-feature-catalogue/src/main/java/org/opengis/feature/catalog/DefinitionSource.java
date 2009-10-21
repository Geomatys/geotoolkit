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

import org.opengis.annotation.Extension;
import org.opengis.annotation.UML;
import org.opengis.metadata.citation.Citation;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 * class that specifies the source of a definition.
 *
 * 
 * @author Guilhem Legal
 * @module pending
 */
//@UML(identifier="FC_DefinitionSource", specification=ISO_19110)
public interface DefinitionSource {
    
     /**
      * an unique identifier used in the XML instance. 
      */
     @Extension
     String getId();
     
     /**
      * actual citation of the source, sufficient to identify the document and how to obtain it. 
      */
     //@UML(identifier="source", obligation=MANDATORY, specification=ISO_19110)
     Citation getSource();

}
