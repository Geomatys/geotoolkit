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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.wps.xml.DataOutput;


/**
 * 
 * This type describes a process output in the execute response.
 * 			
 * 
 * <p>Java class for DataOutputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataOutputType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Data"/>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Reference"/>
 *           &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}DataOutputType"/>
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataOutputType", propOrder = {
    "data",
    "reference",
    "output"
})
public class DataOutputType implements DataOutput {

    @XmlElement(name = "Data")
    protected Data data;
    @XmlElement(name = "Reference")
    protected ReferenceType reference;
    @XmlElement(name = "Output")
    protected DataOutputType output;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String id;

    public DataOutputType() {
        
    }
    
    public DataOutputType(String id, ReferenceType reference) {
        this.id = id;
        this.reference = reference;
    }
    
    public DataOutputType(String id, Data data) {
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
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setReference(ReferenceType value) {
        this.reference = value;
    }

    /**
     * Gets the value of the output property.
     * 
     * @return
     *     possible object is
     *     {@link DataOutputType }
     *     
     */
    public DataOutputType getOutput() {
        return output;
    }

    /**
     * Sets the value of the output property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataOutputType }
     *     
     */
    public void setOutput(DataOutputType value) {
        this.output = value;
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
        return new CodeType(id);
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
        if (object instanceof DataOutputType) {
            final DataOutputType that = (DataOutputType) object;
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

}
