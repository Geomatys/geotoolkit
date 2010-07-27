/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.xsd;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSimpleTypeContainer implements SimpleTypeContainer {

    String nameSpaceUri;
    String tagName;
    Object value;

    public DefaultSimpleTypeContainer(String nameSpaceUri, String tagName, Object value) {
        this.nameSpaceUri = nameSpaceUri;
        this.tagName = tagName;
        this.value = value;
    }

    @Override
    public String getNamespaceUri() {
        return this.nameSpaceUri;
    }

    @Override
    public String getTagName() {
        return this.tagName;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    
}
