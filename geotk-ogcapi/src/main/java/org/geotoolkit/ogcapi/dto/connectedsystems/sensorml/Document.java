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
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;

/**
 * A reference to an external document related to a described object.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Document extends DataTransferObject {

    public static final String JSON_PROPERTY_ROLE = "role";
    private String role;

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_LINK = "link";
    private Link link;

    /**
     * URI qualifying the role of this document (e.g. datasheet, manual). (optional)
     */
    @JsonProperty(JSON_PROPERTY_ROLE)
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    public void setRole(String role) {
        this.role = role;
    }

    public Document role(String role) {
        setRole(role);
        return this;
    }

    /**
     * Name of the document. (required)
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public void setName(String name) {
        this.name = name;
    }

    public Document name(String name) {
        setName(name);
        return this;
    }

    /**
     * Description of the document. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public Document description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Link to the actual document. (required)
     */
    @JsonProperty(JSON_PROPERTY_LINK)
    public Link getLink() {
        return link;
    }

    @JsonProperty(JSON_PROPERTY_LINK)
    public void setLink(Link link) {
        this.link = link;
    }

    public Document link(Link link) {
        setLink(link);
        return this;
    }

}
