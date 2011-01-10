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
package org.geotoolkit.sml.xml.v101;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractTerm;
import org.geotoolkit.swe.xml.v101.CodeSpacePropertyType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element name="codeSpace" type="{http://www.opengis.net/swe/1.0.1}CodeSpacePropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}token"/>
 *       &lt;/sequence>
 *       &lt;attribute name="definition" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codeSpace",
    "value"
})
@XmlRootElement(name = "Term")
public class Term implements AbstractTerm {

    private CodeSpacePropertyType codeSpace;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private URI definition;

    public Term() {

    }

    public Term(final AbstractTerm term) {
        if (term != null) {
            this.value      = term.getValue();
            this.definition = term.getDefinition();
            if (term.getCodeSpace() != null) {
                this.codeSpace = new CodeSpacePropertyType(term.getCodeSpace());
            }
        }
    }

    public Term(final String value, final URI definition) {
        this.codeSpace  = null;
        this.definition = definition;
        this.value      = value;
    }

    public Term(final String value, final String definition) {
        this.codeSpace  = null;
        this.definition = URI.create(definition);
        this.value      = value;
    }

    public Term(final CodeSpacePropertyType codeSpace, final String value, final URI definition) {
        this.codeSpace  = codeSpace;
        this.definition = definition;
        this.value      = value;
    }

    public Term(final CodeSpacePropertyType codeSpace, final String value, final String definition) {
        this.codeSpace  = codeSpace;
        this.definition = URI.create(definition);
        this.value      = value;
    }
    
    /**
     * Gets the value of the codeSpace property.
     * 
     * @return
     *     possible object is
     *     {@link CodeSpacePropertyType }
     *     
     */
    public CodeSpacePropertyType getCodeSpace() {
        return codeSpace;
    }

    /**
     * Sets the value of the codeSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeSpacePropertyType }
     *     
     */
    public void setCodeSpace(final CodeSpacePropertyType value) {
        this.codeSpace = value;
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

    /**
     * Gets the value of the definition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public URI getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefinition(final String value) {
        this.definition = URI.create(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Term]").append("\n");
        if (codeSpace != null) {
            sb.append("codeSpace: ").append(codeSpace).append('\n');
        }
        if (value != null) {
            sb.append("value: ").append(value).append('\n');
        }
        if (definition != null) {
            sb.append("definition: ").append(definition).append('\n');
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

        if (object instanceof Term) {
            final Term that = (Term) object;

            return Utilities.equals(this.codeSpace,  that.codeSpace) &&
                   Utilities.equals(this.value,      that.value)     &&
                   Utilities.equals(this.definition, that.definition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.codeSpace != null ? this.codeSpace.hashCode() : 0);
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + (this.definition != null ? this.definition.hashCode() : 0);
        return hash;
    }

}
