/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.factory.FactoryFinder;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Builder for attribute types and descriptors.
 * <p>
 * Building an attribute type:
 * <pre>
 * <code>
 *  //create the builder
 * 	AttributeTypeBuilder builder = new AttributeTypeBuilder();
 *
 *  //set type information
 *  builder.setName( "intType" ):
 *  builder.setBinding( Integer.class );
 *  builder.setNillable( false );
 *
 *  //build the type
 *  AttributeType type = builder.buildType();
 * </code>
 * </pre>
 * </p>
 * <p>
 * Building an attribute descriptor:
 * <pre>
 * <code>
 *  //create the builder
 * 	AttributeTypeBuilder builder = new AttributeTypeBuilder();
 *
 *  //set type information
 *  builder.setName( "intType" ):
 *  builder.setBinding( Integer.class );
 *  builder.setNillable( false );
 *
 *  //set descriptor information
 *  builder.setMinOccurs(0);
 *  builder.setMaxOccurs(1);
 *  builder.setNillable(true);
 *
 *  //build the descriptor
 *  AttributeDescriptor descriptor = builder.buildDescriptor("intProperty");
 * </code>
 * </pre>
 * <p>
 * This class maintains state and is not thread safe.
 * </p>
 *
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 */
public class AttributeDescriptorBuilder {

    /**
     * factory
     */
    private final FeatureTypeFactory factory;

    private Name name = null;
    private int minOccurs = 1;
    private int maxOccurs = 1;
    /**
     * True if value is allowed to be null.
     * The default value is <code>true</code>.
     */
    private boolean isNillable = true;
    /**
     * User data for the attribute.
     */
    private final Map<Object,Object> userData = new HashMap<Object, Object>();
    private Object defaultValue = null;
    private AttributeType type = null;
    
    /**
     * Constructs the builder.
     *
     */
    public AttributeDescriptorBuilder() {
        this(null);
    }

    /**
     * Constructs the builder specifying the factory used to build attribute types.
     */
    public AttributeDescriptorBuilder(FeatureTypeFactory factory) {
        if(factory == null){
            this.factory = FactoryFinder.getFeatureTypeFactory(null);
        }else{
            this.factory = factory;
        }
    }

    public void reset(){
        minOccurs = 1;
        maxOccurs = 1;
        isNillable = true;
        userData.clear();
        defaultValue = null;
        type = null;
        userData.clear();
    }

    /**
     * Initializes builder state from another attribute descriptor.
     */
    public void copy(final AttributeDescriptor descriptor) {
        reset();
        name = descriptor.getName();
        defaultValue = descriptor.getDefaultValue();
        minOccurs = descriptor.getMinOccurs();
        maxOccurs = descriptor.getMaxOccurs();
        isNillable = descriptor.isNillable();
        type = descriptor.getType();
        userData.putAll(descriptor.getUserData());
    }

    public void setName(final Name name) {
        this.name = name;
    }

    public void setName(String localPart){
        this.name = new DefaultName(localPart);
    }

    public void setName(String namespace, String localPart){
        this.name = new DefaultName(namespace,localPart);
    }

    public void setName(String namespace, String separator, String localPart){
        this.name = new DefaultName(namespace,separator,localPart);
    }

    public Name getName() {
        return name;
    }

    public void addUserData(final Object key, final Object value) {
        userData.put(key, value);
    }

    public void setNillable(final boolean isNillable) {
        this.isNillable = isNillable;
    }

    public boolean isIsNillable() {
        return isNillable;
    }

    public void setMinOccurs(final Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMaxOccurs(final int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public AttributeType getType() {
        return type;
    }

    /**
     * Try to find a default value that matches the type binding class.
     */
    public void findBestDefaultValue(){
        if(type == null){
            throw new IllegalStateException("you can not call this method before the type has been set.");
        }
        if(!isNillable){
            try {
                defaultValue = FeatureUtilities.defaultValue(type.getBinding());
            } catch (Exception e) {
                //do nothing
            }
        }
    }

    /**
     * @return AttributeDescriptor if type is an AttributType or
     *         GeometryDescriptor if type is a GeometryType
     */
    public AttributeDescriptor buildDescriptor(){
        if(type == null){
            throw new IllegalStateException("Descriptor type has not been configured");
        }
        if(name == null){
            throw new IllegalStateException("Descriptor name has not been configured");
        }

        if(defaultValue != null){
            if(!type.getBinding().isInstance(defaultValue)){
                throw new IllegalStateException("Default value class : "+defaultValue.getClass()
                        +" doesn't match type binding : "+ type.getBinding());
            }
        }

        final AttributeDescriptor descriptor;
        if(type instanceof GeometryType){
            descriptor = factory.createGeometryDescriptor(
                (GeometryType)type, name, getMinOccurs(), maxOccurs, isNillable, defaultValue);
        }else{
            descriptor = factory.createAttributeDescriptor(
                type, name, getMinOccurs(), maxOccurs, isNillable, defaultValue);
        }

        // set the user data
        descriptor.getUserData().putAll(userData);
        return descriptor;
    }


    public AttributeDescriptor create(final Name name, final Class binding,
            int min, int max, boolean nillable, Map<Object,Object> userData) {
        return create(name,binding,null,min,max,nillable,userData);
    }

    public AttributeDescriptor create(final Name name, final Class binding, final CoordinateReferenceSystem crs,
            int min, int max, boolean nillable, Map<Object,Object> userData) {

        final PropertyType at;
        if(Geometry.class.isAssignableFrom(binding) ||
                org.opengis.geometry.Geometry.class.isAssignableFrom(binding)){
            at = factory.createGeometryType(name, binding, crs, false, false, null, null, null);
        }
//TODO must check that we can allow collection as simple attribut types
//        else if(Collection.class.isAssignableFrom(binding) ||
//                org.opengis.geometry.Geometry.class.isAssignableFrom(binding)){
//            throw new IllegalArgumentException("Binding class is : "+ binding +" this is a Complex type. Create a complex type using the factory");
//        }
        else{
            //non geometric field
            at = factory.createAttributeType(name, binding, false, false, null, null, null);
        }

        return create(at,name,crs,min,max,nillable,userData);
    }

    public AttributeDescriptor create(final PropertyType at, final Name name,
            int min, int max, boolean nillable, Map<Object,Object> userData){
        return create(at,name,null,min,max,nillable,userData);
    }

    public AttributeDescriptor create(final PropertyType at, final Name name, final CoordinateReferenceSystem crs,
            int min, int max, boolean nillable, Map<Object,Object> userData){
        Object defaultValue = null;
        if(!nillable){
            //search for the best default value.
            try {
                defaultValue = FeatureUtilities.defaultValue(at.getBinding());
            } catch (Exception e) {
                //do nothing
            }
        }

        final AttributeDescriptor desc;
        if(at instanceof GeometryType){
            desc = factory.createGeometryDescriptor((GeometryType)at, name, min, max, nillable, defaultValue);
        }else if(at instanceof AttributeType){
            desc = factory.createAttributeDescriptor((AttributeType)at, name, min, max, nillable, defaultValue);
        }else{
            throw new IllegalArgumentException("Property type is : "+ at.getClass() +" This type is not supported yet.");
        }

        if(userData != null){
            desc.getUserData().putAll(userData);
        }

        return desc;
    }

}