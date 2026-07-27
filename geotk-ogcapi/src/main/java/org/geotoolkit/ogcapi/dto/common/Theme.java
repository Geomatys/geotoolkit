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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Theme
 */
@JsonPropertyOrder({
    Theme.JSON_PROPERTY_CONCEPTS,
    Theme.JSON_PROPERTY_SCHEME
})
public final class Theme extends DataTransferObject {

    public static final String JSON_PROPERTY_CONCEPTS = "concepts";
    private List<Concepts> concepts = new ArrayList<>();

    public static final String JSON_PROPERTY_SCHEME = "scheme";
    private String scheme;

    public Theme() {
    }

    public Theme concepts(List<Concepts> concepts) {
        this.concepts = concepts;
        return this;
    }

    public Theme addConceptsItem(Concepts conceptsItem) {
        if (this.concepts == null) {
            this.concepts = new ArrayList<>();
        }
        this.concepts.add(conceptsItem);
        return this;
    }

    /**
     * One or more entity/concept identifiers from this knowledge system. it is
     * recommended that a resolvable URI be used for each entity/concept
     * identifier.
     *
     * @return concepts
     */
    @JsonProperty(JSON_PROPERTY_CONCEPTS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Concepts> getConcepts() {
        return concepts;
    }

    @JsonProperty(JSON_PROPERTY_CONCEPTS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setConcepts(List<Concepts> concepts) {
        this.concepts = concepts;
    }

    public Theme scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * An identifier for the knowledge organization system used to classify the
     * resource. It is recommended that the identifier be a resolvable URI. The
     * list of schemes used in a searchable catalog can be determined by
     * inspecting the server&#39;s OpenAPI document or, if the server implements
     * CQL2, by exposing a queryable (e.g. named &#x60;scheme&#x60;) and
     * enumerating the list of schemes in the queryable&#39;s schema definition.
     *
     * @return scheme
     */
    @JsonProperty(JSON_PROPERTY_SCHEME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getScheme() {
        return scheme;
    }

    @JsonProperty(JSON_PROPERTY_SCHEME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Return true if this theme object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Theme theme = (Theme) o;
        return Objects.equals(this.concepts, theme.concepts)
                && Objects.equals(this.scheme, theme.scheme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concepts, scheme);
    }

}
