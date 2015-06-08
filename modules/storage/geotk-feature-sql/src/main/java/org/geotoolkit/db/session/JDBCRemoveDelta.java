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
import java.util.Map;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.session.RemoveDelta;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.opengis.util.GenericName;
import org.opengis.filter.Id;

/**
 * Makes all queries in JDBC transaction.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JDBCRemoveDelta extends RemoveDelta {

    public JDBCRemoveDelta(Session session, GenericName typeName, Id filter) {
        super(session, typeName, filter);
    }
    
    @Override
    public Map<String, String> commit(FeatureStore store) throws DataStoreException {
        final DefaultJDBCFeatureStore jdbcstore = (DefaultJDBCFeatureStore) store;
        final Connection cnx = ((JDBCSession)session).getTransaction();
        jdbcstore.removeFeatures(type, removedIds, cnx);
        return null;
    }
    
}
