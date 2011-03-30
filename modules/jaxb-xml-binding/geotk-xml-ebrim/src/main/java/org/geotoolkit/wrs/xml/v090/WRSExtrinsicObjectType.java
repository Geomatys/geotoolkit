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
package org.geotoolkit.wrs.xml.v090;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ebrim.xml.v250.ExtrinsicObjectType;
import org.geotoolkit.util.Utilities;


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
 * @module pending
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
    public void setContent(final SimpleLinkType value) {
        this.content = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (content != null) {
            sb.append("content:").append(content).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof WRSExtrinsicObjectType && super.equals(obj)) {
            final WRSExtrinsicObjectType that = (WRSExtrinsicObjectType) obj;
            return Utilities.equals(this.content, that.content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + super.hashCode();
        hash = 73 * hash + (this.content != null ? this.content.hashCode() : 0);
        return hash;
    }
}
