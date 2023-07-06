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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 * Reference to an input (output) value that is a web accessible resource.
 *
 *
 * <p>Java class for Reference complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Reference">
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
@XmlType(name = "ReferenceType", propOrder = {
    "header",
    "body",
    "bodyReference"
})
public class Reference {

    private boolean isParentInput = true;

    /**
     * Header to set on request when connecting to reference HRef.
     * Note: This is not standard WPS 2.0, we ported it from WPS 1.0. The reason
     * is not only compatibility. It's also useful for advanced request configuration.
     */
    @XmlElement(name = "Header")
    protected List<Header> header;

    protected Object body;
    protected Reference.BodyReference bodyReference;

    protected String href;

    @XmlAttribute(name = "mimeType")
    protected String mimeType;
    @XmlAttribute(name = "encoding")
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute(name = "schema")
    @XmlSchemaType(name = "anyURI")
    protected String schema;

    public Reference() {}

    public Reference(String href) {
        this.href = href;
    }


    public Reference(String href, String mimeType, String encoding) {
        this.href = href;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    public Reference(String href, String mimeType, String encoding, String schema) {
        this.href = href;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.schema = schema;
    }

    /**
     * Gets the value of the body property.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    @XmlElement(name = "Body")
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
    @XmlElement(name = "BodyReference")
    public Reference.BodyReference getBodyReference() {
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
    public void setBodyReference(Reference.BodyReference value) {
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

    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink", required = true)
    @XmlSchemaType(name = "anyURI")
    private String getHrefV2() {
        if (FilterByVersion.isV2() || isParentInput) {
            return href;
        }
        return null;
    }

    private void setHrefV2(String value) {
        this.href = value;
    }

    @XmlAttribute(name = "href", required = true)
    @XmlSchemaType(name = "anyURI")
    private String getHrefV1() {
        if (FilterByVersion.isV1() && !isParentInput) {
            return href;
        }
        return null;
    }

    private void setHrefV1(String value) {
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
    public void setSchema(String value) {
        this.schema = value;
    }

    /**
     * Gets the value of the header property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link InputReferenceType.Header }
     *
     *
     */
    public List<Header> getHeader() {
        if (header == null) {
            header = new ArrayList<>();
        }
        return this.header;
    }

    /**
     * @param isParentOutput the isParentOutput to set
     */
    public void setIsParentInput(boolean isParentInput) {
        this.isParentInput = isParentInput;
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

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Reference) {
            final Reference that = (Reference) object;
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

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

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

    private String method;

    @XmlAttribute
    private String getMethod() {
        if (FilterByVersion.isV1()) {
            if (method == null) {

                if (body != null || bodyReference != null) {
                    method = "POST";
                } else {
                    method = "GET";
                }
            }

            return method;
        }

        return null;
    }

    private void setMethod(String method) {
        this.method = method;
    }
}
