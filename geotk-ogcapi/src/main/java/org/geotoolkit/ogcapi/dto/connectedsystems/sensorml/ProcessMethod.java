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
import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Description of the algorithm/method implemented by a {@link SimpleProcess} or {@link
 * PhysicalComponent}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class ProcessMethod extends DataTransferObject {

    public static final String JSON_PROPERTY_ALGORITHM = "algorithm";
    private JsonNode algorithm;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    /**
     * Arbitrary machine-readable content describing the algorithm (e.g. a formula, a reference
     * implementation); kept raw since the standard leaves its content open-ended. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ALGORITHM)
    public JsonNode getAlgorithm() {
        return algorithm;
    }

    @JsonProperty(JSON_PROPERTY_ALGORITHM)
    public void setAlgorithm(JsonNode algorithm) {
        this.algorithm = algorithm;
    }

    public ProcessMethod algorithm(JsonNode algorithm) {
        setAlgorithm(algorithm);
        return this;
    }

    /**
     * Human readable description of the method. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public ProcessMethod description(String description) {
        setDescription(description);
        return this;
    }

}
