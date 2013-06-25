/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.gco;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB adapter in order to wrap a string to a string, without any operations done
 * except {@link String#trim()}. This adapter is useful when you do not want to do
 * anything on the string, for example to annulate the use of a unintended adapter
 * like {@link StringAdapter} which could be defined in a package-info class.
 * <p>
 * An exception to this rule occurs if the length of the marshalled or unmarshalled
 * string is zero. In this particular case, the string is replaced by {@code null}.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.04
 *
 * @since 3.00
 * @module
 */
public final class StringConverter extends XmlAdapter<String,String> {
    /**
     * Returns the given string trimmed, unless its length is 0
     * in which case this method returns {@code null}.
     *
     * @param  value The string.
     * @return The trimmed string, or {@code null}.
     */
    @Override
    public String unmarshal(String value) {
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                return null;
            }
        }
        return value;
    }

    /**
     * Returns the given string trimmed, unless its length is 0
     * in which case this method returns {@code null}.
     *
     * @param  value The string.
     * @return The trimmed string, or {@code null}.
     */
    @Override
    public String marshal(String value) {
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                return null;
            }
        }
        return value;
    }
}
