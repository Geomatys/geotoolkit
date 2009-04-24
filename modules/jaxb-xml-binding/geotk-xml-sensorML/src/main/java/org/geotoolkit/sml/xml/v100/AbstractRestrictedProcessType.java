/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.sml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractRestrictedProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractRestrictedProcessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/sensorML/1.0}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *           &lt;element ref="{http://www.opengis.net/gml}name" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0}metadataGroup" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractRestrictedProcessType")
@XmlSeeAlso(AbstractPureProcessType.class)
public abstract class AbstractRestrictedProcessType extends AbstractProcessType {

}
