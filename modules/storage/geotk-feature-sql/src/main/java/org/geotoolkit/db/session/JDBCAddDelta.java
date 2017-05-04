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
package org.geotoolkit.db.session;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.session.AddDelta;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;

/**
 * Makes all queries in JDBC transaction.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JDBCAddDelta extends AddDelta{

    public JDBCAddDelta(Session session, String typeName, Collection<? extends Feature> features) {
        super(session, typeName, features);
    }

    @Override
    public Map<String, String> commit(FeatureStore store) throws DataStoreException {
        final DefaultJDBCFeatureStore jdbcstore = (DefaultJDBCFeatureStore) store;
        final Connection cnx = ((JDBCSession)session).getTransaction();

        final List<FeatureId> createdIds = jdbcstore.addFeatures(type, features, cnx, null);

        //iterator and list should have the same size
        final Map<String,String> updates = new HashMap<>();
        final FeatureIterator ite = features.iterator();
        int i=0;
        try{
            if(createdIds != null && !createdIds.isEmpty()){
                while(ite.hasNext()){
                    final Feature f = ite.next();
                    final String id = (String) f.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());
                    updates.put(id, createdIds.get(i).getID());
                    i++;
                }
            }
        }finally{
            ite.close();
        }

        features.clear();
        return updates;
    }

}
