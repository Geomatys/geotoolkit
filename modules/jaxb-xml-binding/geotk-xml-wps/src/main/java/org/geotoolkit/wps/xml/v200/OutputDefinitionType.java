/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.LanguageString;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.wps.xml.DocumentOutputDefinition;
import org.geotoolkit.wps.xml.OutputDefinition;


/**
 *
 * This structure contains information elements that describe the format and transmission mode
 * of the output data that is delivered by a process execution
 *
 *
 * <p>Java class for OutputDefinitionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OutputDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDefinitionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wps/2.0}dataEncodingAttributes"/>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="transmission" type="{http://www.opengis.net/wps/2.0}DataTransmissionModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputDefinitionType", propOrder = {
    "output"
})
public class OutputDefinitionType implements DocumentOutputDefinition {

    @XmlElement(name = "Output")
    protected List<OutputDefinitionType> output;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String id;
    @XmlAttribute(name = "transmission")
    protected DataTransmissionModeType transmission;
    @XmlAttribute(name = "mimeType")
    protected String mimeType;
    @XmlAttribute(name = "encoding")
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute(name = "schema")
    @XmlSchemaType(name = "anyURI")
    protected String schema;

    public OutputDefinitionType() {

    }

    public OutputDefinitionType(String id, Boolean asReference) {
        this.id = id;
        if (asReference != null) {
            if (asReference) {
                this.transmission = DataTransmissionModeType.REFERENCE;
            } else {
                this.transmission = DataTransmissionModeType.VALUE;
            }
        }
    }

    /**
     * Gets the value of the output property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the output property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutput().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OutputDefinitionType }
     *
     *
     */
    public List<OutputDefinitionType> getOutput() {
        if (output == null) {
            output = new ArrayList<OutputDefinitionType>();
        }
        return this.output;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    @Override
    public AbstractCodeType getIdentifier() {
        if (id != null) {
            return new CodeType(id);
        }
        return null;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the transmission property.
     *
     * @return
     *     possible object is
     *     {@link DataTransmissionModeType }
     *
     */
    public DataTransmissionModeType getTransmission() {
        return transmission;
    }

    /**
     * Sets the value of the transmission property.
     *
     * @param value
     *     allowed object is
     *     {@link DataTransmissionModeType }
     *
     */
    public void setTransmission(DataTransmissionModeType value) {
        this.transmission = value;
    }

    /**
     * Gets the value of the mimeType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the encoding property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the schema property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the value of the schema property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setSchema(String value) {
        this.schema = value;
    }

    @Override
    public boolean isReference() {
        if (transmission != null) {
            return transmission.equals(DataTransmissionModeType.REFERENCE);
}
        return false;
    }

    @Override
    public void setAsReference(Boolean value) {
        if (value != null) {
            if (value) {
                this.transmission = DataTransmissionModeType.REFERENCE;
            } else {
                this.transmission = DataTransmissionModeType.VALUE;
            }
        }
        this.transmission = null;

    }

    @Override
    public LanguageString getTitle() {
        return null; //nothing in this implementation
    }

    @Override
    public LanguageString getAbstract() {
        return null; //nothing in this implementation
    }

    @Override
    public DocumentOutputDefinition asDoc() {
        return this;
    }

    @Override
    public String getUom() {
        return null; // nothing in WPS 2.0.0
    }

    @Override
    public void setUom(String value) {
        // nothing in WPS 2.0.0
    }
}
