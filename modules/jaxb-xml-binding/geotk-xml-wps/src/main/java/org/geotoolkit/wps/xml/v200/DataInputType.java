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
import org.geotoolkit.wps.xml.Input;


/**
 * 
 * This structure contains information elements to supply input data for process execution.
 * 			
 * 
 * <p>Java class for DataInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataInputType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Data"/>
 *           &lt;element ref="{http://www.opengis.net/wps/2.0}Reference"/>
 *           &lt;element name="Input" type="{http://www.opengis.net/wps/2.0}DataInputType" maxOccurs="unbounded"/>
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
@XmlType(name = "DataInputType", propOrder = {
    "data",
    "reference",
    "input"
})
public class DataInputType implements Input {

    @XmlElement(name = "Data")
    protected Data data;
    @XmlElement(name = "Reference")
    protected ReferenceType reference;
    @XmlElement(name = "Input")
    protected List<DataInputType> input;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String id;

    public DataInputType() {
        
    }
    
    public DataInputType(String id, ReferenceType reference) {
        this.id = id;
        this.reference = reference;
    }
    
    public DataInputType(String id, Data data) {
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
    @Override
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
    @Override
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
     * Gets the value of the input property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link DataInputType }
     * 
     * 
     */
    public List<DataInputType> getInput() {
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

    @Override
    public AbstractCodeType getIdentifier() {
        if (id != null) {
            return new CodeType(id);
        }
        return null;
    }

    @Override
    public LanguageString getTitle() {
        return null; // not defnied in WPS 2.0
    }

    @Override
    public LanguageString getAbstract() {
        return null; // not defnied in WPS 2.0
    }

}
