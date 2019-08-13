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
 * @module
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

    ////////////////////////////////////////////////////////////////////////
    ////////                                                        ////////
    ////////                        Features                        ////////
    ////////                                                        ////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * When adding features in a datastore, it is not always necessary to have
     * the returned id of the inserted feature.
     * JDBC featurestore for exemple are much more efficient when inserting datas
     * in batch mode. setting this value to false may bring a huge performance
     * gain.
     *
     * Default value is true.
     */
    public static final Key UPDATE_ID_ON_INSERT = new Key(Boolean.class);

    /**
     * Used to identify a PropertyDescriptor if he is part of the FeatureID.
     */
    public static final Key PROPERTY_IS_IDENTIFIER = new Key(Boolean.class);

    /**
     * This flag indicates that the featurestore can ignore features which are smaller
     * than the given resolution. FeatureStore are supposed to
     * try to conform to this request only if it doesnt requiere to much work.
     * For exemple when exploring a quad tree, tiles can be ignored when there bbox
     * is to small or when the feature bbox can be read before.
     *
     * Default value is null.
     */
    public static final Key KEY_IGNORE_SMALL_FEATURES = new Key(double[].class);

    private HintsPending(){}

}
