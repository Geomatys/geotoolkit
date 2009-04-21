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

import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;


/**
 * A mapper between namespace prefixes and URL they represent.
 * It is possible to specify a root namespace, which will be used if no namespace
 * is specified.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
final class OGCNamespacePrefixMapper extends NamespacePrefixMapper {
    /**
     * An empty array of strings.
     */
    private static final String[] EMPTY = new String[0];

    /**
     * URLs for which the prefix to use directly follows them.
     */
    private static final String[] GENERIC_URLS = {
        "http://www.isotc211.org/2005/",
        "http://www.opengis.net/",
        "http://www.w3.org/1999/",
        "http://www.cnig.gouv.fr/2005/",
        "http://purl.org/"
    };

    /**
     * A map of (<var>urls</var>, <var>prefix</var>). Stores URLs for which
     * the prefix to use can not be easily inferred from the URL itself.
     */
    private static final Map<String,String> SPECIFIC_URLS;
    static {
        final Map<String,String> p = new HashMap<String,String>(30);
        p.put("http://www.w3.org/2001/XMLSchema",                         "xsd");
        p.put("http://www.w3.org/2001/XMLSchema-instance",                "xsi");
        p.put("http://www.w3.org/2004/02/skos/core#",                    "skos");
        p.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#",              "rdf");
        p.put("http://www.opengis.net/sensorML/1.0",                     "sml1");
        p.put("http://www.opengis.net/sensorML/1.0.1",                    "sml");
        p.put("http://www.opengis.net/swe/1.0",                          "swe1");
        p.put("http://www.opengis.net/cat/csw/2.0.2",                     "csw");
        p.put("http://www.opengis.net/cat/wrs/1.0",                       "wrs");
        p.put("http://www.opengis.net/cat/wrs",                         "wrs09");
        p.put("http://www.opengis.net/ows-6/utds/0.3",                   "utds");
        p.put("http://www.opengis.net/citygml/1.0",                      "core");
        p.put("http://www.opengis.net/citygml/building/1.0",            "build");
        p.put("http://www.opengis.net/citygml/cityfurniture/1.0",   "furniture");
        p.put("http://www.opengis.net/citygml/transportation/1.0",         "tr");
        p.put("http://www.purl.org/dc/elements/1.1/",                     "dc2");
        p.put("http://www.purl.org/dc/terms/",                           "dct2");
        p.put("http://purl.org/dc/terms/",                                "dct");
        p.put("http://www.inspire.org",                                   "ins");
        p.put("urn:oasis:names:tc:ciq:xsdschema:xAL:2.0",                 "xal");
        p.put("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0",              "rim");
        p.put("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5",            "rim25");
        p.put("urn:oasis:names:tc:xacml:2.0:context:schema:os", "xacml-context");
        p.put("urn:oasis:names:tc:xacml:2.0:policy:schema:os",   "xacml-policy");
        p.put("urn:us:gov:ic:ism:v2",                                   "icism");
        SPECIFIC_URLS = p;
    }

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
    public OGCNamespacePrefixMapper(final String rootNamespace) {
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
        if (namespace == null || namespace.length() == 0) {
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
        return getPreferredPrefix(namespace, suggestion);
    }

    /**
     * Returns the preferred prefix for the given namespace URI, assuming that the namespace is
     * not {@code null}.
     *
     * @param  namespace  The namespace URI for which the prefix needs to be found.
     *                    Should not be {@code null}.
     * @param  suggestion The suggested prefix, returned if the given namespace is not recognized.
     * @return The prefix inferred from the namespace URI.
     */
    static String getPreferredPrefix(String namespace, final String suggestion) {
        String prefix = SPECIFIC_URLS.get(namespace);
        if (prefix != null) {
            return prefix;
        }
        namespace = namespace.toLowerCase(Locale.US);
        for (final String baseURL : GENERIC_URLS) {
            if (namespace.startsWith(baseURL)) {
                final int startAt = baseURL.length();
                final int endAt = namespace.indexOf('/', startAt);
                if (endAt >= 0) {
                    prefix = namespace.substring(startAt, endAt);
                } else {
                    prefix = namespace.substring(startAt);
                }
                return prefix;
            }
        }
        return suggestion;
    }

    /**
     * Returns a list of namespace URIs that should be declared at the root element.
     * This implementation returns an empty list.
     *
     * @return Namespace URIs that should be declared at the root element.
     */
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return EMPTY;
    }
}
