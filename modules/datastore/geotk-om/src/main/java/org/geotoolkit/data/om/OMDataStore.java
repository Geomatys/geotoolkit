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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.type.DefaultGeometryDescriptor;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OMDataStore extends AbstractDataStore {
    /** the feature factory */
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
                        new Hints(Hints.FEATURE_FACTORY,LenientFeatureFactory.class));
    private static final GeometryFactory GF = new GeometryFactory();

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.om");

    private static final String OM_NAMESPACE = "http://www.opengis.net/sampling/1.0";
    private static final Name OM_TN_SAMPLINGPOINT = new DefaultName(OM_NAMESPACE, "SamplingPoint");

    protected static final Name ATT_DESC     = new DefaultName(GML_NAMESPACE, "description");
    protected static final Name ATT_NAME     = new DefaultName(GML_NAMESPACE, "name");
    protected static final Name ATT_SAMPLED  = new DefaultName(OM_NAMESPACE, "sampledFeature");
    protected static final Name ATT_POSITION = new DefaultName(OM_NAMESPACE, "position");

    private static final QueryCapabilities capabilities = new DefaultQueryCapabilities(false);

    private final Map<Name, FeatureType> types = new HashMap<Name,FeatureType>();

    private final Connection connection;

    private static final String SQL_ALL_SAMPLING_POINT = "SELECT * FROM \"observation\".\"sampling_points\"";
    private static final String SQL_WRITE_SAMPLING_POINT = "INSERT INTO \"observation\".\"sampling_points\" VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String SQL_GET_LAST_ID = "SELECT COUNT(*) FROM \"observation\".\"sampling_points\"";


    public OMDataStore(Connection connection) {
        super();
        this.connection = connection;
        initTypes();
    }

    private Connection getConnection(){
        return connection;
    }

    private void initTypes() {
        final FeatureTypeBuilder featureTypeBuilder = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder attributeDescBuilder   = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder attributeTypeBuilder   = new AttributeTypeBuilder();

        featureTypeBuilder.setName(OM_TN_SAMPLINGPOINT);
        
        // gml:description
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(ATT_DESC);
        attributeDescBuilder.setMaxOccurs(1);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        featureTypeBuilder.add(attributeDescBuilder.buildDescriptor());

        // gml:name
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(ATT_NAME);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(false);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        featureTypeBuilder.add(attributeDescBuilder.buildDescriptor());

        // To see BoundedBy

        // sa:sampledFeature href?
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(ATT_SAMPLED);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        featureTypeBuilder.add(attributeDescBuilder.buildDescriptor());

        // sa:Position
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(Point.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(ATT_POSITION);
        attributeDescBuilder.setMaxOccurs(1);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(false);
        attributeDescBuilder.setType(attributeTypeBuilder.buildGeometryType());
        featureTypeBuilder.add(attributeDescBuilder.buildDescriptor());

        featureTypeBuilder.setDefaultGeometry(ATT_POSITION.getLocalPart());

        types.put(OM_TN_SAMPLINGPOINT, featureTypeBuilder.buildFeatureType());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader<FeatureType, Feature> getFeatureReader(Query query) throws DataStoreException {
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
    public void dispose() {
        super.dispose();
        try {
            connection.close();
        } catch (SQLException ex) {
            getLogger().info("SQL Exception while closing O&M datastore");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(Name typeName) throws DataStoreException {
        return handleWriterAppend(typeName);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
        return types.keySet();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        typeCheck(typeName);
        return types.get(typeName);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return capabilities;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        return handleWriter(typeName, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        final FeatureType featureType = getFeatureType(groupName); //raise an error if type doesn't exist
        final List<FeatureId> result = new ArrayList<FeatureId>();

        try {
            for(final Feature feature : newFeatures) {
                FeatureId identifier = feature.getIdentifier();
                if (identifier == null || identifier.getID().isEmpty()) {
                    identifier = getNewFeatureId();
                }

                final Connection cnx = getConnection();
                final PreparedStatement stmtWrite = cnx.prepareStatement(SQL_WRITE_SAMPLING_POINT);

                stmtWrite.setString(1, identifier.getID());
                stmtWrite.setString(2, (String)feature.getProperty(ATT_DESC).getValue());
                stmtWrite.setString(3, (String)feature.getProperty(ATT_NAME).getValue());
                stmtWrite.setString(4, (String)feature.getProperty(ATT_SAMPLED).getValue());
                stmtWrite.setNull(5, java.sql.Types.VARCHAR);
                final Object geometry = feature.getDefaultGeometryProperty().getValue();
                if (geometry instanceof Point) {
                    final Point pt = (Point) geometry;
                    String crsIdentifier = null;
                    if (featureType.getCoordinateReferenceSystem() != null) {
                        try {
                            crsIdentifier = CRS.lookupIdentifier(featureType.getCoordinateReferenceSystem(), true);
                        } catch (FactoryException ex) {
                            LOGGER.log(Level.WARNING, null, ex);
                        }
                    }
                    if (identifier != null) {
                        stmtWrite.setString(6, crsIdentifier);
                    } else {
                        stmtWrite.setNull(6, Types.VARCHAR);
                    }
                    stmtWrite.setInt(7, 2);
                    stmtWrite.setDouble(8, pt.getX());
                    stmtWrite.setDouble(9, pt.getY());
                } else {
                    stmtWrite.setNull(6, Types.VARCHAR);
                    stmtWrite.setNull(7, Types.INTEGER);
                    stmtWrite.setNull(8, Types.DOUBLE);
                    stmtWrite.setNull(9, Types.DOUBLE);
                }
                stmtWrite.executeUpdate();
                result.add(identifier);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, SQL_WRITE_SAMPLING_POINT.toString(), ex);
        }

        return result;
    }

    public FeatureId getNewFeatureId() {
        try {
            final Connection cnx = getConnection();
            final PreparedStatement stmtLastId = cnx.prepareStatement(SQL_GET_LAST_ID);
            final ResultSet result = stmtLastId.executeQuery();
            if (result.next()) {
                final int nb = result.getInt(1) + 1;
                return new DefaultFeatureId("sampling-point-"+nb);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return null;
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // No supported stuffs /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        throw new DataStoreException("Not Supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }


    ////////////////////////////////////////////////////////////////////////////
    // Feature Reader //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private class OMReader implements FeatureReader{

        private boolean firstCRS = true;
        private final FeatureType type;
        private final ResultSet result;
        private Feature current = null;

        private OMReader(FeatureType type) throws SQLException{
            this.type = type;
            final Connection cnx = getConnection();
            final PreparedStatement stmtAll = cnx.prepareStatement(SQL_ALL_SAMPLING_POINT);
            result = stmtAll.executeQuery();
        }

        @Override
        public FeatureType getFeatureType() {
            return type;
        }

        @Override
        public Feature next() throws DataStoreRuntimeException {
            try {
                read();
            } catch (Exception ex) {
                throw new DataStoreRuntimeException(ex);
            }
            Feature candidate = current;
            current = null;
            return candidate;
        }

        @Override
        public boolean hasNext() throws DataStoreRuntimeException {
            try {
                read();
            } catch (Exception ex) {
                throw new DataStoreRuntimeException(ex);
            }
            return current != null;
        }

        private void read() throws Exception{
            if(current != null) return;

            if(!result.next()){
                return;
            }

            if (firstCRS) {
                try {
                    CoordinateReferenceSystem crs = CRS.decode(result.getString("point_srsname"));
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

            final Collection<Property> props = new ArrayList<Property>();
            final String id = result.getString("id");
            final double x = result.getDouble("x_value");
            final double y = result.getDouble("y_value");
            final Coordinate coord = new Coordinate(x, y);

            props.add(FF.createAttribute(result.getString("description"), (AttributeDescriptor) type.getDescriptor(ATT_DESC), null));
            props.add(FF.createAttribute(result.getString("name"), (AttributeDescriptor) type.getDescriptor(ATT_NAME), null));
            props.add(FF.createAttribute(result.getString("sampled_feature"), (AttributeDescriptor) type.getDescriptor(ATT_SAMPLED), null));
            props.add(FF.createAttribute(GF.createPoint(coord), (AttributeDescriptor) type.getDescriptor(ATT_POSITION), null));

            current = FF.createFeature(props, type, id);
        }

        @Override
        public void close() {
            try {
                result.close();
            } catch (SQLException ex) {
                throw new DataStoreRuntimeException(ex);
            }
        }

        @Override
        public void remove() throws DataStoreRuntimeException{
            throw new DataStoreRuntimeException("Not supported.");
        }

    }

}