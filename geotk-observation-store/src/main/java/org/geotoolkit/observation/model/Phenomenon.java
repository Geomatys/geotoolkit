/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.observation.model;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Phenomenon extends AbstractOMEntity implements org.opengis.observation.Phenomenon {

    private String definition;

    // for JSON
    protected Phenomenon() {}

    public Phenomenon(String id) {
        this(id, null, null, null, null);
    }

    public Phenomenon(String id, String name, String definition, String description, Map<String, Object> properties) {
        super(id, name, description, properties);
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("definition=").append(definition).append('\n');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Phenomenon that && super.equals(obj)) {
            return Objects.equals(this.definition, that.definition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + Objects.hashCode(this.definition);
        return hash;
    }
}
