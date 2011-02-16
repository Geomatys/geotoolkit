/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */

package org.geotoolkit.geotnetcab;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.opengis.metadata.citation.OnlineResource;

import org.opengis.metadata.geonetcab.GNC_RelationType;

/**
 * <p>Java class for GNC_Resource_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Resource_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gmd}MD_DataIdentification_Type">
 *       &lt;sequence>
 *         &lt;element name="onlineInformation" type="{http://www.isotc211.org/2005/gmd}CI_OnlineResource_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="references" type="{http://www.mdweb-project.org/files/xsd}GNC_Resource_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Resource_Type", propOrder = {
    "onlineInformation",
    "resourceType",
    "relationType",
    "socialBenefitArea"
})
@XmlSeeAlso({
    GNC_Organisation.class,
    GNC_MaterialResource.class
})
@XmlRootElement(name = "GNC_Resource", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_Resource extends DefaultDataIdentification implements org.opengis.metadata.geonetcab.GNC_Resource {

    private List<OnlineResource> onlineInformation;

    private GNC_ResourceTypeCode resourceType;

    private GNC_RelationType relationType;

    private GNC_ThematicTypeCode socialBenefitArea;
    
    @XmlTransient
    private String href;
    
    /**
     * Gets the value of the onlineInformation property.
     * 
     */
    @Override
    public List<OnlineResource> getOnlineInformation() {
        if (onlineInformation == null) {
            onlineInformation = new ArrayList<OnlineResource>();
        }
        return this.onlineInformation;
    }

    /**
     * Sets the value of the onlineInformation property.
     *
     */
    public void setOnlineInformation(final List<OnlineResource> onlineInformation) {
        this.onlineInformation = onlineInformation;
    }

    /**
     * Sets the value of the onlineInformation property.
     *
     */
    public void setOnlineInformation(final OnlineResource onlineInformation) {
        if (this.onlineInformation == null) {
            this.onlineInformation = new ArrayList<OnlineResource>();
        }
        this.onlineInformation.add(onlineInformation);
    }

    /**
     * @return the href
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @return the resourceType
     */
    public GNC_ResourceTypeCode getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType the resourceType to set
     */
    public void setResourceType(GNC_ResourceTypeCode resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * @return the relationType
     */
    public GNC_RelationType getRelationType() {
        return relationType;
    }

    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(GNC_RelationType relationType) {
        this.relationType = relationType;
    }

    /**
     * @return the socialBenefitArea
     */
    public GNC_ThematicTypeCode getSocialBenefitArea() {
        return socialBenefitArea;
    }

    /**
     * @param socialBenefitArea the socialBenefitArea to set
     */
    public void setSocialBenefitArea(GNC_ThematicTypeCode socialBenefitArea) {
        this.socialBenefitArea = socialBenefitArea;
    }
}
