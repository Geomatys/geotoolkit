/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.sos.v100;

import org.geotoolkit.sos.AbstractGetObservation;
import org.geotoolkit.sos.SensorObservationServiceClient;


/**
 * Implementation for the GetObservation request version 1.0.0.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class GetObservation100 extends AbstractGetObservation {
    /**
     * Defines the server url and its version.
     *
     * @param server The webservice.
     */
    public GetObservation100(final SensorObservationServiceClient server){
        super(server, "1.0.0");
    }

}
