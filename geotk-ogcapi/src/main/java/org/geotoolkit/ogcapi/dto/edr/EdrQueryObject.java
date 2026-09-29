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
package org.geotoolkit.ogcapi.dto.edr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * One entry of the EDR {@code data_queries} object (OGC API - EDR, "EDR Query Object"): a {@code link} to the
 * query endpoint, whose {@code variables} describe the query.
 * <pre>{@code "position": {"link": {"href": ".../position", "rel": "data", "variables": {"query_type": "position", ...}}}}</pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class EdrQueryObject extends DataTransferObject {

    public static final String JSON_PROPERTY_LINK = "link";
    private DataQueryLink link;

    public EdrQueryObject() {
    }

    public EdrQueryObject(DataQueryLink link) {
        this.link = link;
    }

    /**
     * @param link the link to set
     * @see #getLink()
     */
    public EdrQueryObject link(DataQueryLink link) {
        setLink(link);
        return this;
    }

    /**
     * The link to the query endpoint, with the query description in its {@code variables}. (required)
     *
     * @return the link
     */
    @JsonProperty(JSON_PROPERTY_LINK)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueryLink getLink() {
        return link;
    }

    /**
     * @param link the link to set
     * @see #getLink()
     */
    @JsonProperty(JSON_PROPERTY_LINK)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLink(DataQueryLink link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(this.link, ((EdrQueryObject) o).link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }
}
