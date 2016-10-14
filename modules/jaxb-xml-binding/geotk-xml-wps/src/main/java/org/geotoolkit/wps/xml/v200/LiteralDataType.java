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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "LiteralData")
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
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (literalDataDomain != null) {
            sb.append("literalDataDomain:\n");
            for (LiteralDataDomain out : literalDataDomain) {
                sb.append(out).append('\n');
            }
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
        if (object instanceof LiteralDataType && super.equals(object)) {
            final LiteralDataType that = (LiteralDataType) object;
            return Objects.equals(this.literalDataDomain, that.literalDataDomain);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.literalDataDomain);
        return hash;
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
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            if (_default != null) {
                sb.append("_default:").append(_default).append('\n');
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
            if (object instanceof LiteralDataDomain && super.equals(object)) {
                final LiteralDataDomain that = (LiteralDataDomain) object;
                return Objects.equals(this._default, that._default);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this._default);
            return hash;
        }
    }
}
