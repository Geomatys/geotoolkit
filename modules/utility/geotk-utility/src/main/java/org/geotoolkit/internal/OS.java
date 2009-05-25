/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal;


/**
 * The operation system on which Geotoolkit is running.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public enum OS {
    /**
     * Unknown system.
     */
    UNKNOWN,

    /**
     * Windows.
     */
    WINDOWS,

    /**
     * Mac OS.
     */
    MAC_OS,

    /**
     * Linux.
     */
    LINUX;

    /**
     * Returns the operating system Geotoolkit is currently on.
     *
     * @return The operation system.
     */
    public static OS current() {
        final String name = System.getProperty("os.name");
        if (name != null) {
            if (name.indexOf("Windows") >= 0) return WINDOWS;
            if (name.indexOf("Mac OS")  >= 0) return MAC_OS;
            if (name.indexOf("Linux")   >= 0) return LINUX;
        }
        return UNKNOWN;
    }
}
