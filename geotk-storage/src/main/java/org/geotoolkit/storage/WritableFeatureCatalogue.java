/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage;

import java.util.Collection;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface WritableFeatureCatalogue extends FeatureCatalogue {

    /**
     * Remove a set of types in the catalogue.
     * This will also remove all feature instances present.
     *
     * @param names type names to remove
     * @throws DataStoreException if one or multiple name do not exist or can not be removed or removing features failed
     */
    void deleteTypes(Collection<String> names) throws DataStoreException;

    /**
     * Declare new feature types in the catalogue.
     *
     * @param types new types
     * @throws DataStoreException if types can ne be added for any reason
     */
    void addTypes(Collection<FeatureType> types) throws DataStoreException;

}
