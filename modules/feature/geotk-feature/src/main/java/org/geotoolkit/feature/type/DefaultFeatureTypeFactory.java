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

import java.util.Collection;
import java.util.List;

import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.Schema;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;


/**
 * This implementation is capable of creating a good default implementation of
 * the Types used in the feature model.
 * <p>
 * The implementation focus here is on corretness rather then efficiency or even
 * strict error messages. The code serves as a good example, but is not
 * optimized for any particular use.
 * </p>
 *
 * @author Jody Garnett
 */
public class DefaultFeatureTypeFactory implements FeatureTypeFactory {

    public DefaultFeatureTypeFactory() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Schema createSchema(final String uri) {
        return new DefaultSchema(uri);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AssociationDescriptor createAssociationDescriptor(final AssociationType type,
            final Name name, final int minOccurs, final int maxOccurs, final boolean isNillable){
        return new DefaultAssociationDescriptor(type, name, minOccurs, maxOccurs, isNillable);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeDescriptor createAttributeDescriptor(final AttributeType type, final Name name,
            final int minOccurs, final int maxOccurs, final boolean isNillable, final Object defaultValue){
        return new DefaultAttributeDescriptor(type, name, minOccurs, maxOccurs, isNillable, defaultValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryDescriptor createGeometryDescriptor(final GeometryType type, final Name name,
            final int minOccurs, final int maxOccurs, final boolean isNillable, final Object defaultValue){
        return new DefaultGeometryDescriptor(type, name, minOccurs, maxOccurs, isNillable, defaultValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AssociationType createAssociationType(final Name name, final AttributeType relatedType,
            final boolean isAbstract, final List restrictions, final AssociationType superType,
            final InternationalString description){
        return new DefaultAssociationType(name, relatedType,
                isAbstract, restrictions, superType, description);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType createAttributeType(final Name name, final Class binding,
            final boolean isIdentifiable, final boolean isAbstract, final List restrictions,
            final AttributeType superType, final InternationalString description){
        return new DefaultAttributeType(name, binding, isIdentifiable, isAbstract,
                restrictions, superType, description);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComplexType createComplexType(final Name name, final Collection schema,
            final boolean isIdentifiable, final boolean isAbstract, final List restrictions,
            final AttributeType superType, final InternationalString description){
        return new DefaultComplexType(name, schema, isIdentifiable, isAbstract,
                restrictions, superType, description);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryType createGeometryType(final Name name, final Class binding,
            final CoordinateReferenceSystem crs, final boolean isIdentifiable,
            final boolean isAbstract, final List restrictions, final AttributeType superType,
            final InternationalString description){
        return new DefaultGeometryType(name, binding, crs, isIdentifiable,
                isAbstract, restrictions, superType, description);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType createFeatureType(final Name name, final Collection schema,
            final GeometryDescriptor defaultGeometry, final boolean isAbstract,
            final List restrictions, final AttributeType superType, final InternationalString description){
        return new DefaultFeatureType(name, schema, defaultGeometry,
                isAbstract, restrictions, superType, description);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType createSimpleFeatureType(final Name name, final List<AttributeDescriptor> schema,
            final GeometryDescriptor defaultGeometry, final boolean isAbstract,
            final List<Filter> restrictions, final AttributeType superType,
            final InternationalString description){
        return new DefaultSimpleFeatureType(name, schema, defaultGeometry, isAbstract,
                restrictions, superType, description);
    }
}
