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
 * Base of every scalar/range SWE Common 3.0 data component (Boolean, Count(Range), Quantity(Range),
 * Time(Range), Category(Range), Text). Concrete subtypes redeclare {@code constraint}, {@code
 * nilValues} and {@code value} with their own concrete types.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public abstract class AbstractSimpleComponent extends AbstractDataComponent {

    public static final String JSON_PROPERTY_REFERENCE_FRAME = "referenceFrame";
    private String referenceFrame;

    public static final String JSON_PROPERTY_AXIS_ID = "axisID";
    private String axisID;

    /**
     * URI (or URI reference) of the spatial or temporal reference frame this value is defined in. (optional)
     */
    @JsonProperty(JSON_PROPERTY_REFERENCE_FRAME)
    public String getReferenceFrame() {
        return referenceFrame;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_FRAME)
    public void setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;
    }

    /**
     * Axis identifier when this value is one of several coupled values in a coordinate reference frame. (optional)
     */
    @JsonProperty(JSON_PROPERTY_AXIS_ID)
    public String getAxisID() {
        return axisID;
    }

    @JsonProperty(JSON_PROPERTY_AXIS_ID)
    public void setAxisID(String axisID) {
        this.axisID = axisID;
    }

}
