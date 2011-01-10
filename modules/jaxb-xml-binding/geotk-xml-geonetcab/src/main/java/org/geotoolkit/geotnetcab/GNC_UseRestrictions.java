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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.metadata.extent.GeographicDescription;


/**
 * <p>Java class for GNC_UseRestrictions_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_UseRestrictions_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="extentOfRestrictionsl" type="{http://www.isotc211.org/2005/gmd}EX_GeographicDescription_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="geographicRestrictions" type="{http://www.isotc211.org/2005/gco}Boolean_PropertyType"/>
 *         &lt;element name="otherConstraints" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_UseRestrictions_Type", propOrder = {
    "extentOfRestrictions",
    "geographicRestrictions",
    "otherConstraints"
})
public class GNC_UseRestrictions implements org.opengis.metadata.geonetcab.GNC_UseRestrictions {

    private List<GeographicDescription> extentOfRestrictions;
    @XmlElement(required = true)
    private Boolean geographicRestrictions;
    private String otherConstraints;

    /**
     * Gets the value of the extentOfRestrictions property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GeographicDescription }
     * 
     * 
     */
    @Override
    public List<GeographicDescription> getExtentOfRestrictions() {
        if (extentOfRestrictions == null) {
            extentOfRestrictions = new ArrayList<GeographicDescription>();
        }
        return this.extentOfRestrictions;
    }

    public void setExtentOfRestrictions(final List<GeographicDescription> extentOfRestrictions) {
        this.extentOfRestrictions = extentOfRestrictions;
    }

    public void setExtentOfRestrictions(final GeographicDescription extentOfRestrictions) {
        if (this.extentOfRestrictions == null) {
            this.extentOfRestrictions = new ArrayList<GeographicDescription>();
        }
        this.extentOfRestrictions.add(extentOfRestrictions);
    }

    /**
     * Gets the value of the geographicRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Override
    public Boolean getGeographicRestrictions() {
        return geographicRestrictions;
    }

    /**
     * Sets the value of the geographicRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGeographicRestrictions(final Boolean value) {
        this.geographicRestrictions = value;
    }

    /**
     * Gets the value of the otherConstraints property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getOtherConstraints() {
        return otherConstraints;
    }

    /**
     * Sets the value of the otherConstraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherConstraints(final String value) {
        this.otherConstraints = value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GNC_UseRestrictions) {
            GNC_UseRestrictions that = (GNC_UseRestrictions) obj;
            return Utilities.equals(this.extentOfRestrictions, that.extentOfRestrictions) &&
                   Utilities.equals(this.geographicRestrictions, that.geographicRestrictions) &&
                   Utilities.equals(this.otherConstraints, that.otherConstraints);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.extentOfRestrictions != null ? this.extentOfRestrictions.hashCode() : 0);
        hash = 71 * hash + (this.geographicRestrictions != null ? this.geographicRestrictions.hashCode() : 0);
        hash = 71 * hash + (this.otherConstraints != null ? this.otherConstraints.hashCode() : 0);
        return hash;
    }
}
