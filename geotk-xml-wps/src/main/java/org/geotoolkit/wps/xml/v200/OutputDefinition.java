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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;


/**
 *
 * This structure contains information elements that describe the format and transmission mode
 * of the output data that is delivered by a process execution
 *
 *
 * <p>Java class for OutputDefinition complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OutputDefinition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDefinition" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wps/2.0}dataEncodingAttributes"/>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="transmission" type="{http://www.opengis.net/wps/2.0}DataTransmissionMode" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "OutputDefinitionType", propOrder = {
    "id",
    "title",
    "abstract",
    "output"
})
public class OutputDefinition {

    @XmlElement(name = "Output")
    protected List<OutputDefinition> output;
    protected String id;
    protected DataTransmissionMode transmission;
    @XmlAttribute(name = "mimeType")
    protected String mimeType;
    @XmlAttribute(name = "encoding")
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute(name = "schema")
    @XmlSchemaType(name = "anyURI")
    protected String schema;

    public OutputDefinition() {

    }

    public OutputDefinition(String id, Boolean asReference) {
        this.id = id;
        if (asReference != null) {
            if (asReference) {
                this.transmission = DataTransmissionMode.REFERENCE;
            } else {
                this.transmission = DataTransmissionMode.VALUE;
            }
        }
    }

    public OutputDefinition(String id, final String encoding, final String mimeType, final String schema, Boolean asReference) {
        this.id = id;
        this.encoding = encoding;
        this.mimeType = mimeType;
        this.schema = schema;
        if (asReference != null) {
            if (asReference) {
                this.transmission = DataTransmissionMode.REFERENCE;
            } else {
                this.transmission = DataTransmissionMode.VALUE;
            }
        }
    }

    /**
     * Gets the value of the output property.
     *
     */
    public List<OutputDefinition> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "anyURI")
    public String getIdentifier() {
        if (FilterByVersion.isV2()) {
            return id;
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
    public void setIdentifier(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the transmission property.
     *
     * @return
     *     possible object is
     *     {@link DataTransmissionMode }
     *
     */
    public DataTransmissionMode getTransmission() {
        return transmission;
    }

    /**
     * Sets the value of the transmission property.
     *
     * @param value
     *     allowed object is
     *     {@link DataTransmissionMode }
     *
     */
    public void setTransmission(DataTransmissionMode value) {
        this.transmission = value;
    }

    @XmlAttribute(name = "transmission")
    private DataTransmissionMode getTransmissionMarshall() {
        if (FilterByVersion.isV1()) {
            return null;
        }
        return transmission;
    }

    private void setTransmissionMarshall(DataTransmissionMode value) {
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
    public void setSchema(String value) {
        this.schema = value;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String uom;

    private LanguageStringType title;

    private LanguageStringType _abstract;

    @XmlElement(name = "Identifier", namespace=WPSMarshallerPool.OWS_2_0_NAMESPACE, required = true)
    public CodeType getId() {
        return id != null && FilterByVersion.isV1()? new CodeType(id) : null;
    }

    public void setId(final CodeType code) {
        id = code == null? null : code.getValue();
    }

    /**
     *
     * @return An URI defining the unit in which the output is expressed.
     * @deprecated Legacy attribute from WPS 1.0
     */
    @Deprecated
    public String getUom() {
        return uom;
    }

    /**
     *
     * @param value any URI identifying an unit.
     * @deprecated Legacy attribute from WPS 1.0
     */
    @Deprecated
    public void setUom(String value) {
        this.uom = value;
    }

    /**
     * Gets the value of the asReference property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    @XmlAttribute(name="asReference")
    private Boolean isAsReference() {
        return FilterByVersion.isV1()? DataTransmissionMode.REFERENCE.equals(getTransmission()) : null;
    }

    /**
     * Sets the value of the asReference property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    private void setAsReference(final Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            setTransmission(DataTransmissionMode.REFERENCE);
        } else if (Boolean.FALSE.equals(value)) {
            setTransmission(DataTransmissionMode.VALUE);
        } else {
            setTransmission(null);
        }
    }

    @XmlElement(name = "Title", namespace = WPSMarshallerPool.OWS_2_0_NAMESPACE)
    public LanguageStringType getTitle() {
        if (FilterByVersion.isV1()) {
            return title;
        }
        return null;
    }

    private void setTitle(final LanguageStringType value) {
        this.title = value;
    }

    @XmlElement(name = "Abstract", namespace = WPSMarshallerPool.OWS_2_0_NAMESPACE)
    public LanguageStringType getAbstract() {
        if (FilterByVersion.isV1()) {
            return _abstract;
        }
        return null;
    }

    public void setAbstract(final LanguageStringType value) {
        this._abstract = value;
    }

}
