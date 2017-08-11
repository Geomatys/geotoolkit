/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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

import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.content.DefaultFeatureCatalogueDescription;
import org.apache.sis.metadata.iso.content.DefaultFeatureTypeInfo;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.storage.Resource;
import org.opengis.metadata.Metadata;

/**
 * A feature resource provides access to feature definition and I/O operations.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface FeatureResource extends Resource, FeatureSet {

    @Override
    default Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.getIdentifiers().add(getIdentifier());

        final DefaultFeatureCatalogueDescription fcd = new DefaultFeatureCatalogueDescription();
        final DefaultFeatureTypeInfo info = new DefaultFeatureTypeInfo();
        info.setFeatureInstanceCount((int)features(false).count());
        fcd.getFeatureTypeInfo().add(info);

        metadata.getContentInfo().add(fcd);
        return metadata;
    }

    /**
     * Request a subset of features from this resource.
     * The query is optional, if not set this resource will be returned.
     *
     * @param  query a filter to apply on the returned features, or null if none.
     * @return resource of features matching the given query.
     * @throws DataStoreException if an I/O or decoding error occurs.
     */
    default FeatureResource subset(Query query) throws DataStoreException {
        return new SubsetFeatureResource(this, query);
    }

}
