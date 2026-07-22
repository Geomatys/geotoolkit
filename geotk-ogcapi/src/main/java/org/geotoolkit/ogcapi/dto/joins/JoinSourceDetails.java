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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Details of a join source (spec schema {@code DetailsObject}), nested in
 * {@link JoinSource#getDetails()}.
 *
 * @see https://docs.ogc.org/DRAFTS/22-026.html#_response_schema_for_the_join_source
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    JoinSourceDetails.JSON_PROPERTY_COLLECTION_KEY,
    JoinSourceDetails.JSON_PROPERTY_CREATED,
    JoinSourceDetails.JSON_PROPERTY_JOIN_FIELDS,
    JoinSourceDetails.JSON_PROPERTY_JOIN_KEY,
    JoinSourceDetails.JSON_PROPERTY_NUMBER_OF_ROWS,
    JoinSourceDetails.JSON_PROPERTY_SOURCE_FILE
})
@XmlRootElement(name = "JoinSourceDetails")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "JoinSourceDetails")
public final class JoinSourceDetails extends DataTransferObject {

    public static final String JSON_PROPERTY_COLLECTION_KEY = "collectionKey";
    @XmlElement(name = "collectionKey")
    @jakarta.annotation.Nullable
    private String collectionKey;

    public static final String JSON_PROPERTY_CREATED = "created";
    @XmlElement(name = "created")
    @jakarta.annotation.Nullable
    private OffsetDateTime created;

    public static final String JSON_PROPERTY_JOIN_FIELDS = "joinFields";
    @XmlElement(name = "joinFields")
    @jakarta.annotation.Nullable
    private List<String> joinFields = new ArrayList<>();

    public static final String JSON_PROPERTY_JOIN_KEY = "joinKey";
    @XmlElement(name = "joinKey")
    @jakarta.annotation.Nullable
    private String joinKey;

    public static final String JSON_PROPERTY_NUMBER_OF_ROWS = "numberOfRows";
    @XmlElement(name = "numberOfRows")
    @jakarta.annotation.Nullable
    private Integer numberOfRows;

    public static final String JSON_PROPERTY_SOURCE_FILE = "sourceFile";
    @XmlElement(name = "sourceFile")
    @jakarta.annotation.Nullable
    private String sourceFile;

    public JoinSourceDetails() {
    }

    public JoinSourceDetails collectionKey(@jakarta.annotation.Nullable String collectionKey) {
        this.collectionKey = collectionKey;
        return this;
    }

    /**
     * Key of the collection that matches the {@code joinKey} of this join source.
     * @return collectionKey
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COLLECTION_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "collectionKey")
    public String getCollectionKey() {
        return collectionKey;
    }

    @JsonProperty(JSON_PROPERTY_COLLECTION_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "collectionKey")
    public void setCollectionKey(@jakarta.annotation.Nullable String collectionKey) {
        this.collectionKey = collectionKey;
    }

    public JoinSourceDetails created(@jakarta.annotation.Nullable OffsetDateTime created) {
        this.created = created;
        return this;
    }

    /**
     * The time of the join source creation.
     * @return created
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public OffsetDateTime getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "created")
    public void setCreated(@jakarta.annotation.Nullable OffsetDateTime created) {
        this.created = created;
    }

    public JoinSourceDetails joinFields(@jakarta.annotation.Nullable List<String> joinFields) {
        this.joinFields = joinFields;
        return this;
    }

    public JoinSourceDetails addJoinFieldsItem(String joinFieldsItem) {
        if (this.joinFields == null) {
            this.joinFields = new ArrayList<>();
        }
        this.joinFields.add(joinFieldsItem);
        return this;
    }

    /**
     * Names of the data fields joined by this join source.
     * @return joinFields
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_JOIN_FIELDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "joinFields")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getJoinFields() {
        return joinFields;
    }

    @JsonProperty(JSON_PROPERTY_JOIN_FIELDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "joinFields")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setJoinFields(@jakarta.annotation.Nullable List<String> joinFields) {
        this.joinFields = joinFields;
    }

    public JoinSourceDetails joinKey(@jakarta.annotation.Nullable String joinKey) {
        this.joinKey = joinKey;
        return this;
    }

    /**
     * Name of the property that is the join source's key.
     * @return joinKey
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_JOIN_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "joinKey")
    public String getJoinKey() {
        return joinKey;
    }

    @JsonProperty(JSON_PROPERTY_JOIN_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "joinKey")
    public void setJoinKey(@jakarta.annotation.Nullable String joinKey) {
        this.joinKey = joinKey;
    }

    public JoinSourceDetails numberOfRows(@jakarta.annotation.Nullable Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
        return this;
    }

    /**
     * The number of rows in the join source.
     * @return numberOfRows
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_OF_ROWS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberOfRows")
    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_OF_ROWS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberOfRows")
    public void setNumberOfRows(@jakarta.annotation.Nullable Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public JoinSourceDetails sourceFile(@jakarta.annotation.Nullable String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    /**
     * Name of the input data file that was used to create this join source.
     * @return sourceFile
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SOURCE_FILE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "sourceFile")
    public String getSourceFile() {
        return sourceFile;
    }

    @JsonProperty(JSON_PROPERTY_SOURCE_FILE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "sourceFile")
    public void setSourceFile(@jakarta.annotation.Nullable String sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JoinSourceDetails that = (JoinSourceDetails) o;
        return Objects.equals(this.collectionKey, that.collectionKey)
                && Objects.equals(this.created, that.created)
                && Objects.equals(this.joinFields, that.joinFields)
                && Objects.equals(this.joinKey, that.joinKey)
                && Objects.equals(this.numberOfRows, that.numberOfRows)
                && Objects.equals(this.sourceFile, that.sourceFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionKey, created, joinFields, joinKey, numberOfRows, sourceFile);
    }

}
