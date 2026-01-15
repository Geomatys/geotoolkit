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
package org.geotoolkit.ogcapi.model.coverage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        DomainSet.JSON_PROPERTY_TYPE,
        DomainSet.JSON_PROPERTY_GENERAL_GRID
})
@XmlRootElement(name = "DomainSet")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DomainSet")
public class DomainSet extends DataTransferObject implements CoverageResponse {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    private final CoverageResponseType type = CoverageResponseType.DomainSet;

    public static final String JSON_PROPERTY_GENERAL_GRID = "generalGrid";
    @XmlElement(name = "generalGrid")
    @jakarta.annotation.Nullable
    private GeneralGrid generalGrid;

    public DomainSet() {
    }

    public DomainSet(GeneralGrid generalGrid) {
        this.generalGrid = generalGrid;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public CoverageResponseType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_GENERAL_GRID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "generalGrid")
    public GeneralGrid getGeneralGrid() {
        return generalGrid;
    }

    @JsonProperty(JSON_PROPERTY_GENERAL_GRID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "generalGrid")
    public void setGeneralGrid(GeneralGrid generalGrid) {
        this.generalGrid = generalGrid;
    }
}
