/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.inspire.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * List of URLs that give access to translation of capabilties document at hand.
 * 
 * <p>Java class for TranslatedCapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TranslatedCapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Document" type="{http://www.inspire.org}DocumentType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TranslatedCapabilitiesType", propOrder = {
    "document"
})
public class TranslatedCapabilitiesType {

    @XmlElement(name = "Document")
    private List<DocumentType> document;

    public TranslatedCapabilitiesType() {

    }

    public TranslatedCapabilitiesType(List<DocumentType> document) {
        this.document = document;
    }

    /**
     * Gets the value of the document property.
     */
    public List<DocumentType> getDocument() {
        if (document == null) {
            document = new ArrayList<DocumentType>();
        }
        return this.document;
    }

    /**
     * Sets the value of the Document property.
     */
    public void setDocument(List<DocumentType> document) {
        if (document == null) {
            document = new ArrayList<DocumentType>();
        }
        this.document = document;
    }

    /**
     * Sets the value of the Document property.
     */
    public void setDocument(DocumentType document) {
        if (this.document == null) {
            this.document = new ArrayList<DocumentType>();
        }
        this.document.add(document);
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TranslatedCapabilitiesType) {
            final TranslatedCapabilitiesType that = (TranslatedCapabilitiesType) object;
            return Utilities.equals(this.document, that.document);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.document != null ? this.document.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TranslatedCapabilitiesType]\n");
        if ( document != null) {
            sb.append("document:\n");
            for (DocumentType d: document) {
                sb.append(d).append('\n');
            }
        }
        return sb.toString();
    }

}
