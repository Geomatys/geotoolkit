/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.io;

/**
 * Enum of supported encoding for the WPS.
 *
 * @author Quentin Boileau (Geomatys).
 */
public enum WPSEncoding {

    UTF8("utf-8"),
    BASE64("base64");
    public final String encoding;

    private WPSEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public String getValue() {
        return encoding;
    }

    public static WPSEncoding customValueOf(final String candidate) {
        for (final WPSEncoding encoding : values()) {
            if (encoding.getValue() != null) {
                if (encoding.getValue().equalsIgnoreCase(candidate)) {
                    return encoding;
                }
            }
        }
        return null;
    }
}
