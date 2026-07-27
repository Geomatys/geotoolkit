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
package org.geotoolkit.ogcapi.dto.feature;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * FunctionsFunctionsInnerArgumentsInner
 */
@JsonPropertyOrder({
    Argument.JSON_PROPERTY_TITLE,
    Argument.JSON_PROPERTY_DESCRIPTION,
    Argument.JSON_PROPERTY_TYPE
})
public class Argument {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;


    public static final String JSON_PROPERTY_TYPE = "type";
    private List<ValueType> type = new ArrayList<>();

    public Argument() {
    }

    public Argument title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public Argument description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public Argument type(List<ValueType> type) {
        this.type = type;
        return this;
    }

    public Argument addTypeItem(ValueType typeItem) {
        if (this.type == null) {
            this.type = new ArrayList<>();
        }
        this.type.add(typeItem);
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<ValueType> getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(List<ValueType> type) {
        this.type = type;
    }

    /**
     * Return true if this functions_functions_inner_arguments_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Argument functionsFunctionsInnerArgumentsInner = (Argument) o;
        return Objects.equals(this.title, functionsFunctionsInnerArgumentsInner.title)
                && Objects.equals(this.description, functionsFunctionsInnerArgumentsInner.description)
                && Objects.equals(this.type, functionsFunctionsInnerArgumentsInner.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, type);
    }

}
