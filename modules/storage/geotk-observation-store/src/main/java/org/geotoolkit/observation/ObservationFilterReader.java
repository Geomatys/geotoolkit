/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.observation;

import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.xml.Envelope;
import org.opengis.observation.Observation;
import org.opengis.observation.sampling.SamplingFeature;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationFilterReader extends ObservationFilter {

    /**
     * Return a list of Observation templates matching the builded filter.
     *
     * @param version the sosversion for the xml object returned.
     * 
     * @return A list of Observation templates matching the builded filter.
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<Observation> getObservationTemplates(final String version) throws DataStoreException;

     /**
     * Return a list of Observation matching the builded filter.
     * 
     * @param version the sosversion for the xml object returned.
     *
     * @return A list of Observation matching the builded filter.
     * @throws org.apache.sis.storage.DataStoreException
     */
    List<Observation> getObservations(final String version) throws DataStoreException;
    
    /**
     * 
     * @param version the sosversion for the xml object returned.
     * 
     * @return
     * @throws DataStoreException 
     */
    List<SamplingFeature> getFeatureOfInterests(final String version) throws DataStoreException;

    /**
     * Return an encoded block of data in a string.
     * The datas are the results of the matching observations.
     *
     * @return An encoded block of data in a string.
     * @throws org.apache.sis.storage.DataStoreException
     */
    String getResults() throws DataStoreException;

    /**
     * Return an encoded block of data in a string.
     * The datas are the results of the matching observations.
     * The datas are usually encoded as CSV (comma separated value) format.
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Object getOutOfBandResults() throws DataStoreException;

    /**
     * MIME type of the data that will be returned as the result of a GetObservation request.
     * This is usually text/xml; subtype="om/1.0.0".
     * In the case  that data is delivered out of band it might be text/xml;subtype="tml/2.0" for TML or some
     * other MIME type.
     *
     * @param responseFormat the MIME type of the response.
     */
    void setResponseFormat(String responseFormat);
    
    /**
     * return true if the filter reader take in charge the calculation of the collection bounding shape.
     * 
     * @return True if the filter compute itself the bounding shape of the collection. 
     */
    boolean computeCollectionBound();
    
    /**
     * If the filter reader caompute itself the bounding shape of the obervation collection.
     * this methode return the current shape.
     * @return 
     */
    Envelope getCollectionBoundingShape();
}

