/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.postgresql;

import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import javax.sql.DataSource;
import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * GeotoolKit Coverage Store using PostgreSQL Raster model.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PGCoverageStore extends AbstractCoverageStore{

    private DataSource source;
    private int fetchSize;
    private String schema;
    
    public PGCoverageStore(final ParameterValueGroup params, final DataSource source){
        super(params);
        this.source = source;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setDatabaseSchema(String schema) {
        this.schema = schema;
    }

    public String getDatabaseSchema() {
        return schema;
    }

    public DataSource getDataSource() {
        return source;
    }
    
    @Override
    public CoverageStoreFactory getFactory() {
        return CoverageStoreFinder.getFactoryById(PGCoverageStoreFactory.NAME);
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        if (source instanceof ManageableDataSource) {
            try {
                final ManageableDataSource mds = (ManageableDataSource) source;
                source = null;
                mds.close();
            } catch (SQLException e) {
                // it's ok, we did our best..
                getLogger().log(Level.FINE, "Could not close dataSource", e);
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (source != null) {
            getLogger().severe("There's code using JDBC based coverage store and " +
                    "not disposing them. This may lead to temporary loss of database connections. " +
                    "Please make sure all data access code calls CoverageStore.dispose() " +
                    "before freeing all references to it");
            dispose();
        }
        super.finalize();
    }
    
}
