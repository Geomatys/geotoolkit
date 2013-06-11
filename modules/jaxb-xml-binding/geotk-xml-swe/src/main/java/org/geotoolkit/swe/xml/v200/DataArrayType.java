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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.DataArray;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for DataArrayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataArrayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="elementCount" type="{http://www.opengis.net/swe/2.0}CountPropertyType"/>
 *         &lt;element name="elementType">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractDataComponentPropertyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="encoding" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="values" type="{http://www.opengis.net/swe/2.0}EncodedValuesPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataArrayType", propOrder = {
    "elementCount",
    "elementType",
    "encoding",
    "values"
})
@XmlSeeAlso({
    MatrixType.class
})
public class DataArrayType extends AbstractDataComponentType implements DataArray {

    @XmlElement(required = true)
    private CountPropertyType elementCount;
    @XmlElement(required = true)
    private DataArrayType.ElementType elementType;
    private DataArrayType.Encoding encoding;
    //private EncodedValuesPropertyType values;
    private String values;

    /**
     * An empty constructor used by JAXB.
     */
    DataArrayType() {
        
    }

    /**
     * Clone a new data array.
     */
    public DataArrayType(final DataArrayType array) {
        super(array);
        if (array != null) {
            if (array.elementType != null) {
                this.elementType = new ElementType(array.elementType);
            }
            if (array.encoding != null) {
                this.encoding = new Encoding(array.encoding);
            }
            this.values = array.values;
            if (array.elementCount != null) {
               this.elementCount = new CountPropertyType(array.elementCount); 
            }
        }

    }
    
    public DataArrayType(final String id, final int count, final AbstractEncodingType encoding, final String values, final String elementName, final AbstractDataComponentType elementType) {
        super(id, null, null);
        this.elementCount = new CountPropertyType(count); 
        if (encoding != null) {
            this.encoding = new Encoding(encoding);
        }
        this.values       = values;
        if (elementType != null) {
            this.elementType  = new ElementType(elementName, elementType);
        }
    }
    
    /**
     * Gets the value of the elementCount property.
     * 
     * @return
     *     possible object is
     *     {@link CountPropertyType }
     *     
     */
    @Override
    public CountPropertyType getElementCount() {
        return elementCount;
    }

    /**
     * Sets the value of the elementCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link CountPropertyType }
     *     
     */
    public void setElementCount(CountPropertyType value) {
        this.elementCount = value;
    }
    
    @Override
    public void setElementCount(final int value) {
        this.elementCount = new CountPropertyType(value);
    }

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link DataArrayType.ElementType }
     *     
     */
    public DataArrayType.ElementType getElementType() {
        return elementType;
    }

    /**
     * Gets the value of the elementType property.
     */
    @Override
    public DataArrayType.ElementType getPropertyElementType() {
        return elementType;
    }
    
    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataArrayType.ElementType }
     *     
     */
    public void setElementType(DataArrayType.ElementType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link DataArrayType.Encoding }
     *     
     */
    @Override
    public AbstractEncodingType getEncoding() {
        if (encoding != null && encoding.abstractEncoding != null) {
            return encoding.abstractEncoding.getValue();
        }
        return null;
    }
    
    public DataArrayType.Encoding getJbEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataArrayType.Encoding }
     *     
     */
    public void setEncoding(DataArrayType.Encoding value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the values property.
     * 
     * @return
     *     possible object is
     *     {@link EncodedValuesPropertyType }
     *     
     */
    @Override
    public EncodedValuesPropertyType getDataValues() {
        return null;
    }

    /**
     * Sets the value of the values property.
     * 
     * @param value
     *     allowed object is
     *     {@link EncodedValuesPropertyType }
     *     
     */
    @Override
    public void setValues(String value) {
        this.values = value;
    }

    @Override
    public AbstractEncodingPropertyType getPropertyEncoding() {
        if (encoding != null) {
            return new AbstractEncodingPropertyType(encoding.abstractEncoding);
        }
        return null;
    }

    @Override
    public String getValues() {
        return values;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (elementCount != null) {
            sb.append("elementCount:").append(elementCount).append('\n');
        }
        if (elementType != null) {
            sb.append("elementType:").append(elementType).append('\n');
        }
        if (encoding != null) {
            sb.append("encoding:").append(encoding).append('\n');
        }
        if (values != null) {
            sb.append("values:").append(values).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataArrayType && super.equals(object)) {
            final DataArrayType that = (DataArrayType) object;
            return Utilities.equals(this.elementCount,   that.elementCount)   &&
                   Utilities.equals(this.elementType,    that.elementType)   &&
                   Utilities.equals(this.encoding,       that.encoding)    &&
                   Utilities.equals(this.values,         that.values);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + (this.elementCount != null ? this.elementCount.hashCode() : 0);
        hash = 29 * hash + (this.elementType != null ? this.elementType.hashCode() : 0);
        hash = 29 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
        hash = 29 * hash + (this.values != null ? this.values.hashCode() : 0);
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
     *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractDataComponentPropertyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ElementType extends AbstractDataComponentPropertyType {

        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        private String name;

        public ElementType() {
            
        }
        
        public ElementType(final ElementType that) {
            super(that);
            this.name = that.name;
        }
        
        public ElementType(final String name, final AbstractDataComponentType datacompo) {
            super(datacompo);
            this.name = name;
        }
        
        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(super.toString());
            if (name != null) {
                sb.append("name:").append(name).append('\n');
            }
            return sb.toString();
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof ElementType && super.equals(object)) {
                final ElementType that = (ElementType) object;
                return Utilities.equals(this.name, that.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + super.hashCode();
            hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "abstractEncoding"
    })
    public static class Encoding {

        @XmlElementRef(name = "AbstractEncoding", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
        private JAXBElement<? extends AbstractEncodingType> abstractEncoding;

        public Encoding() {
            
        }
        
        public Encoding(final String id, final String decimalSeparator, final String tokenSeparator,
            final String blockSeparator) {
            final ObjectFactory factory = new ObjectFactory();
            this.abstractEncoding = factory.createTextEncoding(new TextEncodingType(id, decimalSeparator, tokenSeparator, blockSeparator));
        }
        
        public Encoding(final Encoding that) {
            this.abstractEncoding = that.abstractEncoding;
        }
        public Encoding(final AbstractEncodingType encoding) {
            final ObjectFactory factory = new ObjectFactory();
            if (encoding instanceof TextEncodingType) {
                this.abstractEncoding = factory.createTextEncoding((TextEncodingType)encoding);
            } else if (encoding instanceof XMLEncodingType) {
                this.abstractEncoding = factory.createXMLEncoding((XMLEncodingType)encoding);
            } else if (encoding instanceof BinaryEncodingType) {
                this.abstractEncoding = factory.createBinaryEncoding((BinaryEncodingType)encoding);
            } else {
                this.abstractEncoding = factory.createAbstractEncoding(encoding);
            }
        }
        
        /**
         * Gets the value of the abstractEncoding property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractEncodingType> getAbstractEncoding() {
            return abstractEncoding;
        }

        /**
         * Sets the value of the abstractEncoding property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
         *     
         */
        public void setAbstractEncoding(JAXBElement<? extends AbstractEncodingType> value) {
            this.abstractEncoding = ((JAXBElement<? extends AbstractEncodingType> ) value);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("[Encoding]\n");
            if (abstractEncoding != null) {
                sb.append("abstractEncoding:").append(abstractEncoding.getValue()).append('\n');
            }
            return sb.toString();
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Encoding) {
                final Encoding that = (Encoding) object;
                if (this.abstractEncoding == null && that.abstractEncoding == null) {
                    return true;
                } else if (this.abstractEncoding != null && that.abstractEncoding != null) {
                    return Utilities.equals(this.abstractEncoding.getValue(), that.abstractEncoding.getValue());
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + (this.abstractEncoding != null ? this.abstractEncoding.hashCode() : 0);
            return hash;
        }
    }

}
