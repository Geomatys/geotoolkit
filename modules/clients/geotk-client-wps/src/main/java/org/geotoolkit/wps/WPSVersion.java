/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps;

/**
 * Static enumeration of WPS server versions.
 */
public enum WPSVersion {
    v100("1.0.0"),
    v200("2.0.0"),
    auto("auto");

    private final String code;

    private WPSVersion(final String code) {
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
     * @throws IllegalArgumentException if the enum class does not contain any enum types for the given string
     *                                  value.
     */
    public static WPSVersion getVersion(final String version) {
        for (WPSVersion vers : WPSVersion.values()) {
            if (vers.getCode().equals(version)) {
                return vers;
            }
        }
        try {
            return WPSVersion.valueOf(version);
        } catch (IllegalArgumentException ex) {
        }
        throw new IllegalArgumentException("The given string \"" + version + "\" is not " + "a known version.");
    }

}
