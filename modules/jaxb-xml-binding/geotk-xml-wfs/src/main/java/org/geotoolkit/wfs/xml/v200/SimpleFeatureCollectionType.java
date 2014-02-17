/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for SimpleFeatureCollectionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SimpleFeatureCollectionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}boundedBy" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}member" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleFeatureCollectionType", propOrder = {
    "boundedBy",
    "member"
})
@XmlSeeAlso({
    FeatureCollectionType.class
})
public class SimpleFeatureCollectionType {

    private EnvelopePropertyType boundedBy;
    private List<MemberPropertyType> member;

    /**
     * Gets the value of the boundedBy property.
     *
     * @return
     *     possible object is
     *     {@link EnvelopePropertyType }
     *
     */
    public EnvelopePropertyType getBoundedBy() {
        return boundedBy;
    }

    /**
     * Sets the value of the boundedBy property.
     *
     * @param value
     *     allowed object is
     *     {@link EnvelopePropertyType }
     *
     */
    public void setBoundedBy(EnvelopePropertyType value) {
        this.boundedBy = value;
    }

    /**
     * Gets the value of the member property.
     */
    public List<MemberPropertyType> getMember() {
        if (member == null) {
            member = new ArrayList<MemberPropertyType>();
        }
        return this.member;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (boundedBy != null) {
            sb.append("boundedBy:").append(boundedBy).append('\n');
        }
        if (member != null && !member.isEmpty()) {
            sb.append("member:\n");
            for (MemberPropertyType m : member) {
                sb.append(m).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SimpleFeatureCollectionType) {
            final SimpleFeatureCollectionType that = (SimpleFeatureCollectionType) obj;
            return Objects.equals(this.boundedBy, that.boundedBy) &&
                   Objects.equals(this.member,    that.member);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.boundedBy != null ? this.boundedBy.hashCode() : 0);
        hash = 23 * hash + (this.member != null ? this.member.hashCode() : 0);
        return hash;
    }
}
