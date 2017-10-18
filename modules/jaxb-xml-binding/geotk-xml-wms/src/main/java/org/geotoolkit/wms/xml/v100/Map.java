/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "format",
    "dcpType"
})
@XmlRootElement(name = "Map")
public class Map {

    @XmlElement(name = "Format", required = true)
    protected Format format;
    @XmlElement(name = "DCPType", required = true)
    protected List<DCPType> dcpType;

    /**
     * Obtient la valeur de la propriété format.
     *
     * @return
     *     possible object is
     *     {@link Format }
     *
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Définit la valeur de la propriété format.
     *
     * @param value
     *     allowed object is
     *     {@link Format }
     *
     */
    public void setFormat(Format value) {
        this.format = value;
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
     * {@link DCPType }
     *
     *
     */
    public List<DCPType> getDCPType() {
        if (dcpType == null) {
            dcpType = new ArrayList<DCPType>();
        }
        return this.dcpType;
    }

}
