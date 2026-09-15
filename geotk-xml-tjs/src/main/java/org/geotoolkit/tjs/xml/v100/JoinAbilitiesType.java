/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Response describing all joining abilities of the TJS instance.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JoinAbilitiesType", propOrder = {
    "spatialFrameworks", "attributeLimit", "outputMechanisms", "outputStylings", "classificationSchemaURL"})
@XmlRootElement(name = "JoinAbilities", namespace = "http://www.opengis.net/tjs/1.0")
public class JoinAbilitiesType extends TJSResponseType {

    @XmlElement(name = "SpatialFrameworks", required = true)
    private SpatialFrameworksType spatialFrameworks;

    @XmlElement(name = "AttributeLimit", required = true)
    private Integer attributeLimit;

    @XmlElement(name = "OutputMechanisms", required = true)
    private OutputMechanismsType outputMechanisms;

    @XmlElement(name = "OutputStylings")
    private OutputStylingsType outputStylings;

    @XmlElement(name = "ClassificationSchemaURL")
    private String classificationSchemaURL;

    @XmlAttribute(required = true)
    private Boolean updateSupported;

    public SpatialFrameworksType getSpatialFrameworks() {
        return spatialFrameworks;
    }

    public void setSpatialFrameworks(SpatialFrameworksType spatialFrameworks) {
        this.spatialFrameworks = spatialFrameworks;
    }

    public Integer getAttributeLimit() {
        return attributeLimit;
    }

    public void setAttributeLimit(Integer attributeLimit) {
        this.attributeLimit = attributeLimit;
    }

    public OutputMechanismsType getOutputMechanisms() {
        return outputMechanisms;
    }

    public void setOutputMechanisms(OutputMechanismsType outputMechanisms) {
        this.outputMechanisms = outputMechanisms;
    }

    public OutputStylingsType getOutputStylings() {
        return outputStylings;
    }

    public void setOutputStylings(OutputStylingsType outputStylings) {
        this.outputStylings = outputStylings;
    }

    public String getClassificationSchemaURL() {
        return classificationSchemaURL;
    }

    public void setClassificationSchemaURL(String classificationSchemaURL) {
        this.classificationSchemaURL = classificationSchemaURL;
    }

    public Boolean getUpdateSupported() {
        return updateSupported;
    }

    public void setUpdateSupported(Boolean updateSupported) {
        this.updateSupported = updateSupported;
    }
}
