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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        DomainSet.JSON_PROPERTY_TYPE,
        DomainSet.JSON_PROPERTY_GENERAL_GRID
})
public class DomainSet extends DataTransferObject implements CoverageResponse {

    public static final String JSON_PROPERTY_TYPE = "type";
    private final CoverageResponseType type = CoverageResponseType.DomainSet;

    public static final String JSON_PROPERTY_GENERAL_GRID = "generalGrid";
    private GeneralGrid generalGrid;

    public DomainSet() {
    }

    public DomainSet(GeneralGrid generalGrid) {
        this.generalGrid = generalGrid;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public CoverageResponseType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_GENERAL_GRID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public GeneralGrid getGeneralGrid() {
        return generalGrid;
    }

    @JsonProperty(JSON_PROPERTY_GENERAL_GRID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setGeneralGrid(GeneralGrid generalGrid) {
        this.generalGrid = generalGrid;
    }
}
