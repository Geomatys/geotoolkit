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
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.swes.xml.ObservationTemplate;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationWriter {

    /**
     * Write a new Observation template into the database
     *
     * @param template An O&M observation
     *
     * @return The new identifiers of the observation
     * 
     * @throws org.apache.sis.storage.DataStoreException
     */
    String writeObservationTemplate(final ObservationTemplate template) throws DataStoreException;
    
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
    String writeOffering(final ObservationOffering offering) throws DataStoreException;

    /**
     * Update an offering after the add of a new Observation.
     * The field updated are offering.procedure, offering.phenomenon, offering.samplingFeature
     *
     * @param offeringID The offering identifier.
     * @param offProc A mapping between an offering and a procedure.
     * @param offPheno A mapping between an offering and a phenomenon.
     * @param offSF A mapping between an offering and a samplingFeature.
     *
     * @throws org.apache.sis.storage.DataStoreException
     */
    void updateOffering(final String offeringID, final String offProc, final List<String> offPheno, final String offSF) throws DataStoreException;

    /**
     * Refresh the cached offerings.
     */
    void updateOfferings();

    /**
     * Record the location of a sensor in a separated dataSource if there is one (depends on the implementation).
     *
     * @param physicalID The physical id of the sensor.
     * @param position The GML position of the sensor.
     * @throws org.apache.sis.storage.DataStoreException
     */
    void recordProcedureLocation(final String physicalID, final AbstractGeometry position) throws DataStoreException;

    /**
     * Return informations about the implementation class.
     */
    String getInfos();

    /**
     * Free all the resources and close dataSource connections.
     */
    void destroy();
}
