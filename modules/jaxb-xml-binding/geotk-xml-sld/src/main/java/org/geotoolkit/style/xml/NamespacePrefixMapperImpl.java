/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.style.xml;

import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;



/**
 * A mapper between namespace prefixes and url they represent.
 * It is possible to specify a root namespace, which will be used if no namespace
 * is specified.
 *
 * @source $URL$
 * @author Cédric Briançon
 */
public class NamespacePrefixMapperImpl extends NamespacePrefixMapper {
    /**
     * If set, this namespace will be the root of the document with no prefix.
     */
    private String rootNamespace;

    /**
     * Builds a mapper of prefixes.
     *
     * @param rootNamespace The root namespace.
     */
    public NamespacePrefixMapperImpl(String rootNamespace) {
        super();
        this.rootNamespace = rootNamespace;
    }

    /**
     * Returns a preferred prefix for the given namespace URI.
     *
     * @param namespaceUri
     *      The namespace URI for which the prefix needs to be found.
     * @param suggestion
     *      The suggested prefix, returned if the given namespace is not recoginzed.
     * @param requirePrefix
     *      Ignored in this implementation.
     *
     * @return
     *      The prefix inferred from the namespace URI.
     */
    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if (namespaceUri == null || namespaceUri.equals("")) {
            return "sld";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.isotc211.org/2005/gmd")){
            return "gmd";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.isotc211.org/2005/gco")){
            return "gco";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.w3.org/2001/XMLSchema-instance")) {
            return "xsi";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.w3.org/1999/xlink")) {
            return "xlink";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.opengis.net/gml")) {
            return "gml";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.opengis.net/sld")) {
            return "sld";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.opengis.net/ogc")) {
            return "ogc";
        }
        if (namespaceUri.equalsIgnoreCase("http://www.opengis.net/se")) {
            return "se";
        }
        return suggestion;
    }

    /**
     * Returns a list of namespace URIs that should be declared at the root element.
     * This implementation returns an empty list.
     */
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] {};
    }
}
