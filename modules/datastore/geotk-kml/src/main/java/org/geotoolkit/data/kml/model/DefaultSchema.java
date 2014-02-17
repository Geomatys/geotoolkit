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
 * @module pending
 */
public class DefaultSchema implements Schema {

    private List<SimpleField> simpleFields;
    private String name;
    private String id;
    private List<Object> schemaExtensions;

    /**
     * 
     */
    public DefaultSchema() {
        this.simpleFields = EMPTY_LIST;
        this.schemaExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param simpleFields
     * @param name
     * @param id
     * @param schemaExtensions
     */
    public DefaultSchema(List<SimpleField> simpleFields,
            String name, String id, List<Object> schemaExtensions) {
        this.simpleFields = (simpleFields == null) ? EMPTY_LIST : simpleFields;
        this.name = name;
        this.id = id;
        this.schemaExtensions = (schemaExtensions == null) ? EMPTY_LIST : schemaExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleField> getSimpleFields() {
        return this.simpleFields;
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
    public String getId() {
        return this.id;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getSchemaExtensions() {
        return this.schemaExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSimpleFields(List<SimpleField> simpleFields) {
        this.simpleFields = (simpleFields == null) ? EMPTY_LIST : simpleFields;
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
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSchemaExtensions(List<Object> schemaExtensions) {
        this.schemaExtensions = (schemaExtensions == null) ? EMPTY_LIST : schemaExtensions;
    }
}
