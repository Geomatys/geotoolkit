/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Resource;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * Draft interface of ISO-19110 FeatureCatalogue.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface FeatureCatalogue extends Resource {

    /**
     * Get list of types defined in the catalogue.
     *
     * @return Set of FeatureType names.
     * @throws DataStoreException if an error occurred while fetching names list.
     */
    Set<GenericName> getTypeNames() throws DataStoreException;

    /**
     * Get FeatureType.
     *
     * @param name type name, not null.
     * @return founded type
     * @throws IllegalNameException if no resource is found for the given identifier, or if more than one resource is found.
     * @throws DataStoreException if another kind of error occurred while searching catalogue.
     */
    FeatureType getFeatureType(String name) throws DataStoreException, IllegalNameException;

}
