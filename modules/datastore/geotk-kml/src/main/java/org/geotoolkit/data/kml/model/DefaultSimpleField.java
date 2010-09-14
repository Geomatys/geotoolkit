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

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSimpleField implements SimpleField {

    private Object displayName;
    private String type;
    private String name;
    private List<Object> simpleFieldExtensions;

    public DefaultSimpleField() {
        this.simpleFieldExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param displayName
     * @param type
     * @param name
     * @param simpleFieldExtensions
     */
    public DefaultSimpleField(Object displayName, String type,
            String name, List<Object> simpleFieldExtensions) {
        this.displayName = displayName;
        this.type = type;
        this.name = name;
        this.simpleFieldExtensions = (simpleFieldExtensions == null) ?
            EMPTY_LIST : simpleFieldExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Object getDisplayName() {
        return this.displayName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {
        return this.type;
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
    public List<Object> getSimpleFieldExtensions() {
        return this.simpleFieldExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDisplayName(Object displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSimpleFieldExtensions(List<Object> simpleFieldExtensions) {
        this.simpleFieldExtensions = (simpleFieldExtensions == null) ?
            EMPTY_LIST : simpleFieldExtensions;
    }
}
