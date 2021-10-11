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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VersionInfoType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="VersionInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="versionName" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}String16" default="1.1" />
 *       &lt;attribute name="comment" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VersionInfoType")
public class VersionInfoType {

    @XmlAttribute
    private String versionName;
    @XmlAttribute
    private String comment;

    /**
     * Gets the value of the versionName property.
     */
    public String getVersionName() {
        if (versionName == null) {
            return "1.1";
        } else {
            return versionName;
        }
    }

    /**
     * Sets the value of the versionName property.
     */
    public void setVersionName(final String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the comment property.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     */
    public void setComment(final String value) {
        this.comment = value;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append('[').append(this.getClass().getSimpleName()).append(']').append('\n');
        if (comment != null) {
            s.append("comment:\n").append(comment).append('\n');
        }
        if (versionName != null) {
            s.append("versionName:\n").append(versionName).append('\n');
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof VersionInfoType) {
            final VersionInfoType that = (VersionInfoType) obj;
            return Objects.equals(this.getVersionName(), that.getVersionName()) &&
                   Objects.equals(this.comment,    that.comment);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.getVersionName() != null ? this.getVersionName().hashCode() : 0);
        hash = 53 * hash + (this.comment != null ? this.comment.hashCode() : 0);
        return hash;
    }
}
