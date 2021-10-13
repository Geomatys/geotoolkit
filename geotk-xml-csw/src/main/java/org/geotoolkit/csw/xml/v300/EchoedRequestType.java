/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v300;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *             Includes a copy of the request message body.
 *
 *
 * <p>Classe Java pour EchoedRequestType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EchoedRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax'/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EchoedRequestType", propOrder = {
    "any"
})
public class EchoedRequestType {

    @XmlAnyElement(lax = true)
    protected Object any;

    /**
     * An empty constructor used by JAXB
     */
    public EchoedRequestType() {
    }

    /**
     * Build a new Echoed request
     */
    public EchoedRequestType(final Object request) {
        this.any = request;
    }

    /**
     * Obtient la valeur de la propriété any.
     */
    public Object getAny() {
        return any;
    }

    /**
     * Définit la valeur de la propriété any.
     */
    public void setAny(Object value) {
        this.any = value;
    }
}
