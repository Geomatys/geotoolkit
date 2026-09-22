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
package org.geotoolkit.wcts.xml.v030;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to a WCTS to perform the {@code IsTransformable} operation ({@code wcts:IsTransformable}).
 *
 * <p>
 * Mirrors the schema's choice of {@code SourceAndTargetCRSs | Transformation | Method}, followed by
 * a choice of {@code GeometryType+} or {@code CoverageType+} (with optional interpolation methods).
 * Only one of {@code transformation}/{@code method} should be set alongside/instead of the CRS pair.
 * </p>
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IsTransformableRequestType", propOrder = {
    "sourceCRS", "targetCRS", "transformation", "method",
    "geometryType", "coverageType", "interpolationMethod"
})
@XmlRootElement(name = "IsTransformable")
public class IsTransformableRequestType {

    @XmlAttribute(required = true)
    private String service = "WCTS";
    @XmlAttribute(required = true)
    private String version = "0.3.0";

    @XmlSchemaType(name = "anyURI")
    private String sourceCRS;
    @XmlSchemaType(name = "anyURI")
    private String targetCRS;
    @XmlSchemaType(name = "anyURI")
    private String transformation;
    @XmlSchemaType(name = "anyURI")
    private String method;

    @XmlElement(name = "GeometryType")
    private List<GeometryTypeType> geometryType;
    @XmlElement(name = "CoverageType")
    private List<CoverageTypeType> coverageType;
    @XmlElement(name = "InterpolationMethod")
    private List<InterpolationMethodType> interpolationMethod;

    public String getService() {
        return service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSourceCRS() {
        return sourceCRS;
    }

    public void setSourceCRS(String sourceCRS) {
        this.sourceCRS = sourceCRS;
    }

    public String getTargetCRS() {
        return targetCRS;
    }

    public void setTargetCRS(String targetCRS) {
        this.targetCRS = targetCRS;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<GeometryTypeType> getGeometryType() {
        if (geometryType == null) {
            geometryType = new ArrayList<>();
        }
        return geometryType;
    }

    public List<CoverageTypeType> getCoverageType() {
        if (coverageType == null) {
            coverageType = new ArrayList<>();
        }
        return coverageType;
    }

    public List<InterpolationMethodType> getInterpolationMethod() {
        if (interpolationMethod == null) {
            interpolationMethod = new ArrayList<>();
        }
        return interpolationMethod;
    }
}
