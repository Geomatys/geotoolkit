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
package org.geotoolkit.gml.xml;

import org.opengis.metadata.Identifier;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public interface AbstractGML {

    String getId();

    void setId(String id);

    String getDescription();

    void setDescription(String description);

    Identifier getName();

    void setName(Identifier name);

    Reference getDescriptionReference();

    Code getParameterName();
}
