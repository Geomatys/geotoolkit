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
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;


/**
 * Dummy implementation of {@link GeometryType}.
 */
public final class DummyGeometryType implements GeometryType {
    private final CoordinateReferenceSystem crs;
    private final Class classe;
    private final Name name;

    public DummyGeometryType(Name name, Class classe, CoordinateReferenceSystem crs) {
        this.crs = crs;
        this.classe = classe;
        this.name = name;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public boolean isIdentified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AttributeType getSuper() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Class<?> getBinding() {
        return classe;
    }

    @Override
    public boolean isAbstract() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Filter> getRestrictions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InternationalString getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Object, Object> getUserData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
