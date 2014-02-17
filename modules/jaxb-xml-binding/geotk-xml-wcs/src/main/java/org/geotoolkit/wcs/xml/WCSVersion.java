/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcs.xml;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public enum WCSVersion {
    v100("1.0.0"),
    v111("1.1.1");

    private final String code;

    WCSVersion(final String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns {@link WCSVersion} that matches from the code.
     * 
     * @param v code value to resolve.
     * @return {@link WCSVersion} from code value.
     * @throws IllegalArgumentException if the code does not correspond to any existing code in this enum
     */
    public static WCSVersion fromCode(final String v) throws IllegalArgumentException {
        for (final WCSVersion candidat : WCSVersion.values()) {
            if (candidat.getCode().equals(v)) {
                return candidat;
            }
        }
        
        try{
            return WCSVersion.valueOf(v);
        }catch(IllegalArgumentException ex){/*we tryed*/}
        
        throw new IllegalArgumentException(v);
    }
    
}
