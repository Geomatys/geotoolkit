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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "CollectionKey")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "CollectionKey")
public final class CollectionKey extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_IS_DEFAULT = "isDefault";
    @XmlElement(name = "isDefault")
    @jakarta.annotation.Nonnull
    private Boolean isDefault;

    public static final String JSON_PROPERTY_LANGUAGE = "language";
    @XmlElement(name = "language")
    @jakarta.annotation.Nullable
    private String language;

    public CollectionKey() {
    }

    public CollectionKey id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

    /**
     * Identifier of the key field.
     * @return id
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nonnull String id) {
        this.id = id;
    }

    public CollectionKey isDefault(@jakarta.annotation.Nonnull Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    /**
     * Contains information on the collection's default key field to be used in the data joins.
     * Value {@code true} indicates the default key field. Exactly one object in a collection's
     * {@code keys} array must have this property set to {@code true}.
     * @return isDefault
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_IS_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "isDefault")
    public Boolean getIsDefault() {
        return isDefault;
    }

    @JsonProperty(JSON_PROPERTY_IS_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "isDefault")
    public void setIsDefault(@jakarta.annotation.Nonnull Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public CollectionKey language(@jakarta.annotation.Nullable String language) {
        this.language = language;
        return this;
    }

    /**
     * Language in which the key field's key values are written, if applicable. Follows the
     * ISO 639-1 language code values.
     * @return language
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LANGUAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty(JSON_PROPERTY_LANGUAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "language")
    public void setLanguage(@jakarta.annotation.Nullable String language) {
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
