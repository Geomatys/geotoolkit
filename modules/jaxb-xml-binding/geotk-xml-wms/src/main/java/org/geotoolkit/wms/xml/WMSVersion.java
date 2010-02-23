/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wms.xml;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public enum WMSVersion {
    v111("1.1.1"),
    v130("1.3.0");

    private final String code;

    WMSVersion(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get the version enum from the string code.
     *
     * @param version
     * @return The enum which matches with the given string.
     * @throws IllegalArgumentException if the enum class does not contain any enum types
     *                                  for the given string value.
     */
    public static WMSVersion getVersion(final String version) {
        for (WMSVersion vers :  values()) {
            if (vers.getCode().equals(version)) {
                return vers;
            }
        }
        throw new IllegalArgumentException("The given string \""+ version +"\" is not " +
                "a known version.");
    }
}
