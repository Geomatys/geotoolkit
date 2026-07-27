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
 * A physical system composed of an assembly of physical (and possibly non-physical) sub-processes
 * connected together, e.g. a platform with several attached sensors.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class PhysicalSystem extends AbstractPhysicalProcess {

    public static final String JSON_PROPERTY_COMPONENTS = "components";
    private List<JsonNode> components;

    public static final String JSON_PROPERTY_CONNECTIONS = "connections";
    private List<Connection> connections;

    public PhysicalSystem() {
        setType("PhysicalSystem");
    }

    /**
     * Named sub-components of this system. Same simplification as
     * {@link AggregateProcess#getComponents()}: kept raw and should be manually mapped by callers
     * based on the presence of a {@code type} discriminator (inline process) versus a bare
     * {@code href} (by-reference link). (optional)
     */
    @JsonProperty(JSON_PROPERTY_COMPONENTS)
    public List<JsonNode> getComponents() {
        return components;
    }

    @JsonProperty(JSON_PROPERTY_COMPONENTS)
    public void setComponents(List<JsonNode> components) {
        this.components = components;
    }

    public PhysicalSystem components(List<JsonNode> components) {
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

    public PhysicalSystem connections(List<Connection> connections) {
        setConnections(connections);
        return this;
    }

}
