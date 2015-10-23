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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotoolkit.util.NamesExt;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.opengis.filter.identity.FeatureId;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.util.GenericName;

/**
 * A builder for features.
 * <p>
 * Simple Usage:
 * <code>
 *  <pre>
 *  //type of features we would like to build ( assume schema = (geom:Point,name:String) )
 *  SimpleFeatureType featureType = ...
 *
 *   //create the builder
 *  SimpleFeatureBuilder builder = new SimpleFeatureBuilder();
 *
 *  //set the type of created features
 *  builder.setType( featureType );
 *
 *  //add the attributes
 *  builder.add( new Point( 0 , 0 ) );
 *  builder.add( "theName" );
 *
 *  //build the feature
 *  SimpleFeature feature = builder.buildFeature( "fid" );
 *  </pre>
 * </code>
 * </p>
 * <p>
 * This builder builds a feature by maintaining state. Each call to {@link #add(Object)}
 * creates a new attribute for the feature and stores it locally. When using the
 * add method to add attributes to the feature, values added must be added in the
 * same order as the attributes as defined by the feature type. The methods
 * {@link #setPropertyValue(String, Object)} and {@link #setPropertyValue(int, Object)} are used to add
 * attributes out of order.
 * </p>
 * <p>
 * Each time the builder builds a feature with a call to {@link #buildFeature(String)}
 * the internal state is reset.
 * </p>
 * <p>
 * This builder can be used to copy features as well. The following code sample
 * demonstrates:
 * <code>
 * <pre>
 *  //original feature
 *  SimpleFeature original = ...;
 *
 *  //create and initialize the builder
 *  SimpleFeatureBuilder builder = new SimpleFeatureBuilder();
 *  builder.init(original);
 *
 *  //create the new feature
 *  SimpleFeature copy = builder.buildFeature( original.getID() );
 *
 *  </pre>
 * </code>
 * </p>
 * <p>
 * The builder also provides a number of static "short-hand" methods which can
 * be used when its not ideal to instantiate a new builder, thought this will
 * trigger some extra object allocations. In time critical code sections it's
 * better to instantiate the builder once and use it to build all the required
 * features.
 * <code>
 *   <pre>
 *   SimpleFeatureType type = ..;
 *   Object[] values = ...;
 *
 *   //build a new feature
 *   SimpleFeature feature = SimpleFeatureBuilder.build( type, values, "fid" );
 *
 *   ...
 *
 *   SimpleFeature original = ...;
 *
 *   //copy the feature
 *   SimpleFeature feature = SimpleFeatureBuilder.copy( original );
 *   </pre>
 * </code>
 * </p>
 * <p>
 * This class is not thread safe nor should instances be shared across multiple
 * threads.
 * </p>
 *
 * @author Justin Deoliveira
 * @author Jody Garnett
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureBuilder {

    /** the feature type */
    private final FeatureType featureType;
    /** the feature factory */
    private final FeatureFactory factory;
    private PropertyDescriptor[] descs;
    private final Map<String,Integer> names = new HashMap<>();
    /** the values */
    private Object[] values;
    /** pointer for next attribute */
    int next;
    private Map<Object, Object>[] userData;
    boolean validating;

    public FeatureBuilder(final FeatureType featureType) {
        this(featureType, FeatureFactory.LENIENT);
    }

    public FeatureBuilder(final FeatureType featureType, final FeatureFactory factory) {
        this.featureType = featureType;
        this.factory = factory;

        descs = featureType.getDescriptors().toArray(new PropertyDescriptor[0]);
        for(int i=0;i<descs.length;i++){
            final GenericName name = descs[i].getName();
            names.put(name.tip().toString(),i);
            names.put(NamesExt.toExpandedString(name),i);
            names.put(NamesExt.toExtendedForm(name),i);
        }
        reset();
    }

    public void reset() {
        values = new Object[featureType.getDescriptors().size()];
        next = 0;
        userData = null;
    }

    /**
     * Returns the simple feature type used by this builder as a feature template
     * @return FeatureType
     */
    public FeatureType getFeatureType() {
        return featureType;
    }

    /**
     * Initialize the builder with the provided feature.
     * <p>
     * This method adds all the attributes from the provided feature. It is
     * useful when copying a feature.
     * </p>
     */
    public void init(final SimpleFeature feature) {
        reset();

        for (Object value : feature.getAttributes()) {
            add(value);
        }
    }

    /**
     * Adds an attribute.
     * <p>
     * This method should be called repeatedly for the number of attributes as
     * specified by the type of the feature.
     * </p>
     */
    public void add(final Object value) {
        setPropertyValue(next, value);
        next++;
    }

    /**
     * Adds a list of attributes.
     */
    public void addAll(final List values) {
        for (Object value : values) {
            add(value);
        }
    }

    /**
     * Adds an array of attributes.
     */
    public void addAll(final Object[] values) {
        for (Object value : values) {
            add(value);
        }
    }

    /**
     * Adds an attribute value by name.
     * <p>
     * This method can be used to add attribute values out of order.
     * </p>
     *
     * @param name
     *            The name of the attribute.
     * @param value
     *            The value of the attribute.
     *
     * @throws IllegalArgumentException
     *             If no such attribute with teh specified name exists.
     */
    public void setPropertyValue(final GenericName name, final Object value) {
        FeatureBuilder.this.setPropertyValue(NamesExt.toExtendedForm(name), value);
    }

    /**
     * Adds an attribute value by name.
     * <p>
     * This method can be used to add attribute values out of order.
     * </p>
     *
     * @param name
     *            The name of the attribute.
     * @param value
     *            The value of the attribute.
     *
     * @throws IllegalArgumentException
     *             If no such attribute with teh specified name exists.
     */
    public void setPropertyValue(final String name, final Object value) {
        Integer index = names.get(name);
        if (index == null) {
            throw new IllegalArgumentException("No such attribute:" + name);
        }
        setPropertyValue(index, value);
    }

    /**
     * Adds an attribute value by index. *
     * <p>
     * This method can be used to add attribute values out of order.
     * </p>
     *
     * @param index
     *            The index of the attribute.
     * @param value
     *            The value of the attribute.
     */
    public void setPropertyValue(final int index, final Object value) {
        if (index >= values.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Can handle " + values.length + " attributes only.\n"
                    +"Can not add value " + value +" at index "+ index+"\n"
                    + featureType);
        }

        final AttributeDescriptor descriptor = (AttributeDescriptor) descs[index];
        values[index] = convert(value, descriptor);
        if (validating) {
            FeatureValidationUtilities.validate(descriptor, values[index]);
        }
    }

    private Object convert(Object value, final AttributeDescriptor descriptor) {
        //make sure the type of the value and the binding of the type match up
        if (value != null) {

            final Class target = descriptor.getType().getBinding();
            try {
                final Object converted = ObjectConverters.convert(value, target);
                if (converted != null) {
                    value = converted;
                }
            } catch (UnconvertibleObjectException e) {
                Logging.recoverableException(null, FeatureBuilder.class, "convert", e);
                // TODO - do we really want to ignore?
            }
        } else {
            //if the content is null and the descriptor says isNillable is false,
            // then set the default value
            if (!descriptor.isNillable()) {
                value = descriptor.getDefaultValue();
                if (value == null) {
                    //no default value, try to generate one
                    value = FeatureUtilities.defaultValue(descriptor.getType().getBinding());
                }
            }
        }
        return value;
    }

    /**
     * Builds the feature.
     * <p>
     * The specified <tt>id</tt> may be <code>null</code>. In this case an
     * id will be generated internally by the builder.
     * </p>
     * <p>
     * After this method returns, all internal builder state is reset.
     * </p>
     *
     * @param id
     *            The id of the feature, or <code>null</code>.
     *
     * @return The new feature.
     */
    public Feature buildFeature(String id) {
        // ensure id
        if (id == null) {
            id = FeatureUtilities.createDefaultFeatureId();
        }

        final Object[] values = this.values;
        final Map<Object, Object>[] userData = this.userData;
        reset();
        final Feature sf = factory.createSimpleFeature(values, (SimpleFeatureType)featureType, id);

        // handle the user data
        if (userData != null) {
            for (int i = 0; i < userData.length; i++) {
                if (userData[i] != null) {
                    sf.getProperty(descs[i].getName()).getUserData().putAll(userData[i]);
                }
            }
        }

        return sf;
    }

    /**
     * Quickly builds the feature using the specified values and id
     * @param id
     * @param values
     * @return SimpleFeature
     */
    public Feature buildFeature(final String id, final Object[] values) {
        addAll(values);
        return buildFeature(id);
    }

    /**
     * Internal method for a temporary FeatureId that can be assigned
     * a real value after a commit.
     * @param suggestedId suggsted id
     */
    public static FeatureId createDefaultFeatureIdentifier(final String suggestedId) {
        if (suggestedId != null) {
            return new DefaultFeatureId(suggestedId);
        }
        return new DefaultFeatureId(FeatureUtilities.createDefaultFeatureId());
    }

    /**
     * Static method to build a new feature.
     * <p>
     * If multiple features need to be created, this method should not be used
     * and instead an instance should be instantiated directly.
     * </p>
     * <p>
     * This method is a short-hand convenience which creates a builder instance
     * internally and adds all the specified attributes.
     * </p>
     */
    public static Feature build(final FeatureType type, final Object[] values,
            final String id) {
        final FeatureBuilder builder = new FeatureBuilder(type);
        builder.addAll(values);
        return builder.buildFeature(id);
    }

    /**
     * * Static method to build a new feature.
     * <p>
     * If multiple features need to be created, this method should not be used
     * and instead an instance should be instantiated directly.
     * </p>
     */
    public static Feature build(final FeatureType type, final List values, final String id) {
        return build(type, values.toArray(), id);
    }

    /**
     * Copies an existing feature, retyping it in the process.
     * <p> Be warned, this method will
     * create its own SimpleFeatureBuilder, which will trigger a scan of the SPI looking for
     * the current default feature factory, which is expensive and has scalability issues.<p>
     * If you need good performance consider using
     * {@link SimpleFeatureBuilder#retype(SimpleFeature, SimpleFeatureBuilder)} instead.
     * <p>
     * If the feature type contains attributes in which the original feature
     * does not have a value for, the value in the resulting feature is set to
     * <code>null</code>.
     * </p>
     * @param feature The original feature.
     * @param featureType The target feature type.
     *
     * @return The copied feature, with a new type.
     */
    public static Feature retype(final Feature feature, final FeatureType featureType) {
        final FeatureBuilder builder = new FeatureBuilder(featureType);
        return retype(feature, builder);
    }

    /**
     * Copies an existing feature, retyping it in the process.
     * <p>
     * If the feature type contains attributes in which the original feature
     * does not have a value for, the value in the resulting feature is set to
     * <code>null</code>.
     * </p>
     * @param feature The original feature.
     * @param builder A builder for the target feature type
     *
     * @return The copied feature, with a new type.
     * @since 2.5.3
     */
    public static Feature retype(final Feature feature, final FeatureBuilder builder) {
        builder.reset();
        for (PropertyDescriptor att : builder.getFeatureType().getDescriptors()) {
            final Object value = feature.getProperty(att.getName()).getValue();
            builder.setPropertyValue(att.getName(), value);
        }
        return builder.buildFeature(feature.getIdentifier().getID());
    }

    /**
     * Adds some user data to the next attributed added to the feature.
     * <p>
     * This value is reset when the next attribute is added.
     * </p>
     * @param key The key of the user data
     * @param value The value of the user data.
     */
    public FeatureBuilder userData(final Object key, final Object value) {
        return setUserData(next, key, value);
    }

    public FeatureBuilder setUserData(final int index, final Object key, final Object value) {
        if (userData == null) {
            userData = new Map[values.length];
        }
        if (userData[index] == null) {
            userData[index] = new HashMap<Object, Object>();
        }
        userData[index].put(key, value);
        return this;
    }

    public boolean isValidating() {
        return validating;
    }

    public void setValidating(final boolean validating) {
        this.validating = validating;
    }
}
