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
package org.geotoolkit.wfs.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;


/**
 * A Web Feature Server MAY use zero or more MetadataURL elements to offer detailed,
 * standardized metadata about the data underneath a particular feature type.
 * The type attribute indicates the standard to which the metadata complies;
 * the format attribute indicates how the metadata is structured.
 * Two types are defined at present:
 *             'TC211' or 'ISO19115' = ISO TC211 19115; 
 *             'FGDC'                = FGDC CSDGM.
 *             'ISO19139'            = ISO 19139
 *          
 * 
 * <p>Java class for MetadataURLType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MetadataURLType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="TC211"/>
 *             &lt;enumeration value="FGDC"/>
 *             &lt;enumeration value="19115"/>
 *             &lt;enumeration value="19139"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="format" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="text/xml"/>
 *             &lt;enumeration value="text/html"/>
 *             &lt;enumeration value="text/sgml"/>
 *             &lt;enumeration value="text/plain"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataURLType", propOrder = {
    "value"
})
public class MetadataURLType {

    @XmlValue
    private String value;
    @XmlAttribute(required = true)
    private String type;
    @XmlAttribute(required = true)
    private String format;

    public MetadataURLType() {

    }

    public MetadataURLType(final String value, final String type, final String format) {
        this.format = format;
        this.value  = value;
        this.type   = type;
    }
    
    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(final String value) {
        this.format = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof MetadataURLType) {
            final MetadataURLType that = (MetadataURLType) object;

            return Utilities.equals(this.format,  that.format) &&
                   Utilities.equals(this.type,    that.type)   &&
                   Utilities.equals(this.value,   that.value);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 29 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 29 * hash + (this.format != null ? this.format.hashCode() : 0);
        return hash;
    }


    /**
     * Retourne une representation de l'objet.
     */

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[metadataURLType]");
        if(format != null) {
            s.append("format:").append(format).append('\n');
        }
        if (type != null)
            s.append("type:").append(type).append('\n');
        if (value != null)
            s.append("value:").append(value).append('\n');
        return s.toString();
    }
}
