/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.MissingResourceException;
import java.io.InputStream;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.collection.BackingStoreException;


/**
 * Maps ISO identifiers to the GeoAPI types (interfaces or {@linkplain CodeList code lists}).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public final class GeoAPI extends Static {
    /**
     * The types for ISO 19115 UML identifiers. The keys are UML identifiers. Values
     * are either classname as {@link String} object, or the {@link Class} object.
     *
     * @see #forName(String)
     */
    private static Map<Object,Object> typeForNames;

    /**
     * Do not allow instantiation of this class.
     */
    private GeoAPI() {
    }

    /**
     * Returns the GeoAPI interface for the given ISO identifier, or {@code null} if none.
     * The identifier argument shall be the value documented in the {@link UML#identifier()}
     * annotation associated with the GeoAPI interface. Examples:
     * <p>
     * <ul>
     *   <li>{@code forUML("CI_Citation")} returns <code>{@linkplain org.opengis.metadata.citation.Citation}.class</code></li>
     *   <li>{@code forUML("CS_AxisDirection")} returns <code>{@linkplain org.opengis.referencing.cs.AxisDirection}.class</code></li>
     * </ul>
     * <p>
     * Only identifiers for the stable part of GeoAPI are recognized. This method does not handle
     * the identifiers for the {@code geoapi-pending} module.
     *
     * @param  identifier The ISO {@linkplain UML} identifier.
     * @return The GeoAPI interface, or {@code null} if the given identifier is unknown.
     */
    public static synchronized Class<?> forUML(final String identifier) {
        if (typeForNames == null) {
            final InputStream in = UML.class.getResourceAsStream("class-index.properties");
            if (in == null) {
                throw new MissingResourceException("class-index.properties", UML.class.getName(), identifier);
            }
            final Properties props = new Properties();
            try {
                props.load(in);
                in.close();
            } catch (Exception e) { // Catch IOException and IllegalArgumentException.
                throw new BackingStoreException(e);
            }
            typeForNames = new HashMap<Object,Object>(props);
        }
        final Object value = typeForNames.get(identifier);
        if (value == null || value instanceof Class<?>) {
            return (Class<?>) value;
        }
        final Class<?> type;
        try {
            type = Class.forName((String) value);
        } catch (ClassNotFoundException e) {
            throw new TypeNotPresentException((String) value, e);
        }
        typeForNames.put(identifier, type);
        return type;
    }
}
