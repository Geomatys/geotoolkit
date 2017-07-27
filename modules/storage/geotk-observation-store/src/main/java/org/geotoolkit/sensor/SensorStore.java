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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.sml.xml.AbstractSensorML;
import org.geotoolkit.storage.DataStoreFactory;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface SensorStore extends AutoCloseable {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    ParameterValueGroup getConfiguration();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    DataStoreFactory getFactory();

    Metadata getMetadata() throws DataStoreException;

    /**
     * Return handled sensorML format by version.
     *
     * @return A map of SOS Version / sensorML formats
     */
    Map<String, List<String>> getAcceptedSensorMLFormats();

    /**
     * Return all sensor identifiers.
     *
     * @return All sensor ID's.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<String> getSensorNames() throws DataStoreException;

     /**
     * Return the number of sensors in the data source.
     *
     * @return The number of sensors.
     * @throws org.apache.sis.storage.DataStoreException
     */
    int getSensorCount() throws DataStoreException;

    /**
     * Return the specified sensorML description from the specified ID.
     *
     * @param sensorID The identifier of the sensor.
     *
     * @return the specified sensor description from the specified ID.
     * @throws org.apache.sis.storage.DataStoreException
     */
    AbstractSensorML getSensorML(final String sensorID) throws DataStoreException;

    /**
     * Delete a Sensor metadata into the data source.
     *
     * @param id The identifier of the sensor
     * @return True if the operation succeed.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    boolean deleteSensor(String id) throws DataStoreException;

    /**
     * Create a new identifier for a sensor.
     * @return The new available identifier.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    String getNewSensorId() throws DataStoreException;

    /**
     * Store a new sensor metadata into the data source.
     *
     * @param id The identifier of the sensor
     * @param sensor The sensor description.
     * @return True if the operation suceed.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    boolean writeSensor(String id, Object sensor) throws DataStoreException;

    /**
     * Replace a sensor metadata into the data source.
     *
     * @param id The identifier of the sensor
     * @param sensor The sensor metadata object
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    int replaceSensor(String id, Object sensor) throws DataStoreException;

    /**
     * Return informations about the implementation class.
     *
     * @return A String description of the used implementations.
     */
    String getInfos();

    void close() throws DataStoreException;

}
