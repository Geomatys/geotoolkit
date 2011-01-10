/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009 Geomatys
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

import java.util.Collection;
import java.util.List;

import org.geotoolkit.util.Utilities;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

/**
 * 
 * Base implementation of FeatureType.
 * 
 * @author gabriel
 * @module pending
 */
public class DefaultFeatureType extends DefaultComplexType implements FeatureType {

    private GeometryDescriptor defaultGeometry;
    private CoordinateReferenceSystem crs;

    public DefaultFeatureType(final Name name, final Collection<PropertyDescriptor> schema,
            final GeometryDescriptor defaultGeometry, final boolean isAbstract,
            final List<Filter> restrictions, final AttributeType superType, final InternationalString description) {
        super(name, schema, true, isAbstract, restrictions, superType, description);
        this.defaultGeometry = defaultGeometry;

        if (defaultGeometry != null && !(defaultGeometry.getType() instanceof GeometryType)) {
            throw new IllegalArgumentException("defaultGeometry must have a GeometryType");
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        if (crs == null) {
            //try to affect crs by loading the geometry descriptor
            getGeometryDescriptor();

            //try to find other geometries to set the crs
            if (crs == null) {
                for (PropertyDescriptor property : descriptors) {
                    if (property instanceof GeometryDescriptor) {
                        GeometryDescriptor geometry = (GeometryDescriptor) property;
                        CoordinateReferenceSystem geomCRS = geometry.getType().getCoordinateReferenceSystem();
                        if (geomCRS != null) {
                            crs = geomCRS;
                            break;
                        }
                    }
                }
            }
        }

        return crs;
    }

     public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
         this.crs = crs;
     }
     
    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryDescriptor getGeometryDescriptor() {
        if (defaultGeometry == null) {
            for (PropertyDescriptor property : descriptors) {
                if (property instanceof GeometryDescriptor) {
                    defaultGeometry = (GeometryDescriptor) property;

                    //initialize crs if we can
                    if(crs == null){
                        //we know that the geom crs might be null, even if it's the case, that wont have any effect.
                        crs = defaultGeometry.getType().getCoordinateReferenceSystem();
                    }
                    break;
                }
            }
        }
        return defaultGeometry;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DefaultFeatureType && super.equals(o)) {
            FeatureType that = (FeatureType) o;
            return Utilities.equals(this.defaultGeometry, that.getGeometryDescriptor());
                
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();

        if (defaultGeometry != null) {
            hashCode = hashCode ^ defaultGeometry.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (getCoordinateReferenceSystem() != null) {
            sb.append("crs=").append(getCoordinateReferenceSystem().getName());
        } else {
            sb.append("crs null");
        }
        return sb.toString();
    }
}
