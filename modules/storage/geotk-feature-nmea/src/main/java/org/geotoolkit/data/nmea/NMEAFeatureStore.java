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

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

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
        builder.addAttribute(Point.class).setName(GEOM_NAME).setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        builder.addAttribute(Double.class).setName(ALT_NAME);
        builder.addAttribute(Double.class).setName(DEPTH_NAME);
        builder.addAttribute(java.util.Date.class).setName(DATE_NAME);
        builder.addAttribute(Double.class).setName(SPEED_NAME);
        NMEA_TYPE = builder.build();
    }

    public NMEAFeatureStore(final ParameterValueGroup input) {
        super(input);
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(NMEAFeatureStoreFactory.NAME);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return Collections.singleton(TYPE_NAME);
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        if (typeName.equalsIgnoreCase(TYPE_NAME.tip().toString())) {
            return NMEA_TYPE;
        }
        throw new DataStoreException("NMEA Feature store manage only data of type " + TYPE_NAME.tip());
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        typeCheck(gquery.getTypeName());
        try {
            return new NMEAFileReader(openConnexion());
        } catch (IOException ex) {
            throw new DataStoreException(ex.getLocalizedMessage(), ex);
        }
    }

    private InputStream openConnexion() throws IOException {
        final URI source = parameters.parameter(NMEAFeatureStoreFactory.PATH.getName().getCode()).valueFile();
        final Path tmpFile = Paths.get(source);
        return Files.newInputStream(tmpFile);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false);
    }

}
