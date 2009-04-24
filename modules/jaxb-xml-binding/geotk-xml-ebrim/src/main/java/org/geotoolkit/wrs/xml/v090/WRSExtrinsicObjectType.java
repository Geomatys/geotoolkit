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

package org.geotoolkit.wrs.xml.v090;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ebrim.xml.v250.ExtrinsicObjectType;


/**
 * 
 *       Extends ExtrinsicObjectType to include a content element that provides 
 *       a reference to a representation of the extrinsic content available in a 
 *       repository; the repository may be maintained by a third-party provider.
 *       
 * 
 * <p>Java class for WRSExtrinsicObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WRSExtrinsicObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ExtrinsicObjectType">
 *       &lt;sequence>
 *         &lt;element name="content" type="{http://www.opengis.net/cat/wrs}SimpleLinkType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WRSExtrinsicObjectType", propOrder = {
    "content"
})
@XmlSeeAlso({
    GeometryType.class
})
@XmlRootElement(name = "WRSExtrinsicObject")
public class WRSExtrinsicObjectType extends ExtrinsicObjectType {

    @XmlElement(required = true)
    private SimpleLinkType content;

    /**
     * Gets the value of the content property.
     */
    public SimpleLinkType getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     */
    public void setContent(SimpleLinkType value) {
        this.content = value;
    }

}
