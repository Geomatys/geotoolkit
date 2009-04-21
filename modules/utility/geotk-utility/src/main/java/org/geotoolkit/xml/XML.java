/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.xml;

import org.geotoolkit.lang.Static;

/**
 * Utility class for some XML-related methods.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@Static
public final class XML {
    /**
     * Do not allow instantiation on this class.
     */
    private XML() {
    }

    /**
     * Returns the preferred prefix for the given namespace URI, assuming that the namespace is
     * not {@code null}.
     * It is the public entry for {@link OGCNamespacePrefixMapper#getPreferredPrefix(String, String)}.
     *
     * @param  namespace  The namespace URI for which the prefix needs to be found.
     * @param  suggestion The suggested prefix, returned if the given namespace is not recognized.
     * @return The prefix inferred from the namespace URI.
     */
    public static String getPreferredPrefix(final String namespace, final String suggestion) {
        return OGCNamespacePrefixMapper.getPreferredPrefix(namespace, suggestion);
    }
}
