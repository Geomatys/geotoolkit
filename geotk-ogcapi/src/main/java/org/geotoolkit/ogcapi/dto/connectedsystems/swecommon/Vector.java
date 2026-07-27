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
import java.util.List;

/**
 * A vector (coordinate tuple in a given reference frame) SWE Common data component.
 *
 * <p>Simplification: the standard restricts {@code coordinates} items to {@code Count}, {@code
 * Quantity} or {@code Time}; this binding types them as the general {@link AbstractDataComponent}
 * for simplicity.</p>
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class Vector extends AbstractDataComponent {

    public static final String JSON_PROPERTY_LOCAL_FRAME = "localFrame";
    private String localFrame;

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    private List<AbstractDataComponent> coordinates;

    public Vector() {
        setType("Vector");
    }

    /**
     * URI of a custom local reference frame. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LOCAL_FRAME)
    public String getLocalFrame() {
        return localFrame;
    }

    @JsonProperty(JSON_PROPERTY_LOCAL_FRAME)
    public void setLocalFrame(String localFrame) {
        this.localFrame = localFrame;
    }

    /**
     * Named coordinate components. (required)
     */
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    public List<AbstractDataComponent> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    public void setCoordinates(List<AbstractDataComponent> coordinates) {
        this.coordinates = coordinates;
    }

}
