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
 *    Lesser General License for more details.
 */
package org.geotoolkit.sos;

import org.geotoolkit.client.Request;
import org.geotoolkit.sos.xml.v100.EventTime;
import org.geotoolkit.sos.xml.v100.GetFeatureOfInterest;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
interface GetFeatureOfInterestRequest extends Request {
    EventTime getEventTime();

    GetFeatureOfInterest.Location getLocation();

    String getFeatureOfInterestId();

    void setEventTime(EventTime eventTime);

    void setLocation(GetFeatureOfInterest.Location location);

    void setFeatureOfInterestId(String featureOfInterestId);

}
