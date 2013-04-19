/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57;

import java.util.Collections;
import java.util.Date;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionHistory;
import org.geotoolkit.version.Versioned;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;

/**
 * Manage versions for a feature.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class S57VersionedFeature implements Versioned<Feature> {

    private final FeatureStore store;
    private final Name typeName;
    private final FeatureId id;
    
    public S57VersionedFeature(FeatureStore store, Name typeName, FeatureId id) {
        this.store = store;
        this.typeName = typeName;
        this.id = id;
    }

    @Override
    public VersionHistory getHistory() throws VersioningException {
        return store.getHistory(typeName);
    }

    @Override
    public Feature getForVersion(Date date) throws VersioningException {
        return getForVersion(getHistory().getVersion(date));
    }

    @Override
    public Feature getForVersion(String versionLabel) throws VersioningException {
        return getForVersion(getHistory().getVersion(versionLabel));
    }

    @Override
    public Feature getForVersion(Version version) throws VersioningException {
        final QueryBuilder qb = new QueryBuilder(typeName);
        qb.setVersionLabel(version.getLabel());
        qb.setFilter(FactoryFinder.getFilterFactory(null).id(Collections.singleton(id)));
        try {
            final FeatureReader reader = store.getFeatureReader(qb.buildQuery());
            if(reader.hasNext()){
                //feature might not exist at this version
                return reader.next();
            }
            return null;
        } catch (DataStoreException ex) {
            throw new VersioningException(ex);
        }
    }
    
}
