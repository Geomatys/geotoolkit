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

package org.geotoolkit.data.sml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class SMLDataStore extends AbstractDataStore {

    private final Map<Name,SimpleFeatureType> types = new HashMap<Name, SimpleFeatureType>();

    private final Connection connection;

    private final static Name SYSTEM         = new DefaultName("http://www.opengis.net/sml/1.0", "System");
    private final static Name COMPONENT      = new DefaultName("http://www.opengis.net/sml/1.0", "Component");
    private final static Name PROCESSCHAIN   = new DefaultName("http://www.opengis.net/sml/1.0", "ProcessChain");
    private final static Name PROCESSMODEL   = new DefaultName("http://www.opengis.net/sml/1.0", "ProcessModel");
    private final static Name DATASOURCETYPE = new DefaultName("http://www.opengis.net/sml/1.0", "DataSourceType");

    private static final String pathDescription        = "SensorML:SensorML:member:description";
    private static final String pathName               = "SensorML:SensorML:member:name";
    private static final String pathKeywords           = "SensorML:SensorML:member:keywords:keyword";
    private static final String pathPhenomenonsSystem  = "SensorML:SensorML:member:outputs:output:field:name";
    private static final String pathPhenomenonsCompo   = "SensorML:SensorML:member:outputs:output:definition";
    private static final String pathInputsDef          = "SensorML:SensorML:member:inputs:input:definition";
    private static final String pathInputsNam          = "SensorML:SensorML:member:inputs:input:name";
    private static final String pathOutputsDef         = "SensorML:SensorML:member:outputs:output:field:definition";
    private static final String pathOutputsNam         = "SensorML:SensorML:member:outputs:output:field:name";
    private static final String pathContactType        = "SensorML:SensorML:member:contact:role";
    private static final String pathProducerOrg        = "SensorML:SensorML:member:contact:organizationName";
    private static final String pathProducerInd        = "SensorML:SensorML:member:contact:individualName";
    private static final String pathComponentsNam      = "SensorML:SensorML:member:components:component:name";
    private static final String pathComponentsRef      = "SensorML:SensorML:member:components:component:href";
    private static final String pathMethod             = "SensorML:SensorML:member:description";  // TODO
    private static final String pathCharacteristicsNam = "SensorML:SensorML:member:characteristics:field:field:name";
    private static final String pathCharacteristicsVal = "SensorML:SensorML:member:characteristics:field:field:value";
    private static final String pathLocation           = "SensorML:SensorML:member:location:pos";
    private static final String pathCRS                = "SensorML:SensorML:member:location:pos:srsName";
    private static final String pathSmlRef             = "SensorML:SensorML:member:documentation:onlineResource:href";

    private PreparedStatement getAllFormId;
    private PreparedStatement getTextValue;
    private PreparedStatement getTextValue2;
    private PreparedStatement getSMLType;

    /*
     * Shared attributes
     */
    private static final Name DESC        = new DefaultName("http://www.opengis.net/gml",     "description");
    private static final Name NAME        = new DefaultName("http://www.opengis.net/gml",     "name");
    private static final Name KEYWORDS    = new DefaultName("http://www.opengis.net/sml/1.0", "keywords");
    private static final Name LOCATION    = new DefaultName("http://www.opengis.net/sml/1.0", "location");
    private static final Name PHENOMENONS = new DefaultName("http://www.opengis.net/sml/1.0", "phenomenons");
    private static final Name SMLTYPE     = new DefaultName("http://www.opengis.net/sml/1.0", "smltype");
    private static final Name SMLREF      = new DefaultName("http://www.opengis.net/sml/1.0", "smlref");
    private static final Name INPUTS      = new DefaultName("http://www.opengis.net/sml/1.0", "inputs");
    private static final Name OUTPUTS     = new DefaultName("http://www.opengis.net/sml/1.0", "outputs");

    /*
     * attribute for sml:System or sml:ProcessChain
     */
    private static final Name PRODUCER    = new DefaultName("http://www.opengis.net/sml/1.0", "producer");
    private static final Name COMPONENTS  = new DefaultName("http://www.opengis.net/sml/1.0", "components");

    /*
     * attribute for sml:ProccessModel
     */
    private static final Name METHOD      = new DefaultName("http://www.opengis.net/sml/1.0", "method");

    /*
     * attribute for sml:DatasourceType
     */
    private static final Name CHARACTERISTICS = new DefaultName("http://www.opengis.net/sml/1.0", "characteristics");

    private static final GeometryFactory GF = new GeometryFactory();

    public SMLDataStore(Connection connection) {
        //not transactional for the moment
        super(false);
        this.connection = connection;
        initTypes();
        initStatement();
    }

    private void initStatement() {
        try {
            getTextValue  = connection.prepareStatement("SELECT \"value\"      FROM \"Storage\".\"TextValues\" WHERE \"path\"=? AND \"form\"=?");
            getTextValue2 = connection.prepareStatement("SELECT \"value\"      FROM \"Storage\".\"TextValues\" WHERE \"path\"=? AND \"form\"=?");
            getSMLType    = connection.prepareStatement("SELECT \"type\"       FROM \"Storage\".\"Values\"     WHERE \"path\"='SensorML:SensorML:member' AND \"form\"=?");
            getAllFormId  = connection.prepareStatement("SELECT \"identifier\" FROM \"Storage\".\"Forms\"      WHERE \"catalog\"='SMLC'");

        } catch (SQLException ex) {
           getLogger().severe("SQL Exception while initializing the prepared statement for SensorML database:" + ex.getMessage());
        }
    }

    private void initTypes() {
        final SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        final AttributeDescriptorBuilder attributeDescBuilder = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder attributeTypeBuilder = new AttributeTypeBuilder();

        // gml:description
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(DESC);
        attributeDescBuilder.setMaxOccurs(1);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attDescription = attributeDescBuilder.buildDescriptor();
        

        // gml:name
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(NAME);
        attributeDescBuilder.setMaxOccurs(1);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(false);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attName = attributeDescBuilder.buildDescriptor();
        
        // sml:keywords
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(KEYWORDS);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attkey = attributeDescBuilder.buildDescriptor();

        // sml:location
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(Point.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(LOCATION);
        attributeDescBuilder.setMaxOccurs(1);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(false);
        attributeDescBuilder.setType(attributeTypeBuilder.buildGeometryType());
        final AttributeDescriptor attLocation = attributeDescBuilder.buildDescriptor();

        // sml:phenomenons
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(PHENOMENONS);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attPhen = attributeDescBuilder.buildDescriptor();

        // sml:smltype
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(SMLTYPE);
        attributeDescBuilder.setMaxOccurs(1);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attSmt = attributeDescBuilder.buildDescriptor();

        // sml:smlref
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(SMLREF);
        attributeDescBuilder.setMaxOccurs(1);
        attributeDescBuilder.setMinOccurs(1);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attSmr = attributeDescBuilder.buildDescriptor();

        // sml:inputs
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(INPUTS);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attInp = attributeDescBuilder.buildDescriptor();

        // sml:outputs
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(OUTPUTS);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attOut = attributeDescBuilder.buildDescriptor();


        /*
         * Feature type sml:Component
         */
        featureTypeBuilder.reset();
        featureTypeBuilder.setName(new DefaultName("http://www.opengis.net/sml/1.0", "Component"));
        featureTypeBuilder.add(0, attDescription);
        featureTypeBuilder.add(1, attName);
        featureTypeBuilder.add(2, attkey);
        featureTypeBuilder.add(3, attLocation);
        featureTypeBuilder.setDefaultGeometry(LOCATION.getLocalPart());
        featureTypeBuilder.add(4, attPhen);
        featureTypeBuilder.add(5, attSmt);
        featureTypeBuilder.add(6, attSmr);
        featureTypeBuilder.add(7, attInp);
        featureTypeBuilder.add(8, attOut);

        final SimpleFeatureType componentType = featureTypeBuilder.buildFeatureType();
        types.put(COMPONENT, componentType);

        // sml:producer
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(PRODUCER);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attProd = attributeDescBuilder.buildDescriptor();

        // sml:component
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(COMPONENTS);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attCom = attributeDescBuilder.buildDescriptor();


        /*
         * Feature type sml:System
         */
        featureTypeBuilder.reset();
        featureTypeBuilder.setName(new DefaultName("http://www.opengis.net/sml/1.0", "System"));
        featureTypeBuilder.add(0, attDescription);
        featureTypeBuilder.add(1, attName);
        featureTypeBuilder.add(2, attkey);
        featureTypeBuilder.add(3, attLocation);
        featureTypeBuilder.setDefaultGeometry(LOCATION.getLocalPart());
        featureTypeBuilder.add(4, attPhen);
        featureTypeBuilder.add(5, attSmt);
        featureTypeBuilder.add(6, attSmr);
        featureTypeBuilder.add(7, attInp);
        featureTypeBuilder.add(8, attOut);
        featureTypeBuilder.add(9, attProd);
        featureTypeBuilder.add(10, attCom);

        final SimpleFeatureType systemType = featureTypeBuilder.buildFeatureType();
        types.put(SYSTEM, systemType);

        /*
         * Feature type sml:ProcessChain
         */
        featureTypeBuilder.reset();
        featureTypeBuilder.setName(new DefaultName("http://www.opengis.net/sml/1.0", "ProcessChain"));
        featureTypeBuilder.add(0, attDescription);
        featureTypeBuilder.add(1, attName);
        featureTypeBuilder.add(2, attkey);
        featureTypeBuilder.add(3, attLocation);
        featureTypeBuilder.setDefaultGeometry(LOCATION.getLocalPart());
        featureTypeBuilder.add(4, attPhen);
        featureTypeBuilder.add(5, attSmt);
        featureTypeBuilder.add(6, attSmr);
        featureTypeBuilder.add(7, attInp);
        featureTypeBuilder.add(8, attOut);
        featureTypeBuilder.add(9, attProd);
        featureTypeBuilder.add(10, attCom);

        final SimpleFeatureType processChainType = featureTypeBuilder.buildFeatureType();
        types.put(PROCESSCHAIN, processChainType);

        // sml:method
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(METHOD);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attMet = attributeDescBuilder.buildDescriptor();

         /*
         * Feature type sml:ProcessModel
         */
        featureTypeBuilder.reset();
        featureTypeBuilder.setName(new DefaultName("http://www.opengis.net/sml/1.0", "ProcessModel"));
        featureTypeBuilder.add(0, attDescription);
        featureTypeBuilder.add(1, attName);
        featureTypeBuilder.add(2, attkey);
        featureTypeBuilder.add(3, attLocation);
        featureTypeBuilder.setDefaultGeometry(LOCATION.getLocalPart());
        featureTypeBuilder.add(4, attPhen);
        featureTypeBuilder.add(5, attSmt);
        featureTypeBuilder.add(6, attSmr);
        featureTypeBuilder.add(7, attInp);
        featureTypeBuilder.add(8, attOut);
        featureTypeBuilder.add(9, attMet);

        final SimpleFeatureType processModelType = featureTypeBuilder.buildFeatureType();
        types.put(PROCESSMODEL, processModelType);

        // sml:characteristics
        attributeTypeBuilder.reset();
        attributeTypeBuilder.setBinding(String.class);
        attributeDescBuilder.reset();
        attributeDescBuilder.setName(CHARACTERISTICS);
        attributeDescBuilder.setMaxOccurs(Integer.MAX_VALUE);
        attributeDescBuilder.setMinOccurs(0);
        attributeDescBuilder.setNillable(true);
        attributeDescBuilder.setType(attributeTypeBuilder.buildType());
        final AttributeDescriptor attChar = attributeDescBuilder.buildDescriptor();

         /*
         * Feature type sml:DataSourceType
         */
        featureTypeBuilder.reset();
        featureTypeBuilder.setName(new DefaultName("http://www.opengis.net/sml/1.0", "DataSourceType"));
        featureTypeBuilder.add(0, attDescription);
        featureTypeBuilder.add(1, attName);
        featureTypeBuilder.add(2, attkey);
        featureTypeBuilder.add(3, attLocation);
        featureTypeBuilder.setDefaultGeometry(LOCATION.getLocalPart());
        featureTypeBuilder.add(4, attPhen);
        featureTypeBuilder.add(5, attSmt);
        featureTypeBuilder.add(6, attSmr);
        featureTypeBuilder.add(7, attInp);
        featureTypeBuilder.add(8, attOut);
        featureTypeBuilder.add(9, attChar);

        final SimpleFeatureType dataSourceType = featureTypeBuilder.buildFeatureType();
        types.put(DATASOURCETYPE, dataSourceType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Map<Name, SimpleFeatureType> getTypes() throws IOException {
        return Collections.unmodifiableMap(types);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(Query query) throws IOException {
        final Name name = query.getTypeName();
        final SimpleFeatureType sft = types.get(name);
        final FeatureCollection<SimpleFeatureType,SimpleFeature> collection = getFeatureCollection(sft, name.getLocalPart());
        return DataUtilities.wrapToReader(sft, collection.features());
    }
    
    private FeatureCollection<SimpleFeatureType,SimpleFeature> getFeatureCollection(SimpleFeatureType sft, String typeName) throws IOException {

        final FeatureCollection<SimpleFeatureType,SimpleFeature> collection = new DefaultFeatureCollection(typeName + "-collection", sft);
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);

        try {
            ResultSet result = getAllFormId.executeQuery();

            boolean firstCRS = true;
            while (result.next()) {
                int formID = result.getInt(1);
                SimpleFeature feature = buildFeature(builder, formID, typeName);
                if (feature != null) {
                    collection.add(feature);

                    if (firstCRS) {
                        getTextValue.setString(1, pathCRS);
                        getTextValue.setInt(2, formID);
                        ResultSet resultCRS = getTextValue.executeQuery();
                        if (resultCRS.next()) {
                            String srsName = resultCRS.getString(1);

                            if (srsName.startsWith("urn:ogc:crs:")) {
                                srsName = "urn:ogc:def:crs:" + srsName.substring(12);
                            }
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
                    }
                }

            }
            result.close();

        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, "SQL exception while reading sensorMLValues table", ex);
        }
        return collection;
    }


    /**
     * Build a simpleFeature
     *
     * @param builder
     * @param formID
     * @param typeName
     * @return
     * @throws SQLException
     */
    private SimpleFeature buildFeature(SimpleFeatureBuilder builder, int formID, String typeName) throws SQLException {
        builder.reset();

        /*
         * we filter on the type
         *
         */
        getSMLType.setInt(1, formID);
        ResultSet result2 = getSMLType.executeQuery();
        String type = null;
        if (result2.next()) {
            type = result2.getString(1);
            builder.set(SMLTYPE, type);
        }
        result2.close();
        if (!type.equals(typeName)) {
            return null;
        }

        /*
         * GML : DESCRIPTION
         */
        getTextValue.setString(1, pathDescription);
        getTextValue.setInt(2, formID);
        result2 = getTextValue.executeQuery();
        if (result2.next()) {
            builder.set(DESC, result2.getString(1));
        }
        result2.close();

        /*
         * GML : NAME
         */
        getTextValue.setString(1, pathName);
        getTextValue.setInt(2, formID);
        result2 = getTextValue.executeQuery();
        if (result2.next()) {
            builder.set(NAME, result2.getString(1));
        }
        result2.close();

        /*
         * SML : KEYWORDS (multiple)
         */
        getTextValue.setString(1, pathKeywords);
        getTextValue.setInt(2, formID);
        result2 = getTextValue.executeQuery();
        List<String> keywords = new ArrayList<String>();
        while (result2.next()) {
            keywords.add(result2.getString(1));
        }
        result2.close();
        builder.set(KEYWORDS, keywords);

        /*
         *  SML : REF
         */
        getTextValue.setString(1, pathSmlRef);
        getTextValue.setInt(2, formID);
        result2 = getTextValue.executeQuery();
        if (result2.next()) {
            builder.set(SMLREF, result2.getString(1));
        }
        result2.close();


        /*
         * SML : INPUTS (multiple map)
         */
        getTextValue.setString(1, pathInputsDef);
        getTextValue.setInt(2, formID);
        result2 = getTextValue.executeQuery();
        getTextValue2.setString(1, pathInputsNam);
        getTextValue2.setInt(2, formID);
        ResultSet result3 = getTextValue2.executeQuery();
        Map<String, String> inputs = new HashMap<String, String>();
        while (result2.next() && result3.next()) {
            inputs.put(result3.getString(1), result2.getString(1));
        }
        result2.close();
        result3.close();
        builder.set(INPUTS, inputs);

        /*
         * SML : OUTPUTS (multiple map)
         */
        getTextValue.setString(1, pathOutputsDef);
        getTextValue.setInt(2, formID);
        result2 = getTextValue.executeQuery();
        getTextValue2.setString(1, pathOutputsNam);
        getTextValue2.setInt(2, formID);
        result3 = getTextValue2.executeQuery();
        Map<String, String> outputs = new HashMap<String, String>();
        while (result2.next() && result3.next()) {
            outputs.put(result3.getString(1), result2.getString(1));
        }
        result2.close();
        result3.close();
        builder.set(OUTPUTS, outputs);

        /*
         * SML : LOCATION (geometric)
         */
        getTextValue.setString(1, pathLocation);
        getTextValue.setInt(2, formID);
        result2 = getTextValue.executeQuery();
        if (result2.next()) {
            String location = result2.getString(1);
            try {
                double x = Double.parseDouble(location.substring(0, location.indexOf(" ")));
                double y = Double.parseDouble(location.substring(location.indexOf(" ") + 1));
                Coordinate coord = new Coordinate(x, y);
                builder.set(LOCATION, GF.createPoint(coord));
            } catch (NumberFormatException ex) {
                getLogger().warning("unable to extract the point coordinate from the text value:" + location);
            }
        }
        result2.close();

        if (typeName.equals("Component")) {

            /*
             * SML : PHENOMENONS
             */
            getTextValue.setString(1, pathPhenomenonsCompo);
            getTextValue.setInt(2, formID);
            result2 = getTextValue.executeQuery();
            List<String> phenomenons = new ArrayList<String>();
            if (result2.next()) {
                phenomenons.add(result2.getString(1));
            }
            result2.close();
            builder.set(PHENOMENONS, phenomenons);

        } else if (typeName.equals("System") || typeName.equals("ProcessChain")) {

            /*
             * SML : PHENOMENONS (multiple)
             */
            getTextValue.setString(1, pathPhenomenonsSystem);
            getTextValue.setInt(2, formID);
            result2 = getTextValue.executeQuery();
            List<String> phenomenons = new ArrayList<String>();
            while (result2.next()) {
                phenomenons.add(result2.getString(1));
            }
            result2.close();
            builder.set(PHENOMENONS, phenomenons);

            
            /*
             * TODO SML : PRODUCER
             *
             */


            /*
             * SML : COMPONENT (multiple map)
             */
            getTextValue.setString(1, pathComponentsRef);
            getTextValue.setInt(2, formID);
            result2 = getTextValue.executeQuery();
            getTextValue2.setString(1, pathComponentsNam);
            getTextValue2.setInt(2, formID);
            result3 = getTextValue2.executeQuery();
            Map<String, String> components = new HashMap<String, String>();
            while (result2.next() && result3.next()) {
                components.put(result3.getString(1), result2.getString(1));
            }
            result2.close();
            result3.close();
            if (components.size() == 0) {
                components = null;
            }
            builder.set(COMPONENTS, components);

        } else if (typeName.equals("ProcessModel")) {
            // TODO method
        } else if (typeName.equals("DataSourceType")) {

            /*
             * SML : CHARACTERISTIC (multiple map)
             */
            getTextValue.setString(1, pathCharacteristicsVal);
            getTextValue.setInt(2, formID);
            result2 = getTextValue.executeQuery();
            getTextValue2.setString(1, pathCharacteristicsNam);
            getTextValue2.setInt(2, formID);
            result3 = getTextValue2.executeQuery();
            Map<String, String> characteristics = new HashMap<String, String>();
            while (result2.next() && result3.next()) {
                characteristics.put(result3.getString(1), result2.getString(1));
            }
            result2.close();
            result3.close();
            builder.set(CHARACTERISTICS, characteristics);
        }
        return builder.buildFeature(formID + ""); // TODO id
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        super.dispose();
        try {
            getAllFormId.close();
            getTextValue.close();
            getTextValue2.close();
            getSMLType.close();
            connection.close();
        } catch (SQLException ex) {
            getLogger().info("SQL Exception while closing SML datastore");
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected JTSEnvelope2D getBounds(Query query) throws IOException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected int getCount(Query query) throws IOException {
        return -1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void createSchema(SimpleFeatureType featureType) throws IOException {
        throw new IOException("Schema creation not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateSchema(Name typeName, SimpleFeatureType featureType) throws IOException {
        throw new IOException("Schema update not supported.");
    }

}
