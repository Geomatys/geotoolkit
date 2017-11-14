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
    v100("1.0.0", true),
    v110("1.1.0", true),
    v111("1.1.1", true),
    v130("1.3.0", false),
    auto("auto", false);

    private final String code;
    /**
     * Indicates iff this version of WMS standard impose coordinate definition
     * where longitude is the first (x) component.
     */
    public final boolean longitudeFirst;

    WMSVersion(final String code, final boolean longitudeFirst) {
        this.code = code;
        this.longitudeFirst = longitudeFirst;
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

        //maybe it's the enum string
        try{
            WMSVersion.valueOf(version);
        }catch(IllegalArgumentException ex){}

        throw new IllegalArgumentException("The given string \""+ version +"\" is not " +
                "a known version.");
    }
}
