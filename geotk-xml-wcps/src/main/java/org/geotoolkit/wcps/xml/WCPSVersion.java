/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.wcps.xml;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public enum WCPSVersion {
    v110("1.1.0");

    private final String code;

    WCPSVersion(final String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns {@link WCPSVersion} that matches from the code.
     *
     * @param v code value to resolve.
     * @return {@link WCPSVersion} from code value.
     * @throws IllegalArgumentException if the code does not correspond to any existing code in this enum
     */
    public static WCPSVersion fromCode(final String v) throws IllegalArgumentException {
        for (final WCPSVersion candidat : WCPSVersion.values()) {
            if (candidat.getCode().equals(v)) {
                return candidat;
            }
        }

        try{
            return WCPSVersion.valueOf(v);
        }catch(IllegalArgumentException ex){/*we tryed*/}

        throw new IllegalArgumentException(v);
    }
}
