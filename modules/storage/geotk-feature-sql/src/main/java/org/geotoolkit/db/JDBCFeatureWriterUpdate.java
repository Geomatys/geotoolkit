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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.factory.Hints;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

public class JDBCFeatureWriterUpdate extends JDBCFeatureReader implements FeatureWriter {

    //we keep the reference a bit longer
    private Feature last;

    public JDBCFeatureWriterUpdate(final DefaultJDBCFeatureStore store, final String sql,
            final FeatureType type, Connection cnx, boolean release, final Hints hints)
            throws SQLException, IOException,DataStoreException {
        super(store, sql, type, cnx, release, hints);
    }

    @Override
    protected Feature toFeature(ResultSet rs) throws SQLException, DataStoreException {
        last = super.toFeature(rs);
        return last;
    }

    @Override
    public void remove() throws FeatureStoreRuntimeException {
        if(last==null){
            throw new FeatureStoreRuntimeException("Cursor is not on a record.");
        }

        final Filter filter = store.getFilterFactory().id(
                Collections.singleton(FeatureExt.getId(last)));
        try {
            store.delete(type, filter, st.getConnection());
        } catch (SQLException e) {
            throw new FeatureStoreRuntimeException(e);
        } catch (DataStoreException e) {
            throw new FeatureStoreRuntimeException(e);
        }
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        if(last==null){
            throw new FeatureStoreRuntimeException("Cursor is not on a record.");
        }

        try {
            //figure out what the fid is
            final PrimaryKey key = store.getDatabaseModel().getPrimaryKey(type.getName().toString());
            final String fid = key.encodeFID(rs);

            final FilterFactory ff = store.getFilterFactory();
            final Id filter = ff.id(Collections.singleton(ff.featureId(fid)));

            //figure out which attributes changed
            final Map<String,Object> changes = new HashMap<>();

            for (final PropertyType att : type.getProperties(true)) {
                if (att instanceof FeatureAssociationRole || att instanceof Operation
                        || AttributeConvention.contains(att.getName())) {
                    //not a writable property
                    continue;
                }

                changes.put(att.getName().tip().toString(), last.getPropertyValue(att.getName().toString()));
            }

            //do the write
            store.updateSingle(type, changes, filter, st.getConnection());
        } catch (Exception e) {
            throw new FeatureStoreRuntimeException(e);
        }
    }
}
