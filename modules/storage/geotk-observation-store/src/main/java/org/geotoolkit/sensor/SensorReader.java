/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.sensor;

import org.geotoolkit.sml.xml.AbstractSensorML;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface SensorReader {

    /**
     * Return handled sensorML format by version.
     *
     * @return A map of SOS Version / sensorML formats
     */
    Map<String, List<String>> getAcceptedSensorMLFormats();

    /**
     * Return the specified sensor description from the specified ID.
     *
     * @param sensorID The identifier of the sensor.
     *
     * @return the specified sensor description from the specified ID.
     * @throws org.apache.sis.storage.DataStoreException
     */
    AbstractSensorML getSensor(final String sensorID) throws DataStoreException;

    /**
     * Return all sensor ID's.
     *
     * @return All sensor ID's.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getSensorNames() throws DataStoreException;

    /**
     * Return informations about the implementation class.
     *
     * @return A String description of the used implementations.
     */
    String getInfos();

    /**
     * Return the number of sensors in the data source.
     *
     * @return The number of sensors.
     * @throws org.apache.sis.storage.DataStoreException
     */
    int getSensorCount() throws DataStoreException;

    /**
     * Remove the specified sensor metadata from the cache.
     * @param sensorID
     */
    void removeFromCache(final String sensorID);

    /**
     * Destroy and free the resource used by the reader.
     */
    void destroy();
}
