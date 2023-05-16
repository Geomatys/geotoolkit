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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v321.AbstractGeometryType;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.w3c.dom.Element;


/**
 * This element is used to embed the data in a WPS request or response.
 * The content can be XML data, plain character data, or specially encoded binary data (i.e. base64).
 *
 *
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.opengis.net/wps/2.0}dataEncodingAttributes"/>
 *       &lt;anyAttribute processContents='skip'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "")
@XmlRootElement(name = "Data")
public class Data {

    protected List<Object> content;
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
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public Data() {

    }

    public Data(Object content) {
        if (content != null) {
            this.content = new ArrayList<>();
            this.content.add(content);
        }
    }

    public Data(Format format, Object content) {
        if (content != null) {
            this.content = new ArrayList<>();
            this.content.add(content);
        }
        if (format != null) {
            this.encoding = format.getEncoding();
            this.mimeType = format.getMimeType();
            this.schema   = format.getSchema();
        }
    }

    /**
     *
     * This element is used to embed the data in a WPS request or response.
     * The content can be XML data, plain character data, or specially encoded binary data (i.e. base64).
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * {@link Element }
     *
     *
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/2.0", type = BoundingBoxType.class),
        @XmlElementRef(name = "LiteralValue", namespace = "http://www.opengis.net/wps/2.0", type = LiteralValue.class),
        @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml/3.2", type = AbstractGeometryType.class),
        @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = org.geotoolkit.gml.xml.v311.AbstractGeometryType.class),
        @XmlElementRef(name = "math", namespace = "http://www.w3.org/1998/Math/MathML", type = org.geotoolkit.mathml.xml.Math.class)
    })
    @XmlAnyElement(lax = true)
    private List<Object> getContentToMarshal() {
        if (getLegacyComplexData() != null) {
            return null;
        }

        return getContent();
    }

    private void setContentToMarshal(List<Object> objects) {
        // Nothing to do, attributes are filled directly in legacy data ??
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
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    public LiteralValue getLiteralData() {
        if (content != null) {
            for (Object obj : content) {
                if (obj instanceof LiteralValue) {
                    return (LiteralValue) obj;
                }
            }
        }
        return null;
    }

    public BoundingBoxType getBoundingBoxData() {
        if (content != null) {
            for (Object obj : content) {
                if (obj instanceof BoundingBoxType) {
                    return (BoundingBoxType) obj;
                }
            }
        }
        return null;
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
        if (content != null) {
            sb.append("content:\n");
            for (Object out : content) {
                sb.append(out).append('\n');
            }
        }
        if (otherAttributes != null) {
            sb.append("Other attributes:\n");
            for (Entry out : otherAttributes.entrySet()) {
                sb.append("key").append(out.getKey()).append(" value:").append(out.getValue()).append('\n');
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
        if (object instanceof Data) {
            final Data that = (Data) object;
            return Objects.equals(this.content, that.content) &&
                   Objects.equals(this.encoding, that.encoding) &&
                   Objects.equals(this.mimeType, that.mimeType) &&
                   Objects.equals(this.schema, that.schema) &&
                   Objects.equals(this.otherAttributes, that.otherAttributes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.content);
        hash = 79 * hash + Objects.hashCode(this.mimeType);
        hash = 79 * hash + Objects.hashCode(this.encoding);
        hash = 79 * hash + Objects.hashCode(this.schema);
        hash = 79 * hash + Objects.hashCode(this.otherAttributes);
        return hash;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    private boolean hasComplexContent() {
        final boolean notOnlyStrings = getContent().stream()
                .filter(obj -> !(obj instanceof String))
                .findAny()
                .isPresent();

        return notOnlyStrings && !getContent().stream()
                .anyMatch(obj -> obj instanceof LiteralValue || obj instanceof BoundingBox);
    }

    @XmlElement(name="ComplexData")
    private ComplexData getLegacyComplexData() {
        if (FilterByVersion.isV1()) {
            // In WPS 1, only ComplexData has one of the following attributes.
            if (
                    getMimeType() != null ||
                    getEncoding() != null ||
                    getSchema() != null   ||
                    hasComplexContent()
                    ) {
                ComplexData complexData = new ComplexData();
                complexData.parent = this;
                return complexData;
            }
        }

        return null;
    }

    private void setLegacyComplexData(ComplexData legacyData) {
        // Nothing to do, attributes are filled directly in legacy data.
    }

    private static class ComplexData extends ParentAware<Data> {

        public ComplexData() {
            super(Data.class);
        }

        @XmlAttribute(name = "mimeType")
        private String getMimeType() {
            return checkParent().getMimeType();
        }

        @XmlAttribute(name = "encoding")
        @XmlSchemaType(name = "anyURI")
        private String getEncoding() {
            return checkParent().getEncoding();
        }

        @XmlAttribute(name = "schema")
        @XmlSchemaType(name = "anyURI")
        private String getSchema() {
            return checkParent().getSchema();
        }

        private void setMimeType(String value) {
            checkParent().setMimeType(value);
        }

        private void setEncoding(String value) {
            checkParent().setEncoding(value);
        }

        private void setSchema(String value) {
            checkParent().setSchema(value);
        }

        @XmlMixed
        @XmlElementRefs({
            @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/2.0", type = BoundingBoxType.class),
            @XmlElementRef(name = "LiteralValue", namespace = "http://www.opengis.net/wps/2.0", type = LiteralValue.class),
            @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml/3.2", type = AbstractGeometryType.class),
            @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = org.geotoolkit.gml.xml.v311.AbstractGeometryType.class),
            @XmlElementRef(name = "math", namespace = "http://www.w3.org/1998/Math/MathML", type = org.geotoolkit.mathml.xml.Math.class)
        })
        @XmlAnyElement(lax = true)
        private List<Object> getContent() {
            return checkParent().getContent();
        }
    }
}
