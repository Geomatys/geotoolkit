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
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;

/**
 *
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 * 
 */
@UML(identifier="SV_ParameterDirection", specification=UNSPECIFIED)
public enum ParameterDirection {

    /**
     * the parameter is an input parameter to the service instance.
     */
    IN("in"),
    
    /**
     * the parameter is an output parameter to the service instance.
     */
    OUT("out"),
    
    /**
     * the parameter is both an input and output parameter to the service instance
     */
    IN_OUT("in/out");
    
    private final String value;

    ParameterDirection(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParameterDirection fromValue(final String v) {
        for (ParameterDirection c: ParameterDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
