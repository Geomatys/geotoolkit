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

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;

import org.geotoolkit.feature.FeatureTypeBuilder;

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
 * @deprecated use FeatureTypeBuilder
 */
@Deprecated
public class SimpleFeatureTypeBuilder extends FeatureTypeBuilder{

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
        super(factory);
    }

    /**
     * The super type of the built type.
     */
    @Override
    public SimpleFeatureType getSuperType() {
        return (SimpleFeatureType) superType;
    }

    /**
     * Builds a feature type from compiled state.
     * <p>
     * After the type is built the running list of attributes is cleared.
     * </p>
     * @return The built feature type.
     */
    @Override
    public SimpleFeatureType buildFeatureType() {
        return buildSimpleFeatureType();
    }

    /**
     * @param original
     * @param types
     * @return SimpleFeatureType
     */
    public static SimpleFeatureType retype(final SimpleFeatureType original, final Name[] types) {
        return (SimpleFeatureType) retype((FeatureType)original, types);
    }

}
