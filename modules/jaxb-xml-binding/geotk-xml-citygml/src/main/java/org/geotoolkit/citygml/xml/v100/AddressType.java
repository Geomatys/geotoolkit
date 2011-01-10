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
package org.geotoolkit.citygml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureEntry;
import org.geotoolkit.gml.xml.v311.MultiPointPropertyType;


/**
 * Type for addresses.
 * It references the xAL address standard issued by the OASIS consortium.
 * Please note, that addresses are modelled as GML features.
 * Every address can be assigned zero or more 2D or 3D point geometries
 * (one gml:MultiPoint geometry) locating the entrance(s).
 * 
 * <p>Java class for AddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddressType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="xalAddress" type="{http://www.opengis.net/citygml/1.0}xalAddressPropertyType"/>
 *         &lt;element name="multiPoint" type="{http://www.opengis.net/gml}MultiPointPropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/1.0}_GenericApplicationPropertyOfAddress" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AddressType", propOrder = {
    "xalAddress",
    "multiPoint",
    "genericApplicationPropertyOfAddress"
})
public class AddressType extends AbstractFeatureEntry {

    @XmlElement(required = true)
    private XalAddressPropertyType xalAddress;
    private MultiPointPropertyType multiPoint;
    @XmlElement(name = "_GenericApplicationPropertyOfAddress")
    private List<Object> genericApplicationPropertyOfAddress;

    /**
     * Gets the value of the xalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link XalAddressPropertyType }
     *     
     */
    public XalAddressPropertyType getXalAddress() {
        return xalAddress;
    }

    /**
     * Sets the value of the xalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link XalAddressPropertyType }
     *     
     */
    public void setXalAddress(final XalAddressPropertyType value) {
        this.xalAddress = value;
    }

    /**
     * Gets the value of the multiPoint property.
     * 
     * @return
     *     possible object is
     *     {@link MultiPointPropertyType }
     *     
     */
    public MultiPointPropertyType getMultiPoint() {
        return multiPoint;
    }

    /**
     * Sets the value of the multiPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiPointPropertyType }
     *     
     */
    public void setMultiPoint(final MultiPointPropertyType value) {
        this.multiPoint = value;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfAddress property.
     */
    public List<Object> getGenericApplicationPropertyOfAddress() {
        if (genericApplicationPropertyOfAddress == null) {
            genericApplicationPropertyOfAddress = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfAddress;
    }

}
