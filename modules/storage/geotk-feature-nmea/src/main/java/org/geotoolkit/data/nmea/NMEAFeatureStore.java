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
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
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

    public final static GenericName TYPE_NAME  = NamesExt.create(null, "NMEA POINT");
    public final static GenericName GEOM_NAME  = NamesExt.create(null, "Location");
    public final static GenericName ALT_NAME   = NamesExt.create(null, "Altitude");
    public final static GenericName DEPTH_NAME = NamesExt.create(null, "Sea-depth");
    public final static GenericName DATE_NAME  = NamesExt.create(null, "Date");
    public final static GenericName SPEED_NAME = NamesExt.create(null, "Speed");

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
    public Set<GenericName> getNames() throws DataStoreException {
        return Collections.singleton(TYPE_NAME);
    }

    @Override
    public FeatureType getFeatureType(GenericName typeName) throws DataStoreException {
        if (typeName.tip().toString().equalsIgnoreCase(TYPE_NAME.tip().toString())) {
            return NMEA_TYPE;
        }
        throw new DataStoreException("NMEA Feature store manage only data of type "+TYPE_NAME.tip().toString());
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
    public void createFeatureType(GenericName typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only, and can process only data of type :"+TYPE_NAME.tip().toString());
    }

    @Override
    public void updateFeatureType(GenericName typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only, and can process only data of type :"+TYPE_NAME.tip().toString());
    }

    @Override
    public void deleteFeatureType(GenericName typeName) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false);
    }

    @Override
    public List<FeatureId> addFeatures(GenericName groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public void updateFeatures(GenericName groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public void removeFeatures(GenericName groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public FeatureWriter getFeatureWriter(GenericName typeName, Filter filter, Hints hints) throws DataStoreException {
        throw new DataStoreException("NMEA Feature Store is read only.");
    }

    @Override
    public void refreshMetaModel() {
    }

}
