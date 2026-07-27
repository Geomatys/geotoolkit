/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Discrete Global Grid System Reference System (DGGRS) Definition.
 */
@JsonPropertyOrder({
    DggrsDefinition.JSON_PROPERTY_TITLE,
    DggrsDefinition.JSON_PROPERTY_DESCRIPTION,
    DggrsDefinition.JSON_PROPERTY_URI,
    DggrsDefinition.JSON_PROPERTY_DGGH,
    DggrsDefinition.JSON_PROPERTY_ZIRS,
    DggrsDefinition.JSON_PROPERTY_SUB_ZONE_ORDER
})
public final class DggrsDefinition extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_URI = "uri";
    private URI uri;

    public static final String JSON_PROPERTY_DGGH = "dggh";
    private Dggs dggh;

    public static final String JSON_PROPERTY_ZIRS = "zirs";
    private Zirs zirs;

    public static final String JSON_PROPERTY_SUB_ZONE_ORDER = "subZoneOrder";
    private SubZoneOrder subZoneOrder;

    public DggrsDefinition() {
    }

    public DggrsDefinition title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public DggrsDefinition description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public DggrsDefinition uri(URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * The authoritative URI associated with this DGGRS definition
     *
     * @return uri
     */
    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getUri() {
        return uri;
    }

    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUri(URI uri) {
        this.uri = uri;
    }

    public DggrsDefinition dggh(Dggs dggh) {
        this.dggh = dggh;
        return this;
    }

    /**
     * Get dggh
     *
     * @return dggh
     */
    @JsonProperty(JSON_PROPERTY_DGGH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Dggs getDggh() {
        return dggh;
    }

    @JsonProperty(JSON_PROPERTY_DGGH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDggh(Dggs dggh) {
        this.dggh = dggh;
    }

    public DggrsDefinition zirs(Zirs zirs) {
        this.zirs = zirs;
        return this;
    }

    /**
     * Get zirs
     *
     * @return zirs
     */
    @JsonProperty(JSON_PROPERTY_ZIRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Zirs getZirs() {
        return zirs;
    }

    @JsonProperty(JSON_PROPERTY_ZIRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setZirs(Zirs zirs) {
        this.zirs = zirs;
    }

    public DggrsDefinition subZoneOrder(SubZoneOrder subZoneOrder) {
        this.subZoneOrder = subZoneOrder;
        return this;
    }

    /**
     * Get subZoneOrder
     *
     * @return subZoneOrder
     */
    @JsonProperty(JSON_PROPERTY_SUB_ZONE_ORDER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public SubZoneOrder getSubZoneOrder() {
        return subZoneOrder;
    }

    @JsonProperty(JSON_PROPERTY_SUB_ZONE_ORDER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setSubZoneOrder(SubZoneOrder subZoneOrder) {
        this.subZoneOrder = subZoneOrder;
    }

    /**
     * Return true if this dggrs-definition object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsDefinition dggrsDefinition = (DggrsDefinition) o;
        return Objects.equals(this.title, dggrsDefinition.title)
                && Objects.equals(this.description, dggrsDefinition.description)
                && Objects.equals(this.uri, dggrsDefinition.uri)
                && Objects.equals(this.dggh, dggrsDefinition.dggh)
                && Objects.equals(this.zirs, dggrsDefinition.zirs)
                && Objects.equals(this.subZoneOrder, dggrsDefinition.subZoneOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, uri, dggh, zirs, subZoneOrder);
    }

}
