/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter;


import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.feature.privy.FeatureExpression;
import org.apache.sis.referencing.CRS;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.filter.binding.Binding;
import org.geotoolkit.filter.binding.Bindings;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.ValueReference;
import org.opengis.util.FactoryException;

/**
 * Immutable property name expression.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultPropertyName extends AbstractExpression implements ValueReference<Object,Object>, FeatureExpression<Object,Object> {

    private final String property;

    /**
     * Stores the last accessor returned.
     */
    private Binding lastAccessor;
    private final boolean isSimple;

    public DefaultPropertyName(final String property) {
        ensureNonNull("property name", property);
        this.property = property;
        isSimple = (property == null || property.isEmpty() || !(property.contains("/") || property.startsWith("*")));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getXPath() {
        return property;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object apply(final Object candidate) {
        if (isSimple && candidate instanceof Feature) {
            return ((Feature) candidate).getValueOrFallback(property, null);
        }

        final Class cs;
        if(candidate == null){
            cs = Object.class;
        }else{
            cs = candidate.getClass();
        }

        Binding cp = lastAccessor;
        final Object value;
        if (cp != null && cp.getBindingClass() != Object.class && cp.getBindingClass().isAssignableFrom(cs)) {
            value = cp.get( candidate, property, null );
        } else {
            final Binding accessor = Bindings.getBinding(cs,property);
            if (accessor == null) {
                return null;
            }
            lastAccessor = accessor;
            value = accessor.get( candidate, property, null );
        }
        /*
         * Apache SIS is not aware of the SRIDGenerator convention, which adds a (1 << 28) bitmask if
         * the authority is CRS. For avoiding a "no such EPSG code error", resolve the CRS in advance.
         */
        if (value instanceof org.locationtech.jts.geom.Geometry) {
            final org.locationtech.jts.geom.Geometry g = (org.locationtech.jts.geom.Geometry) value;
            if (g.getUserData() == null) {
                int srid = g.getSRID();
                if (srid != 0) try {
                    g.setUserData(CRS.forCode(SRIDGenerator.toSRS(srid, SRIDGenerator.Version.V1)));
                } catch (FactoryException e) {
                    Logging.unexpectedException(null, getClass(), "apply", e);
                }
            }
        }
        return value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return "{"+property+"}";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultPropertyName other = (DefaultPropertyName) obj;
        if ((this.property == null) ? (other.property != null) : !this.property.equals(other.property)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.property != null ? this.property.hashCode() : 0);
        return hash;
    }

    @Override
    public PropertyTypeBuilder expectedType(FeatureType valueType, FeatureTypeBuilder addTo) {
        PropertyType pt = (PropertyType) apply(valueType);
        if (pt != null) return addTo.addProperty(pt);
        try {
            PropertyType type = valueType.getProperty(property);        // May throw IllegalArgumentException.
            while (type instanceof Operation) {
                final IdentifiedType result = ((Operation) type).getResult();
                if (result != type && result instanceof PropertyType) {
                    type = (PropertyType) result;
                } else if (result instanceof FeatureType) {
                    return addTo.addAssociation((FeatureType) result).setName(property);
                } else {
                    return null;
                }
            }
            return addTo.addProperty(type);
        } catch (PropertyNotFoundException ex) {
            return addTo.addAttribute(Object.class).setName(property);
        }
    }
}
