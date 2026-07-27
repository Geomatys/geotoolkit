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
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;

/**
 * A generic {@code {items, links}} collection wrapper, reused by the SensorML-encoded ({@code
 * application/sml+json}) collection responses of both the System and Procedure API resources
 * (Part 1). Since {@link AbstractProcess} extends {@link DescribedObject}, {@link #getItems()}
 * can hold any concrete process ({@link SimpleProcess}, {@link AggregateProcess}, {@link
 * PhysicalComponent}, {@link PhysicalSystem}).
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class ProcessCollection extends DataTransferObject {

    public static final String JSON_PROPERTY_ITEMS = "items";
    private List<DescribedObject> items;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * The processes contained in this collection page. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ITEMS)
    public List<DescribedObject> getItems() {
        return items;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    public void setItems(List<DescribedObject> items) {
        this.items = items;
    }

    public ProcessCollection items(List<DescribedObject> items) {
        setItems(items);
        return this;
    }

    /**
     * Web links related to this collection (e.g. pagination). (optional)
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public ProcessCollection links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
