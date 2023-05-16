/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 * WPS operation request base, for all WPS operations, except GetCapabilities.
 * In this XML encoding, no "request" parameter is included, since the element
 * name specifies the specific operation.
 * An 'Extension' element provides a placeholder for extra request parameters
 * that might be defined by WPS extension standards.
 *
 *
 * <p>Java class for RequestBase complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RequestBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WPS" />
 *       &lt;attribute name="version" use="required" type="{http://www.opengis.net/ows/2.0}VersionType" fixed="2.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "RequestBaseType")
@XmlSeeAlso({
    Execute.class,
    DescribeProcess.class,
    GetStatus.class,
    GetResult.class,
    Dismiss.class
})
public abstract class RequestBase extends DocumentBase implements org.geotoolkit.ows.xml.RequestBase {

    public RequestBase() {}

    public RequestBase(String service) {
        this(service, null, null);
    }

    public RequestBase(String service, final String version) {
        this(service, version, null);
    }

    public RequestBase(String service, final String version, final String lang) {
        super(service, version, lang);
    }
}
