/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
import org.geotoolkit.wps.xml.Reference;


/**
 * 
 * Reference to an input (output) value that is a web accessible resource.
 * 			
 * 
 * <p>Java class for ReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="Body" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="BodyReference">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute ref="{http://www.w3.org/1999/xlink}href use="required""/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/wps/2.0}dataEncodingAttributes"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}href use="required""/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceType", propOrder = {
    "body",
    "bodyReference"
})
public class ReferenceType implements Reference {

    @XmlElement(name = "Body")
    protected Object body;
    @XmlElement(name = "BodyReference")
    protected ReferenceType.BodyReference bodyReference;
    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "mimeType")
    protected String mimeType;
    @XmlAttribute(name = "encoding")
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute(name = "schema")
    @XmlSchemaType(name = "anyURI")
    protected String schema;

    public ReferenceType() {
        
    }
    
    public ReferenceType(String href, String mimeType, String encoding) {
        this.href = href;
        this.mimeType = mimeType;
        this.encoding = encoding;
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
        return body;
    }

    /**
     * Sets the value of the body property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setBody(Object value) {
        this.body = value;
    }

    /**
     * Gets the value of the bodyReference property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType.BodyReference }
     *     
     */
    public ReferenceType.BodyReference getBodyReference() {
        return bodyReference;
    }

    /**
     * Sets the value of the bodyReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType.BodyReference }
     *     
     */
    public void setBodyReference(ReferenceType.BodyReference value) {
        this.bodyReference = value;
    }

    /**
     * 
     * HTTP URI that points to the remote resource where the data may be retrieved.
     * 				
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
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setHref(String value) {
        this.href = value;
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
    public void setMimeType(String value) {
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
    public void setEncoding(String value) {
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
    public void setSchema(String value) {
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
        if (object instanceof ReferenceType) {
            final ReferenceType that = (ReferenceType) object;
            return Objects.equals(this.body, that.body) &&
                   Objects.equals(this.bodyReference, that.bodyReference) &&
                   Objects.equals(this.encoding, that.encoding) &&
                   Objects.equals(this.mimeType, that.mimeType) &&
                   Objects.equals(this.schema, that.schema) &&
                   Objects.equals(this.href, that.href);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.body);
        hash = 67 * hash + Objects.hashCode(this.bodyReference);
        hash = 67 * hash + Objects.hashCode(this.href);
        hash = 67 * hash + Objects.hashCode(this.mimeType);
        hash = 67 * hash + Objects.hashCode(this.encoding);
        hash = 67 * hash + Objects.hashCode(this.schema);
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

        @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String href;

        /**
         * 
         * HTTP URI that points to the remote resource where the request body may be retrieved.
         * 							
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
         * Sets the value of the href property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHref(String value) {
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
            int hash = 3;
            hash = 61 * hash + Objects.hashCode(this.href);
            return hash;
        }
    }
}
