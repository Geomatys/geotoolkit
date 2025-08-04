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
 * FunctionsFunctionsInner
 */
@JsonPropertyOrder({
    Function.JSON_PROPERTY_NAME,
    Function.JSON_PROPERTY_DESCRIPTION,
    Function.JSON_PROPERTY_METADATA_URL,
    Function.JSON_PROPERTY_ARGUMENTS,
    Function.JSON_PROPERTY_RETURNS
})
@XmlRootElement(name = "FunctionsFunctionsInner")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "FunctionsFunctionsInner")
public class Function extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nonnull
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_METADATA_URL = "metadataUrl";
    @XmlElement(name = "metadataUrl")
    @jakarta.annotation.Nullable
    private String metadataUrl;

    public static final String JSON_PROPERTY_ARGUMENTS = "arguments";
    @XmlElement(name = "arguments")
    @jakarta.annotation.Nullable
    private List<Argument> arguments = new ArrayList<>();


    public static final String JSON_PROPERTY_RETURNS = "returns";
    @XmlElement(name = "returns")
    @jakarta.annotation.Nonnull
    private List<ValueType> returns = new ArrayList<>();

    public Function() {
    }

    public Function name(@jakarta.annotation.Nonnull String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nonnull String name) {
        this.name = name;
    }

    public Function description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    public Function metadataUrl(@jakarta.annotation.Nullable String metadataUrl) {
        this.metadataUrl = metadataUrl;
        return this;
    }

    /**
     * Get metadataUrl
     *
     * @return metadataUrl
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_METADATA_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadataUrl")
    public String getMetadataUrl() {
        return metadataUrl;
    }

    @JsonProperty(JSON_PROPERTY_METADATA_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadataUrl")
    public void setMetadataUrl(@jakarta.annotation.Nullable String metadataUrl) {
        this.metadataUrl = metadataUrl;
    }

    public Function arguments(@jakarta.annotation.Nullable List<Argument> arguments) {
        this.arguments = arguments;
        return this;
    }

    public Function addArgumentsItem(Argument argumentsItem) {
        if (this.arguments == null) {
            this.arguments = new ArrayList<>();
        }
        this.arguments.add(argumentsItem);
        return this;
    }

    /**
     * Get arguments
     *
     * @return arguments
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ARGUMENTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "arguments")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Argument> getArguments() {
        return arguments;
    }

    @JsonProperty(JSON_PROPERTY_ARGUMENTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "arguments")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setArguments(@jakarta.annotation.Nullable List<Argument> arguments) {
        this.arguments = arguments;
    }

    public Function returns(@jakarta.annotation.Nonnull List<ValueType> returns) {
        this.returns = returns;
        return this;
    }

    public Function addReturnsItem(ValueType returnsItem) {
        if (this.returns == null) {
            this.returns = new ArrayList<>();
        }
        this.returns.add(returnsItem);
        return this;
    }

    /**
     * Get returns
     *
     * @return returns
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_RETURNS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "returns")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<ValueType> getReturns() {
        return returns;
    }

    @JsonProperty(JSON_PROPERTY_RETURNS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "returns")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setReturns(@jakarta.annotation.Nonnull List<ValueType> returns) {
        this.returns = returns;
    }

    /**
     * Return true if this functions_functions_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Function functionsFunctionsInner = (Function) o;
        return Objects.equals(this.name, functionsFunctionsInner.name)
                && Objects.equals(this.description, functionsFunctionsInner.description)
                && Objects.equals(this.metadataUrl, functionsFunctionsInner.metadataUrl)
                && Objects.equals(this.arguments, functionsFunctionsInner.arguments)
                && Objects.equals(this.returns, functionsFunctionsInner.returns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, metadataUrl, arguments, returns);
    }

}
