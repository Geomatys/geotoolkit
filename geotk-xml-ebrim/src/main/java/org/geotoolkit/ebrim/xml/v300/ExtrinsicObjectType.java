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
package org.geotoolkit.ebrim.xml.v300;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * ExtrinsicObject is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject.
 *
 *
 * <p>Java class for ExtrinsicObjectType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExtrinsicObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element name="ContentVersionInfo" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}VersionInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="mimeType" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}LongName" default="application/octet-stream" />
 *       &lt;attribute name="isOpaque" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtrinsicObjectType", propOrder = {
    "contentVersionInfo"
})
@XmlSeeAlso({
    org.geotoolkit.wrs.xml.v100.ExtrinsicObjectType.class
})
@XmlRootElement(name = "ExtrinsicObject")
public class ExtrinsicObjectType extends RegistryObjectType {

    @XmlElement(name = "ContentVersionInfo")
    private VersionInfoType contentVersionInfo;
    @XmlAttribute
    private String mimeType;
    @XmlAttribute
    private Boolean isOpaque;

    /**
     * Gets the value of the contentVersionInfo property.
     */
    public VersionInfoType getContentVersionInfo() {
        return contentVersionInfo;
    }

    /**
     * Sets the value of the contentVersionInfo property.
     */
    public void setContentVersionInfo(final VersionInfoType value) {
        this.contentVersionInfo = value;
    }

    /**
     * Gets the value of the mimeType property.
     */
    public String getMimeType() {
        if (mimeType == null) {
            return "application/octet-stream";
        } else {
            return mimeType;
        }
    }

    /**
     * Sets the value of the mimeType property.
     */
    public void setMimeType(final String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the isOpaque property.
     */
    public boolean isIsOpaque() {
        if (isOpaque == null) {
            return false;
        } else {
            return isOpaque;
        }
    }

    /**
     * Sets the value of the isOpaque property.
     */
    public void setIsOpaque(final Boolean value) {
        this.isOpaque = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (contentVersionInfo != null) {
            sb.append("contentVersionInfo:").append(contentVersionInfo).append('\n');
        }
        if (isOpaque != null) {
            sb.append("isOpaque:").append(isOpaque).append('\n');
        }
        if (mimeType != null) {
            sb.append("mimeType:").append(mimeType).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ExtrinsicObjectType && super.equals(obj)) {
            final ExtrinsicObjectType that = (ExtrinsicObjectType) obj;
            return Objects.equals(this.contentVersionInfo, that.contentVersionInfo) &&
                   Objects.equals(this.isOpaque,           that.isOpaque) &&
                   Objects.equals(this.getMimeType(),           that.getMimeType());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + super.hashCode();
        hash = 67 * hash + (this.contentVersionInfo != null ? this.contentVersionInfo.hashCode() : 0);
        hash = 67 * hash + (this.getMimeType() != null ? this.getMimeType().hashCode() : 0);
        hash = 67 * hash + (this.isOpaque != null ? this.isOpaque.hashCode() : 0);
        return hash;
    }

}
