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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
 *         &lt;element ref="{http://www.opengis.net/ows}HTTP"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
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

    /**
     * Build a new DCP.
     */
    public DCP(final HTTP http){
        this.http = http;
    }

    public DCP(final DCP that){
        if (that != null && that.http != null) {
            this.http = new HTTP(that.http);
        }
    }

    /**
     * Gets the value of the http property.
     */
    @Override
    public HTTP getHTTP() {
        return http;
    }

    /**
     *
     */
    public List<String> getProtocolNames() {
        List<String> response = new ArrayList<String>();
        if (http != null) {
            response = http.getProtocolNames();
        }
        return response;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DCP) {
            final DCP that = (DCP) object;
            return Objects.equals(this.http, that.http);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.http != null ? this.http.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return http.toString();

    }

}
