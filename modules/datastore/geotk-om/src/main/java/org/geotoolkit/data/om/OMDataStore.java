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

package org.geotoolkit.data.om;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;

import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class OMDataStore extends AbstractDataStore {

    private final Map<String,SimpleFeatureType> types = new HashMap<String,SimpleFeatureType>();

    private final Connection connection;

    private PreparedStatement getAllSamplingPoint;

    private static final Name ID       = new DefaultName("http://www.opengis.net/gml", "id");
    private static final Name DESC     = new DefaultName("http://www.opengis.net/gml", "description");
    private static final Name NAME     = new DefaultName("http://www.opengis.net/gml", "name");
    private static final Name SAMPLED  = new DefaultName("http://www.opengis.net/sampling/1.0", "sampledFeature");
    private static final Name POSITION = new DefaultName("http://www.opengis.net/sampling/1.0", "position");

    private static final GeometryFactory GF = new GeometryFactory();

    public OMDataStore(Connection connection) {
        //not transactional for the moment
        super(false);
        this.connection = connection;
        initTypes();
        initStatement();
    }

    private void initStatement() {
        try {
            getAllSamplingPoint = connection.prepareStatement("SELECT * FROM \"observation\".\"sampling_points\"");
        } catch (SQLException ex) {
           LOGGER.severe("SQL Exception while requesting the O&M database:" + ex.getMessage());
        }
    }

    private void initTypes() {
        SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        AttributeTypeBuilder attributeTypeBuilder   = new AttributeTypeBuilder();

        featureTypeBuilder.setName(new DefaultName("http://www.opengis.net/sampling/1.0", "SamplingPoint"));
        
        // gml:id (attribute)
        Name propertyName = ID;
        attributeTypeBuilder.setBinding(String.class);
        attributeTypeBuilder.setMaxOccurs(1);
        attributeTypeBuilder.setMinOccurs(1);
        attributeTypeBuilder.setNillable(false);
        AttributeType propertyType = attributeTypeBuilder.buildType();
        AttributeDescriptor attdesc = attributeTypeBuilder.buildDescriptor(propertyName, propertyType);
        featureTypeBuilder.add(0, attdesc);

        // gml:description
        propertyName = DESC;
        attributeTypeBuilder.setBinding(String.class);
        attributeTypeBuilder.setMaxOccurs(1);
        attributeTypeBuilder.setMinOccurs(0);
        attributeTypeBuilder.setNillable(true);
        propertyType = attributeTypeBuilder.buildType();
        attdesc = attributeTypeBuilder.buildDescriptor(propertyName, propertyType);
        featureTypeBuilder.add(1, attdesc);

        // gml:name
        propertyName = NAME;
        attributeTypeBuilder.setBinding(String.class);
        attributeTypeBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeTypeBuilder.setMinOccurs(1);
        attributeTypeBuilder.setNillable(false);
        propertyType = attributeTypeBuilder.buildType();
        attdesc = attributeTypeBuilder.buildDescriptor(propertyName, propertyType);
        featureTypeBuilder.add(2, attdesc);

        // To see BoundedBy

        // sa:sampledFeature href?
        propertyName = SAMPLED;
        attributeTypeBuilder.setBinding(String.class);
        attributeTypeBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeTypeBuilder.setMinOccurs(1);
        attributeTypeBuilder.setNillable(true);
        propertyType = attributeTypeBuilder.buildType();
        attdesc = attributeTypeBuilder.buildDescriptor(propertyName, propertyType);
        featureTypeBuilder.add(3, attdesc);

        // sa:Position 
        propertyName = POSITION;
        attributeTypeBuilder.setBinding(Point.class);
        attributeTypeBuilder.setMaxOccurs(1);
        attributeTypeBuilder.setMinOccurs(1);
        attributeTypeBuilder.setNillable(false);
        propertyType = attributeTypeBuilder.buildGeometryType();
        attdesc = attributeTypeBuilder.buildDescriptor(propertyName, propertyType);
        featureTypeBuilder.add(4, attdesc);
        featureTypeBuilder.setDefaultGeometry(POSITION.getLocalPart());

        SimpleFeatureType samplingPointType = featureTypeBuilder.buildFeatureType();
        types.put("SamplingPoint", samplingPointType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getTypeNames() throws IOException {
        final Set<String> keys = types.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getSchema(String typeName) throws IOException {
        final SimpleFeatureType sft = types.get(typeName);
        if (sft == null) {
            throw new IOException("Type name : "+ typeName +"is unknowned is this datastore");
        }
        return sft;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String typeName) throws IOException {
        final SimpleFeatureType sft = types.get(typeName);
        final FeatureCollection<SimpleFeatureType,SimpleFeature> collection = new DefaultFeatureCollection(typeName + "-collection", sft);
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
        try {
            ResultSet result = getAllSamplingPoint.executeQuery();
            boolean firstCRS = true;
            while (result.next()) {
                if (firstCRS) {
                    String srsName = result.getString("point_srsname");
                    CoordinateReferenceSystem crs;
                    try {
                        crs = CRS.decode(srsName);
                        ((DefaultSimpleFeatureType)sft).setCoordinateReferenceSystem(crs);
                        firstCRS = false;
                    } catch (NoSuchAuthorityCodeException ex) {
                        throw new IOException(ex);
                    } catch (FactoryException ex) {
                        throw new IOException(ex);
                    }
                    
                }
                builder.reset();
                String id = result.getString("id");
                builder.set(ID, id);
                builder.set(DESC, result.getString("description"));
                builder.set(NAME, result.getString("name"));
                builder.set(SAMPLED, result.getString("sampled_feature"));

                double x         = result.getDouble("x_value");
                double y         = result.getDouble("y_value");
                Coordinate coord = new Coordinate(x, y);
                builder.set(POSITION, GF.createPoint(coord));
                collection.add(builder.buildFeature(id));
            }
            result.close();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL exception while reading the record of samplingPoint table", ex);
        }
        return DataUtilities.wrapToReader(sft, collection.features());
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            connection.close();
        } catch (SQLException ex) {
            LOGGER.info("SQL Exception while closing O&M datastore");
        }

    }
}
