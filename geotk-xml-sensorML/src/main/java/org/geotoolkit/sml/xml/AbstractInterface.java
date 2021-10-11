/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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

package org.geotoolkit.sml.xml;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public interface AbstractInterface {

    AbstractInterfaceDefinition getInterfaceDefinition();

    String getName();

    void setName(String value);

    String getRemoteSchema();

    void setRemoteSchema(String value);

    String getType();

    void setType(String value);

    String getHref();

    void setHref(String value);

    String getRole();

    void setRole(String value);

    String getArcrole();

    void setArcrole(String value);

    String getTitle();

    void setTitle(String value);

    String getShow();

    void setShow(String value);

    String getActuate();

    void setActuate(String value);
}
