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

import java.util.List;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;


/**
 * AttributeType for hold geometry implementations, maintains CRS information.
 * @module pending
 */
public class DefaultGeometryType extends DefaultAttributeType<AttributeType> implements GeometryType {

    protected CoordinateReferenceSystem crs;

    public DefaultGeometryType(final Name name, final Class binding, final CoordinateReferenceSystem crs,
            final boolean identified, final boolean isAbstract, final List<Filter> restrictions,
            final AttributeType superType, final InternationalString description){
        super(name, binding, identified, isAbstract, restrictions, superType, description);
        this.crs = crs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }
    
    /**
     * Set the CoordinateReferenceSystem to the GeometryType
     * @param crs 
     */
    public void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }
    
}
