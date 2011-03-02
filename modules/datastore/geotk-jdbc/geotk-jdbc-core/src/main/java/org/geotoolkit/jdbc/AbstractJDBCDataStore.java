/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import com.vividsolutions.jts.geom.GeometryFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.jdbc.dialect.SQLDialect;

import org.opengis.feature.FeatureFactory;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.filter.FilterFactory;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractJDBCDataStore extends AbstractDataStore implements JDBCDataStore{

    /**
     * Factory used to create feature types
     */
    protected FeatureTypeFactory typeFactory;

    /**
     * Factory used to create features
     */
    protected FeatureFactory featureFactory;

    /**
     * Factory used to create filters
     */
    protected FilterFactory filterFactory;

    /**
     * Factory used to create geometries
     */
    protected GeometryFactory geometryFactory;

    protected DataSource source;
    protected SQLDialect dialect;
    protected String databaseSchema;

    /**
     * The fetch size for this datastore, defaulting to 1000. Set to a value less or equal
     * to 0 to disable fetch size limit and grab all the records in one shot.
     */
    protected int fetchSize;


    public AbstractJDBCDataStore() {
        this(null);
    }

    public AbstractJDBCDataStore(final String namespace) {
        super(namespace);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureTypeFactory getFeatureTypeFactory() {
        return typeFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFeatureTypeFactory(final FeatureTypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFeatureFactory(final FeatureFactory featureFactory) {
        this.featureFactory = featureFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FilterFactory getFilterFactory() {
        return filterFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureFactory getFeatureFactory() {
        return featureFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFilterFactory(final FilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setGeometryFactory(final GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getNamespaceURI() {
        return getDefaultNamespace();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDialect(final SQLDialect dialect) {
        this.dialect = dialect;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDatabaseSchema(final String databaseSchema) {
        this.databaseSchema = databaseSchema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDatabaseSchema() {
        return databaseSchema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDataSource(final DataSource source) {
        this.source = source;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataSource getDataSource(){
        return source;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFetchSize(final int fetchSize) {
        this.fetchSize = fetchSize;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Connection utils ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeSafe(final Connection cx, final Statement st, final ResultSet rs){
        closeSafe(cx);
        closeSafe(st);
        closeSafe(rs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeSafe(final ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (SQLException e) {
            final String msg = "Error occurred closing result set";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeSafe(final Statement st) {
        if (st == null) {
            return;
        }

        try {
            st.close();
        } catch (SQLException e) {
            final String msg = "Error occurred closing statement";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeSafe(final Connection cx) {
        if (cx == null) {
            return;
        }

        try {
            cx.close();
            getLogger().fine("CLOSE CONNECTION");
        } catch (SQLException e) {
            final String msg = "Error occurred closing connection";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

}

