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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A data flow connection between two components of an {@link AggregateProcess} or {@link
 * PhysicalSystem}, each end referenced by path.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Connection extends DataTransferObject {

    public static final String JSON_PROPERTY_SOURCE = "source";
    private String source;

    public static final String JSON_PROPERTY_DESTINATION = "destination";
    private String destination;

    /**
     * Path reference to the source of this connection. (required)
     */
    @JsonProperty(JSON_PROPERTY_SOURCE)
    public String getSource() {
        return source;
    }

    @JsonProperty(JSON_PROPERTY_SOURCE)
    public void setSource(String source) {
        this.source = source;
    }

    public Connection source(String source) {
        setSource(source);
        return this;
    }

    /**
     * Path reference to the destination of this connection. (required)
     */
    @JsonProperty(JSON_PROPERTY_DESTINATION)
    public String getDestination() {
        return destination;
    }

    @JsonProperty(JSON_PROPERTY_DESTINATION)
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Connection destination(String destination) {
        setDestination(destination);
        return this;
    }

}
