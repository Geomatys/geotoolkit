/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/OperationModel.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/

package org.opengis.service;


/**
 *  The model of a service specification.
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal
 */
public enum OperationModel {

    OBJECT("object"),
    
    MESSAGE("message");
    
    private final String value;

    OperationModel(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OperationModel fromValue(String v) {
        for (OperationModel c: OperationModel.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
