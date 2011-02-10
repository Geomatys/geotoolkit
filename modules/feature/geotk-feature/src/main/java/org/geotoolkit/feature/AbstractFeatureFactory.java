/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.feature;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;

import org.opengis.feature.Association;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Factory for creating instances of the Attribute family of classes.
 * 
 * @author Ian Schneider
 * @author Gabriel Roldan
 * @author Justin Deoliveira
 * 
 * @version $Id$
 * @module pending
 */
public abstract class AbstractFeatureFactory implements FeatureFactory {

    protected static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    /**
     * Whether the features to be built should be self validating on construction and value setting, or not.
     * But default, not, subclasses do override this value
     */
    protected final boolean validating;

    protected AbstractFeatureFactory(final boolean validate) {
        this.validating = validate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Association createAssociation(final Attribute related, final AssociationDescriptor descriptor) {
        return new DefaultAssociation(related, descriptor);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Attribute createAttribute(final Object value, final AttributeDescriptor descriptor, final String id) {
        final AttributeType attType = descriptor.getType();
        if(descriptor instanceof GeometryDescriptor){
            return createGeometryAttribute(value, (GeometryDescriptor) descriptor, id, null);
        }else if(attType instanceof ComplexType){
            return createComplexAttribute((Collection)value, (ComplexType)attType, id);
        }

        if(id != null && !id.isEmpty()){
            return new DefaultAttribute(value, descriptor, FF.gmlObjectId(id));
        }else{
            return new DefaultAttribute(value, descriptor, null);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryAttribute createGeometryAttribute(
            final Object value, final GeometryDescriptor descriptor, final String id, final CoordinateReferenceSystem crs) {
        if(id != null && !id.isEmpty()){
            return new DefaultGeometryAttribute(value,descriptor, FF.gmlObjectId(id));
        }else{
            return new DefaultGeometryAttribute(value,descriptor, null);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComplexAttribute createComplexAttribute(
            final Collection<Property> value, final AttributeDescriptor descriptor, final String id) {
        if(descriptor.getType() instanceof FeatureType){
            return createFeature(value, descriptor, id);
        }
        if(id != null && !id.isEmpty()){
            return new DefaultComplexAttribute(value, descriptor, FF.gmlObjectId(id));
        }else{
            return new DefaultComplexAttribute(value, descriptor, null);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComplexAttribute createComplexAttribute(final Collection<Property> value, final ComplexType type, final String id) {
        if(type instanceof FeatureType){
            return createFeature(value, (FeatureType)type, id);
        }
        if(id != null && !id.isEmpty()){
            return new DefaultComplexAttribute(value, type, FF.gmlObjectId(id));
        }else{
            return new DefaultComplexAttribute(value, type, null);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature createFeature(final Collection<Property> value, final AttributeDescriptor descriptor, final String id) {        
        if(descriptor.getType() instanceof SimpleFeatureType){
            //in case we try to create a simple Feature with this method.
            final List<Property> properties = new ArrayList<Property>(value);
            return new DefaultSimpleFeature(descriptor, FF.featureId(id), properties, validating);
        }else{
            return new DefaultFeature(value, descriptor, FF.featureId(id));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature createFeature(final Collection<Property> value, final FeatureType type, final String id) {
        if(type instanceof SimpleFeatureType){
            final List<Property> properties = new ArrayList<Property>(value);
            return new DefaultSimpleFeature((SimpleFeatureType)type, FF.featureId(id), properties, validating);
        }
        return new DefaultFeature(value, type, FF.featureId(id));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeature createSimpleFeature(final Object[] array, final SimpleFeatureType type, final String id) {
        if (type.isAbstract()) {
            throw new IllegalArgumentException("Cannot create an feature of an abstract FeatureType " + type.getTypeName());
        }
        return new DefaultSimpleFeature(type, FF.featureId(id), array, validating);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeature createSimpleFeautre(final Object[] array, final AttributeDescriptor descriptor, final String id) {
        if (descriptor.getType().isAbstract()) {
            throw new IllegalArgumentException("Cannot create an feature of an abstract FeatureType " + descriptor.getType().getName());
        }
        return new DefaultSimpleFeature(descriptor, FF.featureId(id), array, validating);
    }
    
}
