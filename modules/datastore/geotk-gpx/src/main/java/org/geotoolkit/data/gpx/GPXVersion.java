/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.gpx;

/**
 * GPX versions enumeration
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public enum GPXVersion {
    v1_0_0,
    v1_1_0;

    public static GPXVersion toVersion(String code) throws NumberFormatException{
        code = code.trim();
        if("1.0".equals(code)){
            return v1_0_0;
        }else if("1.1".equals(code)){
            return v1_1_0;
        }else{
            throw new NumberFormatException("invalid version number : " + code);
        }
    }
}
