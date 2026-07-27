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
 * A link to an externally hosted contact description, one of the two variants usable in
 * {@link DescribedObject#getContacts()} (the other being {@link ResponsibleParty}).
 *
 * <p>Structurally disambiguated from {@link ResponsibleParty} by the presence of {@code name}
 * and {@code link} (no shared discriminator is defined by the standard).</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class ContactLink extends DataTransferObject {

    public static final String JSON_PROPERTY_ROLE = "role";
    private String role;

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_LINK = "link";
    private Link link;

    /**
     * URI qualifying the role of this contact. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ROLE)
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    public void setRole(String role) {
        this.role = role;
    }

    public ContactLink role(String role) {
        setRole(role);
        return this;
    }

    /**
     * Name of the contact. (required)
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public void setName(String name) {
        this.name = name;
    }

    public ContactLink name(String name) {
        setName(name);
        return this;
    }

    /**
     * Link to the externally hosted contact description. (required)
     */
    @JsonProperty(JSON_PROPERTY_LINK)
    public Link getLink() {
        return link;
    }

    @JsonProperty(JSON_PROPERTY_LINK)
    public void setLink(Link link) {
        this.link = link;
    }

    public ContactLink link(Link link) {
        setLink(link);
        return this;
    }

}
