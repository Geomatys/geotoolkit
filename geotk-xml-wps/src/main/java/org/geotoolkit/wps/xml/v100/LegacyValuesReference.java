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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.ValueReference;


/**
 * References an externally defined finite set of values and ranges for this input.
 *
 * <p>Java class for LegacyValuesReference complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LegacyValuesReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute ref="{http://www.opengis.net/ows/1.1}reference"/>
 *       &lt;attribute name="valuesForm" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValuesReferenceType")
public class LegacyValuesReference implements ValueReference {

    @XmlAttribute(namespace = "http://www.opengis.net/ows/1.1")
    @XmlSchemaType(name = "anyURI")
    protected String reference;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String valuesForm;

    public LegacyValuesReference() {}

    public LegacyValuesReference(final String value, final String reference) {
        this.valuesForm = value;
        this.reference = reference;
    }

    /**
     * Gets the value of the reference property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setReference(final String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the valuesForm property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValuesForm() {
        return valuesForm;
    }

    /**
     * Sets the value of the valuesForm property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValuesForm(final String value) {
        this.valuesForm = value;
    }

    @Override
    public String getValue() {
        return valuesForm;
    }

    @Override
    public void setValue(String value) {
        this.valuesForm = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (reference != null) {
            sb.append("reference:").append(reference).append('\n');
        }
        if (valuesForm != null) {
            sb.append("valuesForm:").append(valuesForm).append('\n');
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
        if (object instanceof LegacyValuesReference) {
            final LegacyValuesReference that = (LegacyValuesReference) object;
            return Objects.equals(this.reference, that.reference) &&
                   Objects.equals(this.valuesForm, that.valuesForm);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.reference);
        hash = 79 * hash + Objects.hashCode(this.valuesForm);
        return hash;
    }
}
