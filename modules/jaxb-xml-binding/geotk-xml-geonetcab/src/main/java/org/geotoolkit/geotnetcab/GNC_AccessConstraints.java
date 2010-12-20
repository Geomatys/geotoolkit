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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GNC_AccessConstraints_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_AccessConstraints_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="dataAccessConditionPortal" type="{http://www.isotc211.org/2005/gmd}URL_PropertyType" minOccurs="0"/>
 *         &lt;element name="nameOfConditions" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="nonCommercialUse" type="{http://www.isotc211.org/2005/gco}Boolean_PropertyType"/>
 *         &lt;element name="thematicUsage" type="{http://www.mdweb-project.org/files/xsd}GNC_ThematicTypeCode_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="useRestrictions" type="{http://www.mdweb-project.org/files/xsd}GNC_UseRestrictions_PropertyType"/>
 *         &lt;element name="userRestrictions" type="{http://www.mdweb-project.org/files/xsd}GNC_UsersRestrictions_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_AccessConstraints_Type", propOrder = {
    "dataAccessConditionPortal",
    "nameOfConditions",
    "nonCommercialUse",
    "thematicUsage",
    "useRestrictions",
    "userRestrictions"
})
public class GNC_AccessConstraints {

    private URL dataAccessConditionPortal;
    @XmlElement(required = true)
    private String nameOfConditions;
    @XmlElement(required = true)
    private Boolean nonCommercialUse;
    @XmlElement(required = true)
    private List<GNC_ThematicTypeCode> thematicUsage;
    @XmlElement(required = true)
    private GNC_UseRestrictions useRestrictions;
    private List<GNC_UsersRestrictions> userRestrictions;

    /**
     * Gets the value of the dataAccessConditionPortal property.
     * 
     * @return
     *     possible object is
     *     {@link URL }
     *     
     */
    public URL getDataAccessConditionPortal() {
        return dataAccessConditionPortal;
    }

    /**
     * Sets the value of the dataAccessConditionPortal property.
     * 
     * @param value
     *     allowed object is
     *     {@link URL }
     *     
     */
    public void setDataAccessConditionPortal(URL value) {
        this.dataAccessConditionPortal = value;
    }

    /**
     * Gets the value of the nameOfConditions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfConditions() {
        return nameOfConditions;
    }

    /**
     * Sets the value of the nameOfConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfConditions(String value) {
        this.nameOfConditions = value;
    }

    /**
     * Gets the value of the nonCommercialUse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getNonCommercialUse() {
        return nonCommercialUse;
    }

    /**
     * Sets the value of the nonCommercialUse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonCommercialUse(Boolean value) {
        this.nonCommercialUse = value;
    }

    /**
     * Gets the value of the thematicUsage property.
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GNCThematicTypeCodePropertyType }
     * 
     * 
     */
    public List<GNC_ThematicTypeCode> getThematicUsage() {
        if (thematicUsage == null) {
            thematicUsage = new ArrayList<GNC_ThematicTypeCode>();
        }
        return this.thematicUsage;
    }

    public void setThematicUsage(List<GNC_ThematicTypeCode> thematicUsage) {
        this.thematicUsage = thematicUsage;
    }

    public void setThematicUsage(GNC_ThematicTypeCode thematicUsage) {
        if (this.thematicUsage == null) {
            this.thematicUsage = new ArrayList<GNC_ThematicTypeCode>();
        }
        this.thematicUsage.add(thematicUsage);
    }

    /**
     * Gets the value of the useRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link GNCUseRestrictionsPropertyType }
     *     
     */
    public GNC_UseRestrictions getUseRestrictions() {
        return useRestrictions;
    }

    /**
     * Sets the value of the useRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCUseRestrictionsPropertyType }
     *     
     */
    public void setUseRestrictions(GNC_UseRestrictions value) {
        this.useRestrictions = value;
    }

    /**
     * Gets the value of the userRestrictions property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCUsersRestrictionsPropertyType }
     * 
     * 
     */
    public List<GNC_UsersRestrictions> getUserRestrictions() {
        if (userRestrictions == null) {
            userRestrictions = new ArrayList<GNC_UsersRestrictions>();
        }
        return this.userRestrictions;
    }

    public void setUserRestrictions(List<GNC_UsersRestrictions> userRestrictions) {
        this.userRestrictions = userRestrictions;
    }

    public void setUserRestrictions(GNC_UsersRestrictions userRestrictions) {
        if (this.userRestrictions == null) {
            this.userRestrictions = new ArrayList<GNC_UsersRestrictions>();
        }
        this.userRestrictions.add(userRestrictions);
    }
}
