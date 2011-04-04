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
package org.geotoolkit.wrs.xml.v100;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Extends rim:ExtrinsicObjectType to add the following:
 *  1. MTOM/XOP based attachment support.
 *  2. XLink based reference to a part in a multipart/related message structure.
 * 
 * NOTE: This content model is planned for RegRep 4.0.
 *       
 * 
 * <p>Java class for ExtrinsicObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtrinsicObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ExtrinsicObjectType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="repositoryItemRef" type="{http://www.opengis.net/cat/wrs/1.0}SimpleLinkType"/>
 *         &lt;element name="repositoryItem" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtrinsicObjectType", propOrder = {
    "repositoryItemRef",
    "repositoryItem"
})
public class ExtrinsicObjectType extends org.geotoolkit.ebrim.xml.v300.ExtrinsicObjectType {

    private SimpleLinkType repositoryItemRef;
    @XmlMimeType("*/*")
    private DataHandler repositoryItem;

    /**
     * Gets the value of the repositoryItemRef property.
     */
    public SimpleLinkType getRepositoryItemRef() {
        return repositoryItemRef;
    }

    /**
     * Sets the value of the repositoryItemRef property.
     */
    public void setRepositoryItemRef(final SimpleLinkType value) {
        this.repositoryItemRef = value;
    }

    /**
     * Gets the value of the repositoryItem property.
     */
    public DataHandler getRepositoryItem() {
        return repositoryItem;
    }

    /**
     * Sets the value of the repositoryItem property.
     */
    public void setRepositoryItem(final DataHandler value) {
        this.repositoryItem = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (repositoryItem != null) {
            sb.append("repositoryItem:").append(repositoryItem).append('\n');
        }
        if (repositoryItemRef != null) {
            sb.append("repositoryItemRef:").append(repositoryItemRef).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ExtrinsicObjectType && super.equals(obj)) {
            final ExtrinsicObjectType that = (ExtrinsicObjectType) obj;
            return Utilities.equals(this.repositoryItem,    that.repositoryItem) &&
                   Utilities.equals(this.repositoryItemRef, that.repositoryItemRef);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.repositoryItemRef != null ? this.repositoryItemRef.hashCode() : 0);
        hash = 31 * hash + (this.repositoryItem != null ? this.repositoryItem.hashCode() : 0);
        return hash;
    }
}
