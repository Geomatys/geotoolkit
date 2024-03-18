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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CompositePhenomenon extends Phenomenon implements org.opengis.observation.CompositePhenomenon {

    private List<Phenomenon> components;

    private CompositePhenomenon() {}

    public CompositePhenomenon(String id) {
        this(id, null, null, null, null, null);
    }

    public CompositePhenomenon(String id, String name, String definition, String description, Map<String, Object> properties, List<Phenomenon> components) {
        super(id, name, definition, description, properties);
        if (components == null) {
            this.components = new ArrayList<>();
        } else {
            this.components = components;
        }
    }

    @Override
    public List<Phenomenon> getComponent() {
        return components;
    }

    public void setComponent(List<Phenomenon> component) {
        if (component != null) {
            this.components = component;
        } else {
            this.components.clear();
        }
    }

    @Override
    @JsonIgnore
    public org.opengis.observation.Phenomenon getBase() {
        return this;
    }

    @Override
    @JsonIgnore
    public int getDimension() {
        return components.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        for (org.opengis.observation.Phenomenon entry : components) {
            sb.append(" - ").append(entry).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CompositePhenomenon that && super.equals(obj)) {
            return Objects.equals(this.components, that.components);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + super.hashCode();
        hash = 23 * hash + Objects.hashCode(this.components);
        return hash;
    }
}
