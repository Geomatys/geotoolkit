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
package org.geotoolkit.stac.dto;

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
import org.geotoolkit.ogcapi.model.common.Link;

import java.util.List;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/EO-Data-Discovery">OpenEO Doc</a>
 * Based on : <a href="https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md">STAC Spec Github</a>
 */
@JsonPropertyOrder({
        Collections.JSON_PROPERTY_LINKS,
        Collections.JSON_PROPERTY_COLLECTIONS
})
@XmlRootElement(name = "Collections")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Collections")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Collections extends DataTransferObject {

    // --- PROPERTY CONSTANTS ---
    public static final String JSON_PROPERTY_LINKS = "links";
    public static final String JSON_PROPERTY_COLLECTIONS = "collections";

    @XmlElement(name = "links")
    private List<Link> links;

    @XmlElement(name = "collections")
    private List<Collection> collections;

    public Collections() {}

    public Collections(List<Collection> collections, List<Link> links) {
        this.collections = collections;
        this.links = links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "links")
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "links")
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @JsonProperty(JSON_PROPERTY_COLLECTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "collections")
    public List<Collection> getCollections() {
        return collections;
    }

    @JsonProperty(JSON_PROPERTY_COLLECTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "collections")
    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }
}
