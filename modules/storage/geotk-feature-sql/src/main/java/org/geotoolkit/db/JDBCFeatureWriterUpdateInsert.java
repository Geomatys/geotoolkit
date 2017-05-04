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
package org.geotoolkit.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.factory.Hints;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;


public class JDBCFeatureWriterUpdateInsert extends JDBCFeatureWriterUpdate {

    JDBCFeatureWriterInsert inserter;

    public JDBCFeatureWriterUpdateInsert(final DefaultJDBCFeatureStore store, final String sql,
            final FeatureType type, Connection cnx, boolean release, final Hints hints)
            throws SQLException, IOException,DataStoreException {
        super(store, sql, type, cnx, release, hints);
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        if ( inserter != null ) {
            return inserter.hasNext();
        }

        //check parent
        boolean hasNext = super.hasNext();
        if ( !hasNext ) {
            try {
                //update phase is up, switch to insert mode
                inserter = new JDBCFeatureWriterInsert(this);
            } catch (SQLException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            return inserter.hasNext();
        }

        return hasNext;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        if ( inserter != null ) {
            return inserter.next();
        }

        return super.next();
    }

    @Override
    public void remove() throws FeatureStoreRuntimeException {
        if ( inserter != null ) {
            inserter.remove();
            return;
        }

        super.remove();
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        if ( inserter != null ) {
            inserter.write();
            return;
        }

        super.write();
    }

    @Override
    public void close() throws FeatureStoreRuntimeException {
        if(inserter!= null) {
            inserter.close();
            inserter = null;
        }
        super.close();
    }

}
