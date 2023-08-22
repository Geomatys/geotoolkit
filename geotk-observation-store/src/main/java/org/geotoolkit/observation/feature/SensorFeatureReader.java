/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.observation.feature;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.sis.feature.internal.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.ObservationFilterReader;
import org.geotoolkit.observation.ObservationStore;
import static org.geotoolkit.observation.feature.OMFeatureTypes.*;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.geotoolkit.observation.query.LocationQuery;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.util.collection.CloseableIterator;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SensorFeatureReader implements CloseableIterator<Feature> {

    private final ObservationStore store;

    private final FeatureType type;

    private final Iterator<Entry<String, Geometry>> sensorLocations;

    public SensorFeatureReader(ObservationStore store, FeatureType type) throws DataStoreException {
        this.store = store;
        this.sensorLocations = this.store.getSensorLocations(new LocationQuery()).entrySet().iterator();
        this.type = type;
    }

    @Override
    public boolean hasNext() {
        return sensorLocations.hasNext();
    }

    @Override
    public Feature next() {
        Entry<String, Geometry> sensorLocation = sensorLocations.next();

        String sensorId = sensorLocation.getKey();
        Geometry geom   = sensorLocation.getValue();

        Feature current = type.newInstance();
        current.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), sensorId);
        current.setPropertyValue(SENSOR_ATT_ID.toString(), sensorId);
        current.setPropertyValue(SENSOR_ATT_POSITION.toString(), geom);

        return current;
    }

    @Override
    public void close() {
        // do nothing
    }

}
