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
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * A process composed of an assembly of sub-processes connected together.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class AggregateProcess extends AbstractProcess {

    public static final String JSON_PROPERTY_COMPONENTS = "components";
    private List<JsonNode> components;

    public static final String JSON_PROPERTY_CONNECTIONS = "connections";
    private List<Connection> connections;

    public AggregateProcess() {
        setType("AggregateProcess");
    }

    /**
     * Named sub-components of this aggregate. Simplification: each item is, per the standard,
     * {@code allOf[SoftNamedProperty, oneOf[SimpleProcess,AggregateProcess,PhysicalComponent,
     * PhysicalSystem,Link]]}; kept raw here and should be manually mapped by callers based on the
     * presence of a {@code type} discriminator (inline process) versus a bare {@code href}
     * (by-reference link). (optional)
     */
    @JsonProperty(JSON_PROPERTY_COMPONENTS)
    public List<JsonNode> getComponents() {
        return components;
    }

    @JsonProperty(JSON_PROPERTY_COMPONENTS)
    public void setComponents(List<JsonNode> components) {
        this.components = components;
    }

    public AggregateProcess components(List<JsonNode> components) {
        setComponents(components);
        return this;
    }

    /**
     * Data flow connections between the components. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONNECTIONS)
    public List<Connection> getConnections() {
        return connections;
    }

    @JsonProperty(JSON_PROPERTY_CONNECTIONS)
    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public AggregateProcess connections(List<Connection> connections) {
        setConnections(connections);
        return this;
    }

}
