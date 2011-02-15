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

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


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
    "usersRestrictions"
})
public class GNC_AccessConstraints implements org.opengis.metadata.geonetcab.GNC_AccessConstraints {

    private URI dataAccessConditionPortal;
    @XmlElement(required = true)
    private String nameOfConditions;
    @XmlElement(required = true)
    private Boolean nonCommercialUse;
    @XmlElement(required = true)
    private List<GNC_ThematicTypeCode> thematicUsage;
    private List<GNC_UserRestriction> usersRestrictions;

    /**
     * Gets the value of the dataAccessConditionPortal property.
     * 
     * @return
     *     possible object is
     *     {@link URL }
     *     
     */
    @Override
    public URI getDataAccessConditionPortal() {
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
    public void setDataAccessConditionPortal(final URI value) {
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
    @Override
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
    public void setNameOfConditions(final String value) {
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
    @Override
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
    public void setNonCommercialUse(final Boolean value) {
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

    public void setThematicUsage(final List<GNC_ThematicTypeCode> thematicUsage) {
        this.thematicUsage = thematicUsage;
    }

    public void setThematicUsage(final GNC_ThematicTypeCode thematicUsage) {
        if (this.thematicUsage == null) {
            this.thematicUsage = new ArrayList<GNC_ThematicTypeCode>();
        }
        this.thematicUsage.add(thematicUsage);
    }

    /**
     * Gets the value of the userRestrictions property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCUserRestrictionPropertyType }
     * 
     * 
     */
    @Override
    public List<GNC_UserRestriction> getUsersRestrictions() {
        if (usersRestrictions == null) {
            usersRestrictions = new ArrayList<GNC_UserRestriction>();
        }
        return this.usersRestrictions;
    }

    public void setUsersRestrictions(final List<GNC_UserRestriction> usersRestrictions) {
        this.usersRestrictions = usersRestrictions;
    }

    public void setUsersRestrictions(final GNC_UserRestriction usersRestrictions) {
        if (this.usersRestrictions == null) {
            this.usersRestrictions = new ArrayList<GNC_UserRestriction>();
        }
        this.usersRestrictions.add(usersRestrictions);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GNC_AccessConstraints) {
            GNC_AccessConstraints that = (GNC_AccessConstraints) obj;
            return Utilities.equals(this.dataAccessConditionPortal, that.dataAccessConditionPortal) &&
                   Utilities.equals(this.nameOfConditions, that.nameOfConditions) &&
                   Utilities.equals(this.nonCommercialUse, that.nonCommercialUse) &&
                   Utilities.equals(this.thematicUsage, that.thematicUsage) &&
                   Utilities.equals(this.usersRestrictions, that.usersRestrictions);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.dataAccessConditionPortal != null ? this.dataAccessConditionPortal.hashCode() : 0);
        hash = 97 * hash + (this.nameOfConditions != null ? this.nameOfConditions.hashCode() : 0);
        hash = 97 * hash + (this.nonCommercialUse != null ? this.nonCommercialUse.hashCode() : 0);
        hash = 97 * hash + (this.thematicUsage != null ? this.thematicUsage.hashCode() : 0);
        hash = 97 * hash + (this.usersRestrictions != null ? this.usersRestrictions.hashCode() : 0);
        return hash;
    }
}
