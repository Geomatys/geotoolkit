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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * One simple literal value (such as an integer or real number) that is embedded in the Execute operation request or response. 
 * 
 * String containing the Literal value (e.g., "49").
 * 
 * <p>Java class for LiteralDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LiteralDataType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="dataType" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="uom" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LiteralDataType", propOrder = {
    "value"
})
public class LiteralDataType implements org.geotoolkit.wps.xml.LiteralDataType {

    @XmlValue
    protected String value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String dataType;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String uom;

    public LiteralDataType() {
        
    }
    
    public LiteralDataType(final String value, final String dataType, final String uom) {
        this.dataType = dataType;
        this.uom      = uom;
        this.value    = value;
    }
    
    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Gets the value of the dataType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setDataType(final String value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the uom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setUom(final String value) {
        this.uom = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (dataType != null) {
            sb.append("dataType:").append(dataType).append('\n');
        }
        if (uom != null) {
            sb.append("uom:").append(uom).append('\n');
        }
        if (value != null) {
            sb.append("value:").append(value).append('\n');
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
        if (object instanceof LiteralDataType) {
            final LiteralDataType that = (LiteralDataType) object;
            return Objects.equals(this.dataType, that.dataType) &&
                   Objects.equals(this.uom, that.uom) &&
                   Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.value);
        hash = 97 * hash + Objects.hashCode(this.dataType);
        hash = 97 * hash + Objects.hashCode(this.uom);
        return hash;
    }

}
