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
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;

/**
 * A paged collection of {@link CommandResult} resources.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class CommandResultCollection extends DataTransferObject {

    public static final String JSON_PROPERTY_ITEMS = "items";
    private List<CommandResult> items;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Command results in this page of the collection. (required)
     */
    @JsonProperty(JSON_PROPERTY_ITEMS)
    public List<CommandResult> getItems() {
        return items;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    public void setItems(List<CommandResult> items) {
        this.items = items;
    }

    public CommandResultCollection items(List<CommandResult> items) {
        setItems(items);
        return this;
    }

    /**
     * Related links, e.g. pagination links. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public CommandResultCollection links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
