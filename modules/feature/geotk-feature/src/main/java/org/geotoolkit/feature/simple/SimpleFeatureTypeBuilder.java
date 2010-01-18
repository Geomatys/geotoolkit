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
package org.geotoolkit.feature.simple;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.Schema;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import com.vividsolutions.jts.geom.Geometry;


/**
 * A builder for simple feature types.
 * <p>
 * Simple Usage:
 * <pre>
 *  <code>
 *  //create the builder
 *  SimpleTypeBuilder builder = new SimpleTypeBuilder();
 *
 *  //set global state
 *  builder.setName( "testType" );
 *  builder.setNamespaceURI( "http://www.geotoolkit.org/" );
 *  builder.setSRS( "EPSG:4326" );
 *
 *  //add attributes
 *  builder.add( "intProperty", Integer.class );
 *  builder.add( "stringProperty", String.class );
 *  builder.add( "pointProperty", Point.class );
 *
 *  //add attribute setting per attribute state
 *  builder.minOccurs(0).maxOccurs(2).nillable(false).add("doubleProperty",Double.class);
 *
 *  //build the type
 *  SimpleFeatureType featureType = builder.buildFeatureType();
 *  </code>
 * </pre>
 * </p>
 * This builder builds type by maintaining state. Two types of state are maintained:
 * <i>Global Type State</i> and <i>Per Attribute State</i>. Methods which set
 * global state are named <code>set&lt;property>()</code>. Methods which set per attribute
 * state are named <code>&lt;property>()</code>. Furthermore calls to per attribute
 * </p>
 * <p>
 * Global state is reset after a call to {@link #buildFeatureType()}. Per
 * attribute state is reset after a call to {@link #add}.
 * </p>
 * <p>
 * A default geometry for the feature type can be specified explictly via
 * {@link #setDefaultGeometry(String)}. However if one is not set the first
 * geometric attribute ({@link GeometryType}) added will be resulting default.
 * So if only specifying a single geometry for the type there is no need to
 * call the method. However if specifying multiple geometries then it is good
 * practice to specify the name of the default geometry type. For instance:
 * <code>
 * 	<pre>
 *  builder.add( "pointProperty", Point.class );
 *  builder.add( "lineProperty", LineString.class );
 *  builder.add( "polygonProperty", "polygonProperty" );
 *
 *  builder.setDefaultGeometry( "lineProperty" );
 * 	</pre>
 * </code>
 * </p>
 *
 * @author Justin Deolivera
 * @author Jody Garnett
 * @module pending
 */
public class SimpleFeatureTypeBuilder {

    /*
     * Factories an builders.
     */
    private final FeatureTypeFactory factory;
    private final AttributeTypeBuilder attributeTypeBuilder;
    private final AttributeDescriptorBuilder attributeDescriptorBuilder;


    /**
     * Map of java class bound to properties types.
     */
    private final Map<Class,AttributeType> bindings = new HashMap<Class, AttributeType>();
    /**
     * Name
     */
    private Name name = null;
    /**
     * Description of type.
     */
    private InternationalString description;
    /**
     * List of attributes.
     * Add some safe check so that we don't add null descriptors.
     */
    private final List<AttributeDescriptor> attributes = new ArrayList<AttributeDescriptor>(){

        @Override
        public boolean add(AttributeDescriptor e) {
            if(e == null){
                throw new NullPointerException("Attribut descriptor can not be null.");
            }
            return super.add(e);
        }

        @Override
        public void add(int i, AttributeDescriptor e) {
            if(e == null){
                throw new NullPointerException("Attribut descriptor can not be null.");
            }
            super.add(i, e);
        }

        @Override
        public boolean addAll(Collection<? extends AttributeDescriptor> clctn) {
            for(AttributeDescriptor att : clctn){
                if(att == null){
                    throw new NullPointerException("Attribut descriptor can not be null.");
                }
            }
            return super.addAll(clctn);
        }

        @Override
        public boolean addAll(int i, Collection<? extends AttributeDescriptor> clctn) {
            for(AttributeDescriptor att : clctn){
                if(att == null){
                    throw new NullPointerException("Attribut descriptor can not be null.");
                }
            }
            return super.addAll(i, clctn);
        }

    };
    /**
     * Additional restrictions on the type.
     */
    private final List<Filter> restrictions = new ArrayList<Filter>();
    /**
     * Name of the default geometry to use
     */
    private Name defaultGeometry;
    /**
     * flag controlling if the type is abstract.
     */
    private boolean isAbstract = false;
    /**
     * the parent type.
     */
    private SimpleFeatureType superType;

    /**
     * Constructs the builder.
     */
    public SimpleFeatureTypeBuilder() {
        this(null);
    }

    /**
     * Constructs the builder specifying the factory for creating feature and
     * feature collection types.
     */
    public SimpleFeatureTypeBuilder(final FeatureTypeFactory factory) {
        if(factory == null){
            this.factory = new DefaultFeatureTypeFactory();
        }else{
            this.factory = factory;
        }

        attributeTypeBuilder = new AttributeTypeBuilder(this.factory);
        attributeDescriptorBuilder = new AttributeDescriptorBuilder(this.factory);
        setBindings(new DefaultSimpleSchema());
        reset();
    }

    /**
     * Initializes the builder with state from a pre-existing feature type.
     */
    public void copy(final SimpleFeatureType type) {
        if (type == null) {
            throw new NullPointerException("Can not copy information from a Null type.");
        }

        name = type.getName();
        description = type.getDescription();
        
        restrictions.clear();
        restrictions.addAll(type.getRestrictions());

        attributes.clear();
        attributes.addAll(type.getAttributeDescriptors());

        isAbstract = type.isAbstract();
        superType = (SimpleFeatureType) type.getSuper();
    }


    /**
     * Completely resets all builder state.
     *
     */
    public void reset() {
        name = null;
        description = null;
        restrictions.clear();
        attributes.clear();
        isAbstract = false;
        superType = BasicFeatureTypes.FEATURE;
    }

    /**
     * Sets the local name and namespace uri of the built type.
     */
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

    /**
     * The name of the built type.
     */
    public Name getName() {
        return name;
    }

    /**
     * Sets the description of the built type.
     */
    public void setDescription(final InternationalString description) {
        this.description = description;
    }

    /**
     * The description of the built type.
     */
    public InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the name of the default geometry attribute of the built type.
     * You should use the the method with Name parameter instead.
     * This method will create a Name without namespace and refer to the first
     * attribut which local name part match this string.
     */
    public void setDefaultGeometry(final String defaultGeometryName) {
        for(AttributeDescriptor desc : attributes){
            if(desc.getLocalName().equals(defaultGeometryName)){
                this.defaultGeometry = desc.getName();
                return;
            }
        }
        throw new IllegalArgumentException("No matching attributs for name "+ defaultGeometryName);
    }

    /**
     * Sets the name of the default geometry attribute of the built type.
     */
    public void setDefaultGeometry(final Name defaultGeometryName) {
        for(AttributeDescriptor desc : attributes){
            if(desc.getName().equals(defaultGeometryName)){
                this.defaultGeometry = desc.getName();
                return;
            }
        }
        throw new IllegalArgumentException("No matching attributs for name "+ defaultGeometryName);
    }

    /**
     * The name of the default geometry attribute of the built type.
     */
    public Name getDefaultGeometry() {
        return defaultGeometry;
    }

    /**
     * Sets the flag controlling if the resulting type is abstract.
     */
    public void setAbstract(final boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    /**
     * The flag controlling if the resulting type is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Sets the super type of the built type.
     */
    public void setSuperType(final SimpleFeatureType superType) {
        this.superType = superType;
    }

    /**
     * The super type of the built type.
     */
    public SimpleFeatureType getSuperType() {
        return superType;
    }

    /**
     * Specifies an attribute type binding.
     * <p>
     * This method is used to associate an attribute type with a java class.
     * The class is retreived from <code>type.getBinding()</code>. When the
     * {@link #add(String, Class)} method is used to add an attribute to the
     * type being built, this binding is used to locate the attribute type.
     * </p>
     *
     * @param type The attribute type.
     */
    public void addBinding(final AttributeType type) {
        bindings.put(type.getBinding(), type);
    }

    /**
     * Specifies a number of attribute type bindings.
     *
     * @param schema The schema containing the attribute types.
     *
     * @see #addBinding(org.opengis.feature.type.AttributeType)
     */
    public void addBindings(final Schema schema) {
        for (Iterator itr = schema.values().iterator(); itr.hasNext();) {
            AttributeType type = (AttributeType) itr.next();
            addBinding(type);
        }
    }

    /**
     * Specifies a number of attribute type bindings clearing out all existing
     * bindings.
     *
     * @param schema The schema contianing attribute types.
     *
     * @see #addBinding(org.opengis.feature.type.AttributeType)
     */
    public void setBindings(final Schema schema) {
        bindings.clear();
        addBindings(schema);
    }

    /**
     * Looks up an attribute type which has been bound to a class.
     *
     * @param binding The class.
     *
     * @return AttributeType The bound attribute type.
     */
    public AttributeType getBinding(final Class<?> binding) {
        return (AttributeType) bindings.get(binding);
    }

    public void add(final String name, final Class binding){
        add(new DefaultName(name),binding);
    }

    /**
     * Adds a new attribute w/ provided name and class.
     *
     * <p>
     * The provided class is used to locate an attribute type binding previously
     * specified by {@link #addBinding(AttributeType)},{@link #addBindings(Schema)},
     * or {@link #setBindings(Schema)}.
     * </p>
     * <p>
     * If not such binding exists then an attribute type is created on the fly.
     * </p>
     * @param name The name of the attribute.
     * @param binding The class the attribute is bound to.
     */
    public void add(final Name name, final Class binding) {
        
        if(Geometry.class.isAssignableFrom(binding) ||
                org.opengis.geometry.Geometry.class.isAssignableFrom(binding)){
            throw new IllegalArgumentException("Use add(Name,Class,CRS) method to add geometric fields.");
        }

        attributeTypeBuilder.reset();
        attributeTypeBuilder.setName(name);
        attributeTypeBuilder.setBinding(binding);
        final AttributeType type = attributeTypeBuilder.buildType();

        attributeDescriptorBuilder.reset();
        attributeDescriptorBuilder.setName(name);
        attributeDescriptorBuilder.setType(type);
        attributeDescriptorBuilder.setMinOccurs(0);
        attributeDescriptorBuilder.setMinOccurs(1);
        attributeDescriptorBuilder.setNillable(true);
        final AttributeDescriptor descriptor = attributeDescriptorBuilder.buildDescriptor();

        add(descriptor);
    }

    /**
     * Adds a descriptor directly to the builder.
     * <p>
     * Use of this method is discouraged. Consider using {@link #add(String, Class)}.
     * </p>
     */
    public void add(final AttributeDescriptor descriptor) {
        attributes.add(descriptor);
    }

    /**
     * Removes an attribute from the builder
     *
     * @param attributeName the name of the AttributeDescriptor to remove
     *
     * @return the AttributeDescriptor with the name attributeName
     * @throws IllegalArgumentException if there is no AttributeDescriptor with the name attributeName
     */
    public AttributeDescriptor remove(final String attributeName) {
        for(final AttributeDescriptor att : attributes){
            if(att.getLocalName().equals(attributeName)){
                attributes.remove(att);
                return att;
            }
        }

        throw new IllegalArgumentException(attributeName + " is not an existing attribute descriptor in this builder");
    }

    /**
     * Adds a descriptor to the builder by index.
     * <p>
     * Use of this method is discouraged. Consider using {@link #add(String, Class)}.
     * </p>
     */
    public void add(final int index, final AttributeDescriptor descriptor) {
        attributes.add(index, descriptor);
    }

    /**
     * Adds a list of descriptors directly to the builder.
     * <p>
     * Use of this method is discouraged. Consider using {@link #add(String, Class)}.
     * </p>
     */
    public void addAll(final List<AttributeDescriptor> descriptors) {
        for (AttributeDescriptor ad : descriptors) {
            add(ad);
        }
    }

    /**
     * Adds an array of descriptors directly to the builder.
     * <p>
     * Use of this method is discouraged. Consider using {@link #add(String, Class)}.
     * </p>
     */
    public void addAll(final AttributeDescriptor[] descriptors) {
        for (AttributeDescriptor ad : descriptors) {
            add(ad);
        }
    }

    public void add(final String name, final Class binding, final CoordinateReferenceSystem crs){
        add(new DefaultName(name),binding,crs);
    }

    /**
     * Adds a new geometric attribute w/ provided name, class, and spatial
     * reference system identifier
     * <p>
     * The <tt>srs</tt> parameter may be <code>null</code>.
     * </p>
     * @param name The name of the attribute.
     * @param binding The class that the attribute is bound to.
     * @param srs The srs of of the geometry, can not be <code>null</code>.
     */
    public void add(final Name name, final Class binding, final String srs) {
        add(name, binding, decode(srs));
    }

    /**
     * Adds a new geometric attribute w/ provided name, class, and coordinate
     * reference system.
     * <p>
     * The <tt>crs</tt> parameter may be <code>null</code>.
     * </p>
     * @param name The name of the attribute.
     * @param binding The class that the attribute is bound to.
     * @param crs The crs of of the geometry, can not be <code>null</code>.
     */
    public void add(final Name name, final Class binding, final CoordinateReferenceSystem crs) {

        if(!( Geometry.class.isAssignableFrom(binding) ||
                org.opengis.geometry.Geometry.class.isAssignableFrom(binding)) ){
            throw new IllegalArgumentException("Use add(Name,Class) method to add non-geometric fields. Field type found : " + binding);
        }

        attributeTypeBuilder.reset();
        attributeTypeBuilder.setName(name);
        attributeTypeBuilder.setBinding(binding);
        attributeTypeBuilder.setCRS(crs);
        final GeometryType type = attributeTypeBuilder.buildGeometryType();

        attributeDescriptorBuilder.reset();
        attributeDescriptorBuilder.setName(name);
        attributeDescriptorBuilder.setType(type);
        final AttributeDescriptor descriptor = attributeDescriptorBuilder.buildDescriptor();

        add(descriptor);
    }

    /**
     * Adds a new geometric attribute w/ provided name, class, and spatial
     * reference system identifier
     * <p>
     * The <tt>srid</tt> parameter may be <code>null</code>.
     * </p>
     * @param name The name of the attribute.
     * @param binding The class that the attribute is bound to.
     * @param srid The srid of of the geometry, may be <code>null</code>.
     */
    public void add(final Name name, final Class binding, final Integer srid) {
        add(name, binding, decode("EPSG:" + srid));
    }

    /**
     * Directly sets the list of attributes.
     * @param attributes the new list of attributes, or null to reset the list
     */
    public void setAttributes(final List<AttributeDescriptor> attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.addAll(attributes);
        }
    }

    /**
     * Directly sets the list of attributes.
     * @param attributes the new list of attributes, or null to reset the list
     */
    public void setAttributes(final AttributeDescriptor[] attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.addAll(Arrays.asList(attributes));
        }
    }

    /**
     * Builds a feature type from compiled state.
     * <p>
     * After the type is built the running list of attributes is cleared.
     * </p>
     * @return The built feature type.
     */
    public SimpleFeatureType buildFeatureType() {
        GeometryDescriptor defaultGeometry = null;

        //was a default geometry set?
        if (this.defaultGeometry != null) {

            for(final AttributeDescriptor desc : attributes){
                if(desc.getName().equals(this.defaultGeometry)){
                    //ensure the attribute is a geometry attribute
                    if(!(desc instanceof GeometryDescriptor)){
                        throw new IllegalStateException("Default geometry : "+ this.defaultGeometry + " is not a GeometryDescriptor.");
                    }
                    defaultGeometry = (GeometryDescriptor) desc;
                    break;
                }
            }

            if(defaultGeometry == null){
                throw new IllegalStateException("Default geometry : "+ this.defaultGeometry + " can not be found in the attributs list.");
            }
        }

        if (defaultGeometry == null) {
            //none was set by name, look for first geometric type
            for (final AttributeDescriptor att : attributes) {
                if (att instanceof GeometryDescriptor) {
                    defaultGeometry = (GeometryDescriptor) att;
                    break;
                }
            }
        }

        return factory.createSimpleFeatureType(
                name, attributes, defaultGeometry, isAbstract,
                restrictions, superType, description);
    }


    /**
     * Decodes a srs, supplying a useful error message if there is a problem.
     */
    private CoordinateReferenceSystem decode(final String srs) {
        try {
            return CRS.decode(srs);
        } catch (Exception e) {
            String msg = "SRS '" + srs + "' unknown:" + e.getLocalizedMessage();
            throw (IllegalArgumentException) new IllegalArgumentException(msg).initCause(e);
        }
    }

    /**
     * @param original
     * @param types
     * @return SimpleFeatureType
     */
    public static SimpleFeatureType retype(final SimpleFeatureType original, final Name[] types) {
        final SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();

        //initialize the builder
        b.copy(original);

        //clear the attributes
        b.attributes.clear();

        //add attributes in order
        for (int i=0; i<types.length; i++) {
            b.add(original.getDescriptor(types[i]));
        }

        return b.buildFeatureType();
    }
}
