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
package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.openeo.process.dto.deserializer.DataTypeSchemaTypeDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Process-Discovery">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        DataTypeSchema.JSON_PROPERTY_TITLE,
        DataTypeSchema.JSON_PROPERTY_DESCRIPTION,
        DataTypeSchema.JSON_PROPERTY_PROPERTIES,
        DataTypeSchema.JSON_PROPERTY_ITEMS,
        DataTypeSchema.JSON_PROPERTY_REQUIRED,
        DataTypeSchema.JSON_PROPERTY_TYPE,
        DataTypeSchema.JSON_PROPERTY_SUB_TYPE
})
@XmlRootElement(name = "DataTypeSchema")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DataTypeSchema")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataTypeSchema extends DataTransferObject {

    public enum Type {
        ARRAY("array"),
        BOOLEAN("boolean"),
        INTEGER("integer"),
        NULL("null"),
        NUMBER("number"),
        OBJECT("object"),
        STRING("string");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Type fromValue(String value, boolean isArray) {

            if (isArray) {
                return ARRAY;
            }

            if (value.equalsIgnoreCase("Real")) {
                return NUMBER;
            } else if (value.equalsIgnoreCase("CharacterString")) {
                return STRING;
            }

            for (Type type : Type.values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return OBJECT;
            //throw new IllegalArgumentException("Invalid type: " + value);
        }

        public Class<?> getClassAssociated(String subtype) {
            switch (this) {
                case ARRAY -> {
                    if (subtype.equalsIgnoreCase("string") || subtype.equalsIgnoreCase("CharacterString")) {
                        return String[].class;
                    } else if (subtype.equalsIgnoreCase("Real") || subtype.equalsIgnoreCase("Double")) {
                        return double[].class;
                    } else if (subtype.equalsIgnoreCase("Integer") || subtype.equalsIgnoreCase("Int")) {
                        return int[].class;
                    } else if (subtype.equalsIgnoreCase("Boolean") || subtype.equalsIgnoreCase("Bool")) {
                        return boolean[].class;
                    } else if (subtype.equalsIgnoreCase("Object")) {
                        return Object[].class;
                    }
                    return ArrayList.class;
                }
                case BOOLEAN -> {
                    return Boolean.class;
                }
                case INTEGER -> {
                    return Integer.class;
                }
                case NULL -> {
                    return null;
                }
                case NUMBER -> {
                    return Double.class;
                }
                case OBJECT -> {
                    return Object.class;
                }
                case STRING -> {
                    return String.class;
                }
                default -> {
                    return null;
                }
            }
        }
    }

    public DataTypeSchema() {}

    public DataTypeSchema(List<Type> type, String subType) {
        this.type = type;
        this.subType = subType;
    }

    public DataTypeSchema(String title, String description, Map<String,Object> properties, Map<String, Object> items,
                          List<String> required, List<Type> type, String subType) {
        this.title = title;
        this.description = description;
        this.properties = properties;
        this.items = items;
        this.required = required;
        this.type = type;
        this.subType = subType;
    }

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlTransient
    @jakarta.annotation.Nullable
    private Map<String, Object> properties;

    public static final String JSON_PROPERTY_ITEMS = "items";
    @XmlTransient
    @jakarta.annotation.Nullable
    private Map<String, Object> items;

    public static final String JSON_PROPERTY_REQUIRED = "required";
    @XmlElementWrapper(name = "required")
    @XmlElement(name = "property")
    @JacksonXmlElementWrapper(localName = "required", useWrapping = false)
    @JacksonXmlProperty(localName = "property")
    @jakarta.annotation.Nullable
    private List<String> required;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElementWrapper(name = "type")
    @XmlElement(name = "item")
    @JacksonXmlElementWrapper(localName = "type", useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    @JsonDeserialize(using = DataTypeSchemaTypeDeserializer.class)
    @jakarta.annotation.Nonnull
    private List<Type> type = new ArrayList<>();

    public static final String JSON_PROPERTY_SUB_TYPE = "subtype";
    @XmlElement(name = "subtype")
    @jakarta.annotation.Nullable
    private String subType;

    /**
     * Get title
     *
     * @return title
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nullable String title) {
        this.title = title;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    /**
     * Get properties
     *
     * @return properties
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public void setProperties(@jakarta.annotation.Nullable Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Get items
     *
     * @return items
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ITEMS)
    public Map<String, Object> getItems() {
        return items;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    public void setItems(@jakarta.annotation.Nullable Map<String, Object> items) {
        this.items = items;
    }

    /**
     * Get required
     *
     * @return required
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JacksonXmlProperty(localName = JSON_PROPERTY_REQUIRED)
    public List<String> getRequired() {
        return required;
    }

    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JacksonXmlProperty(localName = JSON_PROPERTY_REQUIRED)
    public void setRequired(@jakarta.annotation.Nullable List<String> required) {
        this.required = required;
    }

    /**
     * Get type
     *
     * @return type
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public List<Type> getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JacksonXmlProperty(localName = JSON_PROPERTY_TYPE)
    public void setType(@jakarta.annotation.Nonnull List<Type> type) {
        this.type = type;
    }

    /**
     * Get subType
     *
     * @return subType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SUB_TYPE)
    @JacksonXmlProperty(localName = "subtype")
    public String getSubType() {
        return subType;
    }

    @JsonProperty(JSON_PROPERTY_SUB_TYPE)
    @JacksonXmlProperty(localName = "subtype")
    public void setSubType(@jakarta.annotation.Nullable String subType) {
        this.subType = subType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTypeSchema that = (DataTypeSchema) o;
        return Objects.equals(type, that.type) && Objects.equals(subType, that.subType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subType);
    }
}
