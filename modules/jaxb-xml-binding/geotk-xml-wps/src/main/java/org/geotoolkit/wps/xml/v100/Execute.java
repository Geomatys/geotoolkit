/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wps.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *         &lt;element name="DataInputs" type="{http://www.opengis.net/wps/1.0.0}DataInputsType" minOccurs="0"/>
 *         &lt;element name="ResponseForm" type="{http://www.opengis.net/wps/1.0.0}ResponseFormType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifier",
    "dataInputs",
    "responseForm"
})
@XmlRootElement(name = "Execute")
public class Execute extends RequestBaseType implements org.geotoolkit.wps.xml.Execute {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected CodeType identifier;
    @XmlElement(name = "DataInputs")
    protected DataInputsType dataInputs;
    @XmlElement(name = "ResponseForm")
    protected ResponseFormType responseForm;

    public Execute() {

    }

    public Execute(final String language, final CodeType identifier, DataInputsType dataInputs, ResponseFormType responseForm) {
        super(language);
        this.identifier = identifier;
        this.dataInputs = dataInputs;
        this.responseForm = responseForm;
    }

    /**
     * Identifier of the Process to be executed. This Process identifier shall be as listed in the ProcessOfferings section of the WPS Capabilities document.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    @Override
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Identifier of the Process to be executed. This Process identifier shall be as listed in the ProcessOfferings section of the WPS Capabilities document.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setIdentifier(final CodeType value) {
        this.identifier = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIdentifier(String identifiers) {
        setIdentifier(new CodeType(identifiers));
    }

    /**
     * Gets the value of the dataInputs property.
     *
     * @return
     *     possible object is
     *     {@link DataInputsType }
     *
     */
    public DataInputsType getDataInputs() {
        return dataInputs;
    }

    @Override
    public List<InputType> getInput() {
        List<InputType> results = new ArrayList<>();
        if (dataInputs != null && dataInputs.input != null) {
            for (InputType in : dataInputs.input) {
                results.add(in);
            }
        }
        return results;
    }

    /**
     * Sets the value of the dataInputs property.
     *
     * @param value
     *     allowed object is
     *     {@link DataInputsType }
     *
     */
    public void setDataInputs(final DataInputsType value) {
        this.dataInputs = value;
    }

    /**
     * Gets the value of the responseForm property.
     *
     * @return
     *     possible object is
     *     {@link ResponseFormType }
     *
     */
    public ResponseFormType getResponseForm() {
        return responseForm;
    }

    @Override
    public List<OutputDefinitionType> getOutput() {
        List<OutputDefinitionType> results = new ArrayList<>();
        if (responseForm != null) {
            if (responseForm.rawDataOutput != null) {
                results.add(responseForm.rawDataOutput);
            } else if (responseForm.responseDocument != null) {
                for (DocumentOutputDefinitionType out : responseForm.responseDocument.getOutput()) {
                    results.add(out);
                }
            }
            return results;
        } else {
            return null;
        }
    }

    /**
     * Sets the value of the responseForm property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseFormType }
     *
     */
    public void setResponseForm(final ResponseFormType value) {
        this.responseForm = value;
    }

    @Override
    public boolean isLineage() {
        if (responseForm != null && responseForm.responseDocument != null) {
            return responseForm.responseDocument.isLineage();
        }
        return false;
    }

    @Override
    public void setLineage(boolean outLineage) {
        if (responseForm==null) {
           responseForm = new ResponseFormType();
        }
        if (responseForm.responseDocument==null) {
            responseForm.responseDocument = new ResponseDocumentType();
        }
        responseForm.responseDocument.lineage = outLineage;
    }


    @Override
    public boolean isRawOutput() {
        if (responseForm != null) {
            if (responseForm.rawDataOutput != null) {
               return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDocumentOutput() {
        if (responseForm != null) {
            if (responseForm.rawDataOutput != null) {
               return false;
            }
        }
        return true;
    }

    @Override
    public boolean isStatus() {
        if (responseForm != null) {
            if (responseForm.responseDocument != null) {
               return responseForm.responseDocument.isStatus();
            }
        }
        return false;
    }

    @Override
    public boolean isStoreExecuteResponse() {
        if (responseForm != null) {
            if (responseForm.responseDocument != null) {
               return responseForm.responseDocument.isStoreExecuteResponse();
            }
        }
        return false;
    }

}
