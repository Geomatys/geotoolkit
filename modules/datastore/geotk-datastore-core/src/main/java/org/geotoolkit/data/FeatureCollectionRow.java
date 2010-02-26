/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data;

import org.opengis.feature.Feature;

/**
 * Define a group of feature related through the join condition of the source.
 *
 * This class is the counterpart of javax.jcr.query.Row
 * from JSR-283 (Java Content Repository 2).
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureCollectionRow {

    /**
     * Return the current feature of the only selector.
     *
     * @return Feature
     * @exception DataStoreException if the source have more then one selector.
     */
    Feature getFeature() throws DataStoreException;

    /**
     * Return the current feature of the requested selector.
     *
     * @return Feature
     * @exception DataStoreException.
     */
    Feature getFeature(String selector) throws DataStoreException;

}
