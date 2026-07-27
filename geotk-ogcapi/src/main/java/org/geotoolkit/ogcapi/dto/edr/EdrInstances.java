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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * Response of the {@code /collections/{collectionId}/instances} operation.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    EdrInstances.JSON_PROPERTY_LINKS,
    EdrInstances.JSON_PROPERTY_INSTANCES
})
public final class EdrInstances extends DataTransferObject {

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_INSTANCES = "instances";
    private List<EdrCollection> instances = new ArrayList<>();

    public EdrInstances() {
    }

    public EdrInstances links(List<Link> links) {
        this.links = links;
        return this;
    }

    public EdrInstances addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public EdrInstances instances(List<EdrCollection> instances) {
        this.instances = instances;
        return this;
    }

    public EdrInstances addInstancesItem(EdrCollection instancesItem) {
        if (this.instances == null) {
            this.instances = new ArrayList<>();
        }
        this.instances.add(instancesItem);
        return this;
    }

    /**
     * Get instances
     *
     * @return instances
     */
    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<EdrCollection> getInstances() {
        return instances;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setInstances(List<EdrCollection> instances) {
        this.instances = instances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EdrInstances other = (EdrInstances) o;
        return Objects.equals(this.links, other.links)
                && Objects.equals(this.instances, other.instances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, instances);
    }

}
