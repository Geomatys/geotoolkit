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
package org.geotoolkit.ebrim.xml.v300;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ebrim.xml.EbrimInternationalString;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for InternationalStringType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InternationalStringType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}LocalizedString"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InternationalStringType", propOrder = {
    "localizedString"
})
public class InternationalStringType implements EbrimInternationalString {

    @XmlElement(name = "LocalizedString")
    private List<LocalizedStringType> localizedString;

    public InternationalStringType() {

    }

    public InternationalStringType(final LocalizedStringType ls) {
        this.localizedString  = new ArrayList<LocalizedStringType>();
        if (ls != null) {
            this.localizedString.add(ls);
        }
    }

    public InternationalStringType(final List<LocalizedStringType> ls) {
        this.localizedString  = ls;
    }

    /**
     * Gets the value of the localizedString property.
     */
    public List<LocalizedStringType> getLocalizedString() {
        if (localizedString == null) {
            localizedString = new ArrayList<LocalizedStringType>();
        }
        return this.localizedString;
    }
    
    /**
     * Set the values of localizedString.
     * 
     * @param localizedString
     */
    public void setLocalizedString(final List<LocalizedStringType> localizedString) {
        this.localizedString = localizedString;
    }
    
    /**
     * Add a singleton value to the localizedString list.
     * 
     * @param localizedString
     */
    public void setLocalizedString(final LocalizedStringType localizedString) {
        if (this.localizedString == null) {
            this.localizedString = new ArrayList<LocalizedStringType>();
        }
        this.localizedString.add(localizedString);
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append('[').append(this.getClass().getSimpleName()).append(']').append('\n');
        if (localizedString != null) {
            s.append("localizedString:\n");
            for (LocalizedStringType sl: localizedString) {
                s.append(sl).append('\n');
            }
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof InternationalStringType) {
            final InternationalStringType that = (InternationalStringType) obj;
            return Utilities.equals(this.localizedString, that.localizedString);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.localizedString != null ? this.localizedString.hashCode() : 0);
        return hash;
    }
}
