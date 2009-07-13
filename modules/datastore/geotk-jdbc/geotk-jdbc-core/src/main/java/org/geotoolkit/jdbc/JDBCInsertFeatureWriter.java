/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class JDBCInsertFeatureWriter extends JDBCFeatureReader implements FeatureWriter<SimpleFeatureType, SimpleFeature> {

    ResultSetFeature last;

    public JDBCInsertFeatureWriter(final String sql, final Connection cx, final JDBCFeatureSource featureSource,
            final Hints hints) throws SQLException, IOException
    {
        super(sql, cx, featureSource, featureSource.getSchema(), hints);
        last = new ResultSetFeature( rs, cx );
    }

    public JDBCInsertFeatureWriter(final PreparedStatement ps, final Connection cx, final JDBCFeatureSource featureSource,
            final Hints hints) throws SQLException, IOException
    {
        super( ps, cx, featureSource, featureSource.getSchema(), hints );
        last = new ResultSetFeature( rs, ps.getConnection() );
    }

    public JDBCInsertFeatureWriter(final JDBCUpdateFeatureWriter other) {
        super(other);
        last = other.last;
    }

    @Override
    public boolean hasNext() throws IOException {
        return false;
    }

    @Override
    public SimpleFeature next() throws IOException {
        //init, setting id to null explicity since the feature is yet to be
        // inserted
        last.init(null);
        return last;
    }

    @Override
    public void remove() throws IOException {
        //noop
    }

    @Override
    public void write() throws IOException {
        try {
            //do the insert
            dataStore.insert(last, featureType, st.getConnection());

            //the datastore sets as userData, grab it and update the fid
            String fid = (String) last.getUserData().get( "fid" );
            last.setID( fid );
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();

        if ( last != null ) {
            last.close();
            last = null;
        }
    }
}
