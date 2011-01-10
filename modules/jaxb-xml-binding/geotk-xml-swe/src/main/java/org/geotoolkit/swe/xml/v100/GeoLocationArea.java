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
package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractEnvelopeProperty;
import org.geotoolkit.swe.xml.AbstractGeoLocationArea;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractVectorType">
 *       &lt;sequence>
 *         &lt;element name="member" type="{http://www.opengis.net/swe/1.0}EnvelopePropertyType" maxOccurs="unbounded"/>
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
@XmlType(name = "", propOrder = {
    "member"
})
public class GeoLocationArea extends AbstractVectorType implements AbstractGeoLocationArea {

    @XmlElement(required = true)
    private List<EnvelopePropertyType> member;

    public GeoLocationArea() {

    }

    public GeoLocationArea(final AbstractGeoLocationArea gla) {
        super(gla);
        if (gla != null && gla.getMember() != null) {
            this.member = new ArrayList<EnvelopePropertyType>();
            for (AbstractEnvelopeProperty env : gla.getMember()) {
                this.member.add(new EnvelopePropertyType(env));
            }
        }
    }

    /**
     * Gets the value of the member property.
     */
    public List<EnvelopePropertyType> getMember() {
        if (member == null) {
            member = new ArrayList<EnvelopePropertyType>();
        }
        return this.member;
    }

}
