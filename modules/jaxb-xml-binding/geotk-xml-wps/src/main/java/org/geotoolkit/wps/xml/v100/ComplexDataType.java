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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.w3c.dom.Element;


/**
 * Complex data (such as an image), including a definition of the complex value data structure (i.e., schema, format, and encoding).  May be an ows:Manifest data structure.
 *
 * <p>Java class for ComplexDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ComplexDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.opengis.net/wps/1.0.0}ComplexDataEncoding"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexDataType", propOrder = {
    "content"
})
//@XmlSeeAlso(org.geotoolkit.wps.xml.v100.ext.GeoJSONType.class)
public class ComplexDataType implements org.geotoolkit.wps.xml.ComplexDataType {

    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = AbstractGeometryType.class),
        @XmlElementRef(name = "math", namespace = "http://www.w3.org/1998/Math/MathML", type = org.geotoolkit.mathml.xml.Math.class),
        @XmlElementRef(name = "GeoJSON", namespace = "http://geotoolkit.org", type = org.geotoolkit.wps.xml.v100.ext.GeoJSONType.class)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> content;
    @XmlAttribute
    protected String mimeType;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String schema;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    
    public ComplexDataType() {
        
    }
    
    public ComplexDataType(String encoding, final String mimeType, final String schema) {
        this.encoding = encoding;
        this.mimeType = mimeType;
        this.schema   = schema;
    }
    
    /**
     * Complex data (such as an image), including a definition of the complex value data structure (i.e., schema, format, and encoding). 
     * May be an ows:Manifest data structure.Gets the value of the content property.
     *
     *  @return Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link String }
     *
     */
    @Override
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
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

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
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
            for (Object o : content) {
                sb.append(o).append('\n');
            }
        }
        if (otherAttributes != null) {
            sb.append("Other attributes:\n");
            for (Entry o : otherAttributes.entrySet()) {
                sb.append("key:").append(o.getKey()).append(" value:").append(o.getValue()).append('\n');
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
        if (object instanceof ComplexDataType) {
            final ComplexDataType that = (ComplexDataType) object;
            return Objects.equals(this.content, that.content) &&
                   Objects.equals(this.encoding, that.encoding) &&
                   Objects.equals(this.schema, that.schema) &&
                   Objects.equals(this.otherAttributes, that.otherAttributes) &&
                   Objects.equals(this.mimeType, that.mimeType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.content);
        hash = 71 * hash + Objects.hashCode(this.mimeType);
        hash = 71 * hash + Objects.hashCode(this.encoding);
        hash = 71 * hash + Objects.hashCode(this.schema);
        hash = 71 * hash + Objects.hashCode(this.otherAttributes);
        return hash;
    }

}
