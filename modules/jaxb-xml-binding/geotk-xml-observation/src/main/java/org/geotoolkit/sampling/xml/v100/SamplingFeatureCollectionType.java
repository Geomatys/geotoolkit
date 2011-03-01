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

package org.geotoolkit.sampling.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A Sampling Feature Collection is a simple collection of sampling features. 
 * The relationship of members to the collection is equivalent to a part-whole relation. 
 * A collection is a sampling feature so must still carry the sampledFeature association to indicate intention.
 * 
 * <p>Java class for SamplingFeatureCollectionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SamplingFeatureCollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sampling/1.0}SamplingFeatureType">
 *       &lt;sequence>
 *         &lt;element name="member" type="{http://www.opengis.net/sampling/1.0}SamplingFeaturePropertyType" maxOccurs="unbounded"/>
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
@XmlType(name = "SamplingFeatureCollectionType", propOrder = {
    "member"
})
public class SamplingFeatureCollectionType extends SamplingFeatureType {

    @XmlElement(required = true)
    private List<SamplingFeaturePropertyType> member;

    /**
     * Gets the value of the member property.
     */
    public List<SamplingFeaturePropertyType> getMember() {
        if (member == null) {
            member = new ArrayList<SamplingFeaturePropertyType>();
        }
        return this.member;
    }

}
