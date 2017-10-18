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
    "http"
})
@XmlRootElement(name = "DCPType")
public class DCPType {

    @XmlElement(name = "HTTP", required = true)
    protected HTTP http;

    /**
     * Obtient la valeur de la propriété http.
     *
     * @return
     *     possible object is
     *     {@link HTTP }
     *
     */
    public HTTP getHTTP() {
        return http;
    }

    /**
     * Définit la valeur de la propriété http.
     *
     * @param value
     *     allowed object is
     *     {@link HTTP }
     *
     */
    public void setHTTP(HTTP value) {
        this.http = value;
    }

}
