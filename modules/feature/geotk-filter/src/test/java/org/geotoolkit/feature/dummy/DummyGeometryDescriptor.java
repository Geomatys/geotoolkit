/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.feature.dummy;

import java.util.List;
import java.util.Map;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;


/**
 * Dummy implementation of {@link GeometryDescriptor}.
 * @module pending
 */
public final class DummyGeometryDescriptor implements GeometryDescriptor {

    private final String name;
    private final Class type;
    private final CoordinateReferenceSystem crs;

    public DummyGeometryDescriptor(String name, Class type, CoordinateReferenceSystem crs) {
        this.name = name;
        this.type = type;
        this.crs = crs;
    }

    @Override
    public String getLocalName() {
        return name;
    }

    @Override
    public Object getDefaultValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Name getName() {
        return new DummyName(name);
    }

    @Override
    public int getMinOccurs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMaxOccurs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNillable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Object, Object> getUserData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GeometryType getType() {
        return new DummyGeometryType(DummyGeometryDescriptor.this.getName(), type, crs);
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

}
