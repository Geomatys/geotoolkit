/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.ows.xml;

import java.util.Map;
import org.geotoolkit.util.Versioned;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public interface RequestBase extends Versioned {

    String getService();

    void setService(final String value);

    void setVersion(final String version);

    public default Map<String,String> toKVP() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Request get not be converted to Rest parameters.");
    }
}
