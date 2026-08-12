/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.observation;

import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.sos.xml.v100.EventTime;

/**
 * To retrieve only the result data of observations matching an observation template.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetResult extends RequestParameters {

    private String observationTemplateId;
    private EventTime[] eventTimes;

    /**
     * Identifier of the observation template to query. (required)
     *
     * @return the observationTemplateId
     */
    public String getObservationTemplateId() {
        return observationTemplateId;
    }

    public void setObservationTemplateId(String observationTemplateId) {
        this.observationTemplateId = observationTemplateId;
    }

    public GetResult observationTemplateId(String observationTemplateId) {
        setObservationTemplateId(observationTemplateId);
        return this;
    }

    public EventTime[] getEventTimes() {
        return eventTimes;
    }

    public void setEventTimes(EventTime... eventTimes) {
        this.eventTimes = eventTimes;
    }

    public GetResult eventTimes(EventTime... eventTimes) {
        setEventTimes(eventTimes);
        return this;
    }

}
