/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2019, Geomatys
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
package org.geotoolkit.data.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.data.csv.CSVUtils.LatLonConfig;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
class CSVReader implements Iterator<Feature>, AutoCloseable {

    protected final CSVStore store;
    protected final ReadWriteLock fileLock;
    protected final FeatureType featureType;
    protected final WKTReader reader = new WKTReader(new GeometryFactory(PackedCoordinateSequenceFactory.DOUBLE_FACTORY));
    protected final Scanner scanner;
    protected final AttributeType[] atts;
    protected boolean withId;
    protected Feature current = null;
    protected int inc = 0;
    protected final LatLonConfig latLonConfig;
    private GeometryFactory geometryFactory = new GeometryFactory();

    CSVReader(CSVStore store, final FeatureType featureType, final ReadWriteLock fileLock, final LatLonConfig latLonConfig) throws DataStoreException {
        this.store = store;
        this.fileLock = fileLock;
        this.featureType = featureType;
        this.fileLock.readLock().lock();
        try {
            scanner = new Scanner(store.getFile());
            //skip the type line
            scanner.nextLine();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }

        final List<AttributeType> atts = new ArrayList<>();
        for (PropertyType pt : featureType.getProperties(true)) {
            if (AttributeConvention.contains(pt.getName()) || pt instanceof Operation) continue;
            atts.add((AttributeType) pt);
        }

        this.atts = atts.toArray(new AttributeType[0]);

        // Check if there's identifiers to report.
        if (featureType.hasProperty(AttributeConvention.IDENTIFIER)) {
            withId = true;
        }
        this.latLonConfig = latLonConfig;
        reader.setIsOldJtsCoordinateSyntaxAllowed(false);
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        read();
        final Feature ob = current;
        current = null;
        if (ob == null) {
            throw new FeatureStoreRuntimeException("No more records.");
        }
        return ob;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        read();
        return current != null;
    }

    private void read() throws FeatureStoreRuntimeException {
        if (current != null) {
            return;
        }
        final String line = CSVUtils.getNextLine(scanner);
        if (line != null) {
            Double lat = null;
            Double lon = null;
            final List<String> fields = CSVUtils.toStringList(scanner, line, store.getSeparator());
            current = featureType.newInstance();
            if (withId) current.setPropertyValue(AttributeConvention.IDENTIFIER, inc++);
            final int fieldSize = fields.size();
            for (int i = 0, n = atts.length; i < n; i++) {
                final AttributeType<?> att = atts[i];
                final Object value;
                if (i >= fieldSize) {
                    value = null;
                } else if (AttributeConvention.isGeometryAttribute(att)) {
                    if (fields.get(i).trim().isEmpty()) {
                        value = null;
                    } else {
                        try {
                            value = reader.read(fields.get(i));
                        } catch (ParseException ex) {
                            throw new FeatureStoreRuntimeException(ex);
                        }
                    }
                } else {
                    value = ObjectConverters.convert(fields.get(i), att.getValueClass());
                }
                String attName = att.getName().toString();
                current.setPropertyValue(attName, value);
                if (latLonConfig != null) {
                    if (latLonConfig.latColumn.equals(attName)) {
                        lat = Double.valueOf(fields.get(i));
                    } else if (latLonConfig.lonColumn.equals(attName)) {
                        lon = Double.valueOf(fields.get(i));
                    }
                }
            }
            if (lat != null && lon != null) {
                Point pt;
                if (latLonConfig.isLongitudeFirst) {
                    pt = geometryFactory.createPoint(new CoordinateXY(lon, lat));
                } else {
                    pt = geometryFactory.createPoint(new CoordinateXY(lat, lon));
                }
                current.setPropertyValue(latLonConfig.geoColumnName, pt);
            }
        }
    }

    @Override
    public void close() {
        fileLock.readLock().unlock();
        scanner.close();
    }

}
