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
package org.geotoolkit.ogcapi.model.feature;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;

import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Functions
 */
@JsonPropertyOrder({
    Functions.JSON_PROPERTY_FUNCTIONS
})
@XmlRootElement(name = "Functions")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Functions")
public class Functions extends DataTransferObject {

    public static final String JSON_PROPERTY_FUNCTIONS = "functions";
    @XmlElement(name = "functions")
    @jakarta.annotation.Nonnull
    private List<Function> functions = new ArrayList<>();

    public Functions() {
    }

    public Functions functions(@jakarta.annotation.Nonnull List<Function> functions) {
        this.functions = functions;
        return this;
    }

    public Functions addFunctionsItem(Function functionsItem) {
        if (this.functions == null) {
            this.functions = new ArrayList<>();
        }
        this.functions.add(functionsItem);
        return this;
    }

    /**
     * Get functions
     *
     * @return functions
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_FUNCTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "functions")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Function> getFunctions() {
        return functions;
    }

    @JsonProperty(JSON_PROPERTY_FUNCTIONS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "functions")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setFunctions(@jakarta.annotation.Nonnull List<Function> functions) {
        this.functions = functions;
    }

    /**
     * Return true if this functions object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Functions functions = (Functions) o;
        return Objects.equals(this.functions, functions.functions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functions);
    }

}
