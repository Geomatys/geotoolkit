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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GNC_MaterialResource_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_MaterialResource_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_Resource_Type">
 *       &lt;sequence>
 *         &lt;element name="isStillInProduction" type="{http://www.isotc211.org/2005/gco}Boolean_PropertyType"/>
 *         &lt;element name="feedback" type="{http://www.mdweb-project.org/files/xsd}GNC_UserDefinedMetadata_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="access" type="{http://www.mdweb-project.org/files/xsd}GNC_Access_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="relationType" type="{http://www.mdweb-project.org/files/xsd}GNC_RelationType_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_MaterialResource_Type", propOrder = {
    "isStillInProduction",
    "feedback",
    "access",
    "relationType"
})
@XmlSeeAlso({
    GNC_Service.class,
    GNC_Document.class,
    GNC_Reference.class,
    GNC_Product.class
})
@XmlRootElement(name = "GNC_MaterialResource", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_MaterialResource extends GNC_Resource {

    @XmlElement(required = true)
    private Boolean isStillInProduction;
    private List<GNC_UserDefinedMetadata> feedback;
    private List<GNC_Access> access;
    private List<GNC_RelationType> relationType;

    /**
     * Gets the value of the isStillInProduction property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getIsStillInProduction() {
        return isStillInProduction;
    }

    /**
     * Sets the value of the isStillInProduction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsStillInProduction(Boolean value) {
        this.isStillInProduction = value;
    }

    /**
     * Gets the value of the feedback property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCUserDefinedMetadataPropertyType }
     * 
     * 
     */
    public List<GNC_UserDefinedMetadata> getFeedback() {
        if (feedback == null) {
            feedback = new ArrayList<GNC_UserDefinedMetadata>();
        }
        return this.feedback;
    }

    public void setFeedback(List<GNC_UserDefinedMetadata> feedback) {
        this.feedback = feedback;
    }

    public void setFeedback(GNC_UserDefinedMetadata feedback) {
        if (this.feedback == null) {
            this.feedback = new ArrayList<GNC_UserDefinedMetadata>();
        }
        this.feedback.add(feedback);
    }

    /**
     * Gets the value of the access property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCAccessPropertyType }
     * 
     * 
     */
    public List<GNC_Access> getAccess() {
        if (access == null) {
            access = new ArrayList<GNC_Access>();
        }
        return this.access;
    }

    public void setAccess(List<GNC_Access> access) {
        this.access = access;
    }

    public void setAccess(GNC_Access access) {
        if (this.access == null) {
            this.access = new ArrayList<GNC_Access>();
        }
        this.access.add(access);
    }

    /**
     * Gets the value of the relationType property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCRelationTypePropertyType }
     * 
     * 
     */
    public List<GNC_RelationType> getRelationType() {
        if (relationType == null) {
            relationType = new ArrayList<GNC_RelationType>();
        }
        return this.relationType;
    }

    public void setRelationType(List<GNC_RelationType> relationType) {
        this.relationType = relationType;
    }

    public void setRelationType(GNC_RelationType relationType) {
        if (this.relationType == null) {
            this.relationType = new ArrayList<GNC_RelationType>();
        }
        this.relationType.add(relationType);
    }
}
