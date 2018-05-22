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

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;


/**
 *
 * This type describes a process output in the execute response.
 *
 *
 * <p>Java class for DataOutput complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataOutput">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Data"/>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Reference"/>
 *           &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}DataOutput" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "DataOutputType", propOrder = {
    "identifier",
    "title",
    "abstract",
    "data",
    "reference",
    "output"
})
public class DataOutput {

    @XmlElement(name = "Data")
    protected Data data;
    @XmlElement(name = "Reference")
    protected Reference reference;
    @XmlElement(name = "Output")
    protected List<DataOutput> output;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "anyURI")
    @XmlJavaTypeAdapter(FilterV2.String.class)
    protected String id;

    public DataOutput() {

    }

    public DataOutput(String id, Reference reference) {
        this.id = id;
        this.reference = reference;
    }

    public DataOutput(String id, Data data) {
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
     * {@link DataOutput }
     *
     *
     */
    public List<DataOutput> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (id != null) {
            sb.append("id:").append(id).append('\n');
        }
        if (reference != null) {
            sb.append("reference:").append(reference).append('\n');
        }
        if (data != null) {
            sb.append("data:").append(data).append('\n');
        }
        if (output != null) {
            sb.append("output:").append(output).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataOutput) {
            final DataOutput that = (DataOutput) object;
            return Objects.equals(this.data,      that.data) &&
                   Objects.equals(this.id,        that.id) &&
                   Objects.equals(this.reference, that.reference) &&
                   Objects.equals(this.output,    that.output);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.data);
        hash = 59 * hash + Objects.hashCode(this.reference);
        hash = 59 * hash + Objects.hashCode(this.output);
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
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
    @XmlElement(name="Title", namespace=WPSMarshallerPool.OWS_2_0_NAMESPACE)
    @XmlJavaTypeAdapter(FilterV1.String.class)
    @Deprecated
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @deprecated WPS 1 retro-compatibility purpose. Avoid if possible.
     */
    @XmlElement(name="Abstract", namespace=WPSMarshallerPool.OWS_2_0_NAMESPACE)
    @XmlJavaTypeAdapter(FilterV1.String.class)
    @Deprecated
    public List<String> getAbstract() {
        return _abstract;
    }

    public void setAbstract(List<String> _abstract) {
        this._abstract = _abstract;
    }

    @XmlElement(name="Identifier", namespace=WPSMarshallerPool.OWS_2_0_NAMESPACE)
    @XmlJavaTypeAdapter(FilterV1.CodeType.class)
    @Deprecated
    public CodeType getIdentifier() {
        return new CodeType(id);
    }

    /**
     *
     * @param id The new identifier to affect to this output.
     * @deprecated WPS 1 retro-compatibility purpose. Avoid if possible.
     */
    @Deprecated
    public void setIdentifier(final CodeType id) {
        this.id = id.getValue();
    }

}
