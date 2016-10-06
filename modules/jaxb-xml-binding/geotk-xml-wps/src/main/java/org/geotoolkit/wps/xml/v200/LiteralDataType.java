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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.AllowedValues;
import org.geotoolkit.ows.xml.v200.AnyValue;
import org.geotoolkit.ows.xml.v200.DomainMetadataType;
import org.geotoolkit.ows.xml.v200.ValuesReference;
import org.geotoolkit.wps.xml.LiteralDataDescription;


/**
 * <p>Java class for LiteralDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LiteralDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DataDescriptionType">
 *       &lt;sequence>
 *         &lt;element name="LiteralDataDomain" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/wps/2.0}LiteralDataDomainType">
 *                 &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LiteralDataType", propOrder = {
    "literalDataDomain"
})
public class LiteralDataType extends DataDescriptionType implements LiteralDataDescription {

    @XmlElement(name = "LiteralDataDomain", namespace = "", required = true)
    protected List<LiteralDataType.LiteralDataDomain> literalDataDomain;

    public LiteralDataType() {
        
    }
    
    public LiteralDataType(AllowedValues allowedValues, AnyValue anyValue, ValuesReference valuesReference, DomainMetadataType dataType, DomainMetadataType uom,
                String defaultValue, Boolean _default) {
        literalDataDomain = new ArrayList<>();
        literalDataDomain.add(new LiteralDataDomain(allowedValues, anyValue, valuesReference, dataType, uom, defaultValue, _default));
        
    }
    public LiteralDataType(DomainMetadataType dataType, DomainMetadataType uom) {
        literalDataDomain = new ArrayList<>();
        literalDataDomain.add(new LiteralDataDomain(dataType, uom));
        
    }
    
    /**
     * Gets the value of the literalDataDomain property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link LiteralDataType.LiteralDataDomain }
     * 
     * 
     */
    public List<LiteralDataType.LiteralDataDomain> getLiteralDataDomain() {
        if (literalDataDomain == null) {
            literalDataDomain = new ArrayList<>();
        }
        return this.literalDataDomain;
    }

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opengis.net/wps/2.0}LiteralDataDomainType">
     *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class LiteralDataDomain extends LiteralDataDomainType {

        @XmlAttribute(name = "default")
        protected Boolean _default;

        public LiteralDataDomain() {
            
        }
        
        public LiteralDataDomain(AllowedValues allowedValues, AnyValue anyValue, ValuesReference valuesReference, DomainMetadataType dataType, DomainMetadataType uom,
                String defaultValue, Boolean _default) {
            super(allowedValues, anyValue, valuesReference, dataType, uom, defaultValue);
            this._default = _default;
        }
        
        public LiteralDataDomain(DomainMetadataType dataType, DomainMetadataType uom) {
            super(dataType, uom);
        }

        /**
         * Gets the value of the default property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isDefault() {
            return _default;
        }

        /**
         * Sets the value of the default property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setDefault(Boolean value) {
            this._default = value;
        }

    }

}
