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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to a WCTS to perform the {@code Transform} operation ({@code wcts:Transform}, required).
 *
 * <p>
 * Either {@code sourceCRS}/{@code targetCRS} or {@code transformation} must be set, mirroring the
 * schema's {@code SourceAndTargetCRSs | transformation} choice.
 * </p>
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformRequestType", propOrder = {
    "sourceCRS", "targetCRS", "transformation", "inputData", "interpolationMethod", "outputFormat"
})
@XmlRootElement(name = "Transform")
public class TransformRequestType {

    @XmlAttribute(required = true)
    private String service = "WCTS";
    @XmlAttribute(required = true)
    private String version = "0.3.0";
    @XmlAttribute
    private Boolean store;

    @XmlSchemaType(name = "anyURI")
    private String sourceCRS;
    @XmlSchemaType(name = "anyURI")
    private String targetCRS;
    private TransformationRefType transformation;
    @XmlElement(name = "InputData", required = true)
    private InputDataType inputData;
    @XmlElement(name = "InterpolationMethod")
    private InterpolationMethodType interpolationMethod;
    @XmlElement(name = "OutputFormat")
    private String outputFormat;

    public String getService() {
        return service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
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

    public TransformationRefType getTransformation() {
        return transformation;
    }

    public void setTransformation(TransformationRefType transformation) {
        this.transformation = transformation;
    }

    public InputDataType getInputData() {
        return inputData;
    }

    public void setInputData(InputDataType inputData) {
        this.inputData = inputData;
    }

    public InterpolationMethodType getInterpolationMethod() {
        return interpolationMethod;
    }

    public void setInterpolationMethod(InterpolationMethodType interpolationMethod) {
        this.interpolationMethod = interpolationMethod;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
