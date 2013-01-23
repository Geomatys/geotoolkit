/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.sos.xml;

import java.util.List;
import org.geotoolkit.ows.xml.RequestBase;
import org.opengis.observation.Observation;

/**
 *
 * @author Guilhem Legal (Geomatys).
 */
public interface InsertObservation extends RequestBase {
    
    String getAssignedSensorId();
    
    List<? extends Observation> getObservations();
    
}
