/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sampling.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;


/**
 *  The class SF_SamplingFeatureCollection (Figure 9) is an instance of the
 * 				«metaclass» GF_FeatureType (ISO 19109:2005), which therefore represents a feature
 * 				type. SF_SamplingFeatureCollection shall support one association. 
 * 
 * <p>Java class for SF_SamplingFeatureCollectionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SF_SamplingFeatureCollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="member" type="{http://www.opengis.net/sampling/2.0}SF_SamplingFeaturePropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SF_SamplingFeatureCollectionType", propOrder = {
    "member"
})
public class SFSamplingFeatureCollectionType extends AbstractFeatureType {

    @XmlElement(required = true)
    private List<SFSamplingFeaturePropertyType> member;

    /**
     * Gets the value of the member property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link SFSamplingFeaturePropertyType }
     * 
     * 
     */
    public List<SFSamplingFeaturePropertyType> getMember() {
        if (member == null) {
            member = new ArrayList<SFSamplingFeaturePropertyType>();
        }
        return this.member;
    }

}
