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

import java.math.BigInteger;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.wps.json.FormatDescription;


/**
 * References the XML schema, format, and encoding of a complex value.
 *
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="mimeType" type="{http://www.opengis.net/ows/2.0}MimeType" />
 *       &lt;attribute name="encoding" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="schema" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="maximumMegabytes" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "", propOrder = {
    "legacyMimeType",
    "legacyEncoding",
    "legacySchema"
})
@XmlRootElement(name = "Format")
public class Format {

    @XmlAttribute(name = "mimeType")
    @XmlJavaTypeAdapter(FilterV2.String.class)
    protected String mimeType;
    @XmlAttribute(name = "encoding")
    @XmlSchemaType(name = "anyURI")
    @XmlJavaTypeAdapter(FilterV2.String.class)
    protected String encoding;
    @XmlAttribute(name = "schema")
    @XmlSchemaType(name = "anyURI")
    @XmlJavaTypeAdapter(FilterV2.String.class)
    protected String schema;
    @XmlAttribute(name = "maximumMegabytes")
    @XmlSchemaType(name = "positiveInteger")
    @XmlJavaTypeAdapter(FilterV2.Integer.class)
    protected Integer maximumMegabytes;
    @XmlAttribute(name = "default")
    @XmlJavaTypeAdapter(FilterV2.Boolean.class)
    protected Boolean _default;

    public Format() {

    }

    public Format(final String mimeType, final Boolean _default) {
        this.mimeType = mimeType;
        this._default = _default;
    }


    public Format(final String encoding, final String mimeType, final String schema, final Integer maximumMegabytes) {
        this.encoding = encoding;
        this.mimeType = mimeType;
        this.schema   = schema;
        this.maximumMegabytes = maximumMegabytes;
    }

    public Format(final String encoding, final String mimeType, final String schema, final Integer maximumMegabytes, final Boolean _default) {
        this.encoding = encoding;
        this.mimeType = mimeType;
        this.schema   = schema;
        this.maximumMegabytes = maximumMegabytes;
        this._default = _default;
    }

    public Format(FormatDescription format) {
        if (format != null) {
            this._default = format.isDefault();
            this.encoding = format.getEncoding();
            this.maximumMegabytes = format.getMaximumMegabytes();
            this.mimeType = format.getMimeType();
            this.schema = format.getSchema();
        }
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
     * Gets the value of the maximumMegabytes property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public Integer getMaximumMegabytes() {
        return maximumMegabytes;
    }

    /**
     * Sets the value of the maximumMegabytes property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setMaximumMegabytes(Integer value) {
        this.maximumMegabytes = value;
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
        if (_default == null) {
            return false;
        }
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
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (encoding != null) {
            sb.append("encoding:").append(encoding).append('\n');
        }
        if (mimeType != null) {
            sb.append("mimeType:").append(mimeType).append('\n');
        }
        if (schema != null) {
            sb.append("schema:").append(schema).append('\n');
        }
        if (maximumMegabytes != null) {
            sb.append("maximumMegabytes:").append(maximumMegabytes).append('\n');
        }
        if (_default != null) {
            sb.append("_default:").append(_default).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Format) {
            final Format that = (Format) object;
            return Objects.equals(this.encoding, that.encoding) &&
                   Objects.equals(this.mimeType, that.mimeType) &&
                   Objects.equals(this._default, that._default) &&
                   Objects.equals(this.maximumMegabytes, that.maximumMegabytes) &&
                   Objects.equals(this.schema, that.schema);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.mimeType);
        hash = 97 * hash + Objects.hashCode(this.encoding);
        hash = 97 * hash + Objects.hashCode(this.schema);
        hash = 97 * hash + Objects.hashCode(this.maximumMegabytes);
        hash = 97 * hash + Objects.hashCode(this._default);
        return hash;
    }


    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    @XmlElement(name="MimeType")
    private String getLegacyMimeType() {
        if (FilterByVersion.isV1()) {
            return mimeType;
        }
        return null;
    }

    @XmlElement(name="Encoding")
    private String getLegacyEncoding() {
        if (FilterByVersion.isV1()) {
            return encoding;
        }
        return null;
    }

    @XmlElement(name="Schema")
    private String getLegacySchema() {
        if (FilterByVersion.isV1()) {
            return schema;
        }
        return null;
    }

    private void setLegacyMimeType(String val) {
        mimeType = val;
    }

    private void setLegacyEncoding(String val) {
        encoding = val;
    }

    private void setLegacySchema(String val) {
        schema = val;
    }
}
