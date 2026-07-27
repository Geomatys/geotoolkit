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
package org.geotoolkit.ogcapi.dto.connectedsystems.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.sensorml.Event;

/**
 * An event that occurred in the life of a system, exposed as a Part 2 REST resource.
 *
 * <p>This is exactly {@link org.geotoolkit.ogcapi.dto.connectedsystems.sensorml.Event} ({@code
 * id}, {@code label}, {@code description}, {@code definition}, {@code identifiers}, {@code
 * classifiers}, {@code contacts}, {@code documentation}, {@code time}, {@code properties}, {@code
 * configuration}) plus the {@link #getLinks()} field added at the Part 2 API layer.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class SystemEvent extends Event {

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Related links. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public SystemEvent links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
