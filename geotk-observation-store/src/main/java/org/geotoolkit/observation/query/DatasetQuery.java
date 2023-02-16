/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.observation.query;

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.storage.FeatureQuery;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DatasetQuery extends FeatureQuery {

    /**
     * If supported the store will affect this id to a (single) extracted sensor.
     */
    private final String affectedSensorID;

    /**
     * Filter on the sensor to include int the dataset.
     */
    private final List<String> sensorIds;


    public DatasetQuery() {
        this(null, null);
    }

    public DatasetQuery(String affectedSensorID) {
        this(affectedSensorID, null);
    }

    public DatasetQuery(List<String> sensorIds) {
        this(null, sensorIds);
    }

    private DatasetQuery(String affectedSensorID, List<String> sensorIds) {
        this.affectedSensorID = affectedSensorID;
        if (sensorIds != null) {
            this.sensorIds = sensorIds;
        } else {
            this.sensorIds = new ArrayList<>();
        }
    }

    public String getAffectedSensorID() {
        return affectedSensorID;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }
}
