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
package org.geotoolkit.xml;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import net.jcip.annotations.Immutable;
import org.apache.sis.util.CharSequences;


/**
 * A mapper between namespace prefixes and URL they represent. Identical to
 * {@link OGCNamespacePrefixMapper} except that it depends on the endorsed
 * JAXB jar instead than the implementation bundled in JDK 6.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
@Immutable
final class OGCNamespacePrefixMapper_Endorsed extends NamespacePrefixMapper {
    /**
     * If set, this namespace will be the root of the document with no prefix.
     */
    private final String rootNamespace;

    /**
     * The default prefix to return when no namespace is given.
     */
    private final String defaultPrefix;

    /**
     * Builds a mapper of prefixes.
     *
     * @param rootNamespace The root namespace.
     */
    public OGCNamespacePrefixMapper_Endorsed(final String rootNamespace) {
        this.rootNamespace = rootNamespace;
        defaultPrefix = rootNamespace.substring(rootNamespace.lastIndexOf('/') + 1);
    }

    /**
     * Returns a preferred prefix for the given namespace URI.
     *
     * @param  namespace  The namespace URI for which the prefix needs to be found.
     * @param  suggestion The suggested prefix, returned if the given namespace is not recognized.
     * @param  required   Ignored in this implementation.
     * @return The prefix inferred from the namespace URI.
     */
    @Override
    public String getPreferredPrefix(String namespace, final String suggestion, final boolean required) {
        if (namespace == null || namespace.isEmpty()) {
            return defaultPrefix;
        }
        /*
         * If the current namespace is the one defined as root namespace, this implementation
         * just returns an empty string. This namespace will be defined with a xmlns parameter,
         * and all tags in this namespace will not have any namespace prefix.
         */
        if (namespace.equals(rootNamespace)) {
            return "";
        }
        return Namespaces.getPreferredPrefix(namespace, suggestion);
    }

    /**
     * Returns a list of namespace URIs that should be declared at the root element.
     * This implementation returns an empty list.
     *
     * @return Namespace URIs that should be declared at the root element.
     */
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return CharSequences.EMPTY_ARRAY;
    }
}
