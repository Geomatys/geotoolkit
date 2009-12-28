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
     * place, such as the Hibernate one)
     *
     * @since 2.4
     */
    public static final Key FEATURE_DETACHED = new Key(Boolean.class);

    /**
     * Asks a datastore to perform a topology preserving on the fly
     * generalization of the geometries. The datastore will return
     * geometries generalized at the specified distance.
     */
    public static final Key GEOMETRY_GENERALIZATION = new Key(Double.class);

    /**
     * Asks a datastore to perform a non topology preserving on the fly
     * generalization of the geometries (e.g., returning self crossing
     * polygons as a result of the geoneralization is considered valid).

     */
    public static final Key GEOMETRY_SIMPLIFICATION = new Key(Double.class);

    private HintsPending(){}

}
