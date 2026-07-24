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
package org.geotoolkit.ogcapi.dto.connectedsystems.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A minimal reference to an observed/controlled property, as used by {@link
 * DataStream#getObservedProperties()} and {@link ControlStream#getControlledProperties()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class ObservedPropertyRef extends DataTransferObject {

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    /**
     * URI defining the semantics of this property. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public ObservedPropertyRef definition(String definition) {
        setDefinition(definition);
        return this;
    }

    /**
     * Human readable label of this property. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public void setLabel(String label) {
        this.label = label;
    }

    public ObservedPropertyRef label(String label) {
        setLabel(label);
        return this;
    }

    /**
     * Description of this property. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public ObservedPropertyRef description(String description) {
        setDescription(description);
        return this;
    }

}
