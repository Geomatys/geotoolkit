/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.data.om;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import static org.geotoolkit.data.om.OMFeatureTypes.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SOSDatabaseFeatureStore extends AbstractOMFeatureStore {
    
    private final ManageableDataSource source;

    private static final String SQL_ALL_SAMPLING_POINT_D  = "SELECT * FROM \"om\".\"sampling_features\"";
    private static final String SQL_ALL_SAMPLING_POINT_PG = "SELECT \"id\", \"name\", \"description\", \"sampledfeature\", \"postgis\".st_asBinary(\"shape\"), \"crs\" FROM \"om\".\"sampling_features\" WHERE \"id\"=?";
    private static final String SQL_WRITE_SAMPLING_POINT = "INSERT INTO \"om\".\"sampling_features\" VALUES(?,?,?,?,?,?)";
    private static final String SQL_GET_LAST_ID = "SELECT COUNT(*) FROM \"om\".\"sampling_features\"";
    private static final String SQL_DELETE_SAMPLING_POINT = "DELETE FROM \"om\".\"sampling_features\" WHERE \"id\" = ?";

    private final boolean isPostgres;

    public SOSDatabaseFeatureStore(final ParameterValueGroup params, final ManageableDataSource source, final boolean isPostgres) {
        super(params, "SamplingPoint");
        this.source = source;
        this.isPostgres = isPostgres;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(SOSDatabaseFeatureStoreFactory.NAME);
    }

    private Connection getConnection() throws SQLException{
        return source.getConnection();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final FeatureType sft = getFeatureType(query.getTypeName());
        try {
            return handleRemaining(new OMReader(sft), query);
        } catch (SQLException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(final GenericName typeName, final Hints hints) throws DataStoreException {
        return handleWriterAppend(typeName,hints);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(final GenericName typeName, final Filter filter, final Hints hints) throws DataStoreException {
        final FeatureType sft = getFeatureType(typeName);
        try {
            return handleRemaining(new OMWriter(sft), filter);
        } catch (SQLException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreException {
        super.close();
        try {
            source.close();
        } catch (SQLException ex) {
            getLogger().info("SQL Exception while closing O&M datastore");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(final GenericName groupName, final Collection<? extends Feature> newFeatures, 
            final Hints hints) throws DataStoreException {
        final FeatureType featureType = getFeatureType(groupName); //raise an error if type doesn't exist
        final List<FeatureId> result = new ArrayList<>();


        Connection cnx = null;
        PreparedStatement stmtWrite = null;
        try {
            cnx = getConnection();
            stmtWrite = cnx.prepareStatement(SQL_WRITE_SAMPLING_POINT);

            for(final Feature feature : newFeatures) {
                FeatureId identifier = feature.getIdentifier();
                if (identifier == null || identifier.getID().isEmpty()) {
                    identifier = getNewFeatureId();
                }


                stmtWrite.setString(1, identifier.getID());
                stmtWrite.setString(2, (String)feature.getProperty(ATT_NAME).getValue());
                stmtWrite.setString(3, (String)feature.getProperty(ATT_DESC).getValue());
                stmtWrite.setString(4, (String)feature.getProperty(ATT_SAMPLED).getValue());
                final Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
                if (geom != null) {
                    WKBWriter writer = new WKBWriter();
                    final int SRID = geom.getSRID();
                    stmtWrite.setBytes(5, writer.write(geom));
                    stmtWrite.setInt(6, SRID);
                } else {
                    stmtWrite.setNull(5, java.sql.Types.VARBINARY);
                    stmtWrite.setNull(6, java.sql.Types.INTEGER);
                }
                stmtWrite.executeUpdate();
                result.add(identifier);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, SQL_WRITE_SAMPLING_POINT, ex);
        }finally{
            if(stmtWrite != null){
                try {
                    stmtWrite.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }

            if(cnx != null){
                try {
                    cnx.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
        }

        return result;
    }

    public FeatureId getNewFeatureId() {
        Connection cnx = null;
        PreparedStatement stmtLastId = null;
        try {
            cnx = getConnection();
            stmtLastId = cnx.prepareStatement(SQL_GET_LAST_ID);
            final ResultSet result = stmtLastId.executeQuery();
            if (result.next()) {
                final int nb = result.getInt(1) + 1;
                return new DefaultFeatureId("sampling-point-"+nb);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }finally{
            if(stmtLastId != null){
                try {
                    stmtLastId.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }

            if(cnx != null){
                try {
                    cnx.close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final GenericName groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void refreshMetaModel() {
        return;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // No supported stuffs /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void createFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteFeatureType(final GenericName typeName) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final GenericName groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Feature Reader //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private class OMReader implements FeatureReader {

        protected final Connection cnx;
        private boolean firstCRS = true;
        protected final FeatureType type;
        private final ResultSet result;
        protected Feature current = null;

        private OMReader(final FeatureType type) throws SQLException{
            this.type = type;
            cnx = getConnection();
            final PreparedStatement stmtAll;
            if(isPostgres) {
                stmtAll = cnx.prepareStatement(SQL_ALL_SAMPLING_POINT_PG);
            } else {
                stmtAll = cnx.prepareStatement(SQL_ALL_SAMPLING_POINT_D);
            }
            result = stmtAll.executeQuery();
        }

        @Override
        public FeatureType getFeatureType() {
            return type;
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            try {
                read();
            } catch (Exception ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            Feature candidate = current;
            current = null;
            return candidate;
        }

        @Override
        public boolean hasNext() throws FeatureStoreRuntimeException {
            try {
                read();
            } catch (Exception ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            return current != null;
        }

        protected void read() throws Exception{
            if(current != null) return;

            if(!result.next()){
                return;
            }

            if (firstCRS) {
                try {
                    CoordinateReferenceSystem crs = CRS.decode("EPSG:" + result.getString("crs"));
                    if (type instanceof DefaultSimpleFeatureType) {
                        ((DefaultSimpleFeatureType) type).setCoordinateReferenceSystem(crs);
                    }
                    if (type.getGeometryDescriptor() instanceof DefaultGeometryDescriptor) {
                        ((DefaultGeometryDescriptor) type.getGeometryDescriptor()).setCoordinateReferenceSystem(crs);
                    }
                    firstCRS = false;
                } catch (NoSuchAuthorityCodeException ex) {
                    throw new IOException(ex);
                } catch (FactoryException ex) {
                    throw new IOException(ex);
                }
            }

            final Collection<Property> props = new ArrayList<>();
            final String id = result.getString("id");
            final byte[] b = result.getBytes(5);
            final Geometry geom;
            if (b != null) {
                WKBReader reader = new WKBReader();
                geom             = reader.read(b);
            } else {
                geom = null;
            } 

            props.add(FF.createAttribute(result.getString("description"), (AttributeDescriptor) type.getDescriptor(ATT_DESC), null));
            props.add(FF.createAttribute(result.getString("name"), (AttributeDescriptor) type.getDescriptor(ATT_NAME), null));
            props.add(FF.createAttribute(result.getString("sampledfeature"), (AttributeDescriptor) type.getDescriptor(ATT_SAMPLED), null));
            props.add(FF.createAttribute(geom, (AttributeDescriptor) type.getDescriptor(ATT_POSITION), null));

            current = FF.createFeature(props, type, id);
        }

        @Override
        public void close() {
            try {
                result.close();
                cnx.close();
            } catch (SQLException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        }

        @Override
        public void remove() throws FeatureStoreRuntimeException{
            throw new FeatureStoreRuntimeException("Not supported.");
        }

    }

    private class OMWriter extends OMReader implements FeatureWriter {

        protected Feature candidate = null;

        private OMWriter(final FeatureType type) throws SQLException{
            super(type);
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            try {
                read();
            } catch (Exception ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            candidate = current;
            current = null;
            return candidate;
        }

        @Override
        public void remove() throws FeatureStoreRuntimeException{

            if (candidate == null) {
                return;
            }

            PreparedStatement stmtDelete = null;
            try {
                stmtDelete = cnx.prepareStatement(SQL_DELETE_SAMPLING_POINT);
                stmtDelete.setString(1, candidate.getIdentifier().getID());
                stmtDelete.executeUpdate();

            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, SQL_WRITE_SAMPLING_POINT, ex);
            } finally {
                if (stmtDelete != null) {
                    try {
                        stmtDelete.close();
                    } catch (SQLException ex) {
                        LOGGER.log(Level.WARNING, null, ex);
                    }
                }
            }

        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
            throw new FeatureStoreRuntimeException("Not supported.");
        }
    }
}
