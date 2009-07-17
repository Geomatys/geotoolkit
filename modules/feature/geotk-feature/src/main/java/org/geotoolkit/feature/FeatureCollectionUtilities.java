/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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


import org.geotoolkit.feature.collection.FeatureCollection;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @version $Id$
 *
 * @author Cédric Briançon (Geomatys)
 */
public class FeatureCollectionUtilities {

    protected FeatureCollectionUtilities(){}

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection() {
        return new DefaultFeatureCollection(null, null);
    }

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection(final String id) {
        return new DefaultFeatureCollection(id, null);
    }

    public static FeatureCollection<SimpleFeatureType, SimpleFeature> createCollection(final String id, final SimpleFeatureType ft) {
        return new DefaultFeatureCollection(id, ft);
    }

}
