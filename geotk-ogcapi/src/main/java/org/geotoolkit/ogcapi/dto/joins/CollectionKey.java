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
package org.geotoolkit.ogcapi.dto.joins;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Description of a single key field of a collection, as returned by
 * {@code GET /collections/{collectionId}/keys}.
 *
 * @see https://docs.ogc.org/DRAFTS/22-026.html#_response_schema_for_the_collection_s_key_fields
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    CollectionKey.JSON_PROPERTY_ID,
    CollectionKey.JSON_PROPERTY_IS_DEFAULT,
    CollectionKey.JSON_PROPERTY_LANGUAGE
})
public final class CollectionKey extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_IS_DEFAULT = "isDefault";
    private Boolean isDefault;

    public static final String JSON_PROPERTY_LANGUAGE = "language";
    private String language;

    public CollectionKey() {
    }

    public CollectionKey id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Identifier of the key field.
     * @return id
     */
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setId(String id) {
        this.id = id;
    }

    public CollectionKey isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    /**
     * Contains information on the collection's default key field to be used in the data joins.
     * Value {@code true} indicates the default key field. Exactly one object in a collection's
     * {@code keys} array must have this property set to {@code true}.
     * @return isDefault
     */
    @JsonProperty(JSON_PROPERTY_IS_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Boolean getIsDefault() {
        return isDefault;
    }

    @JsonProperty(JSON_PROPERTY_IS_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public CollectionKey language(String language) {
        this.language = language;
        return this;
    }

    /**
     * Language in which the key field's key values are written, if applicable. Follows the
     * ISO 639-1 language code values.
     * @return language
     */
    @JsonProperty(JSON_PROPERTY_LANGUAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getLanguage() {
        return language;
    }

    @JsonProperty(JSON_PROPERTY_LANGUAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CollectionKey that = (CollectionKey) o;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.isDefault, that.isDefault)
                && Objects.equals(this.language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isDefault, language);
    }

}
