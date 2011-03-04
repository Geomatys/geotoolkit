/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

/**
 * Additional hints that are using in geotoolkit-pending.
 *
 * @module pending
 * @since 2.1
 * @version $Id$
 * @author Martin Desruisseaux
 * @author Jody Garnett
 */
public final class HintsPending extends Hints {

    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                     JTS Geometries                     ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * If it is needed to store a geometry crs. it can done by using the
     * UserData object. it is expected to be directly the CRS or a Map with
     * this key and CRS value associated.
     */
    public static String JTS_GEOMETRY_CRS = "JTSGeometryCRS";

    /**
     * The {@code com.vividsolutions.jts.geom.GeometryFactory} instance to use.
     *
     */
    public static final ClassKey JTS_GEOMETRY_FACTORY = new ClassKey(
            "com.vividsolutions.jts.geom.GeometryFactory");

    /**
     * The {@code com.vividsolutions.jts.geom.CoordinateSequenceFactory} instance to use.
     *
     */
    public static final ClassKey JTS_COORDINATE_SEQUENCE_FACTORY = new ClassKey(
            "com.vividsolutions.jts.geom.CoordinateSequenceFactory");

    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                        Features                        ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * Whether the features returned by the feature collections should be considered detached from
     * the datastore, that is, they are updatable without altering the backing store (makes sense
     * only if features are kept in memory or if there is some transparent persistent mechanism in
     * place, such as the Hibernate one).
     * 
     * Default behavior is true on datastores.
     *
     * @since 2.4
     */
    public static final Key FEATURE_DETACHED = new Key(Boolean.class);

    /**
     * An implementation of datastore might use some properties of the feature type
     * to generate the feature id. To avoid some unexpected modification it
     * might be usefull to hide thoses fields.
     */
    public static final Key FEATURE_HIDE_ID_PROPERTY = new Key(Boolean.class);

    /**
     * Used to identify a PropertyDescriptor if he is part of the FeatureID.
     */
    public static final Key PROPERTY_IS_IDENTIFIER = new Key(Boolean.class);

    /**
     * Used to identify a PropertyDescriptor if the underlying property is
     * dynamicly calculated. thoses properties can not be changed.
     */
    public static final Key PROPERTY_IS_CALCULATED = new Key(Boolean.class);


    /**
     * This flag indicates that the datastore can ignore features which are smaller
     * than the given resolution. Datastore are supposed to
     * try to conform to this request only if it doesnt requiere to much work.
     * For exemple when exploring a quad tree, tiles can be ignored when there bbox
     * is to small or when the feature bbox can be read before.
     *
     * Default value is null.
     */
    public static final Key KEY_IGNORE_SMALL_FEATURES = new Key(double[].class);

    private HintsPending(){}

}
