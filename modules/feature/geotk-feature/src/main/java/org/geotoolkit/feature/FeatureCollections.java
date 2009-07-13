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
package org.geotoolkit.feature;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.feature.utility.FeatureUtilities;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


/**
 * A utility class for working with FeatureCollections.
 * Provides a mechanism for obtaining a FeatureCollection<SimpleFeatureType, SimpleFeature> instance.
 * @author  Ian Schneider
 * @source $URL$
 */
public abstract class FeatureCollections extends Factory {

    /**
     * create a new FeatureCollection<SimpleFeatureType, SimpleFeature> using the current default factory.
     * @return A FeatureCollection<SimpleFeatureType, SimpleFeature> instance.
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> newCollection() {
        return FeatureUtilities.createCollection();
    }

    /**
     * Creates a new FeatureCollection<SimpleFeatureType, SimpleFeature> with a particular id using the current
     * default factory.
     *
     * @param id The id of the feature collection.
     *
     * @return A new FeatureCollection<SimpleFeatureType, SimpleFeature> intsance with the specified id.
     *
     * @since 2.4
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> newCollection(String id) {
        return FeatureUtilities.createCollection(id);
    }

    /**
     * Subclasses must implement this to return a new FeatureCollection<SimpleFeatureType, SimpleFeature> object.
     * @return A new FeatureCollection
     */
    protected abstract FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection();

    /**
     * Subclasses must implement this to return a new FeatureCollection<SimpleFeatureType, SimpleFeature> object
     * with a particular id.
     *
     * @param id The identification of the feature collection.
     *
     * @return A new FeatureCollection<SimpleFeatureType, SimpleFeature> with the specified id.
     */
    protected abstract FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection(String id);
}
