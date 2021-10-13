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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetFeatureTypeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetFeatureTypeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResultFormat" type="{http://www.opengis.net/wfs}ResultFormatType"/>
 *         &lt;element name="DCPType" type="{http://www.opengis.net/wfs}DCPTypeType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFeatureTypeType", propOrder = {
    "resultFormat",
    "dcpType"
})
public class GetFeatureTypeType {

    @XmlElement(name = "ResultFormat", required = true)
    private ResultFormatType resultFormat;
    @XmlElement(name = "DCPType", required = true)
    private List<DCPTypeType> dcpType;

    /**
     * Gets the value of the resultFormat property.
     *
     * @return
     *     possible object is
     *     {@link ResultFormatType }
     *
     */
    public ResultFormatType getResultFormat() {
        return resultFormat;
    }

    /**
     * Sets the value of the resultFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link ResultFormatType }
     *
     */
    public void setResultFormat(ResultFormatType value) {
        this.resultFormat = value;
    }

    /**
     * Gets the value of the dcpType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dcpType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDCPType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DCPTypeType }
     *
     *
     */
    public List<DCPTypeType> getDCPType() {
        if (dcpType == null) {
            dcpType = new ArrayList<DCPTypeType>();
        }
        return this.dcpType;
    }

}
