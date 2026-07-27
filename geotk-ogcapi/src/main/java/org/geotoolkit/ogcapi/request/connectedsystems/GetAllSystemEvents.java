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
package org.geotoolkit.ogcapi.request.connectedsystems;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code GET} operation on {@code /systemEvents}: lists SystemEvent resources across all
 * Systems.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetAllSystemEvents extends RequestParameters {

    private String eventTime;
    private List<String> eventType;
    private List<String> q;
    private List<String> system;
    private Integer limit;

    /**
     * A date-time or a period, per RFC 3339, filtering on the event time. (optional)
     *
     * @return the eventTime
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     * @see #getEventTime()
     */
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     * @see #getEventTime()
     */
    public GetAllSystemEvents eventTime(String eventTime) {
        setEventTime(eventTime);
        return this;
    }

    /**
     * Only events of one of the given types are selected. (optional)
     *
     * @return the eventType
     */
    public List<String> getEventType() {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     * @see #getEventType()
     */
    public void setEventType(List<String> eventType) {
        this.eventType = eventType;
    }

    /**
     * @param eventType the eventType to set
     * @see #getEventType()
     */
    public GetAllSystemEvents eventType(List<String> eventType) {
        setEventType(eventType);
        return this;
    }

    /**
     * Free-text search on the resource's textual properties. (optional)
     *
     * @return the q
     */
    public List<String> getQ() {
        return q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public void setQ(List<String> q) {
        this.q = q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public GetAllSystemEvents q(List<String> q) {
        setQ(q);
        return this;
    }

    /**
     * Only resources associated with one of the given system identifiers are selected. (optional)
     *
     * @return the system
     */
    public List<String> getSystem() {
        return system;
    }

    /**
     * @param system the system to set
     * @see #getSystem()
     */
    public void setSystem(List<String> system) {
        this.system = system;
    }

    /**
     * @param system the system to set
     * @see #getSystem()
     */
    public GetAllSystemEvents system(List<String> system) {
        setSystem(system);
        return this;
    }

    /**
     * The optional limit parameter limits the number of items presented in the response. (optional)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetAllSystemEvents limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
