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
package org.geotoolkit.sos.xml;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public enum SOSVersion {
    v100("1.0.0");

    private final String code;

    SOSVersion(final String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns {@link SOSVersion} that matches from the code.
     *
     * @param v code value to resolve.
     * @return {@link SOSVersion} from code value.
     * @throws IllegalArgumentException if the code does not correspond to any existing code in this enum
     */
    public static SOSVersion fromCode(final String v) throws IllegalArgumentException {
        for (final SOSVersion candidat : SOSVersion.values()) {
            if (candidat.getCode().equals(v)) {
                return candidat;
            }
        }

        try{
            return SOSVersion.valueOf(v);
        }catch(IllegalArgumentException ex){/*we tryed*/}

        throw new IllegalArgumentException(v);
    }

}
