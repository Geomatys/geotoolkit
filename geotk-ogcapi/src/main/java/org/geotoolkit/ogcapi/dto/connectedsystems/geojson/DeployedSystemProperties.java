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
package org.geotoolkit.ogcapi.dto.connectedsystems.geojson;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;

/**
 * The {@code properties} sub-object of a {@link DeployedSystem} GeoJSON feature.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class DeployedSystemProperties extends FeatureProperties {

    public static final String JSON_PROPERTY_SYSTEM_LINK = "system@link";
    private Link systemLink;

    public static final String JSON_PROPERTY_PROCEDURE_LINK = "procedure@link";
    private Link procedureLink;

    /**
     * Link to the deployed system. (required)
     */
    @JsonProperty(JSON_PROPERTY_SYSTEM_LINK)
    public Link getSystemLink() {
        return systemLink;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM_LINK)
    public void setSystemLink(Link systemLink) {
        this.systemLink = systemLink;
    }

    public DeployedSystemProperties systemLink(Link systemLink) {
        setSystemLink(systemLink);
        return this;
    }

    /**
     * Link to the procedure implemented by the deployed system. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public Link getProcedureLink() {
        return procedureLink;
    }

    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public void setProcedureLink(Link procedureLink) {
        this.procedureLink = procedureLink;
    }

    public DeployedSystemProperties procedureLink(Link procedureLink) {
        setProcedureLink(procedureLink);
        return this;
    }

}
