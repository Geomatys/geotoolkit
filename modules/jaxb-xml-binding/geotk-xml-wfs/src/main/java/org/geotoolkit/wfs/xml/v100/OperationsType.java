/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for OperationsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OperationsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/wfs}Insert"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}Update"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}Delete"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}Query"/>
 *         &lt;element ref="{http://www.opengis.net/wfs}Lock"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperationsType", propOrder = {
    "insertOrUpdateOrDelete"
})
public class OperationsType {

    @XmlElementRefs({
        @XmlElementRef(name = "Insert", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "Delete", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "Query", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "Lock", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "Update", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class)
    })
    private List<JAXBElement<EmptyType>> insertOrUpdateOrDelete;

    /**
     * Gets the value of the insertOrUpdateOrDelete property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the insertOrUpdateOrDelete property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInsertOrUpdateOrDelete().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link EmptyType }{@code >}
     * {@link JAXBElement }{@code <}{@link EmptyType }{@code >}
     * {@link JAXBElement }{@code <}{@link EmptyType }{@code >}
     * {@link JAXBElement }{@code <}{@link EmptyType }{@code >}
     * {@link JAXBElement }{@code <}{@link EmptyType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<EmptyType>> getInsertOrUpdateOrDelete() {
        if (insertOrUpdateOrDelete == null) {
            insertOrUpdateOrDelete = new ArrayList<JAXBElement<EmptyType>>();
        }
        return this.insertOrUpdateOrDelete;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof OperationsType) {
            final OperationsType that = (OperationsType) object;

            if (this.insertOrUpdateOrDelete.size() == that.insertOrUpdateOrDelete.size()) {
                for (int i = 0; i < insertOrUpdateOrDelete.size(); i++) {
                    final JAXBElement<EmptyType> thisJb = this.insertOrUpdateOrDelete.get(i);
                    final JAXBElement<EmptyType> thatJb = that.insertOrUpdateOrDelete.get(i);
                    if (!Utilities.equals(thisJb.getName(), thatJb.getName()) ||
                        !Utilities.equals(thisJb.getValue(), thatJb.getValue())) {
                        return false;
                    }    
                }
                return true;
            }
            
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.insertOrUpdateOrDelete != null ? this.insertOrUpdateOrDelete.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[OperationsType]\n");
        if(insertOrUpdateOrDelete != null) {
            s.append("insertOrUpdateOrDelete:\n");
            for (JAXBElement<EmptyType> op : insertOrUpdateOrDelete) {
                s.append(op.getName()).append(op.getValue()).append('\n');
            }
        }
        return s.toString();
    }
}
