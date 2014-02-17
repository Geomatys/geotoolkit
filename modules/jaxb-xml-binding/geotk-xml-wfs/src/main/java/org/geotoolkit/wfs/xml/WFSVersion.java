/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.wfs.xml;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public enum WFSVersion {
    v110("1.1.0"),
    v200("2.0.0");

    private final String code;

    WFSVersion(final String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
    /**
     * Returns {@link WFSVersion} that matches from the code.
     * 
     * @param v code value to resolve.
     * @return {@link WFSVersion} from code value.
     * @throws IllegalArgumentException if the code does not correspond to any existing code in this enum
     */
    public static WFSVersion fromCode(final String v) throws IllegalArgumentException {
        for (final WFSVersion candidat : WFSVersion.values()) {
            if (candidat.getCode().equals(v)) {
                return candidat;
            }
        }
        
        try{
            return WFSVersion.valueOf(v);
        }catch(IllegalArgumentException ex){/*we tryed*/}
        
        throw new IllegalArgumentException(v);
    }

}
