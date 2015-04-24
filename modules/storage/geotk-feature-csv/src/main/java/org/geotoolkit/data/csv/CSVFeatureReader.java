/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.apache.sis.internal.feature.AttributeConvention;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
class CSVFeatureReader implements FeatureReader {

    protected final CSVFeatureStore store;
    protected final ReadWriteLock fileLock;
    protected final FeatureType featureType;
    protected final WKTReader reader = new WKTReader();
    protected final Scanner scanner;
    protected final AttributeType[] atts;
    protected final Feature reuse;
    protected Feature current = null;
    protected int inc = 0;

    CSVFeatureReader(CSVFeatureStore store, final FeatureType featureType, final boolean reuseFeature, final ReadWriteLock fileLock) throws DataStoreException {
        this.store = store;
        this.fileLock = fileLock;
        this.featureType = featureType;
        this.fileLock.readLock().lock();
        if (reuseFeature) {
            reuse = CSVUtils.defaultFeature(featureType, "0");
        } else {
            reuse = null;
        }
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
    }

    @Override
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
            final List<String> fields = CSVUtils.toStringList(scanner, line, store.getSeparator());
            current = null;
            if (reuse == null) {
                current = CSVUtils.defaultFeature(featureType, Integer.toString(inc++));
            } else {
                reuse.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), Integer.toString(inc++));
                current = reuse;
            }
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
                current.setPropertyValue(att.getName().toString(), value);
            }
        }
    }

    @Override
    public void close() {
        fileLock.readLock().unlock();
        scanner.close();
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("Not supported on reader.");
    }

}
