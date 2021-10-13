/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.internal.io;


/**
 * The operation system on which SIS is running.
 */
public enum OS {
    /**
     * Unknown system.
     */
    UNKNOWN(false),

    /**
     * Windows.
     */
    WINDOWS(false),

    /**
     * Mac OS.
     */
    MAC_OS(true),

    /**
     * Linux.
     */
    LINUX(true);

    /**
     * {@code true} if this OS is a kind of Unix.
     */
    public final boolean unix;

    /**
     * Creates a new enumeration.
     */
    private OS(final boolean unix) {
        this.unix = unix;
    }

    /**
     * Returns the name value of {@code "os.name"} property, or {@code null} if the security manager
     * does not allow us to access this information.
     *
     * <div class="note"><b>Note:</b> {@code uname} is an Unix command providing the same information.</div>
     *
     * @return the operation system name, or {@code null} if this information is not available.
     */
    public static String uname() {
        try {
            return System.getProperty("os.name");
        } catch (SecurityException e) {
            return null;
        }
    }

    /**
     * Returns the operating system SIS is currently on.
     *
     * @return the operation system.
     */
    public static OS current() {
        final String name = uname();
        if (name != null) {
            if (name.contains("Windows")) return WINDOWS;
            if (name.contains("Mac OS"))  return MAC_OS;
            if (name.contains("Linux"))   return LINUX;
        }
        return UNKNOWN;
    }
}
