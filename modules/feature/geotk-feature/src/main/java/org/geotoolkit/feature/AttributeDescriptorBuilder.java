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
package org.geotoolkit.feature;

import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

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
            this.factory = new DefaultFeatureTypeFactory();
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
    }

    /**
     * Initializes builder state from another attribute descriptor.
     */
    public void copy(final AttributeDescriptor descriptor) {
        name = descriptor.getName();
        defaultValue = descriptor.getDefaultValue();
        minOccurs = descriptor.getMinOccurs();
        maxOccurs = descriptor.getMaxOccurs();
        isNillable = descriptor.isNillable();
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

    public void setMinOccurs(final int minOccurs) {
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
        try {
            defaultValue = FeatureUtilities.defaultValue(type.getBinding());
        } catch (Exception e) {
            //do nothing
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
            if(type.getBinding().isInstance(defaultValue)){
                throw new IllegalStateException("Default value class : "+defaultValue.getClass()
                        +" doesn't match type binding : "+ type.getBinding());
            }
        }

        final AttributeDescriptor descriptor;
        if(type instanceof GeometryType){
            descriptor = factory.createGeometryDescriptor(
                (GeometryType)type, name, minOccurs, maxOccurs, isNillable, defaultValue);
        }else{
            descriptor = factory.createAttributeDescriptor(
                type, name, minOccurs, maxOccurs, isNillable, defaultValue);
        }

        // set the user data
        descriptor.getUserData().putAll(userData);
        return descriptor;
    }

}