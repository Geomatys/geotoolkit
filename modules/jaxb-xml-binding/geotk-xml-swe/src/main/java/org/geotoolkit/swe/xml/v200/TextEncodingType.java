/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.TextBlock;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for TextEncodingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TextEncodingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractEncodingType">
 *       &lt;attribute name="collapseWhiteSpaces" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="decimalSeparator" type="{http://www.w3.org/2001/XMLSchema}string" default="." />
 *       &lt;attribute name="tokenSeparator" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="blockSeparator" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TextEncodingType")
public class TextEncodingType extends AbstractEncodingType implements TextBlock {

    @XmlAttribute
    private Boolean collapseWhiteSpaces;
    @XmlAttribute
    private String decimalSeparator;
    @XmlAttribute(required = true)
    private String tokenSeparator;
    @XmlAttribute(required = true)
    private String blockSeparator;

    public TextEncodingType() {
        
    }
    
    public TextEncodingType(final String id, final String decimalSeparator, final String tokenSeparator,
            final String blockSeparator) {
        super(id);
        this.blockSeparator   = blockSeparator;
        this.decimalSeparator = decimalSeparator;
        this.tokenSeparator   = tokenSeparator;
    }
            
    /**
     * Gets the value of the collapseWhiteSpaces property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCollapseWhiteSpaces() {
        if (collapseWhiteSpaces == null) {
            return true;
        } else {
            return collapseWhiteSpaces;
        }
    }

    /**
     * Sets the value of the collapseWhiteSpaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCollapseWhiteSpaces(Boolean value) {
        this.collapseWhiteSpaces = value;
    }

    /**
     * Gets the value of the decimalSeparator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecimalSeparator() {
        if (decimalSeparator == null) {
            return ".";
        } else {
            return decimalSeparator;
        }
    }

    /**
     * Sets the value of the decimalSeparator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecimalSeparator(String value) {
        this.decimalSeparator = value;
    }

    /**
     * Gets the value of the tokenSeparator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTokenSeparator() {
        return tokenSeparator;
    }

    /**
     * Sets the value of the tokenSeparator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTokenSeparator(String value) {
        this.tokenSeparator = value;
    }

    /**
     * Gets the value of the blockSeparator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlockSeparator() {
        return blockSeparator;
    }

    /**
     * Sets the value of the blockSeparator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlockSeparator(String value) {
        this.blockSeparator = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof TextEncodingType && super.equals(object)) {
            final TextEncodingType that = (TextEncodingType) object;
            return Utilities.equals(this.blockSeparator,      that.blockSeparator)      &&
                   Utilities.equals(this.collapseWhiteSpaces, that.collapseWhiteSpaces) &&
                   Utilities.equals(this.decimalSeparator,    that.decimalSeparator)    &&
                   Utilities.equals(this.tokenSeparator,      that.tokenSeparator);
        }
        return false;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + super.hashCode();
        hash = 47 * hash + (this.blockSeparator != null ? this.blockSeparator.hashCode() : 0);
        hash = 47 * hash + (this.collapseWhiteSpaces != null ? this.collapseWhiteSpaces.hashCode() : 0);
        hash = 47 * hash + (this.decimalSeparator != null ? this.decimalSeparator.hashCode() : 0);
        hash = 47 * hash + (this.tokenSeparator != null ? this.tokenSeparator.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if(blockSeparator != null) {
            s.append("blockSeparator=").append(blockSeparator).append('\n');
        }
        if(collapseWhiteSpaces != null) {
            s.append("collapseWhiteSpaces=").append(collapseWhiteSpaces).append('\n');
        }
        if(decimalSeparator != null) {
            s.append("decimalSeparator=").append(decimalSeparator).append('\n');
        }
        if(tokenSeparator != null) {
            s.append("tokenSeparator=").append(tokenSeparator).append('\n');
        }
        return s.toString();
    }
}
