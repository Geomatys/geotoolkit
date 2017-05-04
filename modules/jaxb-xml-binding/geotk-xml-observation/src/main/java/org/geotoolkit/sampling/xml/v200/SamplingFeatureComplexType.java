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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.ReferenceType;
import org.apache.sis.util.iso.DefaultNameFactory;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.observation.sampling.SamplingFeatureRelation;
import org.opengis.util.GenericName;


/**
 * A "SamplingFeatureRelation" is used to describe relationships between
 *              sampling features, including part-whole, siblings, etc.
 *
 * <p>Java class for SamplingFeatureComplexType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SamplingFeatureComplexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="role" type="{http://www.opengis.net/gml/3.2}ReferenceType"/>
 *         &lt;element name="relatedSamplingFeature" type="{http://www.opengis.net/sampling/2.0}SF_SamplingFeaturePropertyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SamplingFeatureComplexType", propOrder = {
    "role",
    "relatedSamplingFeature"
})
public class SamplingFeatureComplexType implements SamplingFeatureRelation {

    @XmlElement(required = true)
    private ReferenceType role;
    @XmlElement(required = true)
    private SFSamplingFeaturePropertyType relatedSamplingFeature;

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *
     */
    public ReferenceType getRoleReference() {
        return role;
    }

    @Override
    public GenericName getRole() {
        if (role.getHref() != null) {
            DefaultNameFactory factory = new DefaultNameFactory();
            return factory.createGenericName(null, role.getHref());
        }
        return null;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *
     */
    public void setRole(ReferenceType value) {
        this.role = value;
    }

    /**
     * Gets the value of the relatedSamplingFeature property.
     *
     * @return
     *     possible object is
     *     {@link SFSamplingFeaturePropertyType }
     *
     */
    public SFSamplingFeaturePropertyType getRelatedSamplingFeature() {
        return relatedSamplingFeature;
    }

    @Override
    public SamplingFeature getTarget() {
        if (relatedSamplingFeature != null) {
            return relatedSamplingFeature.getSFSamplingFeature();
        }
        return null;
    }

    /**
     * Sets the value of the relatedSamplingFeature property.
     *
     * @param value
     *     allowed object is
     *     {@link SFSamplingFeaturePropertyType }
     *
     */
    public void setRelatedSamplingFeature(SFSamplingFeaturePropertyType value) {
        this.relatedSamplingFeature = value;
    }

}
