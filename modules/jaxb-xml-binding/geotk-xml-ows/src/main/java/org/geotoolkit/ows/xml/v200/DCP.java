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

package org.geotoolkit.ows.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDCP;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}HTTP"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "http"
})
@XmlRootElement(name = "DCP")
public class DCP implements AbstractDCP {

    @XmlElement(name = "HTTP")
    private HTTP http;

    /**
     * Empty constructor used by JAXB.
     */
    DCP(){

    }

    public DCP(final DCP that){
        if (that != null && that.http != null) {
            this.http = new HTTP(that.http);
        }
    }

    /**
     * Build a new DCP.
     */
    public DCP(final HTTP http){
        this.http = http;
    }

    /**
     * Gets the value of the http property.
     *
     * @return
     *     possible object is
     *     {@link HTTP }
     *
     */
    @Override
    public HTTP getHTTP() {
        return http;
    }

    /**
     * Sets the value of the http property.
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
