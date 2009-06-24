/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.type;

import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class DefaultGeometryDescriptor extends DefaultAttributeDescriptor
        implements GeometryDescriptor {

    public DefaultGeometryDescriptor(final GeometryType type, final Name name, final int min,
            final int max, final boolean isNillable, final Object defaultValue)
    {
        super(type, name, min, max, isNillable, defaultValue);

    }

    @Override
    public GeometryType getType() {
        return (GeometryType) super.getType();
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return getType().getCoordinateReferenceSystem();
    }

    @Override
    public String getLocalName() {
        return getName().getLocalPart();
    }
}
