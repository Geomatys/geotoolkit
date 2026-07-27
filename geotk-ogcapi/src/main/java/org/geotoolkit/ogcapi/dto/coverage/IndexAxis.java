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
package org.geotoolkit.ogcapi.dto.coverage;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        // Since IndexAxis only inherits properties, the order is managed by the parent's @JsonPropertyOrder.
        // We include this annotation for consistency in case future properties are added.
})
public class IndexAxis extends Axis {

    public IndexAxis() {
        super("IndexAxis", null, null, null);
    }

    public IndexAxis(String axisLabel, Object lowerBound, Object upperBound) {
        super("IndexAxis", axisLabel, lowerBound, upperBound);
    }
}
