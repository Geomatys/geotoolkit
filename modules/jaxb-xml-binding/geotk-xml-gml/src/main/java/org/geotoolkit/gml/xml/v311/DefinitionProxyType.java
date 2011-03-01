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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A proxy entry in a dictionary of definitions.
 * An element of this type contains a reference to a remote definition object.
 * This entry is expected to be convenient in allowing multiple elements in one XML document to contain short (abbreviated XPointer) references,
 * which are resolved to an external definition provided in a Dictionary element in the same XML document.
 * 
 * <p>Java class for DefinitionProxyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DefinitionProxyType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}DefinitionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}definitionRef"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DefinitionProxyType", propOrder = {
    "definitionRef"
})
public class DefinitionProxyType extends DefinitionType {

    @XmlElement(required = true)
    private ReferenceType definitionRef;

    DefinitionProxyType() {}

    public DefinitionProxyType(final String id, final ReferenceType definiReferenceType) {
        super(id);
        this.definitionRef = definiReferenceType;
    }

    public DefinitionProxyType(final String id, final String name, final String description, final ReferenceType definiReferenceType) {
        super(id, name, description);
        this.definitionRef = definiReferenceType;
    }

    /**
     * A reference to a remote entry in this dictionary,
     * used when this dictionary entry is identified to allow external references to this specific entry.
     * The remote entry referenced can be in a dictionary in the same or different XML document.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getDefinitionRef() {
        return definitionRef;
    }

    /**
     * A reference to a remote entry in this dictionary,
     * used when this dictionary entry is identified to allow external references to this specific entry.
     * The remote entry referenced can be in a dictionary in the same or different XML document.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setDefinitionRef(final ReferenceType value) {
        this.definitionRef = value;
    }

}
