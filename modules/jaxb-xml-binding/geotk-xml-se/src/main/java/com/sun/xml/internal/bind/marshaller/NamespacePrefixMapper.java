/*
 *    GeoToolkit - An Open Source Java GIS Toolkit
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
package com.sun.xml.internal.bind.marshaller;


/**
 * A placeholder for an internal class from Sun JDK 6. Sun's internal package are not visible at
 * compile time, while they are visible at runtime. This placeholder is used only in order to
 * allows some classes to extend the Sun's class at compile-time. It will not be used at run-time;
 * the "real" Sun's class will be used instead since it come first in the classpath.
 *
 * @author Cédric Briançon (Geomatys)
 */
public abstract class NamespacePrefixMapper {
    public abstract String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix);
    public abstract String[] getPreDeclaredNamespaceUris();
}
