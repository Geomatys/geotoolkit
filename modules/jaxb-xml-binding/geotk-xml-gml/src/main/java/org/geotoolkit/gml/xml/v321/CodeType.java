/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.gml.xml.Code;


/**
 * gml:CodeType is a generalized type to be used for a term, keyword or name.
 * It adds a XML attribute codeSpace to a term, where the value of the codeSpace attribute (if present) shall indicate a dictionary, thesaurus, classification scheme, authority, or pattern for the term.
 *
 * <p>Java class for CodeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CodeType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "value"
})
@XmlSeeAlso({
    CodeWithAuthorityType.class,
    Category.class
})
public class CodeType implements Code {

    @XmlValue
    private String value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String codeSpace;

    public CodeType() {

    }

    public CodeType(final CodeType that) {
        if (that != null) {
            this.codeSpace = that.codeSpace;
            this.value     = that.value;
        }
    }

    public CodeType(final String value) {
        this.value = value;
    }

    public CodeType(final String value, final String codeSpace) {
        this.codeSpace = codeSpace;
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
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
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the codeSpace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getCodeSpace() {
        return codeSpace;
    }

    /**
     * Sets the value of the codeSpace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeSpace(String value) {
        this.codeSpace = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[CodeType]").append("\n");
        if (codeSpace != null) {
            sb.append("codespace: ").append(codeSpace).append('\n');
        }
        if (value != null) {
            sb.append("value: ").append(value).append('\n');
        }
        return sb.toString();
    }
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CodeType) {
            final CodeType that = (CodeType) object;

            return Objects.equals(this.codeSpace, that.codeSpace) &&
                   Objects.equals(this.value,     that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + (this.codeSpace != null ? this.codeSpace.hashCode() : 0);
        return hash;
    }
}
