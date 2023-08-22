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

import java.util.Map;
import org.apache.sis.feature.internal.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.ObservationFilterReader;
import static org.geotoolkit.observation.feature.OMFeatureTypes.*;
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
public class SensorFeatureFilteredReader implements CloseableIterator<Feature> {

    private final ObservationFilterReader observationFilter;

    private final FeatureType type;

    private final long count;

    private int cpt = 0;

    private final LocationQuery query;

    public SensorFeatureFilteredReader(ObservationFilterReader observationFilter, FeatureType type) throws DataStoreException {
        this.observationFilter = observationFilter;
        this.observationFilter.init(new LocationQuery());
        count = observationFilter.getCount();

        this.query = new LocationQuery();
        this.query.setLimit(1);
        this.type = type;
    }

    @Override
    public boolean hasNext() {
        return cpt < count;
    }

    @Override
    public Feature next() {
        try {
            query.setOffset(cpt);
            observationFilter.init(query);
            Map<String, Geometry> sensorLocations = observationFilter.getSensorLocations();

            //  should contains exactly one entry
            String sensorId = sensorLocations.keySet().iterator().next();
            Geometry geom   = sensorLocations.get(sensorId);

            Feature current = type.newInstance();
            current.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), sensorId);
            current.setPropertyValue(SENSOR_ATT_ID.toString(), sensorId);
            current.setPropertyValue(SENSOR_ATT_POSITION.toString(), geom);

            cpt++;

            return current;
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public void close() {
        observationFilter.destroy();
    }

}
