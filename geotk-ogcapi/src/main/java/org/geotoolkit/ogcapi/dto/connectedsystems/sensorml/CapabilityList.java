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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.AbstractDataComponent;

/**
 * A named list of capabilities (functional/performance properties) of a described object, used in
 * {@link DescribedObject#getCapabilities()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class CapabilityList extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public static final String JSON_PROPERTY_CONDITIONS = "conditions";
    private List<AbstractDataComponent> conditions;

    public static final String JSON_PROPERTY_CAPABILITIES = "capabilities";
    private List<AbstractDataComponent> capabilities;

    /**
     * Local identifier of this list. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    public CapabilityList id(String id) {
        setId(id);
        return this;
    }

    /**
     * Human readable label. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public void setLabel(String label) {
        this.label = label;
    }

    public CapabilityList label(String label) {
        setLabel(label);
        return this;
    }

    /**
     * Description of this list. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public CapabilityList description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * URI defining the semantics of this list of capabilities. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public CapabilityList definition(String definition) {
        setDefinition(definition);
        return this;
    }

    /**
     * Conditions under which the capabilities apply. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONDITIONS)
    public List<AbstractDataComponent> getConditions() {
        return conditions;
    }

    @JsonProperty(JSON_PROPERTY_CONDITIONS)
    public void setConditions(List<AbstractDataComponent> conditions) {
        this.conditions = conditions;
    }

    public CapabilityList conditions(List<AbstractDataComponent> conditions) {
        setConditions(conditions);
        return this;
    }

    /**
     * List of capabilities, each modeled as a SWE Common data component. (required)
     */
    @JsonProperty(JSON_PROPERTY_CAPABILITIES)
    public List<AbstractDataComponent> getCapabilities() {
        return capabilities;
    }

    @JsonProperty(JSON_PROPERTY_CAPABILITIES)
    public void setCapabilities(List<AbstractDataComponent> capabilities) {
        this.capabilities = capabilities;
    }

    public CapabilityList capabilities(List<AbstractDataComponent> capabilities) {
        setCapabilities(capabilities);
        return this;
    }

}
