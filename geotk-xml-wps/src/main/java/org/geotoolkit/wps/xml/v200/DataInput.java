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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;

import static org.geotoolkit.wps.xml.WPSMarshallerPool.OWS_2_0_NAMESPACE;

/**
 *
 * This structure contains information elements to supply input data for process execution.
 *
 *
 * <p>Java class for DataInput complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataInput">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Data"/>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Reference"/>
 *           &lt;element name="Input" type="{http://www.opengis.net/wps/2.0}DataInput" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "DataInputType", propOrder = {
    "identifier",
    "title",
    "abstract",
    "data",
    "reference",
    "input"
})
public class DataInput {

    @XmlElement(name = "Data")
    protected Data data;
    @XmlElement(name = "Reference")
    protected Reference reference;
    @XmlElement(name = "Input")
    protected List<DataInput> input;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "anyURI")
    @XmlJavaTypeAdapter(FilterV2.String.class)
    protected String id;

    public DataInput() {

    }

    public DataInput(String id, Reference reference) {
        this.id = id;
        this.reference = reference;
    }

    public DataInput(String id, Data data) {
        this.id = id;
        this.data = data;
    }

    /**
     * Gets the value of the data property.
     *
     * @return
     *     possible object is
     *     {@link Data }
     *
     */
    public Data getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     *
     * @param value
     *     allowed object is
     *     {@link Data }
     *
     */
    public void setData(Data value) {
        this.data = value;
    }

    /**
     * Gets the value of the reference property.
     *
     * @return
     *     possible object is
     *     {@link Reference }
     *
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     *
     * @param value
     *     allowed object is
     *     {@link Reference }
     *
     */
    public void setReference(Reference value) {
        this.reference = value;
    }

    /**
     * Gets the value of the input property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the input property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInput().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataInput }
     *
     *
     */
    public List<DataInput> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
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

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////
    private String title;
    private List<String> _abstract;

    /**
     *
     * @deprecated WPS 1 retro-compatibility purpose. Avoid if possible.
     */
    @XmlElement(name = "Title", namespace = WPSMarshallerPool.OWS_2_0_NAMESPACE)
    @Deprecated
    public String getTitle() {
        if (FilterByVersion.isV1()) {
            return title;
        }
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @deprecated WPS 1 retro-compatibility purpose. Avoid if possible.
     */
    @XmlElement(name = "Abstract", namespace = WPSMarshallerPool.OWS_2_0_NAMESPACE)
    @Deprecated
    public List<String> getAbstract() {
        if (FilterByVersion.isV1()) {
            return _abstract;
        }
        return null;
    }

    public void setAbstract(List<String> _abstract) {
        this._abstract = _abstract;
    }

    @XmlElement(name = "Identifier", namespace=OWS_2_0_NAMESPACE, required = true)
    private CodeType getIdentifier() {
        return id != null && FilterByVersion.isV1()? new CodeType(id) : null;
    }

    private void setIdentifier(final CodeType code) {
        id = code == null? null : code.getValue();
    }
}
