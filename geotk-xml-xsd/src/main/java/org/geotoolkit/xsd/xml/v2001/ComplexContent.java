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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="restriction" type="{http://www.w3.org/2001/XMLSchema}complexRestrictionType"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}extensionType"/>
 *       &lt;/choice>
 *       &lt;attribute name="mixed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
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
@XmlRootElement(name = "complexContent")
public class ComplexContent extends Annotated {

    private ComplexRestrictionType restriction;
    private ExtensionType extension;
    @XmlAttribute
    private Boolean mixed;

    public ComplexContent() {

    }

    public ComplexContent(final ExtensionType extension) {
        this.extension = extension;
    }

    /**
     * Gets the value of the restriction property.
     */
    public ComplexRestrictionType getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     *
     */
    public void setRestriction(final ComplexRestrictionType value) {
        this.restriction = value;
    }

    /**
     * Gets the value of the extension property.
     *
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     */
    public void setExtension(final ExtensionType value) {
        this.extension = value;
    }

    /**
     * Gets the value of the mixed property.
     *
     */
    public Boolean isMixed() {
        return mixed;
    }

    /**
     * Sets the value of the mixed property.
     */
    public void setMixed(final Boolean value) {
        this.mixed = value;
    }

}
