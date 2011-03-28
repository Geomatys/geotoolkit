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
package org.geotoolkit.data.kml.model;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultSimpleData implements SimpleData {

    private final String name;
    private final String content;

    /**
     *
     * @param name
     * @param content
     */
    public DefaultSimpleData(String name, String content) {
        this.name = name;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getContent() {
        return this.content;
    }
}
