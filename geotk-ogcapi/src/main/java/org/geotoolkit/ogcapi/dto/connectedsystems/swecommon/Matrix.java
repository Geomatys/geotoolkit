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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A multi-dimensional (nested array) SWE Common data component, typically used to encode a matrix
 * or tensor of {@code Count}/{@code Quantity} values.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Matrix extends AbstractArrayComponent {

    public static final String JSON_PROPERTY_REFERENCE_FRAME = "referenceFrame";
    private String referenceFrame;

    public static final String JSON_PROPERTY_LOCAL_FRAME = "localFrame";
    private String localFrame;

    public Matrix() {
        setType("Matrix");
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_FRAME)
    public String getReferenceFrame() {
        return referenceFrame;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_FRAME)
    public void setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;
    }

    @JsonProperty(JSON_PROPERTY_LOCAL_FRAME)
    public String getLocalFrame() {
        return localFrame;
    }

    @JsonProperty(JSON_PROPERTY_LOCAL_FRAME)
    public void setLocalFrame(String localFrame) {
        this.localFrame = localFrame;
    }

}
