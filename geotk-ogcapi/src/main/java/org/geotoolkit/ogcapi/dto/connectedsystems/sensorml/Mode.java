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

/**
 * One of the operating modes a process can be configured with, listed in
 * {@link AbstractProcess#getModes()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Mode extends DescribedObject {

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    private Settings configuration;

    /**
     * Settings applied to the process when this mode is active. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public Settings getConfiguration() {
        return configuration;
    }

    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public void setConfiguration(Settings configuration) {
        this.configuration = configuration;
    }

    public Mode configuration(Settings configuration) {
        setConfiguration(configuration);
        return this;
    }

}
