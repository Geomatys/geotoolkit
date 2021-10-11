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

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.sml.xml.AbstractSensorML;

/**
 * An interface used by the SOS worker to store sensorML document into various datasource.
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface SensorWriter {

    /**
     * Store a new SensorML document into the data source.
     *
     * @param id The identifier of the sensor
     * @param sensor The sensor description.
     * @return True if the operation suceed.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    boolean writeSensor(String id, AbstractSensorML sensor) throws DataStoreException;

    /**
     * Delete a SensorML document into the data source.
     *
     * @param id The identifier of the sensor
     * @return True if the operation suceed.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    boolean deleteSensor(String id) throws DataStoreException;

    /**
     * Replace a SensorML document into the data source.
     *
     * @param id The identifier of the sensor
     * @param process The sensorML object
     * @return
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    int replaceSensor(String id, AbstractSensorML process) throws DataStoreException;

    /**
     * Start a transaction on the datasource.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    @Deprecated
    void startTransaction() throws DataStoreException;

    /**
     * Abort if there is a transaction running.
     * Restore the data like they were before the begin of the transaction.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    @Deprecated
    void abortTransaction() throws DataStoreException;

    /**
     * End a transaction (if there is one running)
     * and store the changement made during this transaction on the datasource.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    @Deprecated
    void endTransaction() throws DataStoreException;

    /**
     * Create a new identifier for a sensor.
     *
     * @return The new available identifier.
     * @throws org.apache.sis.storage.DataStoreException
     */
    String getNewSensorId() throws DataStoreException;

    /**
     * Return informations about the implementation class.
     */
    String getInfos();

    /**
     * Free the resources and close the connections to datasource.
     */
    void destroy();
}
