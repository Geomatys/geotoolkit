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
package org.geotoolkit.xsd.xml.v2001;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;choice>
 *         &lt;element name="restriction" type="{http://www.w3.org/2001/XMLSchema}simpleRestrictionType"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}simpleExtensionType"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "restriction",
    "extension"
})
@XmlRootElement(name = "simpleContent")
public class SimpleContent extends Annotated {

    private SimpleRestrictionType restriction;
    private SimpleExtensionType extension;

    /**
     * Gets the value of the restriction property.
     *
     * @return
     *     possible object is
     *     {@link SimpleRestrictionType }
     *
     */
    public SimpleRestrictionType getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     *
     * @param value
     *     allowed object is
     *     {@link SimpleRestrictionType }
     *
     */
    public void setRestriction(final SimpleRestrictionType value) {
        this.restriction = value;
    }

    /**
     * Gets the value of the extension property.
     *
     * @return
     *     possible object is
     *     {@link SimpleExtensionType }
     *
     */
    public SimpleExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     *
     * @param value
     *     allowed object is
     *     {@link SimpleExtensionType }
     *
     */
    public void setExtension(final SimpleExtensionType value) {
        this.extension = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SimpleContent && super.equals(object)) {
            final SimpleContent that = (SimpleContent) object;
            return Objects.equals(this.extension,   that.extension) &&
                   Objects.equals(this.restriction, that.restriction);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + super.hashCode();
        hash = 83 * hash + (this.extension != null ? this.extension.hashCode() : 0);
        hash = 83 * hash + (this.restriction != null ? this.restriction.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (extension != null) {
            sb.append("extension:").append(extension).append('\n');
        }
        if (restriction != null) {
            sb.append("restriction:").append(restriction).append('\n');
        }
        return  sb.toString();
    }
}
