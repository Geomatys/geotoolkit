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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractOMEntity {

    private String id;
    private String name;
    private String description;

    private Map<String, Object> properties = new HashMap<>();

    // for JSON
    protected AbstractOMEntity() {}

    public AbstractOMEntity(String id) {
        this(id, null, null, null);
    }

    public AbstractOMEntity(String id, String name, String description, Map<String, Object> properties) {
        this.id = id;
        this.name = name;
        this.description = description;
        if (properties == null) {
            this.properties = new HashMap<>();
        } else {
            this.properties = properties;
        }
    }

    public AbstractOMEntity(AbstractOMEntity entity) {
        if (entity != null) {
            this.id = entity.id;
            this.name = entity.name;
            this.description = entity.description;
            if (entity.properties == null) {
                this.properties = new HashMap<>();
            } else {
                this.properties = new HashMap<>(entity.properties);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getName()).append("]");
        sb.append("id=").append(id).append('\n');
        sb.append("name=").append(name).append('\n');
        sb.append("description=").append(description).append('\n');
        sb.append("properties=[\n");
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                sb.append(" - ").append(entry.getKey()).append( "=> ").append(entry.getValue()).append('\n');
            }
        }
        sb.append("]\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AbstractOMEntity that) {
            return Objects.equals(this.id,          that.id) &&
                   Objects.equals(this.description, that.description) &&
                   Objects.equals(this.name,        that.name) &&
                   Objects.equals(this.properties,  that.properties);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, properties);
    }
}
