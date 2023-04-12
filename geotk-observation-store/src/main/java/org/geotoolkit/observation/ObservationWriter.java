/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.observation;

import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.model.Observation;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.model.Offering;
import org.geotoolkit.observation.model.Phenomenon;
import org.geotoolkit.observation.model.ProcedureDataset;
import org.locationtech.jts.geom.Geometry;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationWriter {

    /**
     * Write a new Observation into the database
     *
     * @param observation An O&M observation
     *
     * @return The new identifier of the observation.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    String writeObservation(final Observation observation) throws DataStoreException;

    /**
     * Write a list of observations into the database
     *
     * @param observations A list of O&M observations
     *
     * @return The new identifiers of the observation
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<String> writeObservations(final List<Observation> observations) throws DataStoreException;

     /**
     * Write a list of phenomenons into the database
     *
     * @param phenomenons A list of swe phenomenons
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void writePhenomenons(final List<Phenomenon> phenomenons) throws DataStoreException;

    /**
     * Remove an observation with the specified identifier.
     *
     * @param observationID
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void removeObservation(final String observationID) throws DataStoreException;

    /**
     * Remove an observation with the specified procedure.
     *
     * @param procedureID
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void removeObservationForProcedure(final String procedureID) throws DataStoreException;

    /**
     * Remove an observation dataset.
     *
     * @param dataset An complete observation dataset.
     * @return The list of sensor identifier removed.
     */
    List<String> removeDataSet(ObservationDataset dataset) throws DataStoreException;

    /**
     * Remove a procedure from the O&M datasource.
     *
     * @param procedureID
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void removeProcedure(String procedureID) throws DataStoreException;

    /**
     * Write a new Observation offering into the database
     *
     * @param offering
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    void writeOffering(final Offering offering) throws DataStoreException;

    /**
     * Record a procedure with its location and parent.
     *
     * @param procedure
     * @throws org.apache.sis.storage.DataStoreException
     */
    void writeProcedure(final ProcedureDataset procedure) throws DataStoreException;

    /**
     * Record the location of a sensor.
     *
     * @param physicalID The physical id of the sensor.
     * @param position The GML position of the sensor.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void recordProcedureLocation(final String physicalID, final Geometry position) throws DataStoreException;

    /**
     * Free all the resources and close dataSource connections.
     */
    void destroy();
}
