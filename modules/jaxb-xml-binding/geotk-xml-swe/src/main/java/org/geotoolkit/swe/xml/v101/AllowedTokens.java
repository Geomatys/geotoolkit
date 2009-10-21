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
package org.geotoolkit.swe.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
 *         &lt;element name="valueList" type="{http://www.opengis.net/swe/1.0.1}tokenList" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
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
    "valueList"
})
@XmlRootElement(name = "AllowedTokens")
public class AllowedTokens {

    @XmlElementRef(name = "valueList", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private List<JAXBElement<List<String>>> valueList;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    /**
     * Gets the value of the valueList property.
     * 
     */
    public List<JAXBElement<List<String>>> getValueList() {
        if (valueList == null) {
            valueList = new ArrayList<JAXBElement<List<String>>>();
        }
        return this.valueList;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Verify that the object is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AllowedTokens) {
            final AllowedTokens that = (AllowedTokens) object;
            boolean valueL = false;
            if (this.valueList != null && that.valueList != null) {
                if (this.valueList.size() != that.valueList.size()) {
                    valueL = false;
                } else {
                    valueL = true;
                    for (int i = 0; i < this.valueList.size(); i++) {
                        JAXBElement<List<String>> thisJB = this.valueList.get(i);
                        JAXBElement<List<String>> thatJB = that.valueList.get(i);
                        if (!Utilities.equals(thisJB.getValue(), thatJB.getValue())) {
                            valueL = false;
                        }
                    }
                }
            } else if (this.valueList == null && that.valueList == null) {
                valueL = true;
            }
            return Utilities.equals(this.id,  that.id) &&
                   valueL;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.valueList != null ? this.valueList.hashCode() : 0);
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
