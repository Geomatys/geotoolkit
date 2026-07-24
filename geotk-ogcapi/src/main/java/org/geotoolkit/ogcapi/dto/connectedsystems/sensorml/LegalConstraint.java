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

/**
 * Legal constraints (access/use) applicable to a described object.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class LegalConstraint extends DataTransferObject {

    public static final String JSON_PROPERTY_USE_LIMITATIONS = "useLimitations";
    private List<String> useLimitations;

    public static final String JSON_PROPERTY_ACCESS_CONSTRAINTS = "accessConstraints";
    private List<CodeListValue> accessConstraints;

    public static final String JSON_PROPERTY_USE_CONSTRAINTS = "useConstraints";
    private List<CodeListValue> useConstraints;

    public static final String JSON_PROPERTY_OTHER_CONSTRAINTS = "otherConstraints";
    private List<String> otherConstraints;

    /**
     * Free text limitations on the use of the described object. (optional)
     */
    @JsonProperty(JSON_PROPERTY_USE_LIMITATIONS)
    public List<String> getUseLimitations() {
        return useLimitations;
    }

    @JsonProperty(JSON_PROPERTY_USE_LIMITATIONS)
    public void setUseLimitations(List<String> useLimitations) {
        this.useLimitations = useLimitations;
    }

    public LegalConstraint useLimitations(List<String> useLimitations) {
        setUseLimitations(useLimitations);
        return this;
    }

    /**
     * Coded access constraints. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    public List<CodeListValue> getAccessConstraints() {
        return accessConstraints;
    }

    @JsonProperty(JSON_PROPERTY_ACCESS_CONSTRAINTS)
    public void setAccessConstraints(List<CodeListValue> accessConstraints) {
        this.accessConstraints = accessConstraints;
    }

    public LegalConstraint accessConstraints(List<CodeListValue> accessConstraints) {
        setAccessConstraints(accessConstraints);
        return this;
    }

    /**
     * Coded use constraints. (optional)
     */
    @JsonProperty(JSON_PROPERTY_USE_CONSTRAINTS)
    public List<CodeListValue> getUseConstraints() {
        return useConstraints;
    }

    @JsonProperty(JSON_PROPERTY_USE_CONSTRAINTS)
    public void setUseConstraints(List<CodeListValue> useConstraints) {
        this.useConstraints = useConstraints;
    }

    public LegalConstraint useConstraints(List<CodeListValue> useConstraints) {
        setUseConstraints(useConstraints);
        return this;
    }

    /**
     * Free text for any other constraints not covered above. (optional)
     */
    @JsonProperty(JSON_PROPERTY_OTHER_CONSTRAINTS)
    public List<String> getOtherConstraints() {
        return otherConstraints;
    }

    @JsonProperty(JSON_PROPERTY_OTHER_CONSTRAINTS)
    public void setOtherConstraints(List<String> otherConstraints) {
        this.otherConstraints = otherConstraints;
    }

    public LegalConstraint otherConstraints(List<String> otherConstraints) {
        setOtherConstraints(otherConstraints);
        return this;
    }

}
