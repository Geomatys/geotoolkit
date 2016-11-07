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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wps.xml.Reference;


/**
 * Reference to an input or output value that is a web accessible resource. 
 * 
 * <p>Java class for InputReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputReferenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="Header" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="Body" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *           &lt;element name="BodyReference">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attribute ref="{http://www.w3.org/1999/xlink}href use="required""/>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wps/1.0.0}ComplexDataEncoding"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}href use="required""/>
 *       &lt;attribute name="method" default="GET">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="GET"/>
 *             &lt;enumeration value="POST"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputReferenceType", propOrder = {
    "header",
    "body",
    "bodyReference"
})
public class InputReferenceType implements Reference {

    @XmlElement(name = "Header")
    protected List<InputReferenceType.Header> header;
    @XmlElement(name = "Body")
    protected Body body;
    @XmlElement(name = "BodyReference")
    protected InputReferenceType.BodyReference bodyReference;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute
    protected String method;
    @XmlAttribute
    protected String mimeType;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String schema;

    public InputReferenceType() {
        
    }
    
    public InputReferenceType(String href, String mimeType, String encoding) {
        this.href = href;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }
    
    /**
     * Gets the value of the header property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link InputReferenceType.Header }
     * 
     * 
     */
    public List<InputReferenceType.Header> getHeader() {
        if (header == null) {
            header = new ArrayList<>();
        }
        return this.header;
    }

    /**
     * Gets the value of the body property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    @Override
    public Object getBody() {
        if (body != null) {
            return body.getContent();
        }
        return null;
    }

    /**
     * Sets the value of the body property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setBody(final Object value) {
        if (value != null) {
            this.body = new Body(value);
        } else {
            this.body = null;
        }
    }

    /**
     * Gets the value of the bodyReference property.
     * 
     * @return
     *     possible object is
     *     {@link InputReferenceType.BodyReference }
     *     
     */
    public InputReferenceType.BodyReference getBodyReference() {
        return bodyReference;
    }

    /**
     * Sets the value of the bodyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputReferenceType.BodyReference }
     *     
     */
    public void setBodyReference(final InputReferenceType.BodyReference value) {
        this.bodyReference = value;
    }

    /**
     * Reference to a web-accessible resource that can be used as input, or is provided by the process as output. 
     * This attribute shall contain a URL from which this input/output can be electronically retrieved. 
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * Reference to a web-accessible resource that can be used as input, or is provided by the process as output. 
     * This attribute shall contain a URL from which this input/output can be electronically retrieved. 
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethod() {
        if (method == null) {
            return "GET";
        } else {
            return method;
        }
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethod(final String value) {
        this.method = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setMimeType(final String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setEncoding(final String value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the schema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the value of the schema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setSchema(final String value) {
        this.schema = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (href != null) {
            sb.append("href:").append(href).append('\n');
        }
        if (body != null) {
            sb.append("body:").append(body).append('\n');
        }
        if (bodyReference != null) {
            sb.append("bodyReference:").append(bodyReference).append('\n');
        }
        if (encoding != null) {
            sb.append("encoding:").append(encoding).append('\n');
        }
        if (header != null) {
            sb.append("header:");
            for (Header h : header) {
                sb.append(h).append('\n');
            }
        }
        if (method != null) {
            sb.append("method:").append(method).append('\n');
        }
        if (mimeType != null) {
            sb.append("mimeType:").append(mimeType).append('\n');
        }
        if (schema != null) {
            sb.append("schema:").append(schema).append('\n');
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
        if (object instanceof InputReferenceType) {
            final InputReferenceType that = (InputReferenceType) object;
            return Objects.equals(this.body, that.body) &&
                   Objects.equals(this.bodyReference, that.bodyReference) &&
                   Objects.equals(this.encoding, that.encoding) &&
                   Objects.equals(this.header, that.header) &&
                   Objects.equals(this.method, that.method) &&
                   Objects.equals(this.mimeType, that.mimeType) &&
                   Objects.equals(this.schema, that.schema) &&
                   Objects.equals(this.href, that.href);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.header);
        hash = 71 * hash + Objects.hashCode(this.body);
        hash = 71 * hash + Objects.hashCode(this.bodyReference);
        hash = 71 * hash + Objects.hashCode(this.href);
        hash = 71 * hash + Objects.hashCode(this.method);
        hash = 71 * hash + Objects.hashCode(this.mimeType);
        hash = 71 * hash + Objects.hashCode(this.encoding);
        hash = 71 * hash + Objects.hashCode(this.schema);
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
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute ref="{http://www.w3.org/1999/xlink}href use="required""/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BodyReference {

        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String href;

        /**
         * Reference to a remote document to be used as the body of the an HTTP POST request message. 
         * This attribute shall contain a URL from which this input can be electronically retrieved. 
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHref() {
            return href;
        }

        /**
         * Reference to a remote document to be used as the body of the an HTTP POST request message.
         * This attribute shall contain a URL from which this input can be electronically retrieved. 
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHref(final String value) {
            this.href = value;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
            if (href != null) {
                sb.append("href:").append(href).append('\n');
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
            if (object instanceof BodyReference) {
                final BodyReference that = (BodyReference) object;
                return Objects.equals(this.href, that.href);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.href);
            return hash;
        }

    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {

        @XmlAnyElement(lax = true)
        private Object content;
        
        public Body() {
            
        }
        
        public Body(Object content) {
            this.content = content;
        }
        
        public Object getContent() {
            return content;
        }

        public void setContent(final Object value) {
            this.content = value;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
            if (content != null) {
                sb.append("content:").append(content).append('\n');
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
            if (object instanceof Body) {
                final Body that = (Body) object;
                return Objects.equals(this.content, that.content);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 61 * hash + Objects.hashCode(this.content);
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
     *       &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Header {

        @XmlAttribute(required = true)
        protected String key;
        @XmlAttribute(required = true)
        protected String value;

        /**
         * Gets the value of the key property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKey() {
            return key;
        }

        /**
         * Sets the value of the key property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKey(final String value) {
            this.key = value;
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
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
            if (key != null) {
                sb.append("key:").append(key).append('\n');
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
            if (object instanceof Header) {
                final Header that = (Header) object;
                return Objects.equals(this.key, that.key) &&
                       Objects.equals(this.value, that.value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.key);
            hash = 97 * hash + Objects.hashCode(this.value);
            return hash;
        }

    }

}
