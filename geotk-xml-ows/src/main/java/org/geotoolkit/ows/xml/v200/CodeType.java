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

package org.geotoolkit.ows.xml.v200;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.ows.xml.AbstractCodeType;


/**
 * Type copied from basicTypes.xsd of GML 3 with
 *       documentation edited, for possible use outside the ServiceIdentification
 *       section of a service metadata document.
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
@XmlType(name = "CodeType", propOrder = {
    "value"
})
@XmlSeeAlso({
    NilValueType.class
})
public class CodeType implements AbstractCodeType {

    @XmlValue
    private String value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String codeSpace;

    /**
     * Empty constructor used by JAXB.
     */
    protected CodeType() {
    }

    /**
     * Build a new code.
     */
    public CodeType(final String value, final String codespace) {
        this.codeSpace = codespace;
        this.value     = value;
    }

    /**
     * Build a new code without codespace.
     */
    public CodeType(final String value) {
        this.value     = value;
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof  CodeType) {
            final CodeType that = (CodeType) object;
            return Objects.equals(this.codeSpace, that.codeSpace) &&
                   Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.codeSpace);
        hash = 17 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[").append(this.getClass().getSimpleName()).append("]\n");
        if (codeSpace != null) {
            s.append("codeSpace=").append(codeSpace).append('\n');
        }
        if (value != null) {
            s.append("value=").append(value);
        }
        return s.toString();
    }
}
