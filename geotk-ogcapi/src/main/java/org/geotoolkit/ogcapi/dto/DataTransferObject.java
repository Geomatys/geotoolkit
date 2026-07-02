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
package org.geotoolkit.ogcapi.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonInclude(Include.NON_EMPTY)
public abstract class DataTransferObject {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Catch all unknown fields
     */
    private final Map<String, Object> unknownFields = new HashMap<>();

    /**
     * All unknown fields
     */
    @JsonAnyGetter
    public Map<String, Object> otherFields() {
        return unknownFields;
    }

    @JsonAnySetter
    public void setOtherField(String name, Object value) {
        unknownFields.put(name, value);
    }

    /**
     * Create a copy of this object.
     * The copy is created by transforming the object to a JsonNode
     * and back to a class instance using jackson Object mapper.
     *
     * @return json copy of this object.
     * @throws JsonProcessingException
     */
    public DataTransferObject copy() throws JsonProcessingException {
        return MAPPER.treeToValue(MAPPER.valueToTree(this), this.getClass());
    }

    /**
     * Compare objects based on there JSON representation using jackson mapper.
     * Objects are transformed to JSONNode and compared using JsonNode.equals
     *
     * @param obj to compare
     * @return true if objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass())) return false;
        final JsonNode node1 = MAPPER.valueToTree(obj);
        final JsonNode node2 = MAPPER.valueToTree(this);
        return node1.equals(node2);
    }

    /**
     * @return class hashcode
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public final String toString() {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return this.getClass().getName() + " : [Error: could not map class instance as json]";
        }
    }

}
