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

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Procedure extends AbstractOMEntity implements org.opengis.observation.Process {

    // for JSON
    protected Procedure() {}

    public Procedure(String id) {
        super(id, null, null, null);
    }

    public Procedure(String id, String name, String description, Map<String, Object> properties) {
        super(id, name, description, properties);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Procedure) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + super.hashCode();
        return hash;
    }
}
