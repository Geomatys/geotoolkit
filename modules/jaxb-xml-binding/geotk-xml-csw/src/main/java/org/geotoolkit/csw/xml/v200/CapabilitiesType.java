/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.csw.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v100.CapabilitiesBaseType;


/**
 * 
 * XML encoded CSW GetCapabilities operation response. 
 * This document provides clients with service metadata about a specific service instance,
 * including metadata about the tightly-coupled data served.
 * If the server does not implement the updateSequence parameter, 
 * the server shall always return the complete Capabilities document, 
 * without the updateSequence parameter. 
 * When the server implements the updateSequence parameter and the GetCapabilities operation request included
 * the updateSequence parameter with the current value, 
 * the server shall return this element with only the "version" and "updateSequence" attributes. 
 * Otherwise, all optional elements shall be included or not depending on the actual value of the Contents parameter in the GetCapabilities operation request.
 *          
 * 
 * <p>Java class for CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesType")
public class CapabilitiesType extends CapabilitiesBaseType {


}
