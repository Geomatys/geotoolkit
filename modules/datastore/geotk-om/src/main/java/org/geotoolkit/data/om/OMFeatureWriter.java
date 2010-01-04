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

import com.vividsolutions.jts.geom.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMFeatureWriter implements FeatureWriter<SimpleFeatureType, SimpleFeature> {

    private Logger LOGGER = Logger.getLogger("org.geotoolkit.data.om");

    private SimpleFeatureType featureType;

    private List<SimpleFeature> featureList =  new ArrayList<SimpleFeature>();

    private PreparedStatement writeSamplingPoint;

    private PreparedStatement getLastIndentifier;

    private final Connection connection;

    public OMFeatureWriter(SimpleFeatureType featureType, Connection connection) {
        this.featureType = featureType;
        this.connection  = connection;
        initStatement();
    }

    private void initStatement() {
        try {
            writeSamplingPoint = connection.prepareStatement("INSERT INTO \"observation\".\"sampling_points\" VALUES(?,?,?,?,?,?,?,?,?)");
            getLastIndentifier = connection.prepareStatement("SELECT COUNT(*) FROM \"observation\".\"sampling_points\"");
        } catch (SQLException ex) {
           LOGGER.severe("SQL Exception while requesting the O&M database:" + ex.getMessage());
        }
    }

    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    public SimpleFeature next() throws DataStoreRuntimeException {
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        String identifier = "sampling-point-";
        try {
            ResultSet result = getLastIndentifier.executeQuery();
            if (result.next()) {
                int nb = result.getInt(1) + 1;
                identifier = identifier + nb;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        SimpleFeature feature = builder.buildFeature(identifier);
        featureList.add(feature);
        return feature;
    }

    public void remove() throws DataStoreRuntimeException {
        featureList.remove(featureList.size() - 1);
    }

    public void write() throws DataStoreRuntimeException {
        try {
            for (SimpleFeature feature : featureList) {
                writeSamplingPoint.setString(1, feature.getID());
                writeSamplingPoint.setString(2, (String)feature.getAttribute(OMDataStore.DESC));
                writeSamplingPoint.setString(3, (String)feature.getAttribute(OMDataStore.NAME));
                writeSamplingPoint.setString(4, (String)feature.getAttribute(OMDataStore.SAMPLED));
                writeSamplingPoint.setNull(5, java.sql.Types.VARCHAR);
                Object geometry = feature.getDefaultGeometry();
                if (geometry instanceof Point) {
                    Point pt = (Point) geometry;
                    String identifier = null;
                    if (featureType.getCoordinateReferenceSystem() != null) {
                        try {
                            identifier = CRS.lookupIdentifier(featureType.getCoordinateReferenceSystem(), true);
                        } catch (FactoryException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
                    }
                    if (identifier != null) {
                        writeSamplingPoint.setString(6, identifier);
                    } else {
                        writeSamplingPoint.setNull(6, java.sql.Types.VARCHAR);
                    }
                    writeSamplingPoint.setInt(7, 2);
                    writeSamplingPoint.setDouble(8, pt.getX());
                    writeSamplingPoint.setDouble(9, pt.getY());
                } else {
                    writeSamplingPoint.setNull(6, java.sql.Types.VARCHAR);
                    writeSamplingPoint.setNull(7, java.sql.Types.INTEGER);
                    writeSamplingPoint.setNull(8, java.sql.Types.DOUBLE);
                    writeSamplingPoint.setNull(9, java.sql.Types.DOUBLE);
                }
                writeSamplingPoint.executeUpdate();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public boolean hasNext() throws DataStoreRuntimeException {
        return false;
    }

    public void close() throws DataStoreRuntimeException {
        
    }

}
