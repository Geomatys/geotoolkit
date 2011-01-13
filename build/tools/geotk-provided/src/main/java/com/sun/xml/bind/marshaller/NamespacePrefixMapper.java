/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package com.sun.xml.bind.marshaller;


/**
 * A placeholder for class bundled in JAXB. This is the same than the class bundled in
 * JDK 6 except for the package name. Some servers like Glassfish uses the endorsed JAXB
 * implementation instead than the one bundled in JDK 6, so we must be able to support both.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public abstract class NamespacePrefixMapper {
    public abstract String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix);
    public abstract String[] getPreDeclaredNamespaceUris();
}
