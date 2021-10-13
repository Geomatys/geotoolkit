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

package org.geotoolkit.wcs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Java class for ExtensionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExtensionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensionType", propOrder = {
    "any"
})
public class ExtensionType {

    @XmlAnyElement(lax = true)
    private List<Object> any;

    public ExtensionType() {

    }

    public ExtensionType(Object any) {
        this.any = new ArrayList<>();
        this.any.add(any);
    }

    public ExtensionType(List<Object> any) {
        this.any = any;
    }

    /**
     * Gets the value of the any property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    public <T> T getForClass(Class<T> c) {
        if (any != null) {
            for (Object o : any) {
                if (o instanceof JAXBElement) {
                    o = ((JAXBElement)o).getValue();
                }
                if (c.isInstance(o)) {
                    return (T) o;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ExtensionType) {
            final ExtensionType that = (ExtensionType) o;
            if (this.any != null && that.any != null) {
                if (this.any.size() == that.any.size()) {
                    for (int i = 0; i < this.any.size(); i++) {
                        Object thisD = this.any.get(i);
                        Object thatD = that.any.get(i);
                        if (thisD instanceof JAXBElement) {
                            thisD = ((JAXBElement)thisD).getValue();
                        }
                        if (thatD instanceof JAXBElement) {
                            thatD = ((JAXBElement)thatD).getValue();
                        }
                        if (!Objects.equals(thisD, thatD)) {
                            return false;
                        }
                    }
                }
                return true;
            } else if (this.any == null && that.any == null) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.any);
        return hash;
    }

}
