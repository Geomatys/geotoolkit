/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
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
    "format"
})
@XmlRootElement(name = "Exception")
public class Exception {

    @XmlElement(name = "Format", required = true)
    protected Format format;

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

}
