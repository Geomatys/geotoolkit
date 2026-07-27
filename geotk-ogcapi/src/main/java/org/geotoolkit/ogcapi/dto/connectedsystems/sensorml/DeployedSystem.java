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
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;

/**
 * SensorML association object linking a system to its role within a {@link Deployment}, embedded
 * in {@link Deployment#getPlatform()} and {@link Deployment#getDeployedSystems()}.
 *
 * <p>This is NOT a top-level API resource, and is distinct from the unrelated GeoJSON feature of
 * the same simple name, {@code org.geotoolkit.ogcapi.dto.connectedsystems.geojson.DeployedSystem}
 * (Part 1's {@code /deployedSystems} resource): the two live in separate packages.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class DeployedSystem extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_SYSTEM = "system";
    private Link system;

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    private Settings configuration;

    /**
     * Name given to this system within the deployment; only meaningful when used inside
     * {@link Deployment#getDeployedSystems()}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    public void setName(String name) {
        this.name = name;
    }

    public DeployedSystem name(String name) {
        setName(name);
        return this;
    }

    /**
     * Description of the role of this system in the deployment. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public DeployedSystem description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Link to the deployed system/procedure resource. (required)
     */
    @JsonProperty(JSON_PROPERTY_SYSTEM)
    public Link getSystem() {
        return system;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM)
    public void setSystem(Link system) {
        this.system = system;
    }

    public DeployedSystem system(Link system) {
        setSystem(system);
        return this;
    }

    /**
     * Configuration/settings applied to the system for this deployment. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public Settings getConfiguration() {
        return configuration;
    }

    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public void setConfiguration(Settings configuration) {
        this.configuration = configuration;
    }

    public DeployedSystem configuration(Settings configuration) {
        setConfiguration(configuration);
        return this;
    }

}
