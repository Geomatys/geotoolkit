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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v212.AbstractFeatureType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for InsertElementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsertElementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}_Feature" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertElementType", propOrder = {
    "feature"
})
public class InsertElementType {

    @XmlElementRef(name = "AbstractFeature", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractFeatureType>> feature;
    @XmlAttribute
    private String handle;

    /**
     * Gets the value of the feature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the feature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link AbstractFeatureType }{@code >}
     * {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractFeatureCollectionType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends AbstractFeatureType>> getFeature() {
        if (feature == null) {
            feature = new ArrayList<JAXBElement<? extends AbstractFeatureType>>();
        }
        return this.feature;
    }

    /**
     * Gets the value of the handle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandle(String value) {
        this.handle = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[InsertElementType]\n");
        if (handle != null) {
            sb.append("handle").append(handle).append('\n');
        }
        if (feature != null) {
            sb.append("feature:\n");
            for (Object q : feature) {
                sb.append(q).append("\nclass:").append(q.getClass().getName());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof InsertElementType) {
            InsertElementType that = (InsertElementType) obj;
            return Utilities.equals(this.feature, that.feature) &&
                   Utilities.equals(this.handle, that.handle);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.feature != null ? this.feature.hashCode() : 0);
        hash = 17 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        return hash;
    }
}
