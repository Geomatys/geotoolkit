/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.observation.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ProcedureDataset extends Procedure {

    /**
     * The SML type of the process (System, Component, ...)
     */
    public final String type;

    /**
     * The observation type of the process (timeseries, trajectory, profile...)
     */
    public final String omType;

    public final List<ProcedureDataset> children = new ArrayList<>();

    public final GeoSpatialBound spatialBound = new GeoSpatialBound();

    public final List<Field> fields = new ArrayList<>();

    // for JSON
    protected ProcedureDataset() {
        this.type = null;
        this.omType = null;
    }

    public ProcedureDataset(final String id, final String name, final String description, final String type, final String omType, final Collection<Field> fields , Map<String, Object> properties) {
        super(id, name, description, properties);
        this.type = type;
        this.omType = omType;
        this.fields.addAll(fields);
    }

    public ProcedureDataset(final Procedure proc, final String type, final String omType, final Collection<Field> fields) {
        super(proc);
        this.type = type;
        this.omType = omType;
        this.fields.addAll(fields);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        return super.equals(obj)
               && obj instanceof ProcedureDataset that
                && Objects.equals(this.type, that.type)
                && Objects.equals(this.omType, that.omType)
                && Objects.equals(this.children, that.children)
                && Objects.equals(this.fields, that.fields);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 79* Objects.hash(type, omType, children, fields);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());

        if (type != null) {
            sb.append("type:").append(type).append("\n");
        }
        if (omType != null) {
            sb.append("omType:").append(omType).append("\n");
        }
        sb.append("children:\n");
        for (ProcedureDataset child : children) {
            sb.append(child).append("\n");
        }
        sb.append("spatialBound:").append(spatialBound).append('\n');
        sb.append("fields:\n");
        for (Field f : fields) {
            sb.append(f).append("\n");
        }
        return sb.toString();
    }
}
