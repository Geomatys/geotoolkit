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
package org.geotoolkit.data.nmea;

import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A feature store for GPS measures of NMEA standard.
 * Note that for the moment, only reading is supported. Two readings are possible :
 * - From a text file containing a list of measures.
 * - Directly by getting messages sent by a GPS device on serial port.
 *
 * @author Alexis Manin (Geomatys)
 */
public class NMEAFeatureStore extends AbstractFeatureStore {

    public final static Name TYPE_NAME  = DefaultName.create(null, "NMEA POINT");
    public final static Name GEOM_NAME  = DefaultName.create(null, "Location");
    public final static Name ALT_NAME   = DefaultName.create(null, "Altitude");
    public final static Name DEPTH_NAME = DefaultName.create(null, "Sea-depth");
    public final static Name DATE_NAME  = DefaultName.create(null, "Date");
    public final static Name SPEED_NAME = DefaultName.create(null, "Speed");

    /** Feature type to use for nmea data encapsulation */
    public static final FeatureType NMEA_TYPE;
    static {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName(TYPE_NAME);
        builder.add(GEOM_NAME, Point.class, CommonCRS.WGS84.normalizedGeographic());
        builder.setDefaultGeometry(GEOM_NAME);
        builder.add(ALT_NAME, Double.class);
        builder.add(DEPTH_NAME, Double.class);
        builder.add(DATE_NAME, java.util.Date.class);
        builder.add(SPEED_NAME, Double.class);
        NMEA_TYPE = builder.buildFeatureType();
    }

    public NMEAFeatureStore(final ParameterValueGroup input) {
        super(input);
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(NMEAFeatureStoreFactory.NAME);
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        return Collections.singleton(TYPE_NAME);
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        if (typeName.getLocalPart().equalsIgnoreCase(TYPE_NAME.getLocalPart())) {
            return NMEA_TYPE;
        }
        throw new DataStoreException("NMEA Feature store manage only data of type "+TYPE_NAME.getLocalPart());
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        try {
            return new NMEAFileReader(openConnexion());
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex.getLocalizedMessage(), ex);
        }
    }

    private InputStream openConnexion() throws FileNotFoundException {
        final URI source = parameters.parameter(NMEAFeatureStoreFactory.URLP.getName().getCode()).valueFile();
        final File tmpFile = new File(source);
        return new FileInputStream(tmpFile);
    }

      //////////////////////////////////////
     //      UNSUPPORTED OPERATIONS      //
    //////////////////////////////////////

    @Override
    public void createFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only, and can process only data of type :"+TYPE_NAME.getLocalPart());
    }

    @Override
    public void updateFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only, and can process only data of type :"+TYPE_NAME.getLocalPart());
    }

    @Override
    public void deleteFeatureType(Name typeName) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false);
    }

    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public void refreshMetaModel() {
    }

}
