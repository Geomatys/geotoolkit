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
 * A non-decomposable (atomic) process, typically implementing a single algorithm.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class SimpleProcess extends AbstractProcess {

    public static final String JSON_PROPERTY_METHOD = "method";
    private ProcessMethod method;

    public SimpleProcess() {
        setType("SimpleProcess");
    }

    /**
     * Description of the algorithm/method implemented by this process. (optional)
     */
    @JsonProperty(JSON_PROPERTY_METHOD)
    public ProcessMethod getMethod() {
        return method;
    }

    @JsonProperty(JSON_PROPERTY_METHOD)
    public void setMethod(ProcessMethod method) {
        this.method = method;
    }

    public SimpleProcess method(ProcessMethod method) {
        setMethod(method);
        return this;
    }

}
