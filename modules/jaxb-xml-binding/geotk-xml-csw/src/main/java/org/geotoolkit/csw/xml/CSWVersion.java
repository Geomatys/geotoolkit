/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.csw.xml;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public enum CSWVersion {
    v200("2.0.0"),
    v202("2.0.2");

    private final String code;

    CSWVersion(final String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns {@link CSWVersion} that matches from the code.
     * 
     * @param v code value to resolve.
     * @return {@link CSWVersion} from code value.
     * @throws IllegalArgumentException if the code does not correspond to any existing code in this enum
     */
    public static CSWVersion fromCode(final String v) throws IllegalArgumentException {
        for (final CSWVersion candidat : CSWVersion.values()) {
            if (candidat.getCode().equals(v)) {
                return candidat;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
