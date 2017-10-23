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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.wms.xml.AbstractOnlineResource;
import org.geotoolkit.wms.xml.AbstractProtocol;
import org.geotoolkit.wms.xml.v111.OnlineResource;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Get")
public class Get implements AbstractProtocol {

    @XmlAttribute(name = "onlineResource", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String onlineResource;

    /**
     * Obtient la valeur de la propriété onlineResource.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public AbstractOnlineResource getOnlineResource() {
        if (onlineResource == null)
            return null;
        return new OnlineResource(onlineResource);
    }

    /**
     * Définit la valeur de la propriété onlineResource.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnlineResource(String value) {
        this.onlineResource = value;
    }

}
