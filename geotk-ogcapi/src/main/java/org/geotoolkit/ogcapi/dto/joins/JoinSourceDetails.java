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
public final class JoinSourceDetails extends DataTransferObject {

    public static final String JSON_PROPERTY_COLLECTION_KEY = "collectionKey";
    private String collectionKey;

    public static final String JSON_PROPERTY_CREATED = "created";
    private OffsetDateTime created;

    public static final String JSON_PROPERTY_JOIN_FIELDS = "joinFields";
    private List<String> joinFields = new ArrayList<>();

    public static final String JSON_PROPERTY_JOIN_KEY = "joinKey";
    private String joinKey;

    public static final String JSON_PROPERTY_NUMBER_OF_ROWS = "numberOfRows";
    private Integer numberOfRows;

    public static final String JSON_PROPERTY_SOURCE_FILE = "sourceFile";
    private String sourceFile;

    public JoinSourceDetails() {
    }

    public JoinSourceDetails collectionKey(String collectionKey) {
        this.collectionKey = collectionKey;
        return this;
    }

    /**
     * Key of the collection that matches the {@code joinKey} of this join source.
     * @return collectionKey
     */
    @JsonProperty(JSON_PROPERTY_COLLECTION_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCollectionKey() {
        return collectionKey;
    }

    @JsonProperty(JSON_PROPERTY_COLLECTION_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCollectionKey(String collectionKey) {
        this.collectionKey = collectionKey;
    }

    public JoinSourceDetails created(OffsetDateTime created) {
        this.created = created;
        return this;
    }

    /**
     * The time of the join source creation.
     * @return created
     */
    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public OffsetDateTime getCreated() {
        return created;
    }

    @JsonProperty(JSON_PROPERTY_CREATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public JoinSourceDetails joinFields(List<String> joinFields) {
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
    @JsonProperty(JSON_PROPERTY_JOIN_FIELDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getJoinFields() {
        return joinFields;
    }

    @JsonProperty(JSON_PROPERTY_JOIN_FIELDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setJoinFields(List<String> joinFields) {
        this.joinFields = joinFields;
    }

    public JoinSourceDetails joinKey(String joinKey) {
        this.joinKey = joinKey;
        return this;
    }

    /**
     * Name of the property that is the join source's key.
     * @return joinKey
     */
    @JsonProperty(JSON_PROPERTY_JOIN_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getJoinKey() {
        return joinKey;
    }

    @JsonProperty(JSON_PROPERTY_JOIN_KEY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setJoinKey(String joinKey) {
        this.joinKey = joinKey;
    }

    public JoinSourceDetails numberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
        return this;
    }

    /**
     * The number of rows in the join source.
     * @return numberOfRows
     */
    @JsonProperty(JSON_PROPERTY_NUMBER_OF_ROWS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_OF_ROWS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public JoinSourceDetails sourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    /**
     * Name of the input data file that was used to create this join source.
     * @return sourceFile
     */
    @JsonProperty(JSON_PROPERTY_SOURCE_FILE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getSourceFile() {
        return sourceFile;
    }

    @JsonProperty(JSON_PROPERTY_SOURCE_FILE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSourceFile(String sourceFile) {
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
