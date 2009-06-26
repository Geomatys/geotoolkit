/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.feature.dummy;

import org.opengis.feature.type.Name;


/**
 * Dummy implementation of {@link Name}.
 */
public final class DummyName implements Name {
    private final String name;

    public DummyName(String name) {
        this.name = name;
    }

    @Override
    public boolean isGlobal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getNamespaceURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSeparator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLocalPart() {
        return name;
    }

    @Override
    public String getURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
