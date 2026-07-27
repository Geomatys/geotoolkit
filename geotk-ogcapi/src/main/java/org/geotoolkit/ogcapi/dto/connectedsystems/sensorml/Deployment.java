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
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONGeometry;

/**
 * Description of the deployment of one or more systems at a given place and time, e.g. a sensor
 * platform installed at a site.
 *
 * <p>This is a sibling root of {@link DescribedObject}, not part of the {@link AbstractProcess}
 * family: it deliberately is NOT one of {@link DescribedObject}'s {@code @JsonSubTypes}, but it
 * still inherits the {@code type} field from {@link DescribedObject}, which is fixed to the
 * constant {@code "Deployment"} by the no-arg constructor below.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Deployment extends DescribedObject {

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public static final String JSON_PROPERTY_LOCATION = "location";
    private GeoJSONGeometry location;

    public static final String JSON_PROPERTY_PLATFORM = "platform";
    private DeployedSystem platform;

    public static final String JSON_PROPERTY_DEPLOYED_SYSTEMS = "deployedSystems";
    private List<DeployedSystem> deployedSystems;

    public Deployment() {
        setType("Deployment");
    }

    /**
     * URI of the type of deployment. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Deployment definition(String definition) {
        setDefinition(definition);
        return this;
    }

    /**
     * Geographic extent of this deployment. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LOCATION)
    public GeoJSONGeometry getLocation() {
        return location;
    }

    @JsonProperty(JSON_PROPERTY_LOCATION)
    public void setLocation(GeoJSONGeometry location) {
        this.location = location;
    }

    public Deployment location(GeoJSONGeometry location) {
        setLocation(location);
        return this;
    }

    /**
     * The main system (platform) hosting this deployment. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PLATFORM)
    public DeployedSystem getPlatform() {
        return platform;
    }

    @JsonProperty(JSON_PROPERTY_PLATFORM)
    public void setPlatform(DeployedSystem platform) {
        this.platform = platform;
    }

    public Deployment platform(DeployedSystem platform) {
        setPlatform(platform);
        return this;
    }

    /**
     * Systems deployed as part of this deployment. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEPLOYED_SYSTEMS)
    public List<DeployedSystem> getDeployedSystems() {
        return deployedSystems;
    }

    @JsonProperty(JSON_PROPERTY_DEPLOYED_SYSTEMS)
    public void setDeployedSystems(List<DeployedSystem> deployedSystems) {
        this.deployedSystems = deployedSystems;
    }

    public Deployment deployedSystems(List<DeployedSystem> deployedSystems) {
        setDeployedSystems(deployedSystems);
        return this;
    }

}
