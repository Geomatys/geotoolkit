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
package org.geotoolkit.ogcapi.dto.sensorthings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The paged collection-response envelope wrapping an OGC SensorThings API entity set,
 * e.g. {@code GET /v1.1/Things}.
 *
 * <p>Generic over the entity type {@code T} (a {@link SensorThingsEntity} subclass). Unlike other
 * DTOs in this module, this class is not dual JSON/JAXB-annotated: SensorThings has no XML
 * representation, and JAXB does not support generic type parameters via reflection.</p>
 *
 * @param <T> the entity type held by {@link #getValue()}
 * @see https://docs.ogc.org/is/18-088/18-088.html#requirement-resource-path-resource-collection-response
 * @author Quentin Bialota (Geomatys)
 */
public final class EntityList<T> extends DataTransferObject {

    public static final String JSON_PROPERTY_VALUE = "value";
    public static final String JSON_PROPERTY_COUNT = "@iot.count";
    public static final String JSON_PROPERTY_NEXT_LINK = "@iot.nextLink";

    @jakarta.annotation.Nonnull
    private List<T> value = new ArrayList<>();

    @jakarta.annotation.Nullable
    private Integer count;

    @jakarta.annotation.Nullable
    private String nextLink;

    public EntityList() {
    }

    public EntityList<T> value(@jakarta.annotation.Nonnull List<T> value) {
        this.value = value;
        return this;
    }

    public EntityList<T> addValueItem(T item) {
        if (this.value == null) {
            this.value = new ArrayList<>();
        }
        this.value.add(item);
        return this;
    }

    /**
     * The page of entities returned by this request. (required)
     *
     * @return the value
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<T> getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setValue(@jakarta.annotation.Nonnull List<T> value) {
        this.value = value;
    }

    public EntityList<T> count(@jakarta.annotation.Nullable Integer count) {
        this.count = count;
        return this;
    }

    /**
     * The total number of entities in the collection, present only when the {@code $count} query
     * option was requested. (optional)
     *
     * @return the count
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COUNT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getCount() {
        return count;
    }

    @JsonProperty(JSON_PROPERTY_COUNT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCount(@jakarta.annotation.Nullable Integer count) {
        this.count = count;
    }

    public EntityList<T> nextLink(@jakarta.annotation.Nullable String nextLink) {
        this.nextLink = nextLink;
        return this;
    }

    /**
     * The URL of the next page of results, present only when the result set is paged and this is
     * not the last page. (optional)
     *
     * @return the nextLink
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NEXT_LINK)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getNextLink() {
        return nextLink;
    }

    @JsonProperty(JSON_PROPERTY_NEXT_LINK)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNextLink(@jakarta.annotation.Nullable String nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityList<?> other = (EntityList<?>) o;
        return Objects.equals(this.value, other.value)
                && Objects.equals(this.count, other.count)
                && Objects.equals(this.nextLink, other.nextLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, count, nextLink);
    }
}
